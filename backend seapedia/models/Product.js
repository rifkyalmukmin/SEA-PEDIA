const db = require('../config/db');

const Product = {
  async findById(id) {
    const { rows } = await db.query(
      `SELECT p.*, s.name AS store_name
       FROM products p JOIN stores s ON p.store_id = s.id
       WHERE p.id = $1 AND p.is_active = true`,
      [id]
    );
    return rows[0] || null;
  },

  async findAll({ limit = 20, offset = 0, category, search } = {}) {
    let query = 'SELECT p.*, s.name AS store_name FROM products p JOIN stores s ON p.store_id = s.id WHERE p.is_active = true';
    const params = [];
    let idx = 1;

    if (category) {
      query += ` AND p.category = $${idx++}`;
      params.push(category);
    }
    if (search) {
      query += ` AND (p.name ILIKE $${idx} OR p.description ILIKE $${idx})`;
      params.push(`%${search}%`);
      idx++;
    }

    query += ` ORDER BY p.created_at DESC LIMIT $${idx} OFFSET $${idx + 1}`;
    params.push(limit, offset);

    const { rows } = await db.query(query, params);
    return rows;
  },

  async findByStore(storeId, { limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM products WHERE store_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
      [storeId, limit, offset]
    );
    return rows;
  },

  async create({ store_id, name, description, price, stock, image_url, category }) {
    const { rows } = await db.query(
      'INSERT INTO products (store_id, name, description, price, stock, image_url, category) VALUES ($1,$2,$3,$4,$5,$6,$7) RETURNING *',
      [store_id, name, description, price, stock, image_url, category]
    );
    return rows[0];
  },

  async update(id, fields) {
    const keys = Object.keys(fields);
    const values = Object.values(fields);
    const setClause = keys.map((k, i) => `${k} = $${i + 1}`).join(', ');
    const { rows } = await db.query(
      `UPDATE products SET ${setClause}, updated_at = NOW() WHERE id = $${keys.length + 1} RETURNING *`,
      [...values, id]
    );
    return rows[0] || null;
  },

  async delete(id) {
    await db.query('UPDATE products SET is_active = false WHERE id = $1', [id]);
  },
};

module.exports = Product;
