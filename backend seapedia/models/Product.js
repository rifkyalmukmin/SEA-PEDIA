const db = require('../config/db');

// Produk. Skema: id, store_id, name, description, price, stock, image_url, timestamps.
// (Tidak ada kolom is_active / category pada skema SEAPEDIA.)
const Product = {
  // Detail produk + info toko (untuk halaman detail publik).
  async findById(id) {
    const { rows } = await db.query(
      `SELECT p.*,
              s.store_name,
              s.description AS store_description,
              s.seller_id
       FROM products p
       JOIN stores s ON p.store_id = s.id
       WHERE p.id = $1`,
      [id]
    );
    return rows[0] || null;
  },

  // Katalog publik. Mendukung pencarian nama/deskripsi (opsional).
  async findAll({ limit = 20, offset = 0, search } = {}) {
    let query =
      `SELECT p.*, s.store_name
       FROM products p
       JOIN stores s ON p.store_id = s.id`;
    const params = [];
    let idx = 1;

    if (search) {
      query += ` WHERE (p.name ILIKE $${idx} OR p.description ILIKE $${idx})`;
      params.push(`%${search}%`);
      idx++;
    }

    query += ` ORDER BY p.created_at DESC LIMIT $${idx} OFFSET $${idx + 1}`;
    params.push(limit, offset);

    const { rows } = await db.query(query, params);
    return rows;
  },

  async findByStore(storeId, { limit = 50, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM products WHERE store_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
      [storeId, limit, offset]
    );
    return rows;
  },

  async create({ store_id, name, description, price, stock, image_url }) {
    const { rows } = await db.query(
      `INSERT INTO products (store_id, name, description, price, stock, image_url)
       VALUES ($1, $2, $3, $4, $5, $6) RETURNING *`,
      [store_id, name, description, price, stock, image_url || null]
    );
    return rows[0];
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    if (keys.length === 0) return this.findById(id);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE products SET ${setClause} WHERE id = $${keys.length + 1} RETURNING *`,
      [...values, id]
    );
    return rows[0] || null;
  },

  // Hard delete (skema tidak punya soft-delete is_active).
  async delete(id) {
    await db.query('DELETE FROM products WHERE id = $1', [id]);
  },
};

module.exports = Product;
