package com.example.seapedia.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
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
                    onLoginSuccess = {
                        navController.navigate(Screen.BUYER_GRAPH) {
                            popUpTo(Screen.AUTH_GRAPH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.BUYER_GRAPH) {
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
            composable(Screen.Buyer.Home.route) {
                HomeScreen()
            }
            composable(Screen.Buyer.Cart.route) { PlaceholderScreen("Keranjang") }
            composable(Screen.Buyer.Checkout.route) { PlaceholderScreen("Checkout") }
            composable(Screen.Buyer.Order.route) { PlaceholderScreen("Pesanan Saya") }
            composable(Screen.Buyer.Address.route) { PlaceholderScreen("Alamat Pengiriman") }
            composable(Screen.Buyer.Wallet.route) { PlaceholderScreen("Dompet") }
            composable(Screen.ProductDetail.route) { PlaceholderScreen("Detail Produk") }
            composable(Screen.Profile.route) { PlaceholderScreen("Profil") }
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
