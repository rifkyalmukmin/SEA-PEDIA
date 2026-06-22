require('dotenv').config();

const required = ['JWT_SECRET', 'SUPABASE_URL', 'SUPABASE_ANON_KEY', 'DATABASE_URL'];
for (const key of required) {
  if (!process.env[key]) {
    console.warn(`Warning: environment variable ${key} is not set`);
  }
}

module.exports = {
  PORT: process.env.PORT || 3000,
  NODE_ENV: process.env.NODE_ENV || 'development',
  JWT_SECRET: process.env.JWT_SECRET || 'dev_secret',
  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || '7d',
  SUPABASE_URL: process.env.SUPABASE_URL,
  SUPABASE_ANON_KEY: process.env.SUPABASE_ANON_KEY,
  SUPABASE_SERVICE_ROLE_KEY: process.env.SUPABASE_SERVICE_ROLE_KEY,
  DATABASE_URL: process.env.DATABASE_URL,
};
