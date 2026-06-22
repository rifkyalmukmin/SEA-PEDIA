const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Role = require('../models/Role');
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

module.exports = { register, login, getProfile, updateProfile };
