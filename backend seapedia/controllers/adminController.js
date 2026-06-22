const User = require('../models/User');
const Order = require('../models/Order');
const Store = require('../models/Store');
const Voucher = require('../models/Voucher');

const getAllUsers = async (req, res, next) => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (page - 1) * limit;
    const users = await User.findAll({ limit: Number(limit), offset });
    res.json({ success: true, users });
  } catch (err) { next(err); }
};

const getUserById = async (req, res, next) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) return res.status(404).json({ success: false, message: 'User not found' });
    res.json({ success: true, user });
  } catch (err) { next(err); }
};

const updateUser = async (req, res, next) => {
  try {
    const user = await User.update(req.params.id, req.body);
    if (!user) return res.status(404).json({ success: false, message: 'User not found' });
    res.json({ success: true, user });
  } catch (err) { next(err); }
};

const deleteUser = async (req, res, next) => {
  try {
    await User.delete(req.params.id);
    res.json({ success: true, message: 'User deleted' });
  } catch (err) { next(err); }
};

const getAllOrders = async (req, res, next) => {
  try {
    const { page = 1, limit = 20, status } = req.query;
    const offset = (page - 1) * limit;
    const orders = await Order.findAll({ status, limit: Number(limit), offset });
    res.json({ success: true, orders });
  } catch (err) { next(err); }
};

const getAllStores = async (req, res, next) => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (page - 1) * limit;
    const stores = await Store.findAll({ limit: Number(limit), offset });
    res.json({ success: true, stores });
  } catch (err) { next(err); }
};

const getAllVouchers = async (req, res, next) => {
  try {
    const vouchers = await Voucher.findAll();
    res.json({ success: true, vouchers });
  } catch (err) { next(err); }
};

const createVoucher = async (req, res, next) => {
  try {
    const voucher = await Voucher.create(req.body);
    res.status(201).json({ success: true, voucher });
  } catch (err) { next(err); }
};

const updateVoucher = async (req, res, next) => {
  try {
    const voucher = await Voucher.update(req.params.id, req.body);
    if (!voucher) return res.status(404).json({ success: false, message: 'Voucher not found' });
    res.json({ success: true, voucher });
  } catch (err) { next(err); }
};

const deleteVoucher = async (req, res, next) => {
  try {
    await Voucher.delete(req.params.id);
    res.json({ success: true, message: 'Voucher deleted' });
  } catch (err) { next(err); }
};

module.exports = {
  getAllUsers, getUserById, updateUser, deleteUser,
  getAllOrders, getAllStores,
  getAllVouchers, createVoucher, updateVoucher, deleteVoucher,
};
