package com.example.salesapp.Transaction

import com.google.gson.annotations.SerializedName

data class OrderItem(
    val customer: String,
    val date: String,
    val id: Int,
    val image: String,
    val qty: Int,
    var status: String,
    val total_price: Int
)

data class PatchcancelorderRequest(
    @SerializedName("sales_username") val sales_username: String,
    @SerializedName("order_id") val order_id: Int,
)