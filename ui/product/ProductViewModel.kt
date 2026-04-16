package com.example.ecommercetaggingapp.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommercetaggingapp.model.Product

class ProductViewModel : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _quantity = MutableLiveData(1)
    val quantity: LiveData<Int> = _quantity

    private val _isWishlisted = MutableLiveData(false)
    val isWishlisted: LiveData<Boolean> = _isWishlisted

    fun loadProduct(productId: String) {
        _product.value = Product.mockProducts.find { it.id == productId }
    }

    fun increaseQuantity() {
        val current = _quantity.value ?: 1
        if (current < 10) _quantity.value = current + 1
    }

    fun decreaseQuantity() {
        val current = _quantity.value ?: 1
        if (current > 1) _quantity.value = current - 1
    }

    fun toggleWishlist() {
        _isWishlisted.value = !(_isWishlisted.value ?: false)
    }
}
