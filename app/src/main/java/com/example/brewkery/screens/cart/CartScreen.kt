package com.example.brewkery.screens.cart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.data.model.CartItem
import com.example.brewkery.data.model.CartState
import com.example.brewkery.domain.PriceCalculator
import com.example.brewkery.ui.components.BrewkeryButton
import com.example.brewkery.ui.components.CircularBackButton
import com.example.brewkery.ui.components.LocalToastState
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Crema
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.MutedBrown
import com.example.brewkery.ui.theme.RoseAccent
import com.example.brewkery.ui.theme.Terracotta
import com.example.brewkery.ui.theme.WarmBorder

// Screen 3: Cart review, itemized customization, bill breakdown, and checkout
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onExploreMenuClick: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val toastState = LocalToastState.current
    val cartState by viewModel.cartState.collectAsState()

    CartContent(
        cartState = cartState,
        onBackClick = onBackClick,
        onClearCart = {
            if (cartState.items.isNotEmpty()) {
                viewModel.clearCart()
                toastState.show("Cart cleared")
            }
        },
        onIncrement = { viewModel.incrementQuantity(it) },
        onDecrement = { viewModel.decrementQuantity(it) },
        onExploreMenuClick = onExploreMenuClick,
        onPlaceOrderClick = { viewModel.placeOrder(onOrderPlaced) }
    )
}

@Composable
fun CartContent(
    cartState: CartState,
    onBackClick: () -> Unit,
    onClearCart: () -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onPlaceOrderClick: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onExploreMenuClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Crema,
        topBar = {
            TopCartAppBar(
                onBackClick = onBackClick,
                onClearCart = onClearCart
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Crema)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BillSummaryCard(cartState = cartState)

                BrewkeryButton(
                    text = "Place Order Now  •  ${PriceCalculator.formatCurrency(cartState.total)}",
                    icon = Icons.Default.Lock,
                    onClick = {
                        if (cartState.items.isNotEmpty()) {
                            onPlaceOrderClick()
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (cartState.items.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    EmptyCartCard()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartState.items, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrement = { onIncrement(item.id) },
                            onDecrement = { onDecrement(item.id) }
                        )
                    }
                }
            }
        }
    }
}

// Card displaying empty cart message
@Composable
private fun EmptyCartCard(modifier: Modifier = Modifier) {
    val cardShape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, WarmBorder, cardShape)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = "Empty Cart",
            tint = Color(0xFFE8D5C8),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Your in-memory cart is empty.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MutedBrown
        )
    }
}

// Receipt-style cart item row with compact vertical padding
@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val cardShape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, WarmBorder, cardShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Espresso
            )
            Spacer(modifier = Modifier.height(1.dp))
            val optionsText = buildString {
                append(item.sizeLabel)
                if (item.milkName.isNotEmpty()) {
                    append(" • ")
                    append(item.milkName)
                }
            }
            Text(
                text = optionsText,
                fontSize = 11.sp,
                color = MutedBrown
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = PriceCalculator.formatCurrency(item.lineTotal),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Terracotta
            )
        }

        // Stepper pill with warm tint
        val stepperShape = RoundedCornerShape(8.dp)
        Row(
            modifier = Modifier
                .clip(stepperShape)
                .background(Color(0xFFFBF4EE))
                .border(1.dp, WarmBorder.copy(alpha = 0.6f), stepperShape)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "-",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                modifier = Modifier
                    .clickable(onClick = onDecrement)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            Text(
                text = item.quantity.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "+",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso,
                modifier = Modifier
                    .clickable(onClick = onIncrement)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// Bill calculation card
@Composable
private fun BillSummaryCard(cartState: CartState) {
    val cardShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, WarmBorder, cardShape)
            .padding(12.dp)
    ) {
        BillLine(
            label = "Subtotal",
            value = PriceCalculator.formatCurrency(cartState.subtotal)
        )
        Spacer(modifier = Modifier.height(6.dp))
        val deliveryFeeToDisplay = if (cartState.items.isEmpty()) PriceCalculator.FLAT_DELIVERY_FEE else cartState.deliveryFee
        BillLine(
            label = "Delivery Fee",
            value = PriceCalculator.formatCurrency(deliveryFeeToDisplay)
        )
        Spacer(modifier = Modifier.height(6.dp))
        BillLine(
            label = "Est. Tax (8.0%)",
            value = PriceCalculator.formatCurrency(cartState.tax)
        )

        Spacer(modifier = Modifier.height(8.dp))
        DashedDivider(color = WarmBorder)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Payable",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Espresso
            )
            Text(
                text = PriceCalculator.formatCurrency(cartState.total),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Terracotta
            )
        }
    }
}

@Composable
private fun BillLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MutedBrown
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Espresso
        )
    }
}

// Dashed horizontal line divider
@Composable
private fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = WarmBorder,
    strokeWidth: Dp = 1.dp,
    dashWidth: Dp = 6.dp,
    gapWidth: Dp = 4.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
    ) {
        val strokeWidthPx = strokeWidth.toPx()
        val dashWidthPx = dashWidth.toPx()
        val gapWidthPx = gapWidth.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidthPx, gapWidthPx), 0f)

        drawLine(
            color = color,
            start = Offset(0f, strokeWidthPx / 2),
            end = Offset(size.width, strokeWidthPx / 2),
            strokeWidth = strokeWidthPx,
            pathEffect = pathEffect
        )
    }
}

// Top app bar with back and Clear Cart action
@Composable
private fun TopCartAppBar(
    onBackClick: () -> Unit,
    onClearCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircularBackButton(onBackClick = onBackClick)

        Text(
            text = "YOUR CART",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Espresso
        )

        Text(
            text = "Clear Cart",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = RoseAccent,
            modifier = Modifier.clickable(onClick = onClearCart)
        )
    }
}

@Preview(showBackground = true, name = "Empty Cart Screen Preview")
@Composable
fun EmptyCartScreenPreview() {
    BrewkeryTheme {
        CartContent(
            cartState = CartState(),
            onBackClick = {},
            onClearCart = {},
            onIncrement = {},
            onDecrement = {},
            onExploreMenuClick = {},
            onPlaceOrderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Filled Cart Screen Preview")
@Composable
fun FilledCartScreenPreview() {
    val items = listOf(
        CartItem("1", 1, "Caramel Macchiato", "", "Grande (12 oz)", "Oat Milk (Barista Blend)", "50% Mild", 4.85, 0.65, 1),
        CartItem("2", 3, "Normandy Croissant", "", "Single Piece", "Classic (No Spread)", "Warm & Crisp", 3.90, 0.0, 1),
        CartItem("3", 2, "Toasted Caramel Macchiato", "", "Tall (8 oz)", "Oat Milk (Barista Blend)", "Normal", 4.85, 0.0, 1)
    )
    val state = PriceCalculator.calculateCartState(items)

    BrewkeryTheme {
        CartContent(
            cartState = state,
            onBackClick = {},
            onClearCart = {},
            onIncrement = {},
            onDecrement = {},
            onExploreMenuClick = {},
            onPlaceOrderClick = {}
        )
    }
}
