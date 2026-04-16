# 🛍️ EcommerceTaggingApp — Android (Kotlin)

Protótipo nativo Android de e-commerce para validação de tagueamento
com **Firebase Analytics / GA4**.
Desenvolvido em Kotlin com arquitetura MVVM, Navigation Component e ViewBinding.

---

## 📱 Telas

| Tela | Fragment              | Descrição                                        |
|------|-----------------------|--------------------------------------------------|
| A    | HomeFragment          | Grid de produtos, banner promoção, busca         |
| B    | ProductFragment       | Detalhes do produto, wishlist, share, quantidade |
| C    | CartFragment          | Resumo do carrinho, cupom, checkout              |
| D    | SuccessFragment       | Confirmação da compra, reembolso, share          |

---

## 📊 Eventos Firebase Analytics Implementados

### Eventos Gerais

| Evento             | Tela  | Trigger                                   |
|--------------------|-------|-------------------------------------------|
| `screen_view`      | Todas | onResume() de cada Fragment               |
| `login`            | —     | AnalyticsManager disponível               |
| `sign_up`          | —     | AnalyticsManager disponível               |
| `search`           | A     | SearchView — submit da query              |
| `share`            | B, D  | Botão compartilhar → ShareSheet           |
| `select_content`   | D     | Botão "Continuar Comprando"               |
| `generate_lead`    | —     | AnalyticsManager disponível               |
| `tutorial_begin`   | —     | AnalyticsManager disponível               |
| `tutorial_complete`| —     | AnalyticsManager disponível               |

### Eventos de Ecommerce

| Evento               | Tela    | Trigger                                      |
|----------------------|---------|----------------------------------------------|
| `view_item_list`     | A       | onResume — lista de produtos visível         |
| `select_item`        | A       | Tap no card do produto                       |
| `view_item`          | A, B    | Tap no card (A) + onResume (B)               |
| `add_to_cart`        | A, B, C | Botão "Adicionar" / aumento de quantidade    |
| `remove_from_cart`   | C       | Dialog confirmar / botão diminuir qty → 0    |
| `view_cart`          | A, C    | FAB do carrinho (A) / onResume (C)           |
| `add_to_wishlist`    | B       | Botão coração                                |
| `begin_checkout`     | C       | Botão "Finalizar Compra"                     |
| `add_shipping_info`  | C       | Botão "Finalizar Compra"                     |
| `add_payment_info`   | C       | Botão "Finalizar Compra"                     |
| `purchase`           | D       | onViewCreated — uma única vez via ViewModel  |
| `refund`             | D       | Botão "Solicitar Reembolso" → confirmação    |
| `view_promotion`     | A       | onResume — banner visível                    |
| `select_promotion`   | A       | Tap no botão "Ver Ofertas" do banner         |

---

## 🏗️ Arquitetura
