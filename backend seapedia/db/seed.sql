-- ============================================================================
-- SEAPEDIA — Seed Data (PostgreSQL / Supabase)
-- Jalankan SETELAH schema.sql:  npm run db:seed
--
-- Akun demo (password untuk semua akun: "password123"):
--   admin@seapedia.id   → ADMIN
--   seller@seapedia.id  → SELLER
--   buyer@seapedia.id   → BUYER
--   driver@seapedia.id  → DRIVER
--   multi@seapedia.id   → BUYER + SELLER + DRIVER  (untuk demo pemilihan active role)
-- ============================================================================

-- Idempotent: kosongkan dulu agar bisa di-seed berulang.
TRUNCATE TABLE
    order_status_histories, order_items, delivery_jobs, orders,
    cart_items, carts, wallet_transactions, wallets, addresses,
    products, stores, reviews, user_roles, users, promos, vouchers,
    system_settings, roles
RESTART IDENTITY CASCADE;

-- ── Roles ────────────────────────────────────────────────────────────────────
INSERT INTO roles (name) VALUES ('BUYER'), ('SELLER'), ('DRIVER'), ('ADMIN');

-- ── Users (password = "password123", bcrypt cost 10) ─────────────────────────
INSERT INTO users (full_name, email, password, phone, active_role_id) VALUES
    ('Admin SEAPEDIA', 'admin@seapedia.id',  '$2a$10$V.7KrDeMGzlr4ugz2aw0H.ucFYNcBbofxdj3lgeRcvmNH6eB29pUm', '081200000001', (SELECT id FROM roles WHERE name = 'ADMIN')),
    ('Toko Bahari',    'seller@seapedia.id', '$2a$10$V.7KrDeMGzlr4ugz2aw0H.ucFYNcBbofxdj3lgeRcvmNH6eB29pUm', '081200000002', (SELECT id FROM roles WHERE name = 'SELLER')),
    ('Budi Pembeli',   'buyer@seapedia.id',  '$2a$10$V.7KrDeMGzlr4ugz2aw0H.ucFYNcBbofxdj3lgeRcvmNH6eB29pUm', '081200000003', (SELECT id FROM roles WHERE name = 'BUYER')),
    ('Joko Driver',    'driver@seapedia.id', '$2a$10$V.7KrDeMGzlr4ugz2aw0H.ucFYNcBbofxdj3lgeRcvmNH6eB29pUm', '081200000004', (SELECT id FROM roles WHERE name = 'DRIVER')),
    ('Sari Serbabisa', 'multi@seapedia.id',  '$2a$10$V.7KrDeMGzlr4ugz2aw0H.ucFYNcBbofxdj3lgeRcvmNH6eB29pUm', '081200000005', (SELECT id FROM roles WHERE name = 'BUYER'));

-- ── user_roles (multi-role pivot) ─────────────────────────────────────────────
-- Akun tunggal: 1 role masing-masing.
INSERT INTO user_roles (user_id, role_id) VALUES
    ((SELECT id FROM users WHERE email = 'admin@seapedia.id'),  (SELECT id FROM roles WHERE name = 'ADMIN')),
    ((SELECT id FROM users WHERE email = 'seller@seapedia.id'), (SELECT id FROM roles WHERE name = 'SELLER')),
    ((SELECT id FROM users WHERE email = 'buyer@seapedia.id'),  (SELECT id FROM roles WHERE name = 'BUYER')),
    ((SELECT id FROM users WHERE email = 'driver@seapedia.id'), (SELECT id FROM roles WHERE name = 'DRIVER'));

-- Akun multi-role: BUYER + SELLER + DRIVER (demo pemilihan active role).
INSERT INTO user_roles (user_id, role_id) VALUES
    ((SELECT id FROM users WHERE email = 'multi@seapedia.id'), (SELECT id FROM roles WHERE name = 'BUYER')),
    ((SELECT id FROM users WHERE email = 'multi@seapedia.id'), (SELECT id FROM roles WHERE name = 'SELLER')),
    ((SELECT id FROM users WHERE email = 'multi@seapedia.id'), (SELECT id FROM roles WHERE name = 'DRIVER'));

-- ── Wallets (saldo awal untuk demo checkout) ──────────────────────────────────
INSERT INTO wallets (user_id, balance) VALUES
    ((SELECT id FROM users WHERE email = 'buyer@seapedia.id'), 1000000.00),
    ((SELECT id FROM users WHERE email = 'multi@seapedia.id'), 500000.00);

INSERT INTO wallet_transactions (wallet_id, type, amount, description) VALUES
    ((SELECT w.id FROM wallets w JOIN users u ON u.id = w.user_id WHERE u.email = 'buyer@seapedia.id'), 'TOPUP', 1000000.00, 'Top-up awal (seed)'),
    ((SELECT w.id FROM wallets w JOIN users u ON u.id = w.user_id WHERE u.email = 'multi@seapedia.id'), 'TOPUP', 500000.00, 'Top-up awal (seed)');

-- ── Addresses (untuk buyer) ───────────────────────────────────────────────────
INSERT INTO addresses (buyer_id, recipient_name, phone, address_detail, is_default) VALUES
    ((SELECT id FROM users WHERE email = 'buyer@seapedia.id'), 'Budi Pembeli', '081200000003', 'Jl. Laut Biru No. 10, Surabaya', TRUE),
    ((SELECT id FROM users WHERE email = 'multi@seapedia.id'), 'Sari Serbabisa', '081200000005', 'Jl. Pelabuhan No. 5, Jakarta', TRUE);

-- ── Stores ────────────────────────────────────────────────────────────────────
INSERT INTO stores (seller_id, store_name, description, address_detail) VALUES
    ((SELECT id FROM users WHERE email = 'seller@seapedia.id'), 'Bahari Seafood',  'Ikan & seafood segar langsung dari nelayan.', 'Pelabuhan Muara Angke, Jakarta'),
    ((SELECT id FROM users WHERE email = 'multi@seapedia.id'),  'Sari Laut Fresh', 'Hasil laut pilihan kualitas ekspor.',        'Pasar Ikan Kenjeran, Surabaya');

-- ── Products ──────────────────────────────────────────────────────────────────
INSERT INTO products (store_id, name, description, price, stock, image_url) VALUES
    ((SELECT id FROM stores WHERE store_name = 'Bahari Seafood'),  'Udang Vaname 1kg',   'Udang segar ukuran sedang, cocok untuk berbagai masakan.', 95000.00,  50, NULL),
    ((SELECT id FROM stores WHERE store_name = 'Bahari Seafood'),  'Cumi Segar 1kg',     'Cumi-cumi segar tangkapan harian.',                        78000.00,  40, NULL),
    ((SELECT id FROM stores WHERE store_name = 'Bahari Seafood'),  'Ikan Kakap Merah',   'Kakap merah segar per ekor (~500g).',                      62000.00,  30, NULL),
    ((SELECT id FROM stores WHERE store_name = 'Sari Laut Fresh'), 'Kepiting Bakau 1kg', 'Kepiting bakau hidup, daging tebal.',                     135000.00, 25, NULL),
    ((SELECT id FROM stores WHERE store_name = 'Sari Laut Fresh'), 'Kerang Hijau 1kg',   'Kerang hijau segar, sudah dibersihkan.',                   35000.00,  60, NULL);

-- ── Vouchers (punya expiry + sisa pemakaian) ──────────────────────────────────
INSERT INTO vouchers (name, code, discount_type, discount_value, remaining_usage, expiry_date) VALUES
    ('Diskon Pembukaan 10%', 'SEAPEDIA10', 'PERCENTAGE', 10.00, 100, NOW() + INTERVAL '30 days');

-- ── Promos (punya expiry) ─────────────────────────────────────────────────────
INSERT INTO promos (name, code, discount_type, discount_value, expiry_date) VALUES
    ('Potongan Rp15.000', 'HEMAT15K', 'FIXED', 15000.00, NOW() + INTERVAL '30 days');

-- ── System settings (waktu sistem untuk simulasi "next day") ──────────────────
INSERT INTO system_settings (current_datetime) VALUES (NOW());
