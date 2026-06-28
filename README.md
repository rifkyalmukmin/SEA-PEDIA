# SEA-PEDIA 🌊

Aplikasi **marketplace seafood multi-peran** (Buyer, Seller, Driver, Admin) berbasis
Android (Jetpack Compose) dengan backend Node.js API. Dibangun mengikuti
Clean Architecture + MVVM.

> Status: dalam pengembangan bertahap (Level 1 → Level 7). Lihat [`ROADMAP.md`](ROADMAP.md)
> untuk rencana lengkap dan progres per level.

---

## Arsitektur

```
SeaPedia/
├── app/                     # Aplikasi Android (Kotlin + Jetpack Compose)
│   └── src/main/java/com/example/seapedia/
│       ├── domain/          # model, repository (interface), usecase
│       ├── data/            # remote (Retrofit), local (Room), repository impl
│       ├── di/              # modul Hilt (App, Network, Database, Repository)
│       ├── presentation/    # layar Compose per peran (auth, buyer, seller, driver, admin)
│       └── core/            # components, navigation, ui/theme, utils
└── backend seapedia/        # API Node.js (Express + PostgreSQL/Supabase + JWT)
    ├── controllers/  routes/  models/  middleware/  validators/
    ├── config/  utils/  cron/
    └── app.js  server.js
```

### Tech stack

**Android**

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Networking | Retrofit + OkHttp + Gson |
| Local DB | Room |
| Image loading | Coil |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines |
| Persistence | DataStore Preferences |
| Auth | Credential Manager (Google Sign-In) |

**Backend**

| Concern | Library |
|---|---|
| Framework | Express |
| Database | PostgreSQL (Supabase) |
| Auth | JWT + bcryptjs |
| Scheduler | node-cron (overdue handling) |

---

## Menjalankan Aplikasi Android

Butuh Android Studio (Giraffe+), JDK 11, Android SDK 36, device/emulator minSdk 27.

```bash
# Build debug APK
./gradlew assembleDebug

# Cek kompilasi tanpa build APK
./gradlew compileDebugKotlin

# Unit test
./gradlew test
```

### Konfigurasi yang diperlukan
- `app/src/main/.../core/utils/Constants.kt`
  - `BASE_URL` → URL backend.
  - `WEB_CLIENT_ID` → Web Client ID dari Google Cloud Console (untuk Google Sign-In).
- `local.properties` → lokasi Android SDK (otomatis dibuat Android Studio).

---

## Menjalankan Backend

```bash
cd "backend seapedia"
npm install
cp .env.example .env   # lalu isi nilainya
npm start
```

### Environment variables (`.env`)

| Variabel | Keterangan |
|---|---|
| `PORT` | Port server (default 3000) |
| `JWT_SECRET` | Secret untuk menandatangani JWT |
| `JWT_EXPIRES_IN` | Masa berlaku token (default `7d`) |
| `DATABASE_URL` | Connection string PostgreSQL/Supabase |
| `SUPABASE_URL` | URL proyek Supabase |
| `SUPABASE_ANON_KEY` | Anon key Supabase |
| `SUPABASE_SERVICE_ROLE_KEY` | Service role key (verifikasi Google ID token) |

### Endpoint utama
- `GET  /health` — health check
- `/api/auth` — register, login, Google auth, profil
- `/api/products` — katalog publik
- `/api/seller`, `/api/buyer`, `/api/driver`, `/api/admin`
- `/api/orders`, `/api/reviews`

---

## Aturan Bisnis Inti (ringkas)

Detail lengkap di [`CLAUDE.md`](CLAUDE.md) (spesifikasi tantangan) dan
[`ROADMAP.md`](ROADMAP.md) (rencana implementasi).

- **Multi-peran**: satu username non-admin bisa punya beberapa peran; user memilih
  *active role* per sesi, dan otorisasi mengikuti active role.
- **Single-store checkout**: satu keranjang hanya boleh berisi produk dari satu toko.
- **Checkout**: subtotal − diskon + ongkir + PPN 12% = total akhir.
- **Lifecycle pesanan**: `Sedang Dikemas → Menunggu Pengirim → Sedang Dikirim →
  Pesanan Selesai → Dikembalikan`.
- **Diskon**: mendukung Voucher (expiry + sisa pakai) dan Promo (expiry).
- **Overdue**: auto refund/return berdasarkan metode kirim.

---

## Progres Saat Ini

- ✅ Auth: Login, Register, Splash, Google Sign-In
- ✅ Fondasi UI: tema brand Oceanic, komponen reusable (Button, TextField, ProductCard,
  ReviewCard, Rating, Empty/Loading)
- ✅ Level 1 (UI): katalog produk, detail produk, role selection, profil/dashboard,
  ulasan aplikasi publik
- 🚧 Integrasi backend (multi-role, skema DB, checkout penuh) — lihat ROADMAP

---

## Lisensi
Proyek tantangan SEAPEDIA — untuk keperluan pengembangan & penilaian.
