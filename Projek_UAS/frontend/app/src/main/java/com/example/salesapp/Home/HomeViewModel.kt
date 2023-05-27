package com.example.salesapp.Home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.salesapp.AddToCartRequest
import com.example.salesapp.ApiResponse
import com.example.salesapp.API.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _products = MutableLiveData<ArrayList<ProductResponse>?>()
    private val _promos = MutableLiveData<ArrayList<ProductResponse>?>()

    val products: MutableLiveData<ArrayList<ProductResponse>?> get() = _products
    val promos: MutableLiveData<ArrayList<ProductResponse>?> get() = _promos

    val salesUsername: String = "salesA"


    fun fetchAvailProducts() {
        RetrofitClient.product_instance.getAllAvailableProducts().enqueue(object : Callback<ArrayList<ProductResponse>> {
            override fun onResponse(
                call: Call<ArrayList<ProductResponse>>,
                response: Response<ArrayList<ProductResponse>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    _products.value = productList
                }
            }

            override fun onFailure(call: Call<ArrayList<ProductResponse>>, t: Throwable) {
            }
        })
    }

    fun fetchPromos() {
        RetrofitClient.promo_instance.getAllPromos().enqueue(object : Callback<ArrayList<ProductResponse>> {
            override fun onResponse(
                call: Call<ArrayList<ProductResponse>>,
                response: Response<ArrayList<ProductResponse>>
            ) {
                if (response.isSuccessful) {
                    val promosList = response.body()
                    _promos.value = promosList
                }
            }

            override fun onFailure(call: Call<ArrayList<ProductResponse>>, t: Throwable) {
            }
        })
    }

    fun addToCart(product: AddToCartRequest) {
        RetrofitClient.cart_instance.addToCart(product)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("HomeFragment", "Add To Cart Berhasil!")
                        Log.d("HomeFragment", response.body().toString())
                        // Update The Cart

                    } else {
                        Log.d("HomeFragment", "Add To Cart Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("HomeFragment", "Add To Cart Gagal!")
                }
            })
    }

}

