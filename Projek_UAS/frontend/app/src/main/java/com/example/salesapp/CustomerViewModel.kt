package com.example.salesapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerViewModel: ViewModel() {

    private val _customers = MutableLiveData<ArrayList<GetCustomerResponse>?>()
    val customers: MutableLiveData<ArrayList<GetCustomerResponse>?> get() = _customers
    val salesUsername: String = "salesA"

    fun fetchCustomers() {
        RetrofitClient.instance.getCustomers(salesUsername).enqueue(object : Callback<ArrayList<GetCustomerResponse>> {
            override fun onResponse(
                call: Call<ArrayList<GetCustomerResponse>>,
                response: Response<ArrayList<GetCustomerResponse>>
            ) {
                if (response.isSuccessful) {
                    val customerList = response.body()
                    _customers.value = customerList
                }
            }

            override fun onFailure(call: Call<ArrayList<GetCustomerResponse>>, t: Throwable) {
            }
        })
    }

    fun addCustomer(customer: PostCustomerRequest) {
        RetrofitClient.instance.createCustomer(customer)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        fetchCustomers()
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }
            })
    }

    fun unsubscribeCustomer(customer: PatchCustomerRequest) {
        RetrofitClient.instance.unsubscribeCustomer(customer)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CustomerFragment","Berhasil keapus!")
                        fetchCustomers()
                    } else {
                        Log.d("CustomerFragment","Gagal keapus!")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("customerFragment","Gagal keapus!")
                }
            })
    }

    fun sortCustomersByName() {
        val sortedList = customers.value?.toMutableList()?.apply {
            sortBy { it.name }
        }
        customers.value = sortedList as ArrayList<GetCustomerResponse>?
    }

    fun sortCustomersByAddress() {
        val sortedList = customers.value?.toMutableList()?.apply {
            sortBy { it.address }
        }
        customers.value = sortedList as ArrayList<GetCustomerResponse>?
    }



}
