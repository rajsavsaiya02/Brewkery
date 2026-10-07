package com.example.brewkery.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.model.CartItem
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
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

class DetailViewModel(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MenuItem>>(UiState.Loading)
    val uiState: StateFlow<UiState<MenuItem>> = _uiState.asStateFlow()

    private val _selectedSize = MutableStateFlow<SizeOption?>(null)
    val selectedSize: StateFlow<SizeOption?> = _selectedSize.asStateFlow()

    private val _selectedMilk = MutableStateFlow<MilkOption?>(null)
    val selectedMilk: StateFlow<MilkOption?> = _selectedMilk.asStateFlow()

    private val _selectedSugar = MutableStateFlow<String?>(null)
    val selectedSugar: StateFlow<String?> = _selectedSugar.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    val totalItemPrice: StateFlow<Double> = combine(
        _uiState,
        _selectedSize,
        _selectedMilk,
        _quantity
    ) { state, size, milk, qty ->
        if (state is UiState.Success) {
            val base = state.data.basePrice
            val sizeExtra = size?.extraPrice ?: 0.0
            val milkExtra = milk?.extraPrice ?: 0.0
            (base + sizeExtra + milkExtra) * qty
        } else {
            0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun loadItemDetail(itemId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _quantity.value = 1
            _isFavorite.value = false
            menuRepository.fetchItemDetail(itemId)
                .onSuccess { item ->
                    _uiState.value = UiState.Success(item)
                    _selectedSize.value = item.customizations?.sizes?.firstOrNull()
                    _selectedMilk.value = item.customizations?.milkOptions?.firstOrNull()
                    _selectedSugar.value = item.customizations?.sugarLevels?.firstOrNull()
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message = error.localizedMessage ?: "Failed to load item detail",
                        onRetry = { loadItemDetail(itemId) }
                    )
                }
        }
    }

    fun selectSize(size: SizeOption) {
        _selectedSize.value = size
    }

    fun selectMilk(milk: MilkOption) {
        _selectedMilk.value = milk
    }

    fun selectSugar(sugar: String) {
        _selectedSugar.value = sugar
    }

    fun incrementQuantity() {
        _quantity.value += 1
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun addToCart(onAdded: () -> Unit) {
        val state = _uiState.value
        if (state is UiState.Success) {
            val item = state.data
            val size = _selectedSize.value
            val milk = _selectedMilk.value
            val sugar = _selectedSugar.value ?: "Standard"

            val sizeLabel = size?.label ?: "Standard"
            val milkName = milk?.name ?: "Standard Milk"
            val extraPrice = (size?.extraPrice ?: 0.0) + (milk?.extraPrice ?: 0.0)

            val customId = "${item.id}-$sizeLabel-$milkName-$sugar"

            val cartItem = CartItem(
                id = customId,
                itemId = item.id,
                name = item.name,
                imageUrl = item.imageUrl,
                sizeLabel = sizeLabel,
                milkName = milkName,
                sugarLevel = sugar,
                basePrice = item.basePrice,
                extraPrice = extraPrice,
                quantity = _quantity.value
            )

            cartRepository.addToCart(cartItem)
            _quantity.value = 1
            onAdded()
        }
    }
}
