package com.example.salesapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerViewModel: ViewModel() {

    private val _customers = MutableLiveData<ArrayList<Customer>?>()
    val customers: MutableLiveData<ArrayList<Customer>?> get() = _customers
    val username: String = "salesA"

    fun fetchCustomers() {
        RetrofitClient.instance.getCustomers(username).enqueue(object : Callback<ArrayList<Customer>> {
            override fun onResponse(
                call: Call<ArrayList<Customer>>,
                response: Response<ArrayList<Customer>>
            ) {
                if (response.isSuccessful) {
                    val customerList = response.body()
                    _customers.value = customerList
                }
            }

            override fun onFailure(call: Call<ArrayList<Customer>>, t: Throwable) {
            }
        })
    }

}