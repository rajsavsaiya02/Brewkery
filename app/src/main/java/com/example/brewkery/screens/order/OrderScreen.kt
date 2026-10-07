package com.example.brewkery.screens.order

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Crema
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.MutedBrown
import com.example.brewkery.ui.theme.SuccessEmerald
import com.example.brewkery.ui.theme.Terracotta
import com.example.brewkery.ui.theme.WarmBorder

// Screen 4: Order status with dispatched avatar, #BK ticket, and return to menu
@Composable
fun OrderScreen(
    orderId: String,
    itemsCount: Int? = null,
    onBackToMenuClick: () -> Unit
) {
    val displayOrderId = remember(orderId) {
        if (orderId.isNotBlank() && orderId != "{orderId}") {
            if (orderId.startsWith("#BK-")) orderId else "#BK-$orderId"
        } else {
            val randomNum = kotlin.random.Random.nextInt(10000, 99999)
            "#BK-$randomNum"
        }
    }

    val displayItemsCount = remember(itemsCount) {
        if (itemsCount != null && itemsCount > 0) {
            "$itemsCount Item(s)"
        } else {
            "2 Item(s)"
        }
    }

    // Subtle pulse animation for PREPARING badge
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Crema
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Circular coffee mug badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFBF4EE))
                        .border(2.dp, Terracotta, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "☕", fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ORDER DISPATCHED",
                    color = Terracotta,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Brewing in Progress!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Espresso,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your ticket was dispatched to our barista.",
                    fontSize = 13.sp,
                    color = MutedBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Order Ticket Card matching prototype
                val cardShape = RoundedCornerShape(18.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(Color.White)
                        .border(1.dp, WarmBorder, cardShape)
                        .padding(18.dp)
                ) {
                    // Ticket header: code on left, amber pulse badge on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ORDER TICKET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedBrown
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = displayOrderId,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Espresso
                            )
                        }

                        // PREPARING pulse badge
                        Box(
                            modifier = Modifier
                                .alpha(pulseAlpha)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "PREPARING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = WarmBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Estimated Wait
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated Wait:", fontSize = 13.sp, color = MutedBrown)
                        Text(
                            text = "20 - 30 minutes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Terracotta
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Items Ordered
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Items Ordered:", fontSize = 13.sp, color = MutedBrown)
                        Text(
                            text = displayItemsCount,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Espresso
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = WarmBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Barista status
                    Text(
                        text = "Status:",
                        fontSize = 11.sp,
                        color = MutedBrown
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Barista accepted your order!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessEmerald
                    )
                }
            }

            // Dark Espresso "Back to Menu" button
            val btnShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(btnShape)
                    .background(Espresso)
                    .clickable(onClick = onBackToMenuClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Back to Menu",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Order Screen Preview")
@Composable
fun OrderScreenPreview() {
    BrewkeryTheme {
        OrderScreen(
            orderId = "#BK-86446",
            itemsCount = 2,
            onBackToMenuClick = {}
        )
    }
}
