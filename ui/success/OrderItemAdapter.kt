package com.example.ecommercetaggingapp.ui.success

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercetaggingapp.databinding.ItemOrderBinding
import com.example.ecommercetaggingapp.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class OrderItemAdapter :
    ListAdapter<CartItem, OrderItemAdapter.OrderItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        )

        fun bind(cartItem: CartItem) {
            binding.apply {
                tvOrderItemName.text = cartItem.product.name
                tvOrderItemQty.text = "Qtd: ${cartItem.quantity}"
                tvOrderItemPrice.text = currencyFormatter.format(cartItem.subtotal)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.product.id == newItem.product.id

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }
}
