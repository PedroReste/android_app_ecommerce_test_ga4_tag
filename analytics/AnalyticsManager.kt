package com.example.ecommercetaggingapp.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.ecommercetaggingapp.model.CartItem
import com.example.ecommercetaggingapp.model.Order
import com.example.ecommercetaggingapp.model.Product
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/** * AnalyticsManager * * Singleton que centraliza todos os eventos GA4 recomendados: * - Eventos Gerais: screen_view, login, sign_up, search, share, * select_content, generate_lead, tutorial_begin, * tutorial_complete * - Eventos Ecommerce: view_item_list, select_item, view_item, * add_to_cart, remove_from_cart, view_cart, * add_to_wishlist, begin_checkout, add_shipping_info, * add_payment_info, purchase, refund, * view_promotion, select_promotion */
object AnalyticsManager {

    private const val TAG = "AnalyticsManager"
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context) {
        firebaseAnalytics = Firebase.analytics
    }

    // ─────────────────────────────────────────────────────────────
    // MARK: Debug helper
    // ─────────────────────────────────────────────────────────────

    private fun log(eventName: String, params: Bundle?) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "📊 Event: $eventName")
            params?.keySet()?.forEach { key ->
                Log.d(TAG, " └─ $key: ${params.get(key)}")
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // MARK: EVENTOS GERAIS RECOMENDADOS (GA4)
    // ─────────────────────────────────────────────────────────────

    /** * screen_view * Dispara quando o usuário visualiza uma tela. */
    fun logScreenView(screenName: String, screenClass: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        log(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    /** * login * Dispara quando o usuário faz login. */
    fun logLogin(method: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
        log(FirebaseAnalytics.Event.LOGIN, params)
    }

    /** * sign_up * Dispara quando o usuário se cadastra. */
    fun logSignUp(method: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params)
        log(FirebaseAnalytics.Event.SIGN_UP, params)
    }

    /** * search * Dispara quando o usuário realiza uma busca. */
    fun logSearch(searchTerm: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, params)
        log(FirebaseAnalytics.Event.SEARCH, params)
    }

    /** * share * Dispara quando o usuário compartilha conteúdo. */
    fun logShare(contentType: String, itemId: String, method: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params)
        log(FirebaseAnalytics.Event.SHARE, params)
    }

    /** * select_content * Dispara quando o usuário seleciona um conteúdo específico. */
    fun logSelectContent(contentType: String, itemId: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
            putString(FirebaseAnalytics.Param.ITEM_ID, itemId)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
        log(FirebaseAnalytics.Event.SELECT_CONTENT, params)
    }

    /** * generate_lead * Dispara quando um lead é gerado. */
    fun logGenerateLead(currency: String = "BRL", value: Double) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putDouble(FirebaseAnalytics.Param.VALUE, value)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.GENERATE_LEAD, params)
        log(FirebaseAnalytics.Event.GENERATE_LEAD, params)
    }

    /** * tutorial_begin * Dispara quando o usuário inicia um tutorial. */
    fun logTutorialBegin() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
        log(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    /** * tutorial_complete * Dispara quando o usuário conclui um tutorial. */
    fun logTutorialComplete() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
        log(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
    }

    // ─────────────────────────────────────────────────────────────
    // MARK: EVENTOS DE ECOMMERCE (GA4)
    // ─────────────────────────────────────────────────────────────

    /** * view_item_list * Dispara quando uma lista de produtos é exibida ao usuário. */
    fun logViewItemList( items: List<Product>, listId: String, listName: String ) {
        val itemsArray = items.mapIndexed { index, product ->
            product.toAnalyticsBundle(
                index = index,
                listId = listId,
                listName = listName
            )
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_LIST_ID, listId)
            putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, listName)
            putParcelableArray(FirebaseAnalytics.Param.ITEMS, itemsArray.toTypedArray())
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, params)
        log(FirebaseAnalytics.Event.VIEW_ITEM_LIST, params)
    }

    /** * select_item * Dispara quando o usuário seleciona um produto de uma lista. */
    fun logSelectItem( product: Product, index: Int, listId: String, listName: String ) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_LIST_ID, listId)
            putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, listName)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(
                    product.toAnalyticsBundle(
                        index = index,
                        listId = listId,
                        listName = listName
                    )
                )
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
        log(FirebaseAnalytics.Event.SELECT_ITEM, params)
    }

    /** * view_item * Dispara quando o usuário visualiza os detalhes de um produto. */
    fun logViewItem(product: Product) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, product.price)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(product.toAnalyticsBundle())
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
        log(FirebaseAnalytics.Event.VIEW_ITEM, params)
    }

    /** * add_to_cart * Dispara quando um produto é adicionado ao carrinho. */
    fun logAddToCart(product: Product, quantity: Int) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, product.price * quantity)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(product.toAnalyticsBundle(quantity = quantity))
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, params)
        log(FirebaseAnalytics.Event.ADD_TO_CART, params)
    }

    /** * remove_from_cart * Dispara quando um produto é removido do carrinho. */
    fun logRemoveFromCart(product: Product, quantity: Int) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, product.price * quantity)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(product.toAnalyticsBundle(quantity = quantity))
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, params)
        log(FirebaseAnalytics.Event.REMOVE_FROM_CART, params)
    }

    /** * view_cart * Dispara quando o usuário visualiza o carrinho. */
    fun logViewCart(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        val itemsArray = cartItems.map {
            it.product.toAnalyticsBundle(quantity = it.quantity)
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, total)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_CART, params)
        log(FirebaseAnalytics.Event.VIEW_CART, params)
    }

    /** * add_to_wishlist * Dispara quando um produto é adicionado à lista de desejos. */
    fun logAddToWishlist(product: Product) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, product.price)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(product.toAnalyticsBundle())
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, params)
        log(FirebaseAnalytics.Event.ADD_TO_WISHLIST, params)
    }

    /** * begin_checkout * Dispara quando o usuário inicia o processo de checkout. */
    fun logBeginCheckout(cartItems: List<CartItem>, coupon: String? = null) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        val itemsArray = cartItems.map {
            it.product.toAnalyticsBundle(quantity = it.quantity)
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, total)
            coupon?.let { putString(FirebaseAnalytics.Param.COUPON, it) }
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, params)
        log(FirebaseAnalytics.Event.BEGIN_CHECKOUT, params)
    }

    /** * add_payment_info * Dispara quando o usuário adiciona informações de pagamento. */
    fun logAddPaymentInfo( cartItems: List<CartItem>, paymentType: String, coupon: String? = null ) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        val itemsArray = cartItems.map {
            it.product.toAnalyticsBundle(quantity = it.quantity)
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, total)
            putString(FirebaseAnalytics.Param.PAYMENT_TYPE, paymentType)
            coupon?.let { putString(FirebaseAnalytics.Param.COUPON, it) }
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, params)
        log(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, params)
    }

    /** * add_shipping_info * Dispara quando o usuário adiciona informações de entrega. */
    fun logAddShippingInfo( cartItems: List<CartItem>, shippingTier: String, coupon: String? = null ) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        val itemsArray = cartItems.map {
            it.product.toAnalyticsBundle(quantity = it.quantity)
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, total)
            putString(FirebaseAnalytics.Param.SHIPPING_TIER, shippingTier)
            coupon?.let { putString(FirebaseAnalytics.Param.COUPON, it) }
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_SHIPPING_INFO, params)
        log(FirebaseAnalytics.Event.ADD_SHIPPING_INFO, params)
    }

    /** * purchase * Dispara quando a compra é finalizada com sucesso. */
    fun logPurchase(order: Order) {
        val itemsArray = order.items.map {
            it.product.toAnalyticsBundle(quantity = it.quantity)
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, order.id)
            putString(FirebaseAnalytics.Param.AFFILIATION, order.affiliation)
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, order.total)
            putDouble(FirebaseAnalytics.Param.TAX, order.tax)
            putDouble(FirebaseAnalytics.Param.SHIPPING, order.shipping)
            order.coupon?.let { putString(FirebaseAnalytics.Param.COUPON, it) }
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, params)
        log(FirebaseAnalytics.Event.PURCHASE, params)
    }

    /** * refund * Dispara quando uma compra é reembolsada (total ou parcial). */
    fun logRefund(order: Order, partialItems: List<CartItem>? = null) {
        val items = partialItems ?: order.items
        val itemsArray = items.map {
            it.product.toAnalyticsBundle(quantity = it.quantity)
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, order.id)
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
            putDouble(FirebaseAnalytics.Param.VALUE, order.total)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REFUND, params)
        log(FirebaseAnalytics.Event.REFUND, params)
    }

    /** * view_promotion * Dispara quando uma promoção é exibida ao usuário. */
    fun logViewPromotion( promotionId: String, promotionName: String, creativeName: String, creativeSlot: String, items: List<Product> ) {
        val itemsArray = items.map {
            it.toAnalyticsBundle(
                promotionId = promotionId,
                promotionName = promotionName,
                creativeName = creativeName,
                creativeSlot = creativeSlot
            )
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.PROMOTION_ID, promotionId)
            putString(FirebaseAnalytics.Param.PROMOTION_NAME, promotionName)
            putString(FirebaseAnalytics.Param.CREATIVE_NAME, creativeName)
            putString(FirebaseAnalytics.Param.CREATIVE_SLOT, creativeSlot)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_PROMOTION, params)
        log(FirebaseAnalytics.Event.VIEW_PROMOTION, params)
    }

    /** * select_promotion * Dispara quando o usuário clica/seleciona uma promoção. */
    fun logSelectPromotion( promotionId: String, promotionName: String, creativeName: String, creativeSlot: String, items: List<Product> ) {
        val itemsArray = items.map {
            it.toAnalyticsBundle(
                promotionId = promotionId,
                promotionName = promotionName,
                creativeName = creativeName,
                creativeSlot = creativeSlot
            )
        }
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.PROMOTION_ID, promotionId)
            putString(FirebaseAnalytics.Param.PROMOTION_NAME, promotionName)
            putString(FirebaseAnalytics.Param.CREATIVE_NAME, creativeName)
            putString(FirebaseAnalytics.Param.CREATIVE_SLOT, creativeSlot)
            putParcelableArray(
                FirebaseAnalytics.Param.ITEMS,
                itemsArray.toTypedArray()
            )
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_PROMOTION, params)
        log(FirebaseAnalytics.Event.SELECT_PROMOTION, params)
    }
}
