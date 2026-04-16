package com.example.ecommercetaggingapp.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.ecommercetaggingapp.model.CartItem
import com.example.ecommercetaggingapp.model.Order
import com.example.ecommercetaggingapp.service.CartService

class CartViewModel : ViewModel() {

    val cartItems: LiveData<MutableList<CartItem>> = CartService.items

    val isEmpty: LiveData<Boolean> = cartItems.map { it.isEmpty() }

    val subtotal: LiveData<Double> = cartItems.map { items ->
        items.sumOf { it.subtotal }
    }

    val shipping: LiveData<Double> = subtotal.map { sub ->
        if (sub > 5000) 0.0 else 29.90
    }

    val tax: LiveData<Double> = subtotal.map { sub ->
        sub * 0.05
    }

    val total: LiveData<Double> = cartItems.map { items ->
        val sub = items.sumOf { it.subtotal }
        val ship = if (sub > 5000) 0.0 else 29.90
        val t = sub * 0.05
        sub + ship + t
    }

    private val _coupon = MutableLiveData<String?>()
    val coupon: LiveData<String?> = _coupon

    fun setCoupon(value: String?) {
        _coupon.value = value?.ifBlank { null }
    }

    fun buildOrder(): Order {
        return Order.create(
            items = CartService.currentItems,
            coupon = _coupon.value
        )
    }

    fun removeProduct(cartItem: CartItem) {
        CartService.removeProduct(cartItem.product)
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        CartService.updateQuantity(cartItem.product, newQuantity)
    }
}
