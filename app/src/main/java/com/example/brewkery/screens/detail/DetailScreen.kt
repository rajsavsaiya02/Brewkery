package com.example.brewkery.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.brewkery.data.model.CustomizationOptions
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.domain.PriceCalculator
import com.example.brewkery.ui.components.BrewkeryButton
import com.example.brewkery.ui.components.CircleIconButton
import com.example.brewkery.ui.components.CircularBackButton
import com.example.brewkery.ui.components.ErrorView
import com.example.brewkery.ui.components.LoadingView
import com.example.brewkery.ui.components.LocalToastState
import com.example.brewkery.ui.components.QuantityStepper
import com.example.brewkery.ui.theme.AmberGold
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Crema
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.MutedBrown
import com.example.brewkery.ui.theme.RoseAccent
import com.example.brewkery.ui.theme.Terracotta
import com.example.brewkery.ui.theme.WarmBorder
import com.example.brewkery.util.UiState

// Screen 2: Item detail with interactive size, milk, and sugar customization
@Composable
fun DetailScreen(
    itemId: Int,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    onCartNav: () -> Unit
) {
    val toastState = LocalToastState.current

    LaunchedEffect(itemId) {
        viewModel.loadItemDetail(itemId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedMilk by viewModel.selectedMilk.collectAsState()
    val selectedSugar by viewModel.selectedSugar.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val totalPrice by viewModel.totalItemPrice.collectAsState()

    DetailContent(
        uiState = uiState,
        selectedSize = selectedSize,
        selectedMilk = selectedMilk,
        selectedSugar = selectedSugar,
        quantity = quantity,
        isFavorite = isFavorite,
        totalPrice = totalPrice,
        onBackClick = onBackClick,
        onFavoriteToggle = {
            viewModel.toggleFavorite()
            val msg = if (!isFavorite) "Item saved to favorites" else "Removed from favorites"
            toastState.show(msg)
        },
        onIncrement = { viewModel.incrementQuantity() },
        onDecrement = { viewModel.decrementQuantity() },
        onSelectSize = { viewModel.selectSize(it) },
        onSelectMilk = { viewModel.selectMilk(it) },
        onSelectSugar = { viewModel.selectSugar(it) },
        onAddToCart = {
            viewModel.addToCart {
                val itemName = (uiState as? UiState.Success)?.data?.name ?: "Item"
                toastState.show("Added ${quantity}x $itemName to Cart")
                onCartNav()
            }
        }
    )
}

@Composable
fun DetailContent(
    uiState: UiState<MenuItem>,
    selectedSize: SizeOption?,
    selectedMilk: MilkOption?,
    selectedSugar: String?,
    quantity: Int,
    isFavorite: Boolean,
    totalPrice: Double,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSelectSize: (SizeOption) -> Unit,
    onSelectMilk: (MilkOption) -> Unit,
    onSelectSugar: (String) -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        containerColor = Crema,
        topBar = {
            TopDetailAppBar(
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onFavoriteToggle = onFavoriteToggle
            )
        },
        bottomBar = {
            if (uiState is UiState.Success) {
                BottomCartBar(
                    quantity = quantity,
                    totalPrice = totalPrice,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                    onAddToCart = onAddToCart
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(
                    message = uiState.message,
                    onRetry = uiState.onRetry
                )
                is UiState.Success -> {
                    val item = uiState.data
                    val customizations = item.customizations

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        // Hero Product Image
                        val heroShape = RoundedCornerShape(18.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(heroShape)
                                .background(Color(0xFFF7F2EE))
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Dedicated Top-start Badge Component
                            if (!item.badge.isNullOrBlank()) {
                                ProductBadge(
                                    badgeText = item.badge,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Title and Base Price row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Espresso,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                lineHeight = 22.sp,
                                softWrap = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = PriceCalculator.formatCurrency(item.basePrice),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Terracotta,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Description with narrow vertical padding
                        Text(
                            text = item.displayDescription,
                            fontSize = 12.sp,
                            color = MutedBrown,
                            lineHeight = 16.sp,
                            softWrap = true
                        )

                        // Key Ingredients Chips
                        if (!item.ingredients.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "KEY INGREDIENTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedBrown
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                item.ingredients.forEach { ingredient ->
                                    val chipShape = RoundedCornerShape(8.dp)
                                    Box(
                                        modifier = Modifier
                                            .clip(chipShape)
                                            .background(Color.White)
                                            .border(1.dp, WarmBorder, chipShape)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = ingredient,
                                            fontSize = 10.sp,
                                            color = MutedBrown,
                                            softWrap = true
                                        )
                                    }
                                }
                            }
                        }

                        // 1. Size Selection (Fixed 3-slot horizontal grid layout matching prototype screenshots)
                        if (customizations != null && !customizations.sizes.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OptionCard(title = "Size Selection") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val totalSlots = 3
                                    val sizes = customizations.sizes

                                    for (i in 0 until totalSlots) {
                                        if (i < sizes.size) {
                                            val sizeOption = sizes[i]
                                            val isSelected = selectedSize?.id == sizeOption.id || (selectedSize == null && i == 0)
                                            val btnShape = RoundedCornerShape(10.dp)

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(btnShape)
                                                    .background(if (isSelected) Color(0xFFF7EBE1) else Color.White)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSelected) Terracotta else WarmBorder,
                                                        shape = btnShape
                                                    )
                                                    .clickable { onSelectSize(sizeOption) }
                                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = sizeOption.label,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Terracotta else MutedBrown,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 2,
                                                        lineHeight = 13.sp,
                                                        softWrap = true
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "+${PriceCalculator.formatCurrency(sizeOption.extraPrice)}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Terracotta else MutedBrown,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Milk Options / Spreads
                        if (customizations != null && !customizations.milkOptions.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OptionCard(title = "Milk Options / Spreads") {
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    customizations.milkOptions.forEachIndexed { index, milkOption ->
                                        val isSelected = selectedMilk?.id == milkOption.id || (selectedMilk == null && index == 0)
                                        val rowShape = RoundedCornerShape(10.dp)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(rowShape)
                                                .background(if (isSelected) Color(0xFFF7EBE1) else Color.White)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) Terracotta else WarmBorder,
                                                    shape = rowShape
                                                )
                                                .clickable { onSelectMilk(milkOption) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = milkOption.name,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Terracotta else MutedBrown,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 8.dp),
                                                maxLines = 2,
                                                lineHeight = 15.sp,
                                                softWrap = true
                                            )
                                            Text(
                                                text = "+${PriceCalculator.formatCurrency(milkOption.extraPrice)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Terracotta else MutedBrown,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Sugar Levels / Serving
                        if (customizations != null && !customizations.sugarLevels.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OptionCard(title = "Sugar Levels / Serving") {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    customizations.sugarLevels.forEachIndexed { index, sugarLevel ->
                                        val isSelected = selectedSugar == sugarLevel || (selectedSugar == null && index == 0)
                                        val chipShape = RoundedCornerShape(8.dp)

                                        Box(
                                            modifier = Modifier
                                                .clip(chipShape)
                                                .background(if (isSelected) Espresso else Color.White)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) Espresso else WarmBorder,
                                                    shape = chipShape
                                                )
                                                .clickable { onSelectSugar(sugarLevel) }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = sugarLevel,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MutedBrown
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

// Dedicated UI component for top product badge overlay
@Composable
private fun ProductBadge(
    badgeText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 1.dp)
    ) {
        Text(
            text = badgeText.uppercase(),
            color = AmberGold,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
        )
    }
}

// Option section container card
@Composable
private fun OptionCard(
    title: String,
    content: @Composable () -> Unit
) {
    val cardShape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.White)
            .border(1.dp, WarmBorder, cardShape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Espresso
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

// Top App Bar for detail screen
@Composable
private fun TopDetailAppBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircularBackButton(onBackClick = onBackClick)

        Text(
            text = "Item Customizer",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Espresso
        )

        CircleIconButton(
            onClick = onFavoriteToggle,
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) RoseAccent else Espresso
        )
    }
}

// Bottom Bar with quantity stepper and dynamic Add to Cart button
@Composable
private fun BottomCartBar(
    quantity: Int,
    totalPrice: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Crema)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuantityStepper(
            quantity = quantity,
            onIncrement = onIncrement,
            onDecrement = onDecrement
        )

        BrewkeryButton(
            text = "Add to Cart • ${PriceCalculator.formatCurrency(totalPrice)}",
            onClick = onAddToCart,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, name = "Detail Screen Preview")
@Composable
fun DetailScreenPreview() {
    val sizeTall = SizeOption("sz_small", "Tall (8 oz)", 0.0)
    val sizeGrande = SizeOption("sz_medium", "Grande (12 oz)", 0.65)
    val sizeVenti = SizeOption("sz_large", "Venti (16 oz)", 1.25)
    val milkOat = MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.0)

    BrewkeryTheme {
        DetailContent(
            uiState = UiState.Success(
                MenuItem(
                    id = 1,
                    categoryId = "cat_hot_coffee",
                    name = "Toasted Caramel Macchiato",
                    basePrice = 4.85,
                    rating = 4.9,
                    reviews = 312,
                    badge = "BESTSELLER",
                    imageUrl = "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=800&auto=format&fit=crop&q=80",
                    desc = "Rich espresso roast layered with velvety steamed milk.",
                    ingredients = listOf("Signature Espresso", "Steamed Oat Milk"),
                    customizations = CustomizationOptions(
                        sizes = listOf(sizeTall, sizeGrande, sizeVenti),
                        milkOptions = listOf(milkOat),
                        sugarLevels = listOf("0% Unsweetened", "50% Mild", "100% Standard Sweet")
                    )
                )
            ),
            selectedSize = sizeGrande,
            selectedMilk = milkOat,
            selectedSugar = "50% Mild",
            quantity = 1,
            isFavorite = true,
            totalPrice = 5.50,
            onBackClick = {},
            onFavoriteToggle = {},
            onIncrement = {},
            onDecrement = {},
            onSelectSize = {},
            onSelectMilk = {},
            onSelectSugar = {},
            onAddToCart = {}
        )
    }
}
