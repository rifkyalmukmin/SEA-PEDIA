const db = require('../config/db');

// Ulasan APLIKASI (bukan ulasan produk). Sesuai tabel `reviews`:
//   id, user_id (nullable — guest boleh), reviewer_name, rating 1-5, comment, created_at
const Review = {
  async findAll({ limit = 50, offset = 0 } = {}) {
    const { rows } = await db.query(
      `SELECT id, user_id, reviewer_name, rating, comment, created_at
       FROM reviews
       ORDER BY created_at DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );
    return rows;
  },

  async getStats() {
    const { rows } = await db.query(
      `SELECT ROUND(AVG(rating)::numeric, 1) AS avg_rating, COUNT(*)::int AS total
       FROM reviews`
    );
    return rows[0];
  },

  async create({ user_id = null, reviewer_name, rating, comment }) {
    const { rows } = await db.query(
      `INSERT INTO reviews (user_id, reviewer_name, rating, comment)
       VALUES ($1, $2, $3, $4)
       RETURNING id, user_id, reviewer_name, rating, comment, created_at`,
      [user_id, reviewer_name, rating, comment]
    );
    return rows[0];
  },

  async delete(id) {
    await db.query('DELETE FROM reviews WHERE id = $1', [id]);
  },
};

module.exports = Review;
