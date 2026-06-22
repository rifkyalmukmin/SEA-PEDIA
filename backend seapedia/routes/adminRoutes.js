const express = require('express');
const router = express.Router();
const { authenticate } = require('../middleware/authMiddleware');
const { authorizeRoles } = require('../middleware/roleMiddleware');
const {
  getAllUsers,
  getUserById,
  updateUser,
  deleteUser,
  getAllOrders,
  getAllStores,
  createVoucher,
  updateVoucher,
  deleteVoucher,
  getAllVouchers,
} = require('../controllers/adminController');

router.use(authenticate, authorizeRoles('admin'));

router.get('/users', getAllUsers);
router.get('/users/:id', getUserById);
router.put('/users/:id', updateUser);
router.delete('/users/:id', deleteUser);

router.get('/orders', getAllOrders);
router.get('/stores', getAllStores);

router.get('/vouchers', getAllVouchers);
router.post('/vouchers', createVoucher);
router.put('/vouchers/:id', updateVoucher);
router.delete('/vouchers/:id', deleteVoucher);

module.exports = router;
