package com.example.salesapp

data class GetProductResponse(
    val data: List<Product>,
)

data class Product(
    val id: Int,
    val name: String
)
