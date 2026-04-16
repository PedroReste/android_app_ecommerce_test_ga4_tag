package com.example.ecommercetaggingapp.ui.success

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercetaggingapp.R
import com.example.ecommercetaggingapp.analytics.AnalyticsManager
import com.example.ecommercetaggingapp.databinding.FragmentSuccessBinding
import com.example.ecommercetaggingapp.model.Order
import com.example.ecommercetaggingapp.service.CartService
import com.example.ecommercetaggingapp.service.OrderRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.util.Locale

class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SuccessViewModel by viewModels()
    private lateinit var orderItemAdapter: OrderItemAdapter
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    // ─────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = arguments?.getString("orderId") ?: return
        viewModel.loadOrder(orderId)

        setupRecyclerView()
        observeViewModel()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()

        // ✅ screen_view
        AnalyticsManager.logScreenView(
            screenName = "Purchase Success",
            screenClass = "SuccessFragment"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ─────────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        binding.rvOrderItems.apply {
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        viewModel.order.observe(viewLifecycleOwner) { order ->
            order ?: return@observe

            bindOrder(order)

            // ✅ purchase — dispara apenas uma vez via ViewModel
            if (viewModel.shouldLogPurchase()) {
                AnalyticsManager.logPurchase(order)
                CartService.clearCart()
                OrderRepository.clearOrder(order.id)
                playSuccessAnimation()
            }
        }
    }

    private fun bindOrder(order: Order) {
        binding.apply {
            tvOrderId.text = order.id
            tvAffiliation.text = order.affiliation

            tvSubtotalValue.text = currencyFormatter.format(order.subtotal)

            if (order.shipping == 0.0) {
                tvShippingValue.text = "Grátis ✅"
                tvShippingValue.setTextColor(
                    requireContext().getColor(R.color.green_success)
                )
            } else {
                tvShippingValue.text = currencyFormatter.format(order.shipping)
            }

            tvTaxValue.text = currencyFormatter.format(order.tax)
            tvTotalValue.text = currencyFormatter.format(order.total)

            if (!order.coupon.isNullOrBlank()) {
                tvCouponApplied.visibility = View.VISIBLE
                tvCouponApplied.text = "🏷️ Cupom aplicado: ${order.coupon}"
            } else {
                tvCouponApplied.visibility = View.GONE
            }

            orderItemAdapter.submitList(order.items)
        }
    }

    private fun setupButtons() {
        // ✅ Continuar comprando → select_content
        binding.btnContinueShopping.setOnClickListener {
            AnalyticsManager.logSelectContent(
                contentType = "navigation",
                itemId = "continue_shopping"
            )
            findNavController().navigate(
                R.id.action_successFragment_to_homeFragment
            )
        }

        // ✅ Compartilhar pedido → share
        binding.btnShareOrder.setOnClickListener {
            val order = viewModel.order.value ?: return@setOnClickListener

            AnalyticsManager.logShare(
                contentType = "order",
                itemId = order.id,
                method = "android_share_sheet"
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    buildString {
                        append("🛍️ Acabei de comprar na TechStore!\n")
                        append("Pedido: ${order.id}\n")
                        append("Total: ${currencyFormatter.format(order.total)}")
                    }
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar pedido"))
        }

        // ✅ Solicitar reembolso → refund
        binding.btnRefund.setOnClickListener {
            val order = viewModel.order.value ?: return@setOnClickListener

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Solicitar Reembolso")
                .setMessage(
                    "Deseja solicitar reembolso do pedido ${order.id}?\n" +
                    "Total: ${currencyFormatter.format(order.total)}"
                )
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Confirmar") { _, _ ->
                    // ✅ refund
                    AnalyticsManager.logRefund(order)

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("✅ Reembolso Solicitado")
                        .setMessage(
                            "Seu reembolso será processado em até 5 dias úteis."
                        )
                        .setPositiveButton("OK") { d, _ -> d.dismiss() }
                        .show()
                }
                .show()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Animation
    // ─────────────────────────────────────────────────────────────

    private fun playSuccessAnimation() {
        binding.ivSuccessIcon.apply {
            alpha = 0f
            scaleX = 0.3f
            scaleY = 0.3f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setInterpolator(
                    android.view.animation.OvershootInterpolator(1.5f)
                )
                .start()
        }

        binding.tvSuccessTitle.apply {
            alpha = 0f
            translationY = 40f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(300)
                .start()
        }

        binding.tvSuccessSubtitle.apply {
            alpha = 0f
            translationY = 40f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(450)
                .start()
        }
    }
}
