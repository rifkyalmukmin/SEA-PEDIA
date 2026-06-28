const express = require('express');
const router = express.Router();
const {
  register,
  login,
  googleAuth,
  selectRole,
  getMe,
  getProfile,
  updateProfile,
} = require('../controllers/authController');
const { authenticate } = require('../middleware/authMiddleware');
const {
  validateRegister,
  validateLogin,
  validateSelectRole,
} = require('../validators/authValidator');
const { validate } = require('../middleware/validationMiddleware');

router.post('/register', validateRegister, validate, register);
router.post('/login', validateLogin, validate, login);
router.post('/google', googleAuth);
router.post('/select-role', authenticate, validateSelectRole, validate, selectRole);
router.get('/me', authenticate, getMe);
router.get('/profile', authenticate, getProfile);
router.put('/profile', authenticate, updateProfile);

module.exports = router;
