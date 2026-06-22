const db = require('../config/db');

// discount_type: percentage | fixed
const Promo = {
  async findById(id) {
    const { rows } = await db.query('SELECT * FROM promos WHERE id = $1', [id]);
    return rows[0] || null;
  },

  async findByStore(storeId) {
    const { rows } = await db.query(
      'SELECT * FROM promos WHERE store_id = $1 AND is_active = true ORDER BY created_at DESC',
      [storeId]
    );
    return rows;
  },

  async findActive() {
    const { rows } = await db.query(
      'SELECT * FROM promos WHERE is_active = true AND start_date <= NOW() AND end_date >= NOW() ORDER BY created_at DESC'
    );
    return rows;
  },

  async create({ store_id, name, description, discount_type, discount_value, start_date, end_date }) {
    const { rows } = await db.query(
      'INSERT INTO promos (store_id, name, description, discount_type, discount_value, start_date, end_date) VALUES ($1,$2,$3,$4,$5,$6,$7) RETURNING *',
      [store_id, name, description, discount_type, discount_value, start_date, end_date]
    );
    return rows[0];
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE promos SET ${setClause} WHERE id = $${keys.length + 1} RETURNING *`,
      [...values, id]
    );
    return rows[0] || null;
  },

  async delete(id) {
    await db.query('DELETE FROM promos WHERE id = $1', [id]);
  },
};

module.exports = Promo;
