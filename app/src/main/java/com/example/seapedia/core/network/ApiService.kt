package com.example.seapedia.core.network

import com.example.seapedia.domain.AppReview
import com.example.seapedia.domain.Product
import com.example.seapedia.domain.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.PartMap

interface ApiService {

    // Authentication endpoints
    @POST("/api/auth/register")
    fun register(@Body user: User): Call<User>

    @POST("/api/auth/login")
    fun login(@Body user: User): Call<User>

    @POST("/api/auth/google")
    fun googleAuth(@Body user: User): Call<User>

    @POST("/api/auth/select-role")
    fun selectRole(@Body roleSelection: RoleSelection): Call<User>

    @GET("/api/auth/me")
    fun getProfile(): Call<User>

    @PUT("/api/auth/profile")
    fun updateProfile(@Body user: User): Call<User>

    // Product endpoints (public)
    @GET("/api/products")
    fun getProducts(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Call<ProductResponse>

    @GET("/api/products/{id}")
    fun getProduct(@Path("id") id: String): Call<Product>

    @GET("/api/products/search")
    fun searchProducts(@Query("q") query: String, @Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Call<ProductResponse>

    // Seller product endpoints
    @GET("/api/seller/products")
    fun getSellerProducts(): Call<List<Product>>

    @POST("/api/seller/products")
    fun createProduct(@Body product: Product): Call<Product>

    @PUT("/api/seller/products/{id}")
    fun updateProduct(@Path("id") id: String, @Body product: Product): Call<Product>

    @DELETE("/api/seller/products/{id}")
    fun deleteProduct(@Path("id") id: String): Call<ResponseBody>

    // Review endpoints
    @GET("/api/reviews")
    fun getReviews(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Call<ReviewResponse>

    @POST("/api/reviews")
    fun createReview(@Body review: AppReview): Call<AppReview>

    @DELETE("/api/reviews/{id}")
    fun deleteReview(@Path("id") id: String): Call<ResponseBody>

    // Seller endpoints
    @GET("/api/seller/store")
    fun getStore(): Call<Store>

    @POST("/api/seller/store")
    fun createStore(@Body store: Store): Call<Store>

    @PUT("/api/seller/store")
    fun updateStore(@Body store: Store): Call<Store>

    @GET("/api/seller/orders")
    fun getSellerOrders(): Call<List<Order>>

    @PUT("/api/seller/orders/{id}/process")
    fun processOrder(@Path("id") id: String): Call<Order>

    // Buyer endpoints
    @GET("/api/buyer/wallet")
    fun getWallet(): Call<Wallet>

    @POST("/api/buyer/wallet/topup")
    fun topUpWallet(@Body topUp: WalletTopUp): Call<Wallet>

    @GET("/api/buyer/address")
    fun getAddresses(): Call<List<Address>>

    @POST("/api/buyer/address")
    fun addAddress(@Body address: Address): Call<Address>

    @PUT("/api/buyer/address/{id}")
    fun updateAddress(@Path("id") id: String, @Body address: Address): Call<Address>

    @DELETE("/api/buyer/address/{id}")
    fun deleteAddress(@Path("id") id: String): Call<ResponseBody>

    @GET("/api/buyer/cart")
    fun getCart(): Call<Cart>

    @POST("/api/buyer/cart")
    fun addToCart(@Body cartItem: CartItem): Call<Cart>

    @PUT("/api/buyer/cart/{itemId}")
    fun updateCartItem(@Path("itemId") itemId: String, @Body cartItem: CartItem): Call<Cart>

    @DELETE("/api/buyer/cart/{itemId}")
    fun removeFromCart(@Path("itemId") itemId: String): Call<Cart>

    @POST("/api/buyer/checkout")
    fun checkout(@Body checkoutRequest: CheckoutRequest): Call<Order>

    @GET("/api/buyer/orders")
    fun getOrderHistory(): Call<List<Order>>

    @GET("/api/buyer/orders/{id}")
    fun getOrderDetails(@Path("id") id: String): Call<Order>

    // Driver endpoints
    @GET("/api/driver/jobs")
    func getAvailableJobs(): Call<List<Job>>

    @POST("/api/driver/jobs/{id}/take")
    fun takeJob(@Path("id") id: String): Call<Job>

    @POST("/api/driver/jobs/{id}/complete")
    fun completeJob(@Path("id") id: String): Call<Job>

    @GET("/api/driver/jobs/history")
    fun getJobHistory(): Call<List<Job>>

    @GET("/api/driver/earnings")
    fun getEarnings(): Call<Earnings>

    // Admin endpoints
    @GET("/api/admin/dashboard")
    fun getAdminDashboard(): Call<AdminDashboard>

    @GET("/api/admin/monitoring")
    fun getMonitoringData(): Call<AdminMonitoring>

    @GET("/api/admin/overdue")
    fun getOverdueOrders(): Call<List<Order>>

    @POST("/api/admin/vouchers")
    fun createVoucher(@Body voucher: Voucher): Call<Voucher>

    @GET("/api/admin/vouchers")
    fun getVouchers(): Call<List<Voucher>>

    @POST("/api/admin/promos")
    fun createPromo(@Body promo: Promo): Call<Promo>

    @GET("/api/admin/promos")
    fun getPromos(): Call<List<Promo>>

    @POST("/api/admin/simulate-next-day")
    fun simulateNextDay(): Call<ResponseBody>
}

// Data classes for API responses
data class ProductResponse(
    val success: Boolean = true,
    val data: List<Product> = emptyList()
)

data class ReviewResponse(
    val success: Boolean = true,
    val data: List<AppReview> = emptyList()
)

data class RoleSelection(
    val role: String
)

data class Wallet(
    val balance: Long = 0,
    val transactions: List<WalletTransaction> = emptyList()
)

data class WalletTransaction(
    val id: String = "",
    val amount: Long = 0,
    val type: String = "", // "topup" or "purchase"
    val description: String = "",
    val timestamp: String = ""
)

data class WalletTopUp(
    val amount: Long
)

data class Address(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val isDefault: Boolean = false
)

data class Cart(
    val items: List<CartItem> = emptyList(),
    val subtotal: Long = 0,
    val deliveryFee: Long = 0,
    val discount: Long = 0,
    val tax: Long = 0,
    val total: Long = 0
)

data class CartItem(
    val id: String = "",
    val productId: String = "",
    val quantity: Int = 1,
    val price: Long = 0
)

data class CheckoutRequest(
    val addressId: String = "",
    val deliveryMethod: String = "", // "instant", "next_day", "regular"
    val discountCode: String = ""
)

data class Order(
    val id: String = "",
    val status: String = "", // "Sedang Dikemas", "Menunggu Pengirim", etc.
    val items: List<OrderItem> = emptyList(),
    val subtotal: Long = 0,
    val deliveryFee: Long = 0,
    val discount: Long = 0,
    val tax: Long = 0,
    val total: Long = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val statusHistory: List<OrderStatus> = emptyList()
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Long = 0
)

data class OrderStatus(
    val status: String = "",
    val timestamp: String = ""
)

data class Store(
    val id: String = "",
    val name: String = "",
    val description: String = ""
)

data class Job(
    val id: String = "",
    val orderId: String = "",
    val pickupAddress: String = "",
    val deliveryAddress: String = "",
    val status: String = "", // "available", "taken", "completed"
    val assignedDriverId: String = "",
    val createdAt: String = ""
)

data class Earnings(
    val totalEarnings: Long = 0,
    val completedJobs: Int = 0,
    val pendingJobs: Int = 0
)

data class AdminDashboard(
    val totalUsers: Int = 0,
    val totalStores: Int = 0,
    val totalProducts: Int = 0,
    val totalOrders: Int = 0,
    val totalRevenue: Long = 0,
    val pendingOrders: Int = 0,
    val processingOrders: Int = 0,
    val deliveredOrders: Int = 0
)

data class AdminMonitoring(
    val users: List<AdminUser> = emptyList(),
    val stores: List<AdminStore> = emptyList(),
    val products: List<AdminProduct> = emptyList(),
    val orders: List<AdminOrder> = emptyList(),
    vouchers: List<AdminVoucher> = emptyList(),
    promos: List<AdminPromo> = emptyList()
)

data class AdminUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val roles: List<String> = emptyList(),
    val activeRole: String = "",
    val createdAt: String = ""
)

data class AdminStore(
    val id: String = "",
    val name: String = "",
    val sellerId: String = "",
    val productCount: Int = 0
)

data class AdminProduct(
    val id: String = "",
    val name: String = "",
    val storeName: String = "",
    val price: Long = 0,
    val stock: Int = 0
)

data class AdminOrder(
    val id: String = "",
    val buyerName: String = "",
    val storeName: String = "",
    val total: Long = 0,
    val status: String = "",
    val createdAt: String = ""
)

data class AdminVoucher(
    val id: String = "",
    val code: String = "",
    val discountAmount: Long = 0,
    val expiryDate: String = "",
    val usageLimit: Int = 0,
    val usedCount: Int = 0,
    val isActive: Boolean = true
)

data class AdminPromo(
    val id: String = "",
    val code: String = "",
    val discountPercent: Int = 0,
    val expiryDate: String = "",
    val isActive: Boolean = true
)

data class Voucher(
    val id: String = "",
    val code: String = "",
    val discountAmount: Long = 0,
    val expiryDate: String = "",
    val usageLimit: Int = 0
)

data class Promo(
    val id: String = "",
    val code: String = "",
    val discountPercent: Int = 0,
    val expiryDate: String = ""
)

data class AdminDashboardResponse(
    val success: Boolean = true,
    val data: AdminDashboard = AdminDashboard()
)

data class AdminMonitoringResponse(
    val success: Boolean = true,
    val data: AdminMonitoring = AdminMonitoring()
)

data class VoucherResponse(
    val success: Boolean = true,
    val data: Voucher = Voucher()
)

data class PromoResponse(
    val success: Boolean = true,
    val data: Promo = Promo()
)

data class EarningsResponse(
    val success: Boolean = true,
    val data: Earnings = Earnings()
)

data class JobResponse(
    val success: Boolean = true,
    val data: Job = Job()
)

data class OrderResponse(
    val success: Boolean = true,
    val data: Order = Order()
)

data class WalletResponse(
    val success: Boolean = true,
    val data: Wallet = Wallet()
)

data class AddressResponse(
    val success: Boolean = true,
    val data: Address = Address()
)

data class CartResponse(
    val success: Boolean = true,
    val data: Cart = Cart()
)

data class StoreResponse(
    val success: Boolean = true,
    val data: Store = Store()
)

data class ProductListResponse(
    val success: Boolean = true,
    val data: List<Product> = emptyList()
)