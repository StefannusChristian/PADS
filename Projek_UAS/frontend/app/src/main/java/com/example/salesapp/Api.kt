package com.example.salesapp

import retrofit2.Call
import retrofit2.http.*

interface Api {
    //    Get All Products
//    @GET("getallproducts")
//    fun getAllProducts(): Call<ArrayList<GetProductResponse>>
    @GET("getallproducts")
    fun getAllProducts(): Call<ArrayList<GetProductResponse>>
}