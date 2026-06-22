const db = require('../config/db');

const Store = {
  async findById(id) {
    const { rows } = await db.query('SELECT * FROM stores WHERE id = $1', [id]);
    return rows[0] || null;
  },

  async findBySeller(sellerId) {
    const { rows } = await db.query('SELECT * FROM stores WHERE seller_id = $1', [sellerId]);
    return rows[0] || null;
  },

  async findAll({ limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM stores ORDER BY created_at DESC LIMIT $1 OFFSET $2',
      [limit, offset]
    );
    return rows;
  },

  async create({ seller_id, name, description, address, image_url }) {
    const { rows } = await db.query(
      'INSERT INTO stores (seller_id, name, description, address, image_url) VALUES ($1,$2,$3,$4,$5) RETURNING *',
      [seller_id, name, description, address, image_url]
    );
    return rows[0];
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE stores SET ${setClause}, updated_at = NOW() WHERE id = $${keys.length + 1} RETURNING *`,
      [...values, id]
    );
    return rows[0] || null;
  },
};

module.exports = Store;
