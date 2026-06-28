const { body } = require('express-validator');

// Ulasan aplikasi: nama, rating 1-5, komentar. Guest boleh (tidak butuh login).
const validateCreateReview = [
  body('reviewer_name').trim().notEmpty().withMessage('Reviewer name is required')
    .isLength({ max: 100 }).withMessage('Reviewer name too long'),
  body('rating').isInt({ min: 1, max: 5 }).withMessage('Rating must be between 1 and 5'),
  body('comment').trim().notEmpty().withMessage('Comment is required'),
];

module.exports = { validateCreateReview };
