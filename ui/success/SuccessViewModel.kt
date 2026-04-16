package com.example.ecommercetaggingapp.ui.success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommercetaggingapp.model.Order
import com.example.ecommercetaggingapp.service.OrderRepository

class SuccessViewModel : ViewModel() {

    private val _order = MutableLiveData<Order?>()
    val order: LiveData<Order?> = _order

    // Garante que o evento purchase seja disparado apenas uma vez
    private var purchaseLogged = false

    fun loadOrder(orderId: String) {
        _order.value = OrderRepository.getOrder(orderId)
    }

    fun shouldLogPurchase(): Boolean {
        return if (!purchaseLogged) {
            purchaseLogged = true
            true
        } else false
    }
}
