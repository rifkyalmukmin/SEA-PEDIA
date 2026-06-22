const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const {
  getAvailableOrders,
  getDriverDeliveries,
  acceptDelivery,
  updateDeliveryStatus,
} = require('../controllers/driverController');

router.use(authenticate, authorizeRoles('driver'));

router.get('/orders/available', getAvailableOrders);
router.get('/deliveries', getDriverDeliveries);
router.post('/deliveries/:orderId/accept', acceptDelivery);
router.put('/deliveries/:id/status', updateDeliveryStatus);

module.exports = router;
