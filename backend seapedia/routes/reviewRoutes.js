const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const { validate } = require('../middleware/validationMiddleware');
const { validateCreateReview } = require('../validators/reviewValidator');
const { listReviews, createReview, deleteReview } = require('../controllers/reviewController');

// Ulasan APLIKASI (publik) — Level 1.
router.get('/', listReviews);                                   // publik
router.post('/', validateCreateReview, validate, createReview); // guest boleh (tanpa auth)
router.delete('/:id', authenticate, authorizeRoles('admin'), deleteReview); // hapus: admin

module.exports = router;
