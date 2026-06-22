const { body } = require('express-validator');

const validateCreateOrder = [
  body('store_id').notEmpty().withMessage('Store ID is required'),
  body('items').isArray({ min: 1 }).withMessage('Items must be a non-empty array'),
  body('items.*.product_id').notEmpty().withMessage('Product ID is required'),
  body('items.*.quantity').isInt({ min: 1 }).withMessage('Quantity must be at least 1'),
  body('items.*.price').isFloat({ min: 0 }).withMessage('Price must be a positive number'),
  body('delivery_address').trim().notEmpty().withMessage('Delivery address is required'),
];

module.exports = { validateCreateOrder };
