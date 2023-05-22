package com.example.salesapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _products = MutableLiveData<ArrayList<Product>?>()
    private val _promos = MutableLiveData<ArrayList<Product>?>()

    val products: MutableLiveData<ArrayList<Product>?> get() = _products
    val promos: MutableLiveData<ArrayList<Product>?> get() = _promos

    val salesUsername: String = "salesA"


    fun fetchProducts() {
        RetrofitClient.instance.getAllProducts().enqueue(object : Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    _products.value = productList
                }
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
            }
        })
    }

    fun fetchPromos() {
        RetrofitClient.instance.getAllPromos().enqueue(object : Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if (response.isSuccessful) {
                    val promosList = response.body()
                    _promos.value = promosList
                }
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
            }
        })
    }

    fun addToCart(product: AddToCartRequest) {
        RetrofitClient.instance.addToCart(product)
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

