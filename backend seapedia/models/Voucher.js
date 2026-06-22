const db = require('../config/db');

// discount_type: percentage | fixed
const Voucher = {
  async findById(id) {
    const { rows } = await db.query('SELECT * FROM vouchers WHERE id = $1', [id]);
    return rows[0] || null;
  },

  async findByCode(code) {
    const { rows } = await db.query(
      'SELECT * FROM vouchers WHERE code = $1 AND is_active = true AND expires_at > NOW()',
      [code]
    );
    return rows[0] || null;
  },

  async findAll({ limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM vouchers ORDER BY created_at DESC LIMIT $1 OFFSET $2',
      [limit, offset]
    );
    return rows;
  },

  async create({ code, discount_type, discount_value, min_purchase, max_uses, expires_at }) {
    const { rows } = await db.query(
      'INSERT INTO vouchers (code, discount_type, discount_value, min_purchase, max_uses, expires_at) VALUES ($1,$2,$3,$4,$5,$6) RETURNING *',
      [code, discount_type, discount_value, min_purchase, max_uses, expires_at]
    );
    return rows[0];
  },

  async incrementUsage(id) {
    const { rows } = await db.query(
      'UPDATE vouchers SET used_count = used_count + 1 WHERE id = $1 RETURNING *',
      [id]
    );
    return rows[0] || null;
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE vouchers SET ${setClause} WHERE id = $${keys.length + 1} RETURNING *`,
      [...values, id]
    );
    return rows[0] || null;
  },

  async delete(id) {
    await db.query('DELETE FROM vouchers WHERE id = $1', [id]);
  },
};

module.exports = Voucher;
