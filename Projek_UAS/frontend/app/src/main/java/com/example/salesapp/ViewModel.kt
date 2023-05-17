package com.example.salesapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel: ViewModel() {

    val totalPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val cartItems: MutableLiveData<List<CartResponse>> = MutableLiveData(emptyList())

    fun updateTotalPrice() {
        val selectedItems = getSelectedItems()
        var total = 0.0
        for (item in selectedItems) {
            val price = item.price.replace(Regex("[^\\d.]"), "").toDouble()
            total += price * item.quantity
        }
        totalPrice.value = total
    }


    fun incrementQuantity(item: CartResponse) {
        item.quantity++
        updateTotalPrice()
    }

    fun decrementQuantity(item: CartResponse) {
        if (item.quantity > 0) {
            item.quantity--
            updateTotalPrice()
        }
    }

    private fun getSelectedItems(): List<CartResponse> {
        return cartItems.value?.filter { it.isChecked } ?: emptyList()
    }

}