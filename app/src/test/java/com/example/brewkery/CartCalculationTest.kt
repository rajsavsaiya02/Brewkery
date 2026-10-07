package com.example.brewkery

import com.example.brewkery.data.model.CartItem
import com.example.brewkery.domain.PriceCalculator
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CartCalculationTest {

    @Test
    fun `empty cart produces zeroed CartState`() {
        val state = PriceCalculator.calculateCartState(emptyList())

        assertThat(state.subtotal).isEqualTo(0.0)
        assertThat(state.deliveryFee).isEqualTo(0.0)
        assertThat(state.tax).isEqualTo(0.0)
        assertThat(state.total).isEqualTo(0.0)
        assertThat(state.totalQuantity).isEqualTo(0)
    }

    @Test
    fun `single customized item calculates correct totals`() {
        val item = CartItem(
            id = "item-1-large-oat",
            itemId = 1,
            name = "Espresso Roast",
            imageUrl = "",
            sizeLabel = "Large",
            milkName = "Oat Milk",
            sugarLevel = "50%",
            basePrice = 4.85,
            extraPrice = 1.15, // unitPrice = 6.00
            quantity = 1
        )

        val state = PriceCalculator.calculateCartState(listOf(item))

        assertThat(state.subtotal).isEqualTo(6.00)
        assertThat(state.deliveryFee).isEqualTo(2.50)
        assertThat(state.tax).isEqualTo(0.48) // 6.00 * 0.08 = 0.48
        assertThat(state.total).isEqualTo(8.98) // 6.00 + 2.50 + 0.48 = 8.98
        assertThat(state.totalQuantity).isEqualTo(1)
    }

    @Test
    fun `multi item multi quantity calculates correctly`() {
        val item1 = CartItem(
            id = "1", itemId = 1, name = "Latte", imageUrl = "",
            sizeLabel = "Medium", milkName = "Whole", sugarLevel = "Normal",
            basePrice = 5.00, extraPrice = 0.50, quantity = 2 // 5.50 * 2 = 11.00
        )
        val item2 = CartItem(
            id = "2", itemId = 2, name = "Croissant", imageUrl = "",
            sizeLabel = "Regular", milkName = "None", sugarLevel = "None",
            basePrice = 3.50, extraPrice = 0.00, quantity = 1 // 3.50
        )

        val state = PriceCalculator.calculateCartState(listOf(item1, item2))

        assertThat(state.subtotal).isEqualTo(14.50)
        assertThat(state.deliveryFee).isEqualTo(2.50)
        assertThat(state.tax).isEqualTo(1.16) // 14.50 * 0.08 = 1.16
        assertThat(state.total).isEqualTo(18.16)
        assertThat(state.totalQuantity).isEqualTo(3)
    }

    @Test
    fun `currency formatter formats double correctly`() {
        assertThat(PriceCalculator.formatCurrency(6.0)).isEqualTo("$6.00")
        assertThat(PriceCalculator.formatCurrency(18.16)).isEqualTo("$18.16")
    }
}
