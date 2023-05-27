package com.example.salesapp.Cart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.salesapp.*
import com.example.salesapp.API.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {

    val totalPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val cartList: MutableLiveData<ArrayList<GetCartResponse>?> = MutableLiveData()

    fun fetchCartItems() {
        RetrofitClient.cart_instance.getDetailCarts("salesA")
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
        RetrofitClient.cart_instance.removeCartProduct(cart)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment", "Remove Cart Berhasil!")
                        fetchCartItems()
                    } else {
                        Log.d("CartFragment", "Remove Cart Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("CartFragment", "Remove Cart Gagal!")
                }
            })
    }

    fun removeAllCarts(request: RemoveAllCartRequest) {
        RetrofitClient.cart_instance.removeAllCartProduct(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment", "Remove All Cart Berhasil!")
                        fetchCartItems()
                    } else {
                        Log.d("CartFragment", "Remove All Cart Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("CartFragment", "Remove All Cart Gagal!")
                }
            })
    }

    fun updateDetailCarts(request: UpdateDetailCartsRequest) {
        RetrofitClient.cart_instance.updateDetailCarts(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment", "Update Detail Carts Berhasil!")
                        fetchCartItems()
                    } else {
                        Log.d("CartFragment", "Update Detail Carts Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("CartFragment", "Update Detail Carts Gagal!")
                }
            })
    }

    fun addOrder(request: AddOrderRequest, cart: UpdateDetailCartsRequest) {
        RetrofitClient.cart_instance.addOrder(request)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartFragment",response.body().toString())
                        Log.d("CartFragment", "Add Order Berhasil!")
                        updateDetailCarts(cart)
                    } else {
                        Log.d("CartFragment", "Add Order Gagal!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("CartFragment", "Add Order Gagal!")
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