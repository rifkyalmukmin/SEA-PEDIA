const { body } = require('express-validator');

const validateRegister = [
  body('name').trim().notEmpty().withMessage('Name is required'),
  body('email').isEmail().normalizeEmail().withMessage('Valid email is required'),
  body('password').isLength({ min: 6 }).withMessage('Password must be at least 6 characters'),
  // Mendukung role tunggal (back-compat) atau array roles untuk multi-role.
  body('role').optional().isIn(['buyer', 'seller', 'driver']).withMessage('Invalid role'),
  body('roles').optional().isArray({ min: 1 }).withMessage('roles must be a non-empty array'),
  body('roles.*').optional().isIn(['buyer', 'seller', 'driver']).withMessage('Invalid role in roles'),
];

const validateLogin = [
  body('email').isEmail().normalizeEmail().withMessage('Valid email is required'),
  body('password').notEmpty().withMessage('Password is required'),
];

const validateSelectRole = [
  body('role').isIn(['buyer', 'seller', 'driver', 'admin']).withMessage('Invalid role'),
];

module.exports = { validateRegister, validateLogin, validateSelectRole };
