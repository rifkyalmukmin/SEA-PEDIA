package com.example.seapedia.core.navigation

sealed class Screen(val route: String) {

    // ── Auth ─────────────────────────────────────────────────────────────────
    object Splash        : Screen("splash")
    object Login         : Screen("login")
    object Register      : Screen("register")
    object RoleSelection : Screen("role_selection")

    // ── Shared ───────────────────────────────────────────────────────────────
    object Profile : Screen("profile")
    object ProductList : Screen("product_list")
    object AppReview : Screen("app_review")

    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }

    object Review : Screen("review/{productId}") {
        fun createRoute(productId: String) = "review/$productId"
    }

    // ── Buyer ────────────────────────────────────────────────────────────────
    sealed class Buyer(route: String) : Screen(route) {
        object Home     : Buyer("buyer_home")
        object Cart     : Buyer("buyer_cart")
        object Checkout : Buyer("buyer_checkout")
        object Order    : Buyer("buyer_order")
        object Address  : Buyer("buyer_address")
        object Wallet   : Buyer("buyer_wallet")
    }

    // ── Seller ───────────────────────────────────────────────────────────────
    sealed class Seller(route: String) : Screen(route) {
        object Store         : Seller("seller_store")
        object Product       : Seller("seller_product")
        object IncomingOrder : Seller("seller_incoming_order")
        object Report        : Seller("seller_report")
    }

    // ── Driver ───────────────────────────────────────────────────────────────
    sealed class Driver(route: String) : Screen(route) {
        object Jobs      : Driver("driver_jobs")
        object ActiveJob : Driver("driver_active_job")
        object History   : Driver("driver_history")
        object Earning   : Driver("driver_earning")
    }

    // ── Admin ────────────────────────────────────────────────────────────────
    sealed class Admin(route: String) : Screen(route) {
        object Dashboard  : Admin("admin_dashboard")
        object pushMonitoring : Admin("admin_monitoring")
        object Overdue    : Admin("admin_overdue")
        object Promo      : Admin("admin_promo")
        object Voucher    : Admin("admin_voucher")
    }

    companion object {
        const val AUTH_GRAPH   = "auth_graph"
        const val BUYER_GRAPH  = "buyer_graph"
        const val SELLER_GRAPH = "seller_graph"
        const val DRIVER_GRAPH = "driver_graph"
        const val ADMIN_GRAPH  = "admin_graph"
    }
}
