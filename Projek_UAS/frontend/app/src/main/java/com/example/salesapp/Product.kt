package com.example.salesapp

data class Product(
    val available_qty: Int,
    val id: Int,
    val img_link: String,
    val is_promo: Boolean,
    val name: String,
    val ordered_qty: Int,
    val price: Int,
    val total_qty: Int,
    val warehouse_id: Int,
    val description: String,
    val category_name: String
)

