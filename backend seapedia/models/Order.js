const db = require('../config/db');

// status: pending | confirmed | processing | ready_for_pickup | on_delivery | delivered | cancelled | overdue
const Order = {
  async findById(id) {
    const { rows } = await db.query(
      `SELECT o.*, u.name AS buyer_name, s.name AS store_name
       FROM orders o
       JOIN users u ON o.buyer_id = u.id
       JOIN stores s ON o.store_id = s.id
       WHERE o.id = $1`,
      [id]
    );
    return rows[0] || null;
  },

  async findItemsByOrderId(orderId) {
    const { rows } = await db.query(
      `SELECT oi.*, p.name, p.image_url
       FROM order_items oi JOIN products p ON oi.product_id = p.id
       WHERE oi.order_id = $1`,
      [orderId]
    );
    return rows;
  },

  async findByBuyer(buyerId, { limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM orders WHERE buyer_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
      [buyerId, limit, offset]
    );
    return rows;
  },

  async findByStore(storeId, { limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM orders WHERE store_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
      [storeId, limit, offset]
    );
    return rows;
  },

  async findByDriver(driverId, { limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      'SELECT * FROM orders WHERE driver_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
      [driverId, limit, offset]
    );
    return rows;
  },

  async findAll({ status, limit = 20, offset = 0 } = {}) {
    let query = 'SELECT * FROM orders';
    const params = [];
    if (status) {
      query += ' WHERE status = $1';
      params.push(status);
    }
    query += ` ORDER BY created_at DESC LIMIT $${params.length + 1} OFFSET $${params.length + 2}`;
    params.push(limit, offset);
    const { rows } = await db.query(query, params);
    return rows;
  },

  async create({ buyer_id, store_id, voucher_id, total_price, delivery_address, notes }) {
    const { rows } = await db.query(
      'INSERT INTO orders (buyer_id, store_id, voucher_id, total_price, delivery_address, notes, status) VALUES ($1,$2,$3,$4,$5,$6,$7) RETURNING *',
      [buyer_id, store_id, voucher_id, total_price, delivery_address, notes, 'pending']
    );
    return rows[0];
  },

  async addItem(orderId, productId, quantity, price) {
    const { rows } = await db.query(
      'INSERT INTO order_items (order_id, product_id, quantity, price) VALUES ($1,$2,$3,$4) RETURNING *',
      [orderId, productId, quantity, price]
    );
    return rows[0];
  },

  async updateStatus(id, status) {
    const { rows } = await db.query(
      'UPDATE orders SET status = $1, updated_at = NOW() WHERE id = $2 RETURNING *',
      [status, id]
    );
    return rows[0] || null;
  },

  async assignDriver(orderId, driverId) {
    const { rows } = await db.query(
      'UPDATE orders SET driver_id = $1, status = $2, updated_at = NOW() WHERE id = $3 RETURNING *',
      [driverId, 'on_delivery', orderId]
    );
    return rows[0] || null;
  },

  async findOverdue() {
    const { rows } = await db.query(
      `SELECT * FROM orders WHERE status NOT IN ('delivered', 'cancelled', 'overdue')
       AND created_at < NOW() - INTERVAL '3 days'`
    );
    return rows;
  },
};

module.exports = Order;
