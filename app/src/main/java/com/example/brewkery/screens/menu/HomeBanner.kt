package com.example.brewkery.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.data.model.ActiveOrder
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.MutedBrown
import com.example.brewkery.ui.theme.SuccessEmerald
import com.example.brewkery.ui.theme.Terracotta
import com.example.brewkery.ui.theme.WarmBorder

// Dynamic Store Info / Active Order tracking banner
@Composable
fun HomeBanner(
    activeOrder: ActiveOrder?,
    onTrackOrderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerShape = RoundedCornerShape(16.dp)

    // Warm artisan gradient matching the prototype
    val backgroundBrush = if (activeOrder != null) {
        Brush.horizontalGradient(listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFFAEEE5), Color(0xFFF7EBE1)))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(bannerShape)
            .background(backgroundBrush)
            .border(1.dp, WarmBorder, bannerShape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (activeOrder != null) {
            // Active order state with live tracking
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon indicator box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, WarmBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🟢", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ACTIVE ORDER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = SuccessEmerald
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(SuccessEmerald)
                        )
                    }
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Order ${activeOrder.orderId}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Espresso
                    )
                    Text(
                        text = "Preparing (${activeOrder.estimatedWait})",
                        fontSize = 11.sp,
                        color = MutedBrown
                    )
                }
            }

            // Track button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessEmerald)
                    .clickable { onTrackOrderClick(activeOrder.orderId) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Track",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            // Default store info state
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Scooter icon box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, WarmBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛵", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "STORE INFO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Terracotta
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Terracotta)
                        )
                    }
                    Text(
                        text = "Delivery in 20 - 30 mins",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Espresso
                    )
                    Text(
                        text = "$2.50 flat fee",
                        fontSize = 11.sp,
                        color = MutedBrown
                    )
                }
            }

            // Open badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, WarmBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Open",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Espresso
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Banner Default")
@Composable
fun HomeBannerDefaultPreview() {
    BrewkeryTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HomeBanner(activeOrder = null, onTrackOrderClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Home Banner Active Order")
@Composable
fun HomeBannerActiveOrderPreview() {
    BrewkeryTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HomeBanner(
                activeOrder = ActiveOrder(
                    orderId = "#BK-74921",
                    estimatedWait = "25 mins",
                    itemsCount = 2,
                    totalAmount = 12.65,
                    status = "PREPARING"
                ),
                onTrackOrderClick = {}
            )
        }
    }
}
