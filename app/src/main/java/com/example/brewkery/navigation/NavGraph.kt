package com.example.brewkery.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.brewkery.data.repository.CartRepositoryImpl
import com.example.brewkery.data.repository.MenuRepositoryImpl
import com.example.brewkery.screens.cart.CartScreen
import com.example.brewkery.screens.cart.CartViewModel
import com.example.brewkery.screens.detail.DetailScreen
import com.example.brewkery.screens.detail.DetailViewModel
import com.example.brewkery.screens.menu.MenuScreen
import com.example.brewkery.screens.menu.MenuViewModel
import com.example.brewkery.screens.order.OrderScreen
import com.example.brewkery.ui.components.LocalToastState
import com.example.brewkery.ui.components.ToastHost
import com.example.brewkery.ui.components.ToastState

// Simple destination routes for the 4-screen flow
sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
    data object Cart : Screen("cart")
    data object Order : Screen("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }
}

// Main navigation host connecting all screens
@Composable
fun BrewkeryNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val toastState = remember { ToastState() }

    // Shared repositories (in-memory SSOT for cart and cached menu)
    val menuRepository = remember { MenuRepositoryImpl() }
    val cartRepository = remember { CartRepositoryImpl() }

    // Scoped ViewModels
    val menuViewModel: MenuViewModel = viewModel {
        MenuViewModel(menuRepository, cartRepository)
    }
    val detailViewModel: DetailViewModel = viewModel {
        DetailViewModel(menuRepository, cartRepository)
    }
    val cartViewModel: CartViewModel = viewModel {
        CartViewModel(cartRepository)
    }

    CompositionLocalProvider(LocalToastState provides toastState) {
        Box(modifier = modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Menu.route,
                modifier = Modifier.fillMaxSize()
            ) {
        // Screen 1: Menu Catalog
        composable(Screen.Menu.route) {
            MenuScreen(
                viewModel = menuViewModel,
                onCustomizeClick = { itemId ->
                    navController.navigate(Screen.Detail.createRoute(itemId))
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onTrackOrderClick = { orderId ->
                    navController.navigate(Screen.Order.createRoute(orderId))
                }
            )
        }

        // Screen 2: Item Detail & Customizer
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1
            DetailScreen(
                itemId = itemId,
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onCartNav = {
                    navController.navigate(Screen.Cart.route) {
                        popUpTo(Screen.Menu.route)
                    }
                }
            )
        }

        // Screen 3: Cart & Checkout
        composable(Screen.Cart.route) {
            CartScreen(
                viewModel = cartViewModel,
                onBackClick = { navController.popBackStack() },
                onExploreMenuClick = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                onOrderPlaced = { orderId ->
                    navController.navigate(Screen.Order.createRoute(orderId)) {
                        popUpTo(Screen.Menu.route)
                    }
                }
            )
        }

        // Screen 4: Order Status & Confirmation
        composable(
            route = Screen.Order.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderIdArg = backStackEntry.arguments?.getString("orderId") ?: ""
            val activeOrder by cartViewModel.activeOrder.collectAsState()

            OrderScreen(
                orderId = orderIdArg,
                itemsCount = activeOrder?.itemsCount,
                onBackToMenuClick = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                }
            )
        }
    }

    // Global Artisan Toast Overlay
    ToastHost(toastState = toastState)
}
}
}

