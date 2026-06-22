require('./config/env');
const app = require('./app');
const { PORT } = require('./config/env');
const { startOverdueCron } = require('./cron/overdueCron');

const start = async () => {
  startOverdueCron();
  app.listen(PORT, () => {
    console.log(`SeaPedia API running on port ${PORT}`);
  });
};

start();
