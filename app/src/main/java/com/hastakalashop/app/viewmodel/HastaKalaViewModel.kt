package com.hastakalashop.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastakalashop.app.data.model.Product
import com.hastakalashop.app.data.model.ProductSalesAggregate
import com.hastakalashop.app.data.model.Sale
import com.hastakalashop.app.data.repository.HastaKalaRepository
import com.hastakalashop.app.data.repository.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiUiState(
    val isLoading: Boolean = false,
    val suggestion: String? = null,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HastaKalaViewModel @Inject constructor(
    private val repository: HastaKalaRepository
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(TimeRange.THIS_WEEK)
    val selectedRange: StateFlow<TimeRange> = _selectedRange

    fun setRange(range: TimeRange) { _selectedRange.value = range }

    val products: StateFlow<List<Product>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val salesForRange: StateFlow<List<Sale>> = _selectedRange
        .flatMapLatest { repository.salesForRange(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val productSalesForRange: StateFlow<List<ProductSalesAggregate>> = _selectedRange
        .flatMapLatest { repository.productSalesForRange(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalRevenueForRange: StateFlow<Double> = _selectedRange
        .flatMapLatest { repository.totalRevenueForRange(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val saleCountForRange: StateFlow<Int> = _selectedRange
        .flatMapLatest { repository.saleCountForRange(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val productSalesAllTime: StateFlow<List<ProductSalesAggregate>> = repository.productSalesAllTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun recordSale(product: Product, quantity: Int = 1) {
        viewModelScope.launch { repository.recordSale(product, quantity) }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch { repository.addProduct(product) }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch { repository.deleteProduct(id) }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { repository.updateProduct(product) }
    }

    fun updateStock(productId: String, newStock: Int) {
        viewModelScope.launch { repository.updateStock(productId, newStock) }
    }



    private val _aiPlanState = MutableStateFlow(AiUiState())
    val aiPlanState: StateFlow<AiUiState> = _aiPlanState

    private val _aiBestSellerState = MutableStateFlow(AiUiState())
    val aiBestSellerState: StateFlow<AiUiState> = _aiBestSellerState

    fun fetchAiProductionPlan() {
        viewModelScope.launch {
            _aiPlanState.value = AiUiState(isLoading = true)
            val result = repository.getAiProductionPlan(
                productSalesAllTime.value,
                lowStockProducts.value
            )
            _aiPlanState.value = result.fold(
                onSuccess = { AiUiState(suggestion = it) },
                onFailure = { AiUiState(error = it.message ?: "AI request failed") }
            )
        }
    }

    fun fetchAiBestSellerInsight() {
        viewModelScope.launch {
            _aiBestSellerState.value = AiUiState(isLoading = true)
            val result = repository.getAiBestSellerInsight(productSalesAllTime.value.firstOrNull())
            _aiBestSellerState.value = result.fold(
                onSuccess = { AiUiState(suggestion = it) },
                onFailure = { AiUiState(error = it.message ?: "AI request failed") }
            )
        }
    }
}