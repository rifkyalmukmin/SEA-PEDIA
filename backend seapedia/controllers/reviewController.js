const Review = require('../models/Review');

// Daftar ulasan aplikasi (publik). Guest pun boleh melihat.
const listReviews = async (req, res, next) => {
  try {
    const { page = 1, limit = 50 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);
    const [reviews, stats] = await Promise.all([
      Review.findAll({ limit: Number(limit), offset }),
      Review.getStats(),
    ]);
    res.json({ success: true, stats, data: reviews });
  } catch (err) {
    next(err);
  }
};

// Kirim ulasan aplikasi. Guest boleh (tanpa login, tanpa transaksi).
// Jika request membawa token valid, user_id ikut dicatat.
const createReview = async (req, res, next) => {
  try {
    const { reviewer_name, rating, comment } = req.body;
    const userId = req.user ? req.user.id : null;

    const review = await Review.create({
      user_id: userId,
      reviewer_name: String(reviewer_name).trim(),
      rating: Number(rating),
      comment: String(comment).trim(),
    });

    res.status(201).json({ success: true, message: 'Review submitted', data: review });
  } catch (err) {
    next(err);
  }
};

const deleteReview = async (req, res, next) => {
  try {
    await Review.delete(req.params.id);
    res.json({ success: true, message: 'Review deleted' });
  } catch (err) {
    next(err);
  }
};

module.exports = { listReviews, createReview, deleteReview };
