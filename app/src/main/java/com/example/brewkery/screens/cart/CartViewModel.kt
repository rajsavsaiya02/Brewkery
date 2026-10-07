package com.example.brewkery.screens.cart

import androidx.lifecycle.ViewModel
import com.example.brewkery.data.model.ActiveOrder
import com.example.brewkery.data.model.CartState
import com.example.brewkery.data.repository.CartRepository
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    val cartState: StateFlow<CartState> = cartRepository.cartState
    val activeOrder: StateFlow<ActiveOrder?> = cartRepository.activeOrder

    fun incrementQuantity(cartItemId: String) {
        cartRepository.updateQuantity(cartItemId, 1)
    }

    fun decrementQuantity(cartItemId: String) {
        cartRepository.updateQuantity(cartItemId, -1)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }

    fun placeOrder(onOrderPlaced: (String) -> Unit) {
        if (cartState.value.items.isNotEmpty()) {
            val order = cartRepository.placeOrder()
            onOrderPlaced(order.orderId)
        }
    }
}
