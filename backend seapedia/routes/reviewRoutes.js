const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const {
  getProductReviews,
  createReview,
  deleteReview,
} = require('../controllers/reviewController');

router.get('/products/:productId', getProductReviews);
router.post('/', authenticate, authorizeRoles('buyer'), createReview);
router.delete('/:id', authenticate, authorizeRoles('buyer', 'admin'), deleteReview);

module.exports = router;
