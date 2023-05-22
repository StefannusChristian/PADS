package com.example.salesapp

import com.google.gson.annotations.SerializedName

data class GetCartResponse(
    val id: Int,
    val is_available: Boolean,
    val product_available_qty: Int,
    val product_category: String,
    val product_description: String,
    val product_id: Int,
    val product_img: String,
    val product_name: String,
    val product_price: Int,
    val promo: Int,
    val promo_price: Int,
    var qty: Int,
    var isChecked: Boolean = false
)

data class RemoveCartRequest(
    @SerializedName("sales_username") val sales_username: String,
    @SerializedName("product_id") val product_id: Int,
)

data class RemoveAllCartRequest(
    @SerializedName("sales_username") val sales_username: String,
)


