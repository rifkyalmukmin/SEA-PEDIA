package com.example.seapedia.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.seapedia.core.utils.Constants
import com.example.seapedia.presentation.ProductDetailScreen
import com.example.seapedia.presentation.ProductListScreen
import com.example.seapedia.presentation.ProfileScreen
import com.example.seapedia.presentation.ReviewScreen
import com.example.seapedia.presentation.auth.login.LoginScreen
import com.example.seapedia.presentation.auth.register.RegisterScreen
import com.example.seapedia.presentation.home.HomeScreen
import com.example.seapedia.presentation.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.AUTH_GRAPH
    ) {

        // ── Auth graph ─────────────────────────────────────────────────────
        navigation(
            startDestination = Screen.Splash.route,
            route = Screen.AUTH_GRAPH
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToRole = { role ->
                        navController.navigate(roleToGraph(role)) {
                            popUpTo(Screen.AUTH_GRAPH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = { /* TODO */ },
                    onLoginSuccess = { role ->
                        navController.navigate(roleToGraph(role)) {
                            popUpTo(Screen.AUTH_GRAPH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = { role ->
                        navController.navigate(roleToGraph(role)) {
                            popUpTo(Screen.AUTH_GRAPH) { inclusive = true }
                        }
                    }
                )
            }
        }

        // ── Buyer graph ────────────────────────────────────────────────────
        navigation(
            startDestination = Screen.Buyer.Home.route,
            route = Screen.BUYER_GRAPH
        ) {
            composable(Screen.Buyer.Home.route) { HomeScreen() }
            composable(Screen.ProductList.route) {
                ProductListScreen(
                    onProductClick = { id ->
                        navController.navigate(Screen.ProductDetail.createRoute(id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId").orEmpty()
                ProductDetailScreen(
                    productId = productId,
                    isGuest = false,
                    onBack = { navController.popBackStack() },
                    onRequireLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.AppReview.route) {
                ReviewScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onLogout = {
                    navController.navigate(Screen.AUTH_GRAPH) {
                        popUpTo(Screen.BUYER_GRAPH) { inclusive = true }
                    }
                })
            }
            composable(Screen.Buyer.Cart.route) { PlaceholderScreen("Keranjang") }
            composable(Screen.Buyer.Checkout.route) { PlaceholderScreen("Checkout") }
            composable(Screen.Buyer.Order.route) { PlaceholderScreen("Pesanan Saya") }
            composable(Screen.Buyer.Address.route) { PlaceholderScreen("Alamat Pengiriman") }
            composable(Screen.Buyer.Wallet.route) { PlaceholderScreen("Dompet") }
        }

        // ── Seller graph ───────────────────────────────────────────────────
        navigation(
            startDestination = Screen.Seller.Store.route,
            route = Screen.SELLER_GRAPH
        ) {
            composable(Screen.Seller.Store.route) { PlaceholderScreen("Toko Saya") }
            composable(Screen.Seller.Product.route) { PlaceholderScreen("Kelola Produk") }
            composable(Screen.Seller.IncomingOrder.route) { PlaceholderScreen("Pesanan Masuk") }
            composable(Screen.Seller.Report.route) { PlaceholderScreen("Laporan Penjualan") }
        }

        // ── Driver graph ───────────────────────────────────────────────────
        navigation(
            startDestination = Screen.Driver.Jobs.route,
            route = Screen.DRIVER_GRAPH
        ) {
            composable(Screen.Driver.Jobs.route) { PlaceholderScreen("Daftar Job") }
            composable(Screen.Driver.ActiveJob.route) { PlaceholderScreen("Job Aktif") }
            composable(Screen.Driver.History.route) { PlaceholderScreen("Riwayat Pengiriman") }
            composable(Screen.Driver.Earning.route) { PlaceholderScreen("Penghasilan") }
        }

        // ── Admin graph ────────────────────────────────────────────────────
        navigation(
            startDestination = Screen.Admin.Dashboard.route,
            route = Screen.ADMIN_GRAPH
        ) {
            composable(Screen.Admin.Dashboard.route) { PlaceholderScreen("Dashboard Admin") }
            composable(Screen.Admin.Monitoring.route) { PlaceholderScreen("Monitoring") }
            composable(Screen.Admin.Overdue.route) { PlaceholderScreen("Overdue") }
            composable(Screen.Admin.Promo.route) { PlaceholderScreen("Kelola Promo") }
            composable(Screen.Admin.Voucher.route) { PlaceholderScreen("Kelola Voucher") }
        }
    }
}

private fun roleToGraph(role: String): String = when (role) {
    Constants.ROLE_SELLER -> Screen.SELLER_GRAPH
    Constants.ROLE_DRIVER -> Screen.DRIVER_GRAPH
    Constants.ROLE_ADMIN  -> Screen.ADMIN_GRAPH
    else                  -> Screen.BUYER_GRAPH
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
