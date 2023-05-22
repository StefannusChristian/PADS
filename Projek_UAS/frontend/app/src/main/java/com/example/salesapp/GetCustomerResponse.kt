package com.example.salesapp

import com.google.gson.annotations.SerializedName

data class GetCustomerResponse(
    val address: String,
    val sales_id: String,
    val username: String,
    val img_link: String
)

data class PostCustomerRequest(
    @SerializedName("sales_username") val sales_username: String,
    @SerializedName("customer_username") val customer_username: String,
    @SerializedName("customer_address") val customer_address: String,
    @SerializedName("customer_img_link") val customer_img_link: String
)

data class PostResponse(
    val message: String,
    val status: String
)

data class AddToCartRequest(
    @SerializedName("sales_username") val sales_username: String,
    @SerializedName("product_id") val product_id: Int,
    @SerializedName("qty") val qty: Int,
)

data class PatchCustomerRequest(
    @SerializedName("sales_username") val sales_username: String,
    @SerializedName("customer_username") val customer_username: String,
)
