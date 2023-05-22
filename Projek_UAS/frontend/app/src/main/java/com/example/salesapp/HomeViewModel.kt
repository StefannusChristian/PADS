package com.example.salesapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.salesapp.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _products = MutableLiveData<ArrayList<Product>?>()
    private val _promos = MutableLiveData<ArrayList<Product>?>()

    val products: MutableLiveData<ArrayList<Product>?> get() = _products
    val promos: MutableLiveData<ArrayList<Product>?> get() = _promos

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

}

