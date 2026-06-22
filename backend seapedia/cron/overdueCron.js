const cron = require('node-cron');
const Order = require('../models/Order');

// Runs every day at 01:00 to mark orders overdue after 3 days
const startOverdueCron = () => {
  cron.schedule('0 1 * * *', async () => {
    console.log('[OverdueCron] Checking for overdue orders...');
    try {
      const overdueOrders = await Order.findOverdue();
      for (const order of overdueOrders) {
        await Order.updateStatus(order.id, 'overdue');
        console.log(`[OverdueCron] Order ${order.id} marked as overdue`);
      }
      console.log(`[OverdueCron] Done. ${overdueOrders.length} orders updated.`);
    } catch (err) {
      console.error('[OverdueCron] Error:', err.message);
    }
  });
  console.log('[OverdueCron] Scheduled daily at 01:00');
};

module.exports = { startOverdueCron };
