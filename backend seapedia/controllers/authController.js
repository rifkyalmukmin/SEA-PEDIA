const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Role = require('../models/Role');
const supabase = require('../config/supabase');
const { JWT_SECRET, JWT_EXPIRES_IN } = require('../config/env');

const register = async (req, res, next) => {
  try {
    const { name, email, password, phone, role } = req.body;

    const existing = await User.findByEmail(email);
    if (existing) {
      return res.status(409).json({ success: false, message: 'Email already registered' });
    }

    const roleRecord = await Role.findByName(role || 'buyer');
    if (!roleRecord) {
      return res.status(400).json({ success: false, message: 'Invalid role' });
    }

    const hashed = await bcrypt.hash(password, 10);
    const user = await User.create({ name, email, password: hashed, phone, role_id: roleRecord.id });

    const token = jwt.sign({ id: user.id, role: roleRecord.name }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });

    res.status(201).json({ success: true, token, user });
  } catch (err) {
    next(err);
  }
};

const login = async (req, res, next) => {
  try {
    const { email, password } = req.body;

    const user = await User.findByEmail(email);
    if (!user) {
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    const match = await bcrypt.compare(password, user.password);
    if (!match) {
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    const role = await Role.findById(user.role_id);
    const token = jwt.sign({ id: user.id, role: role.name }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });

    const { password: _, ...safeUser } = user;
    res.json({ success: true, token, user: { ...safeUser, role: role.name } });
  } catch (err) {
    next(err);
  }
};

// Verifikasi Google ID Token via Supabase, lalu issue JWT internal
const googleAuth = async (req, res, next) => {
  try {
    const { idToken, role } = req.body;
    if (!idToken) {
      return res.status(400).json({ success: false, message: 'idToken is required' });
    }

    // Verifikasi token Google ke Supabase
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
    const name = googleUser.user_metadata?.full_name || googleUser.user_metadata?.name || email.split('@')[0];
    const avatarUrl = googleUser.user_metadata?.avatar_url || '';

    // Cek apakah user sudah terdaftar via google_id
    let existingUser = await User.findByGoogleId(googleId);

    // Fallback: cek via email (user sudah register manual sebelumnya)
    if (!existingUser) {
      existingUser = await User.findByEmail(email);
    }

    let dbUser;
    let roleName;

    if (existingUser) {
      dbUser = existingUser;
      const roleRecord = await Role.findById(existingUser.role_id);
      roleName = roleRecord.name;
    } else {
      // User baru — daftarkan dengan role dari request (default: buyer)
      const roleRecord = await Role.findByName(role || 'buyer');
      if (!roleRecord) {
        return res.status(400).json({ success: false, message: 'Invalid role' });
      }
      dbUser = await User.createWithGoogle({
        name,
        email,
        google_id: googleId,
        avatar_url: avatarUrl,
        role_id: roleRecord.id,
      });
      roleName = roleRecord.name;
    }

    const token = jwt.sign({ id: dbUser.id, role: roleName }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
    const { password: _, ...safeUser } = dbUser;

    res.json({
      success: true,
      token,
      user: { ...safeUser, role: roleName, avatarUrl },
    });
  } catch (err) {
    next(err);
  }
};

const getProfile = async (req, res, next) => {
  try {
    const user = await User.findById(req.user.id);
    if (!user) return res.status(404).json({ success: false, message: 'User not found' });
    res.json({ success: true, user });
  } catch (err) {
    next(err);
  }
};

const updateProfile = async (req, res, next) => {
  try {
    const { name, phone } = req.body;
    const user = await User.update(req.user.id, { name, phone });
    res.json({ success: true, user });
  } catch (err) {
    next(err);
  }
};

module.exports = { register, login, googleAuth, getProfile, updateProfile };
