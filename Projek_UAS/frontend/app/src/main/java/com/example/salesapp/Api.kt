package com.example.salesapp

import android.util.Log
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getallproducts")
    fun getAllProducts(): Call<ArrayList<Product>>

    @GET("getallpromos")
    fun getAllPromos(): Call<ArrayList<Product>>

}
