package com.example.salesapp.Transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.API.RetrofitClient
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private val _orderList = MutableLiveData<List<OrderItem>>()
    val orderList: LiveData<List<OrderItem>> get() = _orderList

    fun fetchOrder(sales_username: String) {
        viewModelScope.launch {
            val response = RetrofitClient.transaction_instance.getOrder(sales_username)
            if (response.isSuccessful) {
                response.body()?.let { orders ->
                    _orderList.value = orders
                }
            }
        }
    }

    fun changeOrderStatus(position: Int) {
        val orderListValue = _orderList.value.orEmpty().toMutableList()
        val order = orderListValue[position]
        order.status = "canceled"
        _orderList.value = orderListValue
    }

    fun cancelOrder(sales_uname: String, order_id: Int) {
        val filetoPatch = PatchcancelorderRequest(sales_uname, order_id)
        viewModelScope.launch {
            val response = RetrofitClient.transaction_instance.patchOrder(filetoPatch)
            if (response.isSuccessful) {
                fetchOrder(sales_uname)
            }
        }
    }
}