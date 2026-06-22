const Product = require('../models/Product');

const getAllProducts = async (req, res, next) => {
  try {
    const { page = 1, limit = 20, category } = req.query;
    const offset = (page - 1) * limit;
    const products = await Product.findAll({ limit: Number(limit), offset, category });
    res.json({ success: true, products });
  } catch (err) {
    next(err);
  }
};

const getProduct = async (req, res, next) => {
  try {
    const product = await Product.findById(req.params.id);
    if (!product) return res.status(404).json({ success: false, message: 'Product not found' });
    res.json({ success: true, product });
  } catch (err) {
    next(err);
  }
};

const searchProducts = async (req, res, next) => {
  try {
    const { q, page = 1, limit = 20, category } = req.query;
    const offset = (page - 1) * limit;
    const products = await Product.findAll({ search: q, limit: Number(limit), offset, category });
    res.json({ success: true, products });
  } catch (err) {
    next(err);
  }
};

module.exports = { getAllProducts, getProduct, searchProducts };
