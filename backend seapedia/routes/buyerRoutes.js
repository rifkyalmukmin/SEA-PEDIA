const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const {
  getCart,
  addToCart,
  updateCartItem,
  removeFromCart,
  clearCart,
} = require('../controllers/cartController');
const {
  getBuyerOrders,
  createOrder,
  getOrderDetail,
  cancelOrder,
} = require('../controllers/orderController');

router.use(authenticate, authorizeRoles('buyer'));

router.get('/cart', getCart);
router.post('/cart', addToCart);
router.put('/cart/:productId', updateCartItem);
router.delete('/cart/:productId', removeFromCart);
router.delete('/cart', clearCart);

router.get('/orders', getBuyerOrders);
router.post('/orders', createOrder);
router.get('/orders/:id', getOrderDetail);
router.put('/orders/:id/cancel', cancelOrder);

module.exports = router;
