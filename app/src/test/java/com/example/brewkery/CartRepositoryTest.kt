package com.example.brewkery

import com.example.brewkery.data.model.CartItem
import com.example.brewkery.data.repository.CartRepositoryImpl
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {

    private lateinit var cartRepository: CartRepositoryImpl

    @Before
    fun setUp() {
        cartRepository = CartRepositoryImpl()
    }

    @Test
    fun `addToCart adds new item to cartState`() {
        val item = CartItem(
            id = "item-1",
            itemId = 1,
            name = "Cappuccino",
            imageUrl = "",
            sizeLabel = "Medium",
            milkName = "Oat",
            sugarLevel = "Standard",
            basePrice = 4.50,
            extraPrice = 0.50,
            quantity = 1
        )

        cartRepository.addToCart(item)

        val state = cartRepository.cartState.value
        assertThat(state.items).hasSize(1)
        assertThat(state.totalQuantity).isEqualTo(1)
        assertThat(state.subtotal).isEqualTo(5.00)
    }

    @Test
    fun `addToCart merges duplicate item by incrementing quantity`() {
        val item = CartItem(
            id = "item-1",
            itemId = 1,
            name = "Cappuccino",
            imageUrl = "",
            sizeLabel = "Medium",
            milkName = "Oat",
            sugarLevel = "Standard",
            basePrice = 4.50,
            extraPrice = 0.50,
            quantity = 1
        )

        cartRepository.addToCart(item)
        cartRepository.addToCart(item)

        val state = cartRepository.cartState.value
        assertThat(state.items).hasSize(1)
        assertThat(state.items.first().quantity).isEqualTo(2)
        assertThat(state.totalQuantity).isEqualTo(2)
        assertThat(state.subtotal).isEqualTo(10.00)
    }

    @Test
    fun `updateQuantity removes item when quantity reaches zero`() {
        val item = CartItem(
            id = "item-1",
            itemId = 1,
            name = "Americano",
            imageUrl = "",
            sizeLabel = "Small",
            milkName = "None",
            sugarLevel = "None",
            basePrice = 3.00,
            extraPrice = 0.00,
            quantity = 1
        )

        cartRepository.addToCart(item)
        cartRepository.updateQuantity("item-1", -1)

        val state = cartRepository.cartState.value
        assertThat(state.items).isEmpty()
        assertThat(state.totalQuantity).isEqualTo(0)
    }

    @Test
    fun `placeOrder generates active order and clears cart`() {
        val item = CartItem(
            id = "item-1",
            itemId = 1,
            name = "Cold Brew",
            imageUrl = "",
            sizeLabel = "Large",
            milkName = "None",
            sugarLevel = "None",
            basePrice = 5.00,
            extraPrice = 0.00,
            quantity = 2
        )

        cartRepository.addToCart(item)
        val activeOrder = cartRepository.placeOrder()

        assertThat(activeOrder.orderId).startsWith("#BK-")
        assertThat(activeOrder.itemsCount).isEqualTo(2)
        assertThat(cartRepository.cartState.value.items).isEmpty()
        assertThat(cartRepository.activeOrder.value).isEqualTo(activeOrder)
    }
}
