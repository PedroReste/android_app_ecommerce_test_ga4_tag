package com.example.ecommercetaggingapp.ui.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercetaggingapp.databinding.ItemProductBinding
import com.example.ecommercetaggingapp.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onProductClick: (Product, Int) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormatter = NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        )

        fun bind(product: Product, position: Int) {
            binding.apply {
                // Textos
                tvProductName.text = product.name
                tvBrand.text = product.brand
                tvRating.text = "⭐ ${product.rating} (${product.reviewCount})"
                tvPrice.text = currencyFormatter.format(product.price)

                // Preço original com tachado
                if (product.isOnSale) {
                    tvOriginalPrice.apply {
                        text = currencyFormatter.format(product.originalPrice)
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        isVisible = true
                    }
                    tvSaleBadge.apply {
                        text = "-${product.discount.toInt()}%"
                        isVisible = true
                    }
                } else {
                    tvOriginalPrice.isVisible = false
                    tvSaleBadge.isVisible = false
                }

                // Click no card → detalhes
                root.setOnClickListener {
                    onProductClick(product, position)
                }

                // Botão adicionar ao carrinho
                btnAddToCart.setOnClickListener {
                    onAddToCart(product)
                    // Feedback visual
                    btnAddToCart.text = "✓ Adicionado"
                    btnAddToCart.isEnabled = false
                    root.postDelayed({
                        btnAddToCart.text = "Adicionar"
                        btnAddToCart.isEnabled = true
                    }, 1500)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) =
            oldItem == newItem
    }
}
