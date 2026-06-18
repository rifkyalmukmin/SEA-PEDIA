# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

This is an Android project built with Gradle. Use Android Studio or the Gradle wrapper:

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented (on-device) tests
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.example.seapedia.ExampleUnitTest"

# Check for compilation errors without building APK
./gradlew compileDebugKotlin
```

## Architecture

SeaPedia is a multi-role Android marketplace app (seafood e-commerce) with roles: **Buyer**, **Seller**, **Driver**, and **Admin**. It follows Clean Architecture + MVVM with Hilt for DI.

### Layer structure

```
domain/          — model.kt, repository.kt, usecase.kt (interfaces & domain models)
data/
  remote/        — api.kt (Retrofit), dto.kt, response.kt
  local/         — database.kt (Room), dao.kt, entity.kt
  repository/    — *RepositoryImpl.kt (one per role + Auth + Product)
di/              — Hilt modules: AppModule, NetworkModule, DatabaseModule, RepositoryModule
presentation/    — one file per screen, grouped by role (auth/, buyer/, seller/, driver/, admin/)
core/
  components/    — shared Composables (SeaButton, SeaTextField, ProductCard, ReviewCard, etc.)
  navigation/    — NavGraph.kt, BottomBar.kt, Screen.kt (sealed route definitions)
  ui/theme/      — Color.kt, Theme.kt, Type.kt
  utils/         — Constants.kt, Formatter.kt, Resource.kt (sealed Result wrapper)
worker/          — OverdueWorker.kt (WorkManager background task for overdue orders)
```

### Key conventions

- **Resource<T>** (`core/utils/Resource.kt`) — sealed class wrapping Loading/Success/Error; used as the return type for all repository and use-case calls flowing to ViewModels.
- **Screen** (`core/navigation/Screen.kt`) — sealed class defining all navigation routes; add new screens here before wiring them in NavGraph.
- Presentation files are flat `.kt` files (not classes) per screen, containing the Composable function and its ViewModel in the same file.
- Hilt modules are split by concern: `NetworkModule` provides Retrofit/OkHttp, `DatabaseModule` provides Room, `RepositoryModule` binds interfaces to impls, `AppModule` holds everything else (DataStore, etc.).

### Tech stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| DI | Hilt 2.59 |
| Networking | Retrofit 2.11 + OkHttp 4.12 + Gson |
| Local DB | Room 2.6 |
| Image loading | Coil 2.7 |
| Navigation | Navigation Compose 2.7 |
| Async | Kotlin Coroutines 1.8 |
| Persistence | DataStore Preferences 1.1 |
| Background | WorkManager (OverdueWorker) |

### Role-based navigation

Each role has its own bottom navigation bar and screen set. The splash screen (`presentation/splash/`) reads persisted auth state (DataStore) and routes to the correct role's graph on launch.
