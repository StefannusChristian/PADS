package com.example.salesapp

data class Product(
    val available_qty: Int,
    val category_id: Int,
    val id: Int,
    val img_link: String,
    val promo: Int,
    val name: String,
    val ordered_qty: Int,
    val price: Int,
    val total_qty: Int,
    val warehouse_id: Int,
    val description: String,
)

