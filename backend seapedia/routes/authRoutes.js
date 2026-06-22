const express = require('express');
const router = express.Router();
const { register, login, googleAuth, getProfile, updateProfile } = require('../controllers/authController');
const { authenticate } = require('../middleware/authMiddleware');
const { validateRegister, validateLogin } = require('../validators/authValidator');
const { validate } = require('../middleware/validationMiddleware');

router.post('/register', validateRegister, validate, register);
router.post('/login', validateLogin, validate, login);
router.post('/google', googleAuth);
router.get('/profile', authenticate, getProfile);
router.put('/profile', authenticate, updateProfile);

module.exports = router;
