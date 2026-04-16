package com.example.ecommercetaggingapp.model

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val subcategory: String,
    val price: Double,
    val originalPrice: Double,
    val currency: String = "BRL",
    val iconName: String,           // Nome do Material Icon
    val description: String,
    val variant: String,
    val rating: Double,
    val reviewCount: Int,
    val isOnSale: Boolean
) {
    // Desconto calculado
    val discount: Double
        get() = if (originalPrice > price) {
            ((originalPrice - price) / originalPrice) * 100
        } else 0.0

    val discountValue: Double
        get() = if (originalPrice > price) originalPrice - price else 0.0

    /** * Converte o produto para Bundle GA4. * Inclui todos os parâmetros de item recomendados pelo GA4. */
    fun toAnalyticsBundle( quantity: Int = 1, index: Int? = null, listId: String? = null, listName: String? = null, promotionId: String? = null, promotionName: String? = null, creativeName: String? = null, creativeSlot: String? = null ): Bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.ITEM_ID, id)
        putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        putString(FirebaseAnalytics.Param.ITEM_BRAND, brand)
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, subcategory)
        putString(FirebaseAnalytics.Param.ITEM_VARIANT, variant)
        putDouble(FirebaseAnalytics.Param.PRICE, price)
        putString(FirebaseAnalytics.Param.CURRENCY, currency)
        putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
        putDouble(FirebaseAnalytics.Param.DISCOUNT, discountValue)

        index?.let { putInt(FirebaseAnalytics.Param.INDEX, it) }
        listId?.let { putString(FirebaseAnalytics.Param.ITEM_LIST_ID, it) }
        listName?.let { putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, it) }
        promotionId?.let { putString(FirebaseAnalytics.Param.PROMOTION_ID, it) }
        promotionName?.let { putString(FirebaseAnalytics.Param.PROMOTION_NAME, it) }
        creativeName?.let { putString(FirebaseAnalytics.Param.CREATIVE_NAME, it) }
        creativeSlot?.let { putString(FirebaseAnalytics.Param.CREATIVE_SLOT, it) }
    }

    companion object {
        val mockProducts = listOf(
            Product(
                id = "SKU-001",
                name = "Samsung Galaxy S24 Ultra",
                brand = "Samsung",
                category = "Eletrônicos",
                subcategory = "Smartphones",
                price = 6999.99,
                originalPrice = 7999.99,
                iconName = "smartphone",
                description = "O flagship definitivo da Samsung com câmera de 200MP e S Pen integrada. Tela Dynamic AMOLED 2X de 6.8 polegadas com 120Hz adaptativo e brilho de 2600 nits.",
                variant = "256GB / Titânio Preto",
                rating = 4.8,
                reviewCount = 3241,
                isOnSale = true
            ),
            Product(
                id = "SKU-002",
                name = "Notebook Dell XPS 15",
                brand = "Dell",
                category = "Eletrônicos",
                subcategory = "Notebooks",
                price = 11999.99,
                originalPrice = 11999.99,
                iconName = "laptop",
                description = "Notebook premium com tela OLED 3.5K de 15.6 polegadas. Intel Core i7 de 13ª geração com NVIDIA RTX 4060 e 32GB de RAM.",
                variant = "32GB RAM / 1TB SSD",
                rating = 4.6,
                reviewCount = 876,
                isOnSale = false
            ),
            Product(
                id = "SKU-003",
                name = "Sony WH-1000XM5",
                brand = "Sony",
                category = "Eletrônicos",
                subcategory = "Áudio",
                price = 1599.99,
                originalPrice = 1999.99,
                iconName = "headphones",
                description = "Headphone premium com melhor cancelamento de ruído do mercado. 30 horas de bateria, áudio Hi-Res e design ultra-leve de apenas 250g.",
                variant = "Preto",
                rating = 4.9,
                reviewCount = 8921,
                isOnSale = true
            ),
            Product(
                id = "SKU-004",
                name = "Google Pixel 9 Pro",
                brand = "Google",
                category = "Eletrônicos",
                subcategory = "Smartphones",
                price = 5499.99,
                originalPrice = 5999.99,
                iconName = "phone_android",
                description = "O smartphone com a melhor câmera computacional do mercado. Chip Tensor G4, 7 anos de atualizações garantidas e recursos exclusivos de IA.",
                variant = "128GB / Obsidian",
                rating = 4.7,
                reviewCount = 2156,
                isOnSale = true
            ),
            Product(
                id = "SKU-005",
                name = "iPad Pro M4 11\"",
                brand = "Apple",
                category = "Eletrônicos",
                subcategory = "Tablets",
                price = 9999.99,
                originalPrice = 9999.99,
                iconName = "tablet",
                description = "O tablet mais poderoso do mundo com chip M4 e tela OLED Ultra Retina XDR. Compatível com Apple Pencil Pro e Magic Keyboard Folio.",
                variant = "256GB / Wi-Fi / Preto Espacial",
                rating = 4.9,
                reviewCount = 1234,
                isOnSale = false
            ),
            Product(
                id = "SKU-006",
                name = "Samsung Galaxy Watch 7",
                brand = "Samsung",
                category = "Eletrônicos",
                subcategory = "Wearables",
                price = 1899.99,
                originalPrice = 2299.99,
                iconName = "watch",
                description = "Smartwatch com sensor BioActive avançado para monitoramento completo de saúde. Tela Super AMOLED de 1.3 polegadas com autonomia de 40 horas.",
                variant = "44mm / Cream",
                rating = 4.5,
                reviewCount = 1876,
                isOnSale = true
            ),
            Product(
                id = "SKU-007",
                name = "LG OLED C3 55\"",
                brand = "LG",
                category = "Eletrônicos",
                subcategory = "TVs",
                price = 4799.99,
                originalPrice = 5999.99,
                iconName = "tv",
                description = "TV OLED com qualidade de imagem perfeita e pretos absolutos. Processador α9 Gen6 AI, Dolby Vision IQ e compatível com todos os formatos HDR.",
                variant = "55 polegadas / 4K",
                rating = 4.8,
                reviewCount = 3456,
                isOnSale = true
            ),
            Product(
                id = "SKU-008",
                name = "JBL Flip 6",
                brand = "JBL",
                category = "Eletrônicos",
                subcategory = "Áudio",
                price = 699.99,
                originalPrice = 899.99,
                iconName = "speaker",
                description = "Caixa de som portátil com som potente e graves profundos. À prova d'água IP67, 12 horas de bateria e conexão PartyBoost para múltiplos alto-falantes.",
                variant = "Azul",
                rating = 4.7,
                reviewCount = 12543,
                isOnSale = true
            )
        )
    }
}
