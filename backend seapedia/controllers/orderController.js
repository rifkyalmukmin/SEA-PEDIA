const Order = require('../models/Order');
const Cart = require('../models/Cart');
const Voucher = require('../models/Voucher');

const getBuyerOrders = async (req, res, next) => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (page - 1) * limit;
    const orders = await Order.findByBuyer(req.user.id, { limit: Number(limit), offset });
    res.json({ success: true, orders });
  } catch (err) { next(err); }
};

const createOrder = async (req, res, next) => {
  try {
    const { store_id, items, delivery_address, notes, voucher_code } = req.body;

    let voucher = null;
    if (voucher_code) {
      voucher = await Voucher.findByCode(voucher_code);
      if (!voucher) return res.status(400).json({ success: false, message: 'Invalid or expired voucher' });
    }

    let totalPrice = items.reduce((sum, i) => sum + i.price * i.quantity, 0);

    if (voucher) {
      const discount = voucher.discount_type === 'percentage'
        ? (totalPrice * voucher.discount_value) / 100
        : voucher.discount_value;
      totalPrice = Math.max(0, totalPrice - discount);
    }

    const order = await Order.create({
      buyer_id: req.user.id,
      store_id,
      voucher_id: voucher?.id || null,
      total_price: totalPrice,
      delivery_address,
      notes,
    });

    for (const item of items) {
      await Order.addItem(order.id, item.product_id, item.quantity, item.price);
    }

    if (voucher) await Voucher.incrementUsage(voucher.id);
    await Cart.clearCart(req.user.id);

    res.status(201).json({ success: true, order });
  } catch (err) { next(err); }
};

const getOrderDetail = async (req, res, next) => {
  try {
    const order = await Order.findById(req.params.id);
    if (!order) return res.status(404).json({ success: false, message: 'Order not found' });
    const items = await Order.findItemsByOrderId(order.id);
    res.json({ success: true, order: { ...order, items } });
  } catch (err) { next(err); }
};

const cancelOrder = async (req, res, next) => {
  try {
    const order = await Order.findById(req.params.id);
    if (!order) return res.status(404).json({ success: false, message: 'Order not found' });
    if (!['pending', 'confirmed'].includes(order.status)) {
      return res.status(400).json({ success: false, message: 'Order cannot be cancelled at this stage' });
    }
    const updated = await Order.updateStatus(req.params.id, 'cancelled');
    res.json({ success: true, order: updated });
  } catch (err) { next(err); }
};

module.exports = { getBuyerOrders, createOrder, getOrderDetail, cancelOrder };
