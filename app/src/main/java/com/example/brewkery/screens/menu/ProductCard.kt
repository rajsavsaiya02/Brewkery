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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.domain.PriceCalculator
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.MutedBrown
import com.example.brewkery.ui.theme.Terracotta
import com.example.brewkery.ui.theme.WarmBorder

// Compact artisan catalog item card matching HTML prototype
@Composable
fun ProductCard(
    item: MenuItem,
    onCustomizeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, WarmBorder, cardShape)
            .clickable { onCustomizeClick(item.id) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail (56dp compact artisan size)
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF7F2EE))
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Product details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Category badge pill if available
            if (!item.badge.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.badge.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))
            }

            // Product name
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Espresso,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Rating & reviews count
            Text(
                text = "⭐ ${item.rating} (${item.reviews})",
                fontSize = 10.sp,
                color = MutedBrown
            )

            Spacer(modifier = Modifier.height(1.dp))

            // Price and customize action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Base price in Terracotta monospace
                Text(
                    text = PriceCalculator.formatCurrency(item.basePrice),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Terracotta
                )

                // Customize action pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Terracotta)
                        .clickable { onCustomizeClick(item.id) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Customize",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Product Card Preview")
@Composable
fun ProductCardPreview() {
    BrewkeryTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProductCard(
                item = MenuItem(
                    id = 1,
                    categoryId = "cat_hot_coffee",
                    name = "Toasted Caramel Macchiato",
                    basePrice = 4.85,
                    rating = 4.9,
                    reviews = 312,
                    badge = "BESTSELLER",
                    imageUrl = "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=600&auto=format&fit=crop&q=80",
                    desc = "Rich espresso roast layered with velvety steamed milk.",
                    ingredients = listOf("Signature Espresso", "Steamed Milk")
                ),
                onCustomizeClick = {}
            )
        }
    }
}
