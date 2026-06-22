const { body } = require('express-validator');

const validateCreateReview = [
  body('product_id').notEmpty().withMessage('Product ID is required'),
  body('order_id').notEmpty().withMessage('Order ID is required'),
  body('rating').isInt({ min: 1, max: 5 }).withMessage('Rating must be between 1 and 5'),
  body('comment').optional().trim(),
];

module.exports = { validateCreateReview };
