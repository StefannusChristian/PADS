package com.example.salesapp.Inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.API.RetrofitClient
import com.example.salesapp.Home.ProductResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class InventoryViewModel: ViewModel() {
    private val _inventory = MutableLiveData<List<ProductResponse>?>()
    val inventory: MutableLiveData<List<ProductResponse>?> get() = _inventory

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchInventoryData() {
        viewModelScope.launch {
            _loading.value = true
            val response = try {
                RetrofitClient.product_instance.getAllProducts()
            } catch (e: IOException) {
                // Handle IOException, e.g., show error message
                null
            } catch (e: HttpException) {
                // Handle HttpException, e.g., show error message
                null
            }

            if (response != null && response.isSuccessful && response.body() != null) {
                _inventory.value = response.body()
            } else {
                // Handle unsuccessful response or null response
            }

            _loading.value = false
        }
    }

    fun sortByAvailable() {
        val sortedList = _inventory.value?.toMutableList()?.apply {
            sortByDescending { it.available_qty }
        }
        _inventory.value = sortedList
    }

    fun sortByTotal() {
        val sortedList = _inventory.value?.toMutableList()?.apply {
            sortByDescending { it.total_qty }
        }
        _inventory.value = sortedList
    }

    fun sortByOrdered() {
        val sortedList = _inventory.value?.toMutableList()?.apply {
            sortByDescending { it.ordered_qty }
        }
        _inventory.value = sortedList
    }
}