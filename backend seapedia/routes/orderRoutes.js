const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const {
  getBuyerOrders,
  createOrder,
  getOrderDetail,
  cancelOrder,
} = require('../controllers/orderController');

router.use(authenticate);

router.get('/', authorizeRoles('buyer'), getBuyerOrders);
router.post('/', authorizeRoles('buyer'), createOrder);
router.get('/:id', getOrderDetail);
router.put('/:id/cancel', authorizeRoles('buyer'), cancelOrder);

module.exports = router;
