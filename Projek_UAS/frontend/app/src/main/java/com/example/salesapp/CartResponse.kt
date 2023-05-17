package com.example.salesapp

data class CartResponse(
    val imageUrl: String,
    val price: String,
    val desc: String,
    var quantity: Int,
    var isChecked: Boolean
)


