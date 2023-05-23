package com.example.salesapp

data class Product(
    val available_qty: Int,
    val category: String,
    val description: String,
    val id: Int,
    val img_link: String,
    val name: String,
    val ordered_qty: Int,
    val price: Int,
    val promo: Int,
    val promo_price: Int,
    val total_qty: Int
)