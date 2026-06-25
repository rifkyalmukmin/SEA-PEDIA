package com.example.seapedia.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String,
    val phone: String = "",
    val avatarUrl: String = ""
)

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

