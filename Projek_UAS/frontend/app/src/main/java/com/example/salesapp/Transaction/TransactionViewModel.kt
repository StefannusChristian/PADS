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

    fun fetchOrder() {
        Log.d("TransactionFragment","Masuk Fetch Order si Transaction ViewModel")
        viewModelScope.launch {
            Log.d("TransactionFragment","Masuk Fetch Order dibawah viewModelScope")
            val response = RetrofitClient.transaction_instance.getOrder()
            Log.d("TransactionFragment",response.body().toString()+" INI RESPONSE")
            if (response.isSuccessful) {
            Log.d("TransactionFragment","RESPONSE NYA SUKSES!")
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

    fun cancelOrder(sales_uname: String = "salesA", order_id: Int) {
        Log.d("Transaction Fragment", "Cancel Order Kepanggil")
        val filetoPatch = PatchcancelorderRequest(sales_uname, order_id)
        viewModelScope.launch {
            val response = RetrofitClient.transaction_instance.patchOrder(filetoPatch)
            if (response.isSuccessful) {
                Log.d("Transaction Fragment", response.body().toString())
                fetchOrder()
            }
        }
    }
}