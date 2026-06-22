const db = require('../config/db');

const Review = {
  async findById(id) {
    const { rows } = await db.query(
      `SELECT r.*, u.name AS buyer_name, p.name AS product_name
       FROM reviews r
       JOIN users u ON r.buyer_id = u.id
       JOIN products p ON r.product_id = p.id
       WHERE r.id = $1`,
      [id]
    );
    return rows[0] || null;
  },

  async findByProduct(productId, { limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      `SELECT r.*, u.name AS buyer_name
       FROM reviews r JOIN users u ON r.buyer_id = u.id
       WHERE r.product_id = $1
       ORDER BY r.created_at DESC LIMIT $2 OFFSET $3`,
      [productId, limit, offset]
    );
    return rows;
  },

  async findByBuyer(buyerId) {
    const { rows } = await db.query(
      `SELECT r.*, p.name AS product_name
       FROM reviews r JOIN products p ON r.product_id = p.id
       WHERE r.buyer_id = $1 ORDER BY r.created_at DESC`,
      [buyerId]
    );
    return rows;
  },

  async getAverageRating(productId) {
    const { rows } = await db.query(
      'SELECT ROUND(AVG(rating)::numeric, 1) AS avg_rating, COUNT(*) AS total FROM reviews WHERE product_id = $1',
      [productId]
    );
    return rows[0];
  },

  async create({ buyer_id, product_id, order_id, rating, comment }) {
    const { rows } = await db.query(
      'INSERT INTO reviews (buyer_id, product_id, order_id, rating, comment) VALUES ($1,$2,$3,$4,$5) RETURNING *',
      [buyer_id, product_id, order_id, rating, comment]
    );
    return rows[0];
  },

  async delete(id) {
    await db.query('DELETE FROM reviews WHERE id = $1', [id]);
  },
};

module.exports = Review;
