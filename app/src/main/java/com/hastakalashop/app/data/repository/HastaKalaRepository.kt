package com.hastakalashop.app.data.repository

import com.hastakalashop.app.data.ai.GeminiService
import com.hastakalashop.app.data.firebase.AuthManager
import com.hastakalashop.app.data.firebase.FirebaseDataSource
import com.hastakalashop.app.data.model.Product
import com.hastakalashop.app.data.model.ProductSalesAggregate
import com.hastakalashop.app.data.model.Sale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class HastaKalaRepository @Inject constructor(
    private val firebase: FirebaseDataSource,
    private val authManager: AuthManager,
    private val geminiService: GeminiService
) {
    private val uidFlow: Flow<String> = flow {
        val uid = authManager.ensureSignedIn()
        if (!firebase.isSeeded(uid)) {
            firebase.seedDefaultProducts(uid)
        }
        emit(uid)
    }

    val products: Flow<List<Product>> = uidFlow.flatMapLatest { uid ->
        firebase.observeProducts(uid)
    }

    val sales: Flow<List<Sale>> = uidFlow.flatMapLatest { uid ->
        firebase.observeSales(uid)
    }

    val lowStockProducts: Flow<List<Product>> = products
        .flatMapLatest { list -> flowOf(list.filter { it.stock <= 5 }.sortedBy { it.stock }) }

    fun salesForRange(range: TimeRange): Flow<List<Sale>> =
        sales.flatMapLatest { all ->
            val start = range.startTimestamp()
            flowOf(all.filter { it.timestamp >= start })
        }

    fun productSalesForRange(range: TimeRange): Flow<List<ProductSalesAggregate>> =
        salesForRange(range).flatMapLatest { flowOf(aggregate(it)) }

    val productSalesAllTime: Flow<List<ProductSalesAggregate>> =
        sales.flatMapLatest { flowOf(aggregate(it)) }

    fun totalRevenueForRange(range: TimeRange): Flow<Double> =
        salesForRange(range).flatMapLatest { flowOf(it.sumOf { s -> s.totalAmount }) }

    fun saleCountForRange(range: TimeRange): Flow<Int> =
        salesForRange(range).flatMapLatest { flowOf(it.size) }

    private fun aggregate(sales: List<Sale>): List<ProductSalesAggregate> {
        return sales.groupBy { it.productName to it.color }
            .map { (key, group) ->
                ProductSalesAggregate(
                    productName = key.first,
                    color = key.second,
                    totalQuantity = group.sumOf { it.quantity },
                    totalRevenue = group.sumOf { it.totalAmount }
                )
            }.sortedByDescending { it.totalQuantity }
    }

    suspend fun recordSale(product: Product, quantity: Int) {
        val uid = authManager.ensureSignedIn()
        val sale = Sale(
            productId = product.id,
            productName = product.name,
            color = product.color,
            quantity = quantity,
            totalAmount = product.pricePerUnit * quantity,
            timestamp = System.currentTimeMillis()
        )
        firebase.addSale(uid, sale)
        firebase.decrementStock(uid, product.id, quantity)
    }

    suspend fun addProduct(product: Product) {
        val uid = authManager.ensureSignedIn()
        firebase.addProduct(uid, product)
    }

    suspend fun deleteProduct(productId: String) {
        val uid = authManager.ensureSignedIn()
        firebase.deleteProduct(uid, productId)
    }

    suspend fun updateProduct(product: Product) {
        val uid = authManager.ensureSignedIn()
        firebase.updateProduct(uid, product)
    }

    suspend fun updateStock(productId: String, newStock: Int) {
        val uid = authManager.ensureSignedIn()
        val ref = firebase.productsRef(uid).child(productId).child("stock")
        ref.setValue(newStock).await()
    }

    suspend fun getAiProductionPlan(
        sales: List<ProductSalesAggregate>,
        lowStock: List<Product>
    ): Result<String> = geminiService.suggestProductionPlan(sales, lowStock)

    suspend fun getAiBestSellerInsight(top: ProductSalesAggregate?): Result<String> =
        geminiService.explainBestSeller(top)
}

enum class TimeRange {
    TODAY, THIS_WEEK, THIS_MONTH, ALL_TIME;

    fun startTimestamp(): Long {
        val cal = Calendar.getInstance()
        return when (this) {
            TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.timeInMillis
            }
            THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.timeInMillis
            }
            ALL_TIME -> 0L
        }
    }

    val label: String get() = when (this) {
        TODAY -> "Today"
        THIS_WEEK -> "This Week"
        THIS_MONTH -> "This Month"
        ALL_TIME -> "All Time"
    }
}