package com.example.seapedia.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,                       // peran aktif (kompat lama); "" jika belum dipilih
    val token: String,
    val roles: List<String> = emptyList(),  // semua peran yang dimiliki
    val activeRole: String? = null,         // peran aktif (null jika multi-role belum memilih)
    val phone: String = "",
    val avatarUrl: String = ""
) {
    /** True bila user punya >1 peran dan belum memilih peran aktif. */
    val needsRoleSelection: Boolean
        get() = roles.size > 1 && activeRole.isNullOrBlank()
}

data class Store(
    val id: String,
    val name: String,
    val description: String = "",
    val rating: Double = 0.0,
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Long,
    val stock: Int,
    val store: Store,
    val category: String = "",
    val imageUrl: String? = null,
)

data class AppReview(
    val id: String,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
)

