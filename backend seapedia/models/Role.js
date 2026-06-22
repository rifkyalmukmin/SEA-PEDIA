const db = require('../config/db');

// Roles: buyer | seller | driver | admin
const Role = {
  async findAll() {
    const { rows } = await db.query('SELECT * FROM roles ORDER BY id');
    return rows;
  },

  async findById(id) {
    const { rows } = await db.query('SELECT * FROM roles WHERE id = $1', [id]);
    return rows[0] || null;
  },

  async findByName(name) {
    const { rows } = await db.query('SELECT * FROM roles WHERE name = $1', [name]);
    return rows[0] || null;
  },
};

module.exports = Role;
