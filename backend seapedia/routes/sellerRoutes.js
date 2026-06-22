const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const {
  getStore,
  createStore,
  updateStore,
  getSellerProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  getSellerOrders,
  updateOrderStatus,
  getSellerPromos,
  createPromo,
  updatePromo,
  deletePromo,
} = require('../controllers/sellerController');

router.use(authenticate, authorizeRoles('seller'));

router.get('/store', getStore);
router.post('/store', createStore);
router.put('/store', updateStore);

router.get('/products', getSellerProducts);
router.post('/products', createProduct);
router.put('/products/:id', updateProduct);
router.delete('/products/:id', deleteProduct);

router.get('/orders', getSellerOrders);
router.put('/orders/:id/status', updateOrderStatus);

router.get('/promos', getSellerPromos);
router.post('/promos', createPromo);
router.put('/promos/:id', updatePromo);
router.delete('/promos/:id', deletePromo);

module.exports = router;
