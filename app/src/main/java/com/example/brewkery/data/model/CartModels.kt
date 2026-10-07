package com.example.brewkery.data.model

data class CartItem(
    val id: String,
    val itemId: Int,
    val name: String,
    val imageUrl: String,
    val sizeLabel: String,
    val milkName: String,
    val sugarLevel: String,
    val basePrice: Double,
    val extraPrice: Double,
    val quantity: Int
) {
    val unitPrice: Double get() = basePrice + extraPrice
    val lineTotal: Double get() = unitPrice * quantity
}

data class CartState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val totalQuantity: Int = 0
)

data class ActiveOrder(
    val orderId: String,
    val estimatedWait: String = "20 - 30 minutes",
    val itemsCount: Int,
    val totalAmount: Double,
    val status: String = "PREPARING"
)
