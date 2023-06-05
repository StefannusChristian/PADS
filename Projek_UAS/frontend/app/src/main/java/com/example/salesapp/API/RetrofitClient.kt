package com.example.salesapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //    Change this to your IP address and corresponding port
    private const val BASE_URL = "http://192.168.18.94:5000"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val product_instance: productAPI by lazy { retrofit.create(productAPI::class.java) }
    val promo_instance: promoAPI by lazy { retrofit.create(promoAPI::class.java) }
    val cart_instance: cartAPI by lazy { retrofit.create(cartAPI::class.java) }
    val customers_instance: customersAPI by lazy { retrofit.create(customersAPI::class.java) }
    val login_signup_instance: loginRegisterAPI by lazy { retrofit.create(loginRegisterAPI::class.java)}
    val transaction_instance: transactionAPI by lazy { retrofit.create(transactionAPI::class.java)}
    val sales_instance: salesAPI by lazy { retrofit.create(salesAPI::class.java)}

}
