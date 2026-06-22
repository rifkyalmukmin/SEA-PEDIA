const db = require('../config/db');

const User = {
  async findById(id) {
    const { rows } = await db.query(
      'SELECT id, name, email, phone, role_id, created_at FROM users WHERE id = $1',
      [id]
    );
    return rows[0] || null;
  },

  async findByEmail(email) {
    const { rows } = await db.query('SELECT * FROM users WHERE email = $1', [email]);
    return rows[0] || null;
  },

  async create({ name, email, password, phone, role_id }) {
    const { rows } = await db.query(
      'INSERT INTO users (name, email, password, phone, role_id) VALUES ($1, $2, $3, $4, $5) RETURNING id, name, email, phone, role_id, created_at',
      [name, email, password, phone, role_id]
    );
    return rows[0];
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE users SET ${setClause}, updated_at = NOW() WHERE id = $${keys.length + 1} RETURNING id, name, email, phone, role_id`,
      [...values, id]
    );
    return rows[0] || null;
  },

  async delete(id) {
    await db.query('DELETE FROM users WHERE id = $1', [id]);
  },

  async findAll({ limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT id, name, email, phone, role_id, created_at FROM users ORDER BY created_at DESC LIMIT $1 OFFSET $2',
      [limit, offset]
    );
    return rows;
  },
};

module.exports = User;
