const formatCurrency = (amount) =>
  new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR' }).format(amount);

const paginate = (page = 1, limit = 20) => ({
  limit: Number(limit),
  offset: (Number(page) - 1) * Number(limit),
});

const successResponse = (res, data, statusCode = 200) =>
  res.status(statusCode).json({ success: true, ...data });

const errorResponse = (res, message, statusCode = 400) =>
  res.status(statusCode).json({ success: false, message });

module.exports = { formatCurrency, paginate, successResponse, errorResponse };
