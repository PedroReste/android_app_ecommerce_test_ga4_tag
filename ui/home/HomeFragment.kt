package com.example.ecommercetaggingapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommercetaggingapp.R
import com.example.ecommercetaggingapp.analytics.AnalyticsManager
import com.example.ecommercetaggingapp.databinding.FragmentHomeBinding
import com.example.ecommercetaggingapp.model.Product
import com.example.ecommercetaggingapp.service.CartService
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    // ─────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupPromotionBanner()
        observeViewModel()
        updateCartBadge()
    }

    override fun onResume() {
        super.onResume()

        // ✅ screen_view
        AnalyticsManager.logScreenView(
            screenName = "Home",
            screenClass = "HomeFragment"
        )

        // ✅ view_item_list
        AnalyticsManager.logViewItemList(
            items = Product.mockProducts,
            listId = viewModel.listId,
            listName = viewModel.listName
        )

        // ✅ view_promotion — banner visível
        AnalyticsManager.logViewPromotion(
            promotionId = "PROMO-SALE-30",
            promotionName = "Super Sale 30% OFF",
            creativeName = "banner_home_sale",
            creativeSlot = "home_top_banner",
            items = Product.mockProducts.take(3)
        )

        updateCartBadge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ─────────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product, index ->
                // ✅ select_item
                AnalyticsManager.logSelectItem(
                    product = product,
                    index = index,
                    listId = viewModel.listId,
                    listName = viewModel.listName
                )

                // ✅ view_item (antecipado)
                AnalyticsManager.logViewItem(product)

                // Navegar para detalhes
                val bundle = bundleOf("productId" to product.id)
                findNavController().navigate(
                    R.id.action_homeFragment_to_productFragment,
                    bundle
                )
            },
            onAddToCart = { product ->
                CartService.addProduct(product)

                // ✅ add_to_cart
                AnalyticsManager.logAddToCart(product, quantity = 1)

                updateCartBadge()
                showSnackbar("✅ ${product.name} adicionado ao carrinho!")
            }
        )

        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.takeIf { it.isNotBlank() }?.let { term ->
                    // ✅ search
                    AnalyticsManager.logSearch(searchTerm = term)
                    viewModel.search(term)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: "")
                return true
            }
        })
    }

    private fun setupPromotionBanner() {
        binding.btnPromotionCta.setOnClickListener {
            // ✅ select_promotion
            AnalyticsManager.logSelectPromotion(
                promotionId = "PROMO-SALE-30",
                promotionName = "Super Sale 30% OFF",
                creativeName = "banner_home_sale",
                creativeSlot = "home_top_banner",
                items = Product.mockProducts.take(3)
            )
            showSnackbar("🔥 Super Sale! Até 30% OFF em produtos selecionados!")
        }

        binding.fabCart.setOnClickListener {
            // ✅ view_cart
            AnalyticsManager.logViewCart(CartService.currentItems)
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            binding.tvEmptySearch.visibility =
                if (products.isEmpty()) View.VISIBLE else View.GONE
        }

        CartService.items.observe(viewLifecycleOwner) {
            updateCartBadge()
        }
    }

    private fun updateCartBadge() {
        val count = CartService.totalItems
        binding.tvCartBadge.apply {
            text = count.toString()
            visibility = if (count > 0) View.VISIBLE else View.GONE
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
