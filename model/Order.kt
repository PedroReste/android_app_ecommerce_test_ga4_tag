package com.example.ecommercetaggingapp.model

import kotlin.random.Random

data class Order(
    val id: String,
    val items: List<CartItem>,
    val affiliation: String,
    val shipping: Double,
    val tax: Double,
    val coupon: String?
) {
    val subtotal: Double
        get() = items.sumOf { it.subtotal }

    val total: Double
        get() = subtotal + shipping + tax

    companion object {
        fun create(items: List<CartItem>, coupon: String? = null): Order {
            val subtotal = items.sumOf { it.subtotal }
            val shipping = if (subtotal > 5000) 0.0 else 29.90
            val tax = subtotal * 0.05

            return Order(
                id = "ORD-${Random.nextInt(100000, 999999)}",
                items = items.toList(),
                affiliation = "TechStore Android App",
                shipping = shipping,
                tax = tax,
                coupon = coupon?.ifBlank { null }
            )
        }
    }
}
