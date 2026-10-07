package com.example.brewkery.data.repository

import com.example.brewkery.data.model.ActiveOrder
import com.example.brewkery.data.model.CartItem
import com.example.brewkery.data.model.CartState
import com.example.brewkery.domain.PriceCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

interface CartRepository {
    val cartState: StateFlow<CartState>
    val activeOrder: StateFlow<ActiveOrder?>

    fun addToCart(item: CartItem)
    fun updateQuantity(cartItemId: String, delta: Int)
    fun clearCart()
    fun placeOrder(): ActiveOrder
}

class CartRepositoryImpl : CartRepository {

    private val _cartState = MutableStateFlow(CartState())
    override val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    private val _activeOrder = MutableStateFlow<ActiveOrder?>(null)
    override val activeOrder: StateFlow<ActiveOrder?> = _activeOrder.asStateFlow()

    override fun addToCart(item: CartItem) {
        _cartState.update { currentState ->
            val existingIndex = currentState.items.indexOfFirst { it.id == item.id }
            val updatedItems = if (existingIndex >= 0) {
                currentState.items.mapIndexed { index, existingItem ->
                    if (index == existingIndex) {
                        existingItem.copy(quantity = existingItem.quantity + item.quantity)
                    } else {
                        existingItem
                    }
                }
            } else {
                currentState.items + item
            }
            PriceCalculator.calculateCartState(updatedItems)
        }
    }

    override fun updateQuantity(cartItemId: String, delta: Int) {
        _cartState.update { currentState ->
            val updatedItems = currentState.items.mapNotNull { existingItem ->
                if (existingItem.id == cartItemId) {
                    val newQty = existingItem.quantity + delta
                    if (newQty > 0) {
                        existingItem.copy(quantity = newQty)
                    } else {
                        null
                    }
                } else {
                    existingItem
                }
            }
            PriceCalculator.calculateCartState(updatedItems)
        }
    }

    override fun clearCart() {
        _cartState.value = CartState()
    }

    override fun placeOrder(): ActiveOrder {
        val currentCart = _cartState.value
        val orderNum = Random.nextInt(10000, 99999)
        val newOrder = ActiveOrder(
            orderId = "#BK-$orderNum",
            estimatedWait = "20 - 30 minutes",
            itemsCount = currentCart.totalQuantity,
            totalAmount = currentCart.total,
            status = "PREPARING"
        )
        _activeOrder.value = newOrder
        clearCart()
        return newOrder
    }
}
