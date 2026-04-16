package com.example.ecommercetaggingapp.ui.product

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ecommercetaggingapp.R
import com.example.ecommercetaggingapp.analytics.AnalyticsManager
import com.example.ecommercetaggingapp.databinding.FragmentProductBinding
import com.example.ecommercetaggingapp.service.CartService
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    // ─────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getString("productId") ?: return
        viewModel.loadProduct(productId)

        observeViewModel()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()

        // ✅ screen_view
        AnalyticsManager.logScreenView(
            screenName = "Product Detail",
            screenClass = "ProductFragment"
        )

        // ✅ view_item — disparado novamente ao retomar a tela
        viewModel.product.value?.let { product ->
            AnalyticsManager.logViewItem(product)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ─────────────────────────────────────────────────────────────
    // Observers
    // ─────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            product ?: return@observe
            bindProduct()
        }

        viewModel.quantity.observe(viewLifecycleOwner) { qty ->
            binding.tvQuantity.text = qty.toString()
        }

        viewModel.isWishlisted.observe(viewLifecycleOwner) { wishlisted ->
            binding.btnWishlist.apply {
                setIconResource(
                    if (wishlisted) R.drawable.ic_heart_filled
                    else R.drawable.ic_heart_outline
                )
                text = if (wishlisted) "Salvo" else "Favoritar"
            }
        }
    }

    private fun bindProduct() {
        val product = viewModel.product.value ?: return

        binding.apply {
            tvProductName.text = product.name
            tvBrand.text = product.brand
            tvVariant.text = product.variant
            tvDescription.text = product.description
            tvCategory.text = product.category
            tvRating.text = "⭐ ${product.rating} · ${product.reviewCount} avaliações"
            tvPrice.text = currencyFormatter.format(product.price)

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
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Buttons
    // ─────────────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.btnDecreaseQty.setOnClickListener {
            viewModel.decreaseQuantity()
        }

        binding.btnIncreaseQty.setOnClickListener {
            viewModel.increaseQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
            val product = viewModel.product.value ?: return@setOnClickListener
            val qty = viewModel.quantity.value ?: 1

            CartService.addProduct(product, qty)

            // ✅ add_to_cart
            AnalyticsManager.logAddToCart(product, qty)

            // Feedback visual
            binding.btnAddToCart.text = "✅ Adicionado!"
            binding.btnAddToCart.isEnabled = false
            binding.root.postDelayed({
                binding.btnAddToCart.text = "Adicionar ao Carrinho"
                binding.btnAddToCart.isEnabled = true
            }, 1500)

            Snackbar.make(
                binding.root,
                "${product.name} adicionado ao carrinho!",
                Snackbar.LENGTH_SHORT
            ).setAction("Ver Carrinho") {
                AnalyticsManager.logViewCart(CartService.currentItems)
                findNavController().navigate(
                    R.id.action_productFragment_to_cartFragment
                )
            }.show()
        }

        binding.btnWishlist.setOnClickListener {
            val product = viewModel.product.value ?: return@setOnClickListener
            viewModel.toggleWishlist()

            val isNowWishlisted = viewModel.isWishlisted.value ?: false
            if (isNowWishlisted) {
                // ✅ add_to_wishlist
                AnalyticsManager.logAddToWishlist(product)
                Snackbar.make(binding.root, "❤️ Adicionado aos favoritos!", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnShare.setOnClickListener {
            val product = viewModel.product.value ?: return@setOnClickListener

            // ✅ share
            AnalyticsManager.logShare(
                contentType = "product",
                itemId = product.id,
                method = "android_share_sheet"
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Confira: ${product.name} por ${currencyFormatter.format(product.price)} na TechStore!"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar produto"))
        }
    }
}
