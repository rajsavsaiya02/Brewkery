package com.example.brewkery.domain

import com.example.brewkery.data.model.CartItem
import com.example.brewkery.data.model.CartState
import java.util.Locale
import kotlin.math.round

object PriceCalculator {
    const val FLAT_DELIVERY_FEE = 2.50
    const val TAX_RATE = 0.08

    fun calculateCartState(items: List<CartItem>): CartState {
        if (items.isEmpty()) {
            return CartState()
        }

        val subtotal = roundToTwoDecimals(items.sumOf { it.lineTotal })
        val deliveryFee = FLAT_DELIVERY_FEE
        val tax = roundToTwoDecimals(subtotal * TAX_RATE)
        val total = roundToTwoDecimals(subtotal + deliveryFee + tax)
        val totalQuantity = items.sumOf { it.quantity }

        return CartState(
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            tax = tax,
            total = total,
            totalQuantity = totalQuantity
        )
    }

    fun formatCurrency(amount: Double): String {
        return String.format(Locale.US, "$%.2f", amount)
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }
}
