package com.example.salesapp.API

import com.example.salesapp.*
import com.example.salesapp.Home.ProductResponse
import com.example.salesapp.Login.LoginItem
import com.example.salesapp.Login.LoginResponse
import com.example.salesapp.SignUp.SignupItem
import com.example.salesapp.Transaction.OrderItem
import com.example.salesapp.Transaction.PatchcancelorderRequest
import retrofit2.Call
import retrofit2.http.*
import retrofit2.Response

interface productAPI {
    @GET("getallproducts")
    suspend fun getAllProducts(): Response<List<ProductResponse>>

    @GET("getallavailableproducts")
    fun getAllAvailableProducts(): Call<ArrayList<ProductResponse>>
}

interface promoAPI {
    @GET("getallpromos")
    fun getAllPromos(): Call<ArrayList<ProductResponse>>

    @GET("getallavailablepromos")
    fun getAllAvailablePromos(): Call<ArrayList<ProductResponse>>

}

interface customersAPI {
    @GET("getcustomers/{salesName}")
    fun getCustomers(@Path("salesName")salesName: String): Call<ArrayList<GetCustomerResponse>>

    @POST("addnewcustomer")
    fun createCustomer(@Body customerRequest: PostCustomerRequest): Call<ApiResponse>

    @PATCH("unsubscribe")
    fun unsubscribeCustomer(@Body patchCustomerRequest: PatchCustomerRequest): Call<ApiResponse>

}

interface cartAPI {
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

    @POST("addorder")
    fun addOrder(@Body addOrderRequest: AddOrderRequest): Call<ApiResponse>

}

interface loginRegisterAPI{
    @POST("login")
    suspend fun postLogin(
        @Body user_pass : LoginItem
    ):Response<LoginResponse>

    @POST("register")
    suspend fun postSignUp(
        @Body regis_info : SignupItem
    ):Response<ApiResponse>
}

interface transactionAPI{
    @GET("getorders/salesA")
    suspend fun getOrder(): Response<List<OrderItem>>

    @PATCH("cancelorder")
    suspend fun patchOrder(
        @Body item: PatchcancelorderRequest
    ):Response<ApiResponse>
}


