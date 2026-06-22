const Store = require('../models/Store');
const Product = require('../models/Product');
const Order = require('../models/Order');
const Promo = require('../models/Promo');

const getStore = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Store not found' });
    res.json({ success: true, store });
  } catch (err) { next(err); }
};

const createStore = async (req, res, next) => {
  try {
    const { name, description, address, image_url } = req.body;
    const existing = await Store.findBySeller(req.user.id);
    if (existing) return res.status(409).json({ success: false, message: 'Store already exists' });
    const store = await Store.create({ seller_id: req.user.id, name, description, address, image_url });
    res.status(201).json({ success: true, store });
  } catch (err) { next(err); }
};

const updateStore = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Store not found' });
    const { name, description, address, image_url } = req.body;
    const updated = await Store.update(store.id, { name, description, address, image_url });
    res.json({ success: true, store: updated });
  } catch (err) { next(err); }
};

const getSellerProducts = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Store not found' });
    const products = await Product.findByStore(store.id);
    res.json({ success: true, products });
  } catch (err) { next(err); }
};

const createProduct = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Create a store first' });
    const { name, description, price, stock, image_url, category } = req.body;
    const product = await Product.create({ store_id: store.id, name, description, price, stock, image_url, category });
    res.status(201).json({ success: true, product });
  } catch (err) { next(err); }
};

const updateProduct = async (req, res, next) => {
  try {
    const product = await Product.update(req.params.id, req.body);
    if (!product) return res.status(404).json({ success: false, message: 'Product not found' });
    res.json({ success: true, product });
  } catch (err) { next(err); }
};

const deleteProduct = async (req, res, next) => {
  try {
    await Product.delete(req.params.id);
    res.json({ success: true, message: 'Product deleted' });
  } catch (err) { next(err); }
};

const getSellerOrders = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Store not found' });
    const orders = await Order.findByStore(store.id);
    res.json({ success: true, orders });
  } catch (err) { next(err); }
};

const updateOrderStatus = async (req, res, next) => {
  try {
    const order = await Order.updateStatus(req.params.id, req.body.status);
    if (!order) return res.status(404).json({ success: false, message: 'Order not found' });
    res.json({ success: true, order });
  } catch (err) { next(err); }
};

const getSellerPromos = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Store not found' });
    const promos = await Promo.findByStore(store.id);
    res.json({ success: true, promos });
  } catch (err) { next(err); }
};

const createPromo = async (req, res, next) => {
  try {
    const store = await Store.findBySeller(req.user.id);
    if (!store) return res.status(404).json({ success: false, message: 'Store not found' });
    const promo = await Promo.create({ store_id: store.id, ...req.body });
    res.status(201).json({ success: true, promo });
  } catch (err) { next(err); }
};

const updatePromo = async (req, res, next) => {
  try {
    const promo = await Promo.update(req.params.id, req.body);
    if (!promo) return res.status(404).json({ success: false, message: 'Promo not found' });
    res.json({ success: true, promo });
  } catch (err) { next(err); }
};

const deletePromo = async (req, res, next) => {
  try {
    await Promo.delete(req.params.id);
    res.json({ success: true, message: 'Promo deleted' });
  } catch (err) { next(err); }
};

module.exports = {
  getStore, createStore, updateStore,
  getSellerProducts, createProduct, updateProduct, deleteProduct,
  getSellerOrders, updateOrderStatus,
  getSellerPromos, createPromo, updatePromo, deletePromo,
};
