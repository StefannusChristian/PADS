package com.example.salesapp.Login

import com.google.gson.annotations.SerializedName

data class LoginItem(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    val sales_username: String,
)
