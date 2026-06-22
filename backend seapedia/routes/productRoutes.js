const express = require('express');
const router = express.Router();
const { getAllProducts, getProduct, searchProducts } = require('../controllers/productController');
const { authenticate } = require('../middleware/authMiddleware');

router.get('/', getAllProducts);
router.get('/search', searchProducts);
router.get('/:id', getProduct);

module.exports = router;
