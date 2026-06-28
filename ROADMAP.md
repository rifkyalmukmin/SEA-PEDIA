# SEAPEDIA — Roadmap Implementasi (Level 1 → Level 7)

Dokumen ini adalah rencana kerja bertahap untuk menyelesaikan SEAPEDIA sesuai
spesifikasi di `CLAUDE.md`. Setiap level berdiri di atas level sebelumnya.
Centang `[ ]` → `[x]` saat selesai. Commit per langkah (jangan squash).

---

## 0. Kondisi Saat Ini (Baseline Audit)

### Android (`app/`)
| Area | Status | Catatan |
|---|---|---|
| Auth (Login/Register/Splash) | ✅ Nyata | UI + ViewModel + Google Sign-In jalan |
| Home | ✅ Ada | `Home.kt` 477 baris |
| Semua layar lain (buyer/seller/driver/admin) | ❌ Stub | hanya `class xxx {}` 2–3 baris |
| `NavGraph.kt` | ⚠️ Parsial | route non-auth = `PlaceholderScreen` |
| `domain/model.kt` | ❌ | hanya `User` |
| Repository impl (Buyer/Seller/Driver/Admin/Product) | ❌ | kosong; hanya `AuthRepositoryImpl` berisi |
| Room (entity/dao/database) | ❌ Stub | |
| `core/components/*` | ❌ Stub | SeaButton/SeaTextField/ProductCard dll kosong |

### Backend (`backend seapedia/`) — Express + PostgreSQL (Supabase) + JWT
| Area | Status | Catatan |
|---|---|---|
| Auth register/login | ✅ | bcrypt + JWT |
| Google auth | ✅ | verifikasi via Supabase |
| Cart CRUD | ✅ dasar | belum ada single-store rule |
| Order create | ⚠️ dasar | hanya subtotal − voucher; **tanpa PPN/ongkir/stock/wallet** |
| Overdue cron | ⚠️ dasar | hanya ubah status `overdue`, **tanpa refund/restore** |
| **Multi-role per user** | ❌ | `users.role_id` tunggal → tidak mendukung banyak role |
| **Active-role mechanism** | ❌ | |
| **Wallet / Address model** | ❌ | |
| **Status lifecycle (ID)** | ❌ | pakai `pending/confirmed/...` Inggris |
| **Status history + timestamp** | ❌ | |
| **DB schema / migrasi** | ❌ | tidak ada file SQL skema di repo |
| Seed data / Swagger / README lengkap | ❌ | README 12 baris |

### Keputusan Arsitektur (perlu konsisten sejak awal)
- **Multi-role**: ganti `users.role_id` → tabel pivot `user_roles(user_id, role_id)`.
  JWT membawa `userId` + `activeRole`. Authorization berbasis `activeRole`.
- **Status order (user-facing, Bahasa Indonesia)** — wajib persis:
  `Sedang Dikemas → Menunggu Pengirim → Sedang Dikirim → Pesanan Selesai → Dikembalikan`.
- **Urutan kalkulasi checkout** (didokumentasikan): 
  `subtotal → diskon → (subtotal-diskon) → +ongkir → PPN 12% atas (subtotal-diskon+ongkir) → total`.
  *(Catatan: aturan basis PPN bebas, tapi harus konsisten & ditulis di README.)*
- **Sumber kebenaran data** = backend. Android konsumsi API. Room hanya cache/sesi.

---

## PRA-SYARAT: Skema Database (kerjakan sebelum Level 1 fungsional)

Tanpa skema, semua model backend (`db.query`) menabrak tabel yang belum ada.

- [ ] Buat `backend seapedia/db/schema.sql` berisi semua tabel:
  `roles, users, user_roles, stores, products, app_reviews,
   addresses, wallets, wallet_transactions, carts, cart_items,
   orders, order_items, order_status_history, vouchers, promos, deliveries`
- [ ] Buat `backend seapedia/db/seed.sql`: 4 role, akun demo tiap role
  (admin via seed), beberapa store + produk, 1 voucher + 1 promo.
- [ ] Tambah script `npm run db:migrate` & `npm run db:seed` di `package.json`.
- [ ] Constraint: `stores.name UNIQUE`, `users.email UNIQUE`,
  cek `products.stock >= 0`, FK + `ON DELETE` yang wajar.

---

## LEVEL 1 — Public Marketplace, Auth, Reviews (20 pts)

### 1A. Multi-role Auth & Active Role (backend) — inti
- [ ] Migrasi `users.role_id` → `user_roles` (pivot). Admin tetap terpisah.
- [ ] `register`: boleh pilih ≥1 role non-admin; simpan ke `user_roles`.
- [ ] `login`: balikan daftar role yang dimiliki user.
- [ ] Endpoint `POST /api/auth/select-role` → set `activeRole`, terbitkan JWT baru.
- [ ] `GET /api/auth/me` → profil + `roles[]` + `activeRole`.
- [ ] `roleMiddleware` validasi **activeRole** (bukan sekadar daftar role).
- [ ] `logout`: invalidasi token (blacklist / client-clear + dokumentasi).

### 1B. Public Catalog & Reviews (backend)
- [ ] `GET /api/products`, `GET /api/products/:id` (publik, tanpa auth).
- [ ] `GET /api/reviews` (publik) & `POST /api/reviews` (guest boleh).
  Field: `reviewer_name`, `rating 1–5`, `comment`. Validasi rating.

### 1C. Android — UI publik & auth
- [ ] Lengkapi `core/components`: `SeaButton`, `SeaTextField`, `ProductCard`,
  `ReviewCard`, `EmptyView`, `LoadingView` (saat ini stub).
- [ ] Landing/Home: tampilkan katalog (API) + section review publik.
- [ ] Product listing + Product detail (read-only) untuk guest.
- [ ] Form review publik (nama, rating, komentar) + list review.
- [ ] **Role selection screen/modal** bila user punya >1 role non-admin
  (`presentation/auth/role_selection.kt` saat ini stub).
- [ ] Profile/dashboard summary: tampilkan roles dimiliki + active role +
  placeholder saldo (wallet/income/earning).
- [ ] `domain/model.kt`: tambah `Product, Store, Review, RoleInfo`.
- [ ] Navigasi: bedakan guest vs logged-in; dashboard shell tiap role.

**Definisi selesai L1:** guest browsing katalog+detail, submit review,
register/login/logout, pilih active role, dashboard entry per role.

---

## LEVEL 2 — Seller Experience (15 pts)

### Backend
- [ ] `Store`: create/update (1 seller 1 store), **nama unik** (constraint + validasi).
- [ ] `GET /api/stores/:id` (publik) + info store di product detail.
- [ ] Product CRUD seller: create/update/delete **hanya milik sendiri**
  (cek `store.owner == activeUser`). Field: name, desc, price, stock, store.
- [ ] Katalog publik membaca produk seller nyata (bukan dummy).

### Android
- [ ] `presentation/seller/store.kt`: form create/update store + error nama dipakai.
- [ ] `presentation/seller/product.kt`: list + form CRUD produk milik seller.
- [ ] Product detail tampilkan blok info store.
- [ ] `domain`: tambah repository+usecase Seller; isi `SellerRepositoryImpl`.

**Definisi selesai L2:** seller buat store unik, CRUD produk, produk tampil di katalog publik.

---

## LEVEL 3 — Buyer Wallet, Cart, Checkout (20 pts)

### Backend
- [ ] `wallets` + `wallet_transactions`; endpoint top-up dummy + riwayat.
- [ ] `addresses` CRUD untuk buyer.
- [ ] **Single-store cart rule**: tolak produk dari store berbeda
  (atau minta clear cart). Cart summary endpoint.
- [ ] **Checkout penuh** `POST /api/orders`:
  - subtotal, ongkir per metode (Instant/Next Day/Regular berbeda),
    PPN 12%, total akhir.
  - tolak jika saldo wallet kurang.
  - kurangi stock **aman** (tidak boleh negatif, transaksi DB).
  - status awal = **`Sedang Dikemas`**.
  - tulis `order_status_history` + timestamp.
- [ ] Order history + detail (buyer) & incoming order list (seller).

### Android
- [ ] `buyer/wallet.kt`: saldo + top-up + riwayat.
- [ ] `buyer/address.kt`: kelola alamat.
- [ ] `buyer/cart.kt`: add/update qty/remove + banner single-store.
- [ ] `buyer/checkout.kt`: ringkasan subtotal/ongkir/PPN/total + pilih metode.
- [ ] `buyer/order.kt`: history + detail + timeline status.
- [ ] `seller/incoming_order.kt`: list pesanan masuk.

**Definisi selesai L3:** top-up → cart (single-store) → checkout (PPN 12% tampil) → order muncul.

---

## LEVEL 4 — Discounts & Seller Order Processing (15 pts)

### Backend
- [ ] `vouchers` (expiry + sisa pemakaian) & `promos` (expiry); admin endpoint generate.
- [ ] List/detail voucher & promo.
- [ ] Validasi kode saat checkout: expired/usage habis ditolak; voucher vs promo dibedakan.
- [ ] Tampilkan efek diskon di ringkasan checkout (subtotal/diskon/ongkir/PPN/total).
- [ ] Seller **process order**: `Sedang Dikemas → Menunggu Pengirim` (+history+timestamp),
  hanya pemilik order. Order belum bisa diambil driver sebelum diproses.

### Android
- [ ] Input kode diskon di checkout + tampil efeknya.
- [ ] Seller: tombol "Proses Pesanan" + timeline status (buyer & seller).
- [ ] `seller/report.kt`: ringkasan income; buyer: ringkasan pengeluaran.

**Definisi selesai L4:** diskon voucher/promo jalan, seller proses order, laporan buyer/seller.

---

## LEVEL 5 — Delivery & Driver Workflow (10 pts)

### Backend
- [ ] `deliveries` terhubung ke order. Job tampil hanya saat `Menunggu Pengirim`.
- [ ] `take job`: `Menunggu Pengirim → Sedang Dikirim`; **1 order 1 driver** (lock anti-race).
- [ ] `confirm complete`: `Sedang Dikirim → Pesanan Selesai` (+history+timestamp).
- [ ] Aturan earning driver (dari ongkir / aturan terdokumentasi).

### Android
- [ ] `driver/jobs.kt`: cari job. `driver/active_job.kt`: ambil + selesaikan.
- [ ] `driver/history.kt` + `driver/earning.kt`.
- [ ] Buyer & seller bisa tracking status pengiriman.

**Definisi selesai L5:** alur penuh checkout → proses seller → driver antar → selesai.

---

## LEVEL 6 — Admin Monitoring & Overdue Handling (10 pts)

### Backend
- [ ] Admin monitoring: users, stores, products, orders, voucher/promo, deliveries, overdue.
- [ ] **Overdue penuh** (perbaiki cron sekarang yang hanya set status):
  - SLA per metode (Instant/Next Day/Regular).
  - auto refund/return → status final **`Dikembalikan`**.
  - refund saldo balik ke wallet + catat di `wallet_transactions`.
  - restore stock; reversal income seller; **cegah double** refund/restore.
  - simpan history + timestamp; jejak terlihat.
- [ ] Simulasi "next day" (endpoint trigger admin / command).

### Android
- [ ] `admin/dashboard.kt` + `admin/monitoring.kt`: data monitoring.
- [ ] `admin/voucher.kt` + `admin/promo.kt`: generate + list + detail.
- [ ] `admin/overdue.kt`: hasil overdue + tombol simulasi next-day.

**Definisi selesai L6:** admin monitoring + kelola diskon + minimal 1 skenario auto-refund/return.

---

## LEVEL 7 — Security Hardening & Finalisasi (10 pts)

### Backend
- [ ] SQL Injection: pastikan **semua** query parameterized (audit `db.query`).
- [ ] XSS: sanitasi/escape konten review & komentar publik sebelum render.
- [ ] Validasi field wajib (email, phone, rating, qty, price, stock, diskon) + error jelas.
- [ ] Authorization server-side per **activeRole** di semua aksi; cegah akses resource milik user lain.
- [ ] Logout invalidasi token; expiry token wajar + terdokumentasi.

### Android
- [ ] Render komentar review sebagai teks aman (tidak eksekusi script, tidak merusak layout).
- [ ] Hormati proteksi role dari backend (jangan percaya UI saja).

### Dokumentasi & Demo
- [ ] API docs: Swagger/OpenAPI atau Postman collection.
- [ ] README lengkap: setup, env vars, **cara buat admin**, single-store rule,
  aturan kombinasi diskon + PPN 12%, aturan earning driver, SLA overdue + cara simulasi waktu,
  catatan keamanan (SQLi/XSS/validasi/sesi/RBAC), panduan testing E2E.
- [ ] Seed/demo account untuk Admin, Seller, Buyer, Driver.

**Definisi selesai L7:** test SQLi/XSS aman, RBAC server-side, dokumentasi & demo data lengkap.

---

## Catatan Prioritas

1. **Skema DB + multi-role** adalah fondasi — kerjakan paling awal; menyentuh
   hampir semua level. Menunda ini membuat L1 tidak benar-benar selesai.
2. Backend dulu per fitur, lalu Android konsumsi API — karena Android masih
   90% stub sementara backend sudah punya kerangka.
3. Commit bertahap per langkah (syarat penilaian: lihat history pengembangan).
4. Bonus (UI bagus, deployment) hanya dihitung jika minimal Level 1 selesai.
