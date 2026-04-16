package com.example.ecommercetaggingapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommercetaggingapp.model.Product

class HomeViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>(Product.mockProducts)
    val products: LiveData<List<Product>> = _products

    private val _filteredProducts = MutableLiveData<List<Product>>(Product.mockProducts)
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    val listId = "home_product_list"
    val listName = "Home - Produtos em Destaque"

    fun search(query: String) {
        if (query.isBlank()) {
            _filteredProducts.value = _products.value
        } else {
            _filteredProducts.value = _products.value?.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true) ||
                product.category.contains(query, ignoreCase = true)
            }
        }
    }
}
