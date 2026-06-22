const db = require('../config/db');

const Cart = {
  async findByBuyer(buyerId) {
    const { rows } = await db.query(
      `SELECT c.*, p.name, p.price, p.image_url, p.stock, s.name AS store_name
       FROM carts c
       JOIN products p ON c.product_id = p.id
       JOIN stores s ON p.store_id = s.id
       WHERE c.buyer_id = $1`,
      [buyerId]
    );
    return rows;
  },

  async findItem(buyerId, productId) {
    const { rows } = await db.query(
      'SELECT * FROM carts WHERE buyer_id = $1 AND product_id = $2',
      [buyerId, productId]
    );
    return rows[0] || null;
  },

  async addItem(buyerId, productId, quantity) {
    const { rows } = await db.query(
      `INSERT INTO carts (buyer_id, product_id, quantity)
       VALUES ($1, $2, $3)
       ON CONFLICT (buyer_id, product_id) DO UPDATE SET quantity = carts.quantity + $3
       RETURNING *`,
      [buyerId, productId, quantity]
    );
    return rows[0];
  },

  async updateQuantity(buyerId, productId, quantity) {
    const { rows } = await db.query(
      'UPDATE carts SET quantity = $1 WHERE buyer_id = $2 AND product_id = $3 RETURNING *',
      [quantity, buyerId, productId]
    );
    return rows[0] || null;
  },

  async removeItem(buyerId, productId) {
    await db.query('DELETE FROM carts WHERE buyer_id = $1 AND product_id = $2', [buyerId, productId]);
  },

  async clearCart(buyerId) {
    await db.query('DELETE FROM carts WHERE buyer_id = $1', [buyerId]);
  },
};

module.exports = Cart;
