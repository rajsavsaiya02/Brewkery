package com.example.brewkery.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.model.ActiveOrder
import com.example.brewkery.data.model.CartState
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.StoreResponse
import com.example.brewkery.data.repository.CartRepository
import com.example.brewkery.data.repository.MenuRepository
import com.example.brewkery.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuViewModel(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<StoreResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<StoreResponse>> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val cartState: StateFlow<CartState> = cartRepository.cartState
    val activeOrder: StateFlow<ActiveOrder?> = cartRepository.activeOrder

    val filteredItems: StateFlow<List<MenuItem>> = combine(
        _uiState,
        _searchQuery,
        _selectedCategory
    ) { state, query, category ->
        if (state is UiState.Success) {
            state.data.menuItems.filter { item ->
                val matchesCategory = (category == "ALL" || item.categoryId.equals(category, ignoreCase = true))
                val matchesQuery = query.isBlank() ||
                        item.name.contains(query, ignoreCase = true) ||
                        item.displayDescription.contains(query, ignoreCase = true) ||
                        (!item.tagline.isNullOrBlank() && item.tagline.contains(query, ignoreCase = true))
                matchesCategory && matchesQuery
            }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadStoreData()
    }

    fun loadStoreData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            menuRepository.fetchStoreData()
                .onSuccess { storeData ->
                    _uiState.value = UiState.Success(storeData)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message = error.localizedMessage ?: "Failed to load store menu",
                        onRetry = { loadStoreData() }
                    )
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: String) {
        _selectedCategory.value = categoryId
    }
}
