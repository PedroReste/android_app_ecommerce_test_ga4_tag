package com.example.ecommercetaggingapp.service

import com.example.ecommercetaggingapp.model.Order

/** * OrderRepository * Armazena temporariamente o pedido em memória * para transferência entre CartFragment → SuccessFragment. */
object OrderRepository {
    private val orders = mutableMapOf<String, Order>()

    fun saveOrder(order: Order) {
        orders[order.id] = order
    }

    fun getOrder(orderId: String): Order? = orders[orderId]

    fun clearOrder(orderId: String) {
        orders.remove(orderId)
    }
}
