package com.example.brewkery.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.data.model.ActiveOrder
import com.example.brewkery.data.model.CartState
import com.example.brewkery.data.model.Category
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.StoreMeta
import com.example.brewkery.data.model.StoreResponse
import com.example.brewkery.domain.PriceCalculator
import com.example.brewkery.ui.components.ErrorView
import com.example.brewkery.ui.components.LoadingView
import com.example.brewkery.ui.theme.AmberGold
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Crema
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.MutedBrown
import com.example.brewkery.ui.theme.Terracotta
import com.example.brewkery.ui.theme.WarmBorder
import com.example.brewkery.util.UiState

// Screen 1: Menu catalog, search, category filters, and active order tracker
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onCustomizeClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrderClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val cartState by viewModel.cartState.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()

    MenuContent(
        uiState = uiState,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        filteredItems = filteredItems,
        cartState = cartState,
        activeOrder = activeOrder,
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onCategorySelected = { viewModel.onCategorySelected(it) },
        onCustomizeClick = onCustomizeClick,
        onCartClick = onCartClick,
        onTrackOrderClick = onTrackOrderClick
    )
}

@Composable
fun MenuContent(
    uiState: UiState<StoreResponse>,
    searchQuery: String,
    selectedCategory: String,
    filteredItems: List<MenuItem>,
    cartState: CartState,
    activeOrder: ActiveOrder?,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCustomizeClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onTrackOrderClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Crema,
        topBar = {
            TopAppBarHeader(
                cartCount = cartState.totalQuantity,
                onCartClick = onCartClick
            )
        },
        bottomBar = {
            // Sticky floating cart bar appears when cart has items
            if (cartState.totalQuantity > 0) {
                FloatingCartBar(
                    totalQuantity = cartState.totalQuantity,
                    subtotal = cartState.subtotal,
                    onCartClick = onCartClick
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
                    val categories = listOf(
                        Category("ALL", "All Items"),
                        Category("cat_hot_coffee", "☕ Hot Coffee"),
                        Category("cat_cold_brews", "🧊 Cold Brews"),
                        Category("cat_bakery", "🥐 Artisan Bakery")
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        // Store Info / Active Order Banner
                        item {
                            HomeBanner(
                                activeOrder = activeOrder,
                                onTrackOrderClick = onTrackOrderClick,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        // Thinner Search Bar with compact prefix & vertical padding
                        item {
                            val searchShape = RoundedCornerShape(12.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .height(40.dp)
                                    .clip(searchShape)
                                    .background(Color.White)
                                    .border(1.dp, WarmBorder, searchShape)
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = MutedBrown,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "Search roast, cold brew, pastry...",
                                                fontSize = 12.sp,
                                                color = MutedBrown
                                            )
                                        }

                                        BasicTextField(
                                            value = searchQuery,
                                            onValueChange = onSearchQueryChanged,
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                            textStyle = TextStyle(
                                                fontSize = 12.sp,
                                                color = Espresso
                                            ),
                                            cursorBrush = SolidColor(Terracotta),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        // Category Chips Row
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    val isSelected = selectedCategory.equals(category.id, ignoreCase = true)
                                    val chipShape = RoundedCornerShape(20.dp)

                                    Box(
                                        modifier = Modifier
                                            .clip(chipShape)
                                            .background(if (isSelected) Espresso else Color.White)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Espresso else WarmBorder,
                                                shape = chipShape
                                            )
                                            .clickable {
                                                focusManager.clearFocus()
                                                onCategorySelected(category.id)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = category.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else MutedBrown
                                        )
                                    }
                                }
                            }
                        }

                        // Catalog Product List
                        items(filteredItems, key = { it.id }) { menuItem ->
                            ProductCard(
                                item = menuItem,
                                onCustomizeClick = onCustomizeClick,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// App bar with artisan logo and pixel-accurate live cart badge button
@Composable
private fun TopAppBarHeader(
    cartCount: Int,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Shop branding
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Espresso),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BK",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Fresh Roast & Bakes",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MutedBrown
                )
                Text(
                    text = "Brewkery Artisans",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Espresso
                )
            }
        }

        // Circular Cart button with pixel-accurate top-right badge supporting 1 or 2 digits
        Box(
            modifier = Modifier
                .padding(top = 4.dp, end = 4.dp)
                .clickable(onClick = onCartClick),
            contentAlignment = Alignment.Center
        ) {
            // Circle Button Container
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, WarmBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = "Cart",
                    tint = Espresso,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Top-Right Badge overlapping top-end edge, centered text, expanding for 2 digits
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 5.dp, y = (-3).dp)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .clip(CircleShape)
                    .background(Terracotta)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cartCount.toString(),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

// Floating cart summary bar matching prototype styling
@Composable
private fun FloatingCartBar(
    totalQuantity: Int,
    subtotal: Double,
    onCartClick: () -> Unit
) {
    val barShape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .clip(barShape)
            .background(Espresso)
            .clickable(onClick = onCartClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quantity pill
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Terracotta),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = totalQuantity.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "View Your Cart",
                        color = Color(0xFFEBD8CB),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = PriceCalculator.formatCurrency(subtotal),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Amber proceed link
            Text(
                text = "Proceed to Checkout →",
                fontWeight = FontWeight.ExtraBold,
                color = AmberGold,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, name = "Menu Screen Preview")
@Composable
fun MenuScreenPreview() {
    val mockItems = listOf(
        MenuItem(
            id = 1,
            categoryId = "cat_hot_coffee",
            name = "Toasted Caramel Macchiato",
            basePrice = 4.85,
            rating = 4.9,
            reviews = 312,
            badge = "BESTSELLER",
            imageUrl = "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=600&auto=format&fit=crop&q=80"
        )
    )

    BrewkeryTheme {
        MenuContent(
            uiState = UiState.Success(
                StoreResponse(
                    meta = StoreMeta(deliveryFee = 2.50),
                    categories = emptyList(),
                    menuItems = mockItems
                )
            ),
            searchQuery = "",
            selectedCategory = "ALL",
            filteredItems = mockItems,
            cartState = CartState(totalQuantity = 2, subtotal = 9.40),
            activeOrder = null,
            onSearchQueryChanged = {},
            onCategorySelected = {},
            onCustomizeClick = {},
            onCartClick = {},
            onTrackOrderClick = {}
        )
    }
}
