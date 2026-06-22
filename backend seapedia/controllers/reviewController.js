const Review = require('../models/Review');

const getProductReviews = async (req, res, next) => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (page - 1) * limit;
    const [reviews, stats] = await Promise.all([
      Review.findByProduct(req.params.productId, { limit: Number(limit), offset }),
      Review.getAverageRating(req.params.productId),
    ]);
    res.json({ success: true, stats, reviews });
  } catch (err) { next(err); }
};

const createReview = async (req, res, next) => {
  try {
    const { product_id, order_id, rating, comment } = req.body;
    const review = await Review.create({ buyer_id: req.user.id, product_id, order_id, rating, comment });
    res.status(201).json({ success: true, review });
  } catch (err) { next(err); }
};

const deleteReview = async (req, res, next) => {
  try {
    await Review.delete(req.params.id);
    res.json({ success: true, message: 'Review deleted' });
  } catch (err) { next(err); }
};

module.exports = { getProductReviews, createReview, deleteReview };
