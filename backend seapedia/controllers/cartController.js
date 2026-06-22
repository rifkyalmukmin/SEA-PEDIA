const Cart = require('../models/Cart');

const getCart = async (req, res, next) => {
  try {
    const items = await Cart.findByBuyer(req.user.id);
    res.json({ success: true, items });
  } catch (err) { next(err); }
};

const addToCart = async (req, res, next) => {
  try {
    const { product_id, quantity = 1 } = req.body;
    const item = await Cart.addItem(req.user.id, product_id, quantity);
    res.status(201).json({ success: true, item });
  } catch (err) { next(err); }
};

const updateCartItem = async (req, res, next) => {
  try {
    const { quantity } = req.body;
    if (quantity <= 0) {
      await Cart.removeItem(req.user.id, req.params.productId);
      return res.json({ success: true, message: 'Item removed' });
    }
    const item = await Cart.updateQuantity(req.user.id, req.params.productId, quantity);
    res.json({ success: true, item });
  } catch (err) { next(err); }
};

const removeFromCart = async (req, res, next) => {
  try {
    await Cart.removeItem(req.user.id, req.params.productId);
    res.json({ success: true, message: 'Item removed from cart' });
  } catch (err) { next(err); }
};

const clearCart = async (req, res, next) => {
  try {
    await Cart.clearCart(req.user.id);
    res.json({ success: true, message: 'Cart cleared' });
  } catch (err) { next(err); }
};

module.exports = { getCart, addToCart, updateCartItem, removeFromCart, clearCart };
