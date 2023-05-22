package com.example.salesapp

import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getallproducts")
    fun getAllProducts(): Call<ArrayList<Product>>

    @GET("getallpromos")
    fun getAllPromos(): Call<ArrayList<Product>>

    @GET("getcustomers/{salesName}")
    fun getCustomers(@Path("salesName")salesName: String): Call<ArrayList<GetCustomerResponse>>

    @POST("addnewcustomer")
    fun createCustomer(@Body customerRequest: PostCustomerRequest): Call<PostResponse>

    @PATCH("unsubscribe")
    fun unsubscribeCustomer(@Body patchCustomerRequest: PatchCustomerRequest): Call<PostResponse>

    @POST("addcartproduct")
    fun addToCart(@Body addToCartRequest: AddToCartRequest): Call<PostResponse>

}
