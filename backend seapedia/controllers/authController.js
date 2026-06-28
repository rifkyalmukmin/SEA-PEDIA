const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Role = require('../models/Role');
const supabase = require('../config/supabase');
const { JWT_SECRET, JWT_EXPIRES_IN } = require('../config/env');

const NON_ADMIN_ROLES = ['buyer', 'seller', 'driver'];

// Terbitkan JWT membawa identitas + active role + daftar role yang dimiliki.
const signToken = (userId, activeRole, roles) =>
  jwt.sign({ id: userId, activeRole: activeRole || null, roles }, JWT_SECRET, {
    expiresIn: JWT_EXPIRES_IN,
  });

// Bentuk objek `data` yang konsisten untuk semua respons auth.
// `role` = active role (bisa null jika user multi-role belum memilih).
const buildAuthData = (user, roles, activeRole, token) => ({
  id: user.id,
  name: user.full_name,
  email: user.email,
  phone: user.phone || '',
  avatarUrl: user.avatar_url || '',
  role: activeRole || null,
  roles,
  token,
});

// Normalisasi input role: terima `roles` (array) atau `role` (string tunggal).
const parseRequestedRoles = (body) => {
  let requested = [];
  if (Array.isArray(body.roles) && body.roles.length > 0) {
    requested = body.roles;
  } else if (body.role) {
    requested = [body.role];
  } else {
    requested = ['buyer']; // default
  }
  return [...new Set(requested.map((r) => String(r).toLowerCase()))];
};

const register = async (req, res, next) => {
  try {
    const { name, email, password, phone } = req.body;
    const requestedRoles = parseRequestedRoles(req.body);

    // Admin tidak boleh self-register.
    const invalid = requestedRoles.filter((r) => !NON_ADMIN_ROLES.includes(r));
    if (invalid.length > 0) {
      return res
        .status(400)
        .json({ success: false, message: `Invalid role(s): ${invalid.join(', ')}` });
    }

    const existing = await User.findByEmail(email);
    if (existing) {
      return res.status(409).json({ success: false, message: 'Email already registered' });
    }

    const hashed = await bcrypt.hash(password, 10);
    const user = await User.create({ full_name: name, email, password: hashed, phone });

    // Pasang semua role yang diminta ke pivot.
    const roleRecords = [];
    for (const roleName of requestedRoles) {
      const roleRecord = await Role.findByName(roleName);
      if (!roleRecord) {
        return res.status(400).json({ success: false, message: `Invalid role: ${roleName}` });
      }
      await User.addRole(user.id, roleRecord.id);
      roleRecords.push(roleRecord);
    }

    // Active role: jika hanya 1 role, set langsung; jika >1, biarkan null (harus pilih).
    let activeRole = null;
    if (roleRecords.length === 1) {
      await User.setActiveRole(user.id, roleRecords[0].id);
      activeRole = requestedRoles[0];
    }

    const token = signToken(user.id, activeRole, requestedRoles);
    res.status(201).json({
      success: true,
      message: 'Registration successful',
      data: buildAuthData(user, requestedRoles, activeRole, token),
    });
  } catch (err) {
    next(err);
  }
};

const login = async (req, res, next) => {
  try {
    const { email, password } = req.body;

    const user = await User.findByEmail(email);
    if (!user || !user.password) {
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    const match = await bcrypt.compare(password, user.password);
    if (!match) {
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    const roles = await User.getRoles(user.id);
    let activeRole = await User.getActiveRole(user.id);

    // Single-role: auto-set active role bila belum ada.
    if (!activeRole && roles.length === 1) {
      const roleRecord = await Role.findByName(roles[0]);
      await User.setActiveRole(user.id, roleRecord.id);
      activeRole = roles[0];
    }

    const token = signToken(user.id, activeRole, roles);
    res.json({
      success: true,
      message: 'Login successful',
      data: buildAuthData(user, roles, activeRole, token),
    });
  } catch (err) {
    next(err);
  }
};

// Verifikasi Google ID Token via Supabase, lalu issue JWT internal.
const googleAuth = async (req, res, next) => {
  try {
    const { idToken, role } = req.body;
    if (!idToken) {
      return res.status(400).json({ success: false, message: 'idToken is required' });
    }

    const { data, error } = await supabase.auth.signInWithIdToken({
      provider: 'google',
      token: idToken,
    });

    if (error || !data.user) {
      return res.status(401).json({ success: false, message: 'Invalid Google token' });
    }

    const googleUser = data.user;
    const email = googleUser.email;
    const googleId = googleUser.id;
    const fullName =
      googleUser.user_metadata?.full_name ||
      googleUser.user_metadata?.name ||
      email.split('@')[0];
    const avatarUrl = googleUser.user_metadata?.avatar_url || '';

    // Cek user existing via google_id, fallback ke email.
    let dbUser = await User.findByGoogleId(googleId);
    if (!dbUser) dbUser = await User.findByEmail(email);

    if (!dbUser) {
      // User baru — daftarkan dengan role dari request (default buyer).
      const roleName = String(role || 'buyer').toLowerCase();
      if (!NON_ADMIN_ROLES.includes(roleName)) {
        return res.status(400).json({ success: false, message: 'Invalid role' });
      }
      dbUser = await User.createWithGoogle({
        full_name: fullName,
        email,
        google_id: googleId,
        avatar_url: avatarUrl,
      });
      const roleRecord = await Role.findByName(roleName);
      await User.addRole(dbUser.id, roleRecord.id);
      await User.setActiveRole(dbUser.id, roleRecord.id);
    }

    const roles = await User.getRoles(dbUser.id);
    let activeRole = await User.getActiveRole(dbUser.id);
    if (!activeRole && roles.length === 1) {
      const roleRecord = await Role.findByName(roles[0]);
      await User.setActiveRole(dbUser.id, roleRecord.id);
      activeRole = roles[0];
    }

    const token = signToken(dbUser.id, activeRole, roles);
    res.json({
      success: true,
      message: 'Google sign-in successful',
      data: buildAuthData(dbUser, roles, activeRole, token),
    });
  } catch (err) {
    next(err);
  }
};

// Pilih active role untuk sesi (user multi-role). Terbitkan JWT baru.
const selectRole = async (req, res, next) => {
  try {
    const requested = String(req.body.role).toLowerCase();

    // Wajib termasuk role yang dimiliki user (sumber kebenaran = DB).
    const roles = await User.getRoles(req.user.id);
    if (!roles.includes(requested)) {
      return res
        .status(403)
        .json({ success: false, message: 'You do not own this role' });
    }

    const roleRecord = await Role.findByName(requested);
    await User.setActiveRole(req.user.id, roleRecord.id);

    const user = await User.findById(req.user.id);
    const token = signToken(user.id, requested, roles);
    res.json({
      success: true,
      message: 'Active role updated',
      data: buildAuthData(user, roles, requested, token),
    });
  } catch (err) {
    next(err);
  }
};

// Profil user saat ini + roles[] + activeRole.
const getMe = async (req, res, next) => {
  try {
    const user = await User.findByIdWithRoles(req.user.id);
    if (!user) return res.status(404).json({ success: false, message: 'User not found' });
    res.json({
      success: true,
      data: {
        id: user.id,
        name: user.full_name,
        email: user.email,
        phone: user.phone || '',
        avatarUrl: user.avatar_url || '',
        roles: user.roles,
        activeRole: user.activeRole,
      },
    });
  } catch (err) {
    next(err);
  }
};

const getProfile = getMe;

const updateProfile = async (req, res, next) => {
  try {
    const { name, phone } = req.body;
    const fields = {};
    if (name !== undefined) fields.full_name = name;
    if (phone !== undefined) fields.phone = phone;
    const user = await User.update(req.user.id, fields);
    res.json({ success: true, data: user });
  } catch (err) {
    next(err);
  }
};

module.exports = { register, login, googleAuth, selectRole, getMe, getProfile, updateProfile };
