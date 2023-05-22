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
    fun createCustomer(@Body customerRequest: PostCustomerRequest): Call<ApiResponse>

    @PATCH("unsubscribe")
    fun unsubscribeCustomer(@Body patchCustomerRequest: PatchCustomerRequest): Call<ApiResponse>

    @POST("addcartproduct")
    fun addToCart(@Body addToCartRequest: AddToCartRequest): Call<ApiResponse>

    @GET("getdetailcarts/{sales_username}")
    fun getDetailCarts(@Path("sales_username")sales_username: String): Call<ArrayList<GetCartResponse>>

    @POST("removecartproduct")
    fun removeCartProduct(@Body removeCartRequest: RemoveCartRequest): Call<ApiResponse>

    @POST("removeallcartproduct")
    fun removeAllCartProduct(@Body removeAllCartRequest: RemoveAllCartRequest): Call<ApiResponse>

    @PATCH("updatedetailcarts")
    fun updateDetailCarts(@Body updateDetailCartsRequest: UpdateDetailCartsRequest): Call<ApiResponse>

}
