package com.example.ecommercetaggingapp.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercetaggingapp.R
import com.example.ecommercetaggingapp.analytics.AnalyticsManager
import com.example.ecommercetaggingapp.databinding.FragmentCartBinding
import com.example.ecommercetaggingapp.model.CartItem
import com.example.ecommercetaggingapp.service.CartService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    // ─────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCouponField()
        setupCheckoutButton()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        // ✅ screen_view
        AnalyticsManager.logScreenView(
            screenName = "Cart",
            screenClass = "CartFragment"
        )

        // ✅ view_cart
        AnalyticsManager.logViewCart(CartService.currentItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ─────────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityDecrease = { cartItem ->
                val oldQty = cartItem.quantity
                val newQty = oldQty - 1

                if (newQty <= 0) {
                    showRemoveConfirmDialog(cartItem)
                } else {
                    // ✅ remove_from_cart (redução de quantidade)
                    AnalyticsManager.logRemoveFromCart(
                        product = cartItem.product,
                        quantity = 1
                    )
                    viewModel.updateQuantity(cartItem, newQty)
                }
            },
            onQuantityIncrease = { cartItem ->
                val newQty = cartItem.quantity + 1

                // ✅ add_to_cart (aumento de quantidade)
                AnalyticsManager.logAddToCart(
                    product = cartItem.product,
                    quantity = 1
                )
                viewModel.updateQuantity(cartItem, newQty)
            },
            onRemove = { cartItem ->
                showRemoveConfirmDialog(cartItem)
            }
        )

        binding.rvCartItems.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
    }

    private fun setupCouponField() {
        binding.etCoupon.doAfterTextChanged { text ->
            viewModel.setCoupon(text?.toString())
        }
    }

    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            val items = CartService.currentItems
            if (items.isEmpty()) return@setOnClickListener

            val coupon = viewModel.coupon.value

            // ✅ add_shipping_info
            AnalyticsManager.logAddShippingInfo(
                cartItems = items,
                shippingTier = "standard",
                coupon = coupon
            )

            // ✅ add_payment_info
            AnalyticsManager.logAddPaymentInfo(
                cartItems = items,
                paymentType = "credit_card",
                coupon = coupon
            )

            // ✅ begin_checkout
            AnalyticsManager.logBeginCheckout(
                cartItems = items,
                coupon = coupon
            )

            // Criar pedido e navegar para sucesso
            val order = viewModel.buildOrder()
            val bundle = bundleOf("orderId" to order.id)

            // Salvar pedido temporariamente no OrderRepository
            OrderRepository.saveOrder(order)

            findNavController().navigate(
                R.id.action_cartFragment_to_successFragment,
                bundle
            )
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Observers
    // ─────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items.toList())
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { empty ->
            binding.groupCartContent.isVisible = !empty
            binding.layoutEmptyCart.isVisible = empty
        }

        viewModel.subtotal.observe(viewLifecycleOwner) { value ->
            binding.tvSubtotalValue.text = currencyFormatter.format(value)
        }

        viewModel.shipping.observe(viewLifecycleOwner) { value ->
            if (value == 0.0) {
                binding.tvShippingValue.text = "Grátis ✅"
                binding.tvShippingValue.setTextColor(
                    requireContext().getColor(R.color.green_success)
                )
            } else {
                binding.tvShippingValue.text = currencyFormatter.format(value)
                binding.tvShippingValue.setTextColor(
                    requireContext().getColor(R.color.white)
                )
            }
        }

        viewModel.tax.observe(viewLifecycleOwner) { value ->
            binding.tvTaxValue.text = currencyFormatter.format(value)
        }

        viewModel.total.observe(viewLifecycleOwner) { value ->
            binding.tvTotalValue.text = currencyFormatter.format(value)
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private fun showRemoveConfirmDialog(cartItem: CartItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remover produto")
            .setMessage("Deseja remover \"${cartItem.product.name}\" do carrinho?")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Remover") { _, _ ->
                // ✅ remove_from_cart
                AnalyticsManager.logRemoveFromCart(
                    product = cartItem.product,
                    quantity = cartItem.quantity
                )
                viewModel.removeProduct(cartItem)

                Snackbar.make(
                    binding.root,
                    "${cartItem.product.name} removido",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}
