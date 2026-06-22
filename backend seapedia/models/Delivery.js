const db = require('../config/db');

// status: assigned | picked_up | on_the_way | delivered | failed
const Delivery = {
  async findById(id) {
    const { rows } = await db.query('SELECT * FROM deliveries WHERE id = $1', [id]);
    return rows[0] || null;
  },

  async findByOrder(orderId) {
    const { rows } = await db.query('SELECT * FROM deliveries WHERE order_id = $1', [orderId]);
    return rows[0] || null;
  },

  async findByDriver(driverId, { limit = 20, offset = 0 } = {}) {
    const { rows } = await db.query(
      `SELECT d.*, o.delivery_address, u.name AS buyer_name
       FROM deliveries d
       JOIN orders o ON d.order_id = o.id
       JOIN users u ON o.buyer_id = u.id
       WHERE d.driver_id = $1
       ORDER BY d.created_at DESC LIMIT $2 OFFSET $3`,
      [driverId, limit, offset]
    );
    return rows;
  },

  async create({ order_id, driver_id, pickup_address, delivery_address }) {
    const { rows } = await db.query(
      'INSERT INTO deliveries (order_id, driver_id, pickup_address, delivery_address, status) VALUES ($1,$2,$3,$4,$5) RETURNING *',
      [order_id, driver_id, pickup_address, delivery_address, 'assigned']
    );
    return rows[0];
  },

  async updateStatus(id, status) {
    const { rows } = await db.query(
      'UPDATE deliveries SET status = $1, updated_at = NOW() WHERE id = $2 RETURNING *',
      [status, id]
    );
    return rows[0] || null;
  },
};

module.exports = Delivery;
