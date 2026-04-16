package com.example.ecommercetaggingapp.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercetaggingapp.databinding.ItemCartBinding
import com.example.ecommercetaggingapp.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onQuantityDecrease: (CartItem) -> Unit,
    private val onQuantityIncrease: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        )

        fun bind(cartItem: CartItem) {
            binding.apply {
                tvCartItemName.text = cartItem.product.name
                tvCartItemVariant.text = cartItem.product.variant
                tvCartItemPrice.text = currencyFormatter.format(cartItem.subtotal)
                tvCartItemQuantity.text = cartItem.quantity.toString()

                btnDecreaseQty.setOnClickListener {
                    onQuantityDecrease(cartItem)
                }

                btnIncreaseQty.setOnClickListener {
                    onQuantityIncrease(cartItem)
                }

                btnRemoveItem.setOnClickListener {
                    onRemove(cartItem)
                }
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
