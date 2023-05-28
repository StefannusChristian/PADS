package com.example.salesapp.SignUp

import com.google.gson.annotations.SerializedName

data class SignupItem(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)
