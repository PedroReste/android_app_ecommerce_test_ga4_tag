package com.example.ecommercetaggingapp.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommercetaggingapp.model.CartItem
import com.example.ecommercetaggingapp.model.Product

/** * CartService — Singleton que gerencia o estado do carrinho. * Usa LiveData para que os Fragments observem mudanças reativas. */
object CartService {

    private val _items = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val items: LiveData<MutableList<CartItem>> = _items

    val currentItems: List<CartItem>
        get() = _items.value ?: emptyList()

    val totalItems: Int
        get() = currentItems.sumOf { it.quantity }

    val totalValue: Double
        get() = currentItems.sumOf { it.subtotal }

    val isEmpty: Boolean
        get() = currentItems.isEmpty()

    // ─────────────────────────────────────────────────────────────

    fun addProduct(product: Product, quantity: Int = 1) {
        val list = _items.value ?: mutableListOf()
        val existing = list.indexOfFirst { it.product.id == product.id }

        if (existing >= 0) {
            list[existing] = list[existing].copy(
                quantity = list[existing].quantity + quantity
            )
        } else {
            list.add(CartItem(product, quantity))
        }
        _items.value = list
    }

    fun removeProduct(product: Product) {
        val list = _items.value ?: return
        list.removeAll { it.product.id == product.id }
        _items.value = list
    }

    fun updateQuantity(product: Product, quantity: Int) {
        if (quantity <= 0) {
            removeProduct(product)
            return
        }
        val list = _items.value ?: return
        val index = list.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            list[index] = list[index].copy(quantity = quantity)
            _items.value = list
        }
    }

    fun clearCart() {
        _items.value = mutableListOf()
    }
}
