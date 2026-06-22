const Order = require('../models/Order');
const Delivery = require('../models/Delivery');

const getAvailableOrders = async (req, res, next) => {
  try {
    const orders = await Order.findAll({ status: 'ready_for_pickup' });
    res.json({ success: true, orders });
  } catch (err) { next(err); }
};

const getDriverDeliveries = async (req, res, next) => {
  try {
    const deliveries = await Delivery.findByDriver(req.user.id);
    res.json({ success: true, deliveries });
  } catch (err) { next(err); }
};

const acceptDelivery = async (req, res, next) => {
  try {
    const order = await Order.findById(req.params.orderId);
    if (!order) return res.status(404).json({ success: false, message: 'Order not found' });
    if (order.status !== 'ready_for_pickup') {
      return res.status(400).json({ success: false, message: 'Order is not available for pickup' });
    }

    const delivery = await Delivery.create({
      order_id: order.id,
      driver_id: req.user.id,
      pickup_address: order.store_address || '',
      delivery_address: order.delivery_address,
    });

    await Order.assignDriver(order.id, req.user.id);

    res.status(201).json({ success: true, delivery });
  } catch (err) { next(err); }
};

const updateDeliveryStatus = async (req, res, next) => {
  try {
    const { status } = req.body;
    const delivery = await Delivery.updateStatus(req.params.id, status);
    if (!delivery) return res.status(404).json({ success: false, message: 'Delivery not found' });

    if (status === 'delivered') {
      await Order.updateStatus(delivery.order_id, 'delivered');
    }

    res.json({ success: true, delivery });
  } catch (err) { next(err); }
};

module.exports = { getAvailableOrders, getDriverDeliveries, acceptDelivery, updateDeliveryStatus };
