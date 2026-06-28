-- ============================================================================
-- SEAPEDIA — Database Schema (PostgreSQL / Supabase)
-- Dikonversi dari db_seapedia.sql (MySQL) ke PostgreSQL.
-- Jalankan: npm run db:migrate  (atau paste ke Supabase SQL Editor)
-- ============================================================================

-- ── ENUM types ──────────────────────────────────────────────────────────────
DROP TYPE IF EXISTS role_name        CASCADE;
DROP TYPE IF EXISTS wallet_txn_type  CASCADE;
DROP TYPE IF EXISTS discount_type    CASCADE;
DROP TYPE IF EXISTS delivery_method  CASCADE;
DROP TYPE IF EXISTS order_status      CASCADE;
DROP TYPE IF EXISTS delivery_job_status CASCADE;

CREATE TYPE role_name           AS ENUM ('BUYER', 'SELLER', 'DRIVER', 'ADMIN');
CREATE TYPE wallet_txn_type     AS ENUM ('TOPUP', 'PAYMENT', 'REFUND');
CREATE TYPE discount_type       AS ENUM ('PERCENTAGE', 'FIXED');
CREATE TYPE delivery_method     AS ENUM ('INSTANT', 'NEXT_DAY', 'REGULAR');
CREATE TYPE order_status        AS ENUM ('PACKAGING', 'WAITING_FOR_DRIVER', 'IN_DELIVERY', 'COMPLETED', 'RETURNED');
CREATE TYPE delivery_job_status AS ENUM ('AVAILABLE', 'TAKEN', 'COMPLETED');

-- ── Tabel ───────────────────────────────────────────────────────────────────

CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name role_name NOT NULL UNIQUE
);

CREATE TABLE users (
    id             BIGSERIAL PRIMARY KEY,
    full_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255),                     -- NULL utk akun Google-only
    google_id      VARCHAR(255) UNIQUE,
    avatar_url     VARCHAR(255),
    phone          VARCHAR(20),
    active_role_id BIGINT REFERENCES roles(id),
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE reviews (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reviewer_name VARCHAR(100) NOT NULL,
    rating        SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment       TEXT NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE stores (
    id             BIGSERIAL PRIMARY KEY,
    seller_id      BIGINT NOT NULL REFERENCES users(id),
    store_name     VARCHAR(100) NOT NULL UNIQUE,
    description    TEXT,
    address_detail TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    store_id    BIGINT NOT NULL REFERENCES stores(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    price       DECIMAL(15,2) NOT NULL CHECK (price >= 0),
    stock       INT NOT NULL CHECK (stock >= 0),
    image_url   VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE addresses (
    id             BIGSERIAL PRIMARY KEY,
    buyer_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_name VARCHAR(100) NOT NULL,
    phone          VARCHAR(20) NOT NULL,
    address_detail TEXT NOT NULL,
    is_default     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE wallets (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance    DECIMAL(15,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE wallet_transactions (
    id          BIGSERIAL PRIMARY KEY,
    wallet_id   BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
    type        wallet_txn_type NOT NULL,
    amount      DECIMAL(15,2) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Single-store cart rule: satu buyer punya satu cart, terikat ke satu store.
CREATE TABLE carts (
    id         BIGSERIAL PRIMARY KEY,
    buyer_id   BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    store_id   BIGINT NOT NULL REFERENCES stores(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_items (
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity   INT NOT NULL CHECK (quantity > 0),
    UNIQUE (cart_id, product_id)
);

CREATE TABLE vouchers (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    code            VARCHAR(50) NOT NULL UNIQUE,
    discount_type   discount_type NOT NULL,
    discount_value  DECIMAL(15,2) NOT NULL CHECK (discount_value >= 0),
    remaining_usage INT NOT NULL CHECK (remaining_usage >= 0),
    expiry_date     TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE promos (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    code           VARCHAR(50) NOT NULL UNIQUE,
    discount_type  discount_type NOT NULL,
    discount_value DECIMAL(15,2) NOT NULL CHECK (discount_value >= 0),
    expiry_date    TIMESTAMP NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE orders (
    id                      BIGSERIAL PRIMARY KEY,
    buyer_id                BIGINT NOT NULL REFERENCES users(id),
    store_id                BIGINT NOT NULL REFERENCES stores(id),
    address_id              BIGINT NOT NULL REFERENCES addresses(id),
    voucher_id              BIGINT REFERENCES vouchers(id),
    promo_id                BIGINT REFERENCES promos(id),
    shipping_recipient_name VARCHAR(100) NOT NULL,
    shipping_phone          VARCHAR(20) NOT NULL,
    shipping_address        TEXT NOT NULL,
    delivery_method         delivery_method NOT NULL,
    subtotal                DECIMAL(15,2) NOT NULL,
    discount_amount         DECIMAL(15,2) NOT NULL DEFAULT 0,
    delivery_fee            DECIMAL(15,2) NOT NULL,
    ppn_amount              DECIMAL(15,2) NOT NULL,
    final_total             DECIMAL(15,2) NOT NULL,
    status                  order_status NOT NULL DEFAULT 'PACKAGING',
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    expired_at              TIMESTAMP NOT NULL,
    returned_at             TIMESTAMP
);

CREATE TABLE order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id   BIGINT NOT NULL REFERENCES products(id),
    product_name VARCHAR(100) NOT NULL,
    price        DECIMAL(15,2) NOT NULL,
    quantity     INT NOT NULL CHECK (quantity > 0),
    subtotal     DECIMAL(15,2) NOT NULL
);

CREATE TABLE order_status_histories (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status     order_status NOT NULL,
    notes      TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE delivery_jobs (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    driver_id    BIGINT REFERENCES users(id) ON DELETE SET NULL,
    earning      DECIMAL(15,2) NOT NULL DEFAULT 0,
    status       delivery_job_status NOT NULL DEFAULT 'AVAILABLE',
    taken_at     TIMESTAMP,
    completed_at TIMESTAMP
);

-- Untuk simulasi "next day" (Level 6). Satu baris saja.
CREATE TABLE system_settings (
    id               BIGSERIAL PRIMARY KEY,
    current_datetime TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ── Index untuk query yang sering dipakai ────────────────────────────────────
CREATE INDEX idx_products_store         ON products(store_id);
CREATE INDEX idx_orders_buyer           ON orders(buyer_id);
CREATE INDEX idx_orders_store           ON orders(store_id);
CREATE INDEX idx_orders_status          ON orders(status);
CREATE INDEX idx_order_items_order      ON order_items(order_id);
CREATE INDEX idx_status_hist_order      ON order_status_histories(order_id);
CREATE INDEX idx_delivery_jobs_status   ON delivery_jobs(status);
CREATE INDEX idx_wallet_txn_wallet      ON wallet_transactions(wallet_id);

-- ── Trigger: auto-update kolom updated_at (pengganti ON UPDATE MySQL) ─────────
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated    BEFORE UPDATE ON users    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_stores_updated   BEFORE UPDATE ON stores   FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_products_updated BEFORE UPDATE ON products FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_wallets_updated  BEFORE UPDATE ON wallets  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
