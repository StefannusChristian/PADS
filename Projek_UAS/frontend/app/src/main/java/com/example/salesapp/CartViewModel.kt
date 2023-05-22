package com.example.salesapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {

    val totalPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val cartList: MutableLiveData<ArrayList<GetCartResponse>?> = MutableLiveData()

    fun fetchCartItems() {
        RetrofitClient.instance.getDetailCarts("salesA")
            .enqueue(object : Callback<ArrayList<GetCartResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<GetCartResponse>>,
                    response: Response<ArrayList<GetCartResponse>>
                ) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment", "Fetch Products Berhasil!")
                        val cartResponse = response.body()
                        cartResponse?.forEach { item ->
                            item.isChecked = false
                        }
                        cartList.value = cartResponse
                    } else {
                        Log.d("CartFragment", "Fetch Products Gagal!")
                    }
                }

                override fun onFailure(call: Call<ArrayList<GetCartResponse>>, t: Throwable) {
                    Log.d("CartFragment", "Fetch Products Gagal!")
                }
            })
    }

    fun removeCart(cart: RemoveCartRequest) {
        RetrofitClient.instance.removeCartProduct(cart)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment", "Remove Products Berhasil!")
                        fetchCartItems()
                    } else {
                        Log.d("CartFragment", "Remove Products Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("CartFragment", "Remove Products Gagal!")
                }
            })
    }

    fun removeAllCarts(request: RemoveAllCartRequest) {
        RetrofitClient.instance.removeAllCartProduct(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment", "Remove All Products Berhasil!")
                        fetchCartItems()
                    } else {
                        Log.d("CartFragment", "Remove All Products Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("CartFragment", "Remove All Products Gagal!")
                }
            })
    }

    fun updateTotalPrice() {
        val selectedItems = getSelectedItems()
        var total = 0.0
        for (item in selectedItems) {
            val price = item.product_price
            total += price * item.qty
        }
        totalPrice.value = total
    }

    fun incrementQuantity(item: GetCartResponse) {
        if (item.qty < item.product_available_qty) {
            item.qty++
        }
        updateTotalPrice()
    }

    fun decrementQuantity(item: GetCartResponse) {
        if (item.qty > 0) {
            item.qty--
        }
        updateTotalPrice()
    }

    fun cancelSelection() {
        val items = cartList.value?.map { it.copy(isChecked = false) }
        cartList.value = items?.toMutableList() as ArrayList<GetCartResponse>?
        updateTotalPrice()
    }


    private fun getSelectedItems(): List<GetCartResponse> {
        return cartList.value?.filter { it.isChecked } ?: emptyList()
    }
}