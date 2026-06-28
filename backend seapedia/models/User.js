const db = require('../config/db');
const { toApiRole } = require('../utils/helpers');

// Kolom aman untuk dikembalikan ke client (tanpa password).
const SAFE_COLS = 'id, full_name, email, phone, google_id, avatar_url, active_role_id, created_at';

const User = {
  async findById(id) {
    const { rows } = await db.query(
      `SELECT ${SAFE_COLS} FROM users WHERE id = $1`,
      [id]
    );
    return rows[0] || null;
  },

  async findByEmail(email) {
    const { rows } = await db.query('SELECT * FROM users WHERE email = $1', [email]);
    return rows[0] || null;
  },

  async findByGoogleId(googleId) {
    const { rows } = await db.query('SELECT * FROM users WHERE google_id = $1', [googleId]);
    return rows[0] || null;
  },

  async create({ full_name, email, password, phone }) {
    const { rows } = await db.query(
      `INSERT INTO users (full_name, email, password, phone)
       VALUES ($1, $2, $3, $4)
       RETURNING ${SAFE_COLS}`,
      [full_name, email, password, phone || null]
    );
    return rows[0];
  },

  async createWithGoogle({ full_name, email, google_id, avatar_url }) {
    const { rows } = await db.query(
      `INSERT INTO users (full_name, email, google_id, avatar_url)
       VALUES ($1, $2, $3, $4)
       RETURNING ${SAFE_COLS}`,
      [full_name, email, google_id, avatar_url || null]
    );
    return rows[0];
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    if (keys.length === 0) return this.findById(id);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE users SET ${setClause} WHERE id = $${keys.length + 1}
       RETURNING ${SAFE_COLS}`,
      [...values, id]
    );
    return rows[0] || null;
  },

  async delete(id) {
    await db.query('DELETE FROM users WHERE id = $1', [id]);
  },

  async findAll({ limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      `SELECT ${SAFE_COLS} FROM users ORDER BY created_at DESC LIMIT $1 OFFSET $2`,
      [limit, offset]
    );
    return rows;
  },

  // ── Multi-role ─────────────────────────────────────────────────────────────

  // Tambahkan satu peran ke user (idempotent).
  async addRole(userId, roleId) {
    await db.query(
      `INSERT INTO user_roles (user_id, role_id) VALUES ($1, $2)
       ON CONFLICT (user_id, role_id) DO NOTHING`,
      [userId, roleId]
    );
  },

  // Daftar nama peran yang dimiliki user, lowercase (mis. ['buyer','seller']).
  async getRoles(userId) {
    const { rows } = await db.query(
      `SELECT r.name FROM user_roles ur
       JOIN roles r ON r.id = ur.role_id
       WHERE ur.user_id = $1
       ORDER BY r.id`,
      [userId]
    );
    return rows.map((row) => toApiRole(row.name));
  },

  // Set active role via id role.
  async setActiveRole(userId, roleId) {
    await db.query('UPDATE users SET active_role_id = $1 WHERE id = $2', [roleId, userId]);
  },

  // Nama active role (lowercase) atau null jika belum dipilih.
  async getActiveRole(userId) {
    const { rows } = await db.query(
      `SELECT r.name FROM users u
       JOIN roles r ON r.id = u.active_role_id
       WHERE u.id = $1`,
      [userId]
    );
    return rows[0] ? toApiRole(rows[0].name) : null;
  },

  // Profil + roles[] + activeRole, untuk endpoint /me.
  async findByIdWithRoles(userId) {
    const user = await this.findById(userId);
    if (!user) return null;
    const [roles, activeRole] = await Promise.all([
      this.getRoles(userId),
      this.getActiveRole(userId),
    ]);
    return { ...user, roles, activeRole };
  },
};

module.exports = User;
