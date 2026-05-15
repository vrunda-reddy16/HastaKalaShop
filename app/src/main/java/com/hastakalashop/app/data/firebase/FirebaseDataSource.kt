package com.hastakalashop.app.data.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hastakalashop.app.data.model.Product
import com.hastakalashop.app.data.model.Sale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    private val database: FirebaseDatabase,
    private val authManager: AuthManager
) {
    private fun userRef(uid: String) = database.getReference("users").child(uid)
    internal fun productsRef(uid: String) = userRef(uid).child("products")
    private fun salesRef(uid: String) = userRef(uid).child("sales")
    private fun seededFlagRef(uid: String) = userRef(uid).child("seeded")

    fun observeProducts(uid: String): Flow<List<Product>> = callbackFlow {
        val ref = productsRef(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.children.mapNotNull { child ->
                    child.getValue(Product::class.java)?.copy(id = child.key ?: "")
                }
                trySend(products)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addProduct(uid: String, product: Product): String {
        val ref = productsRef(uid).push()
        val id = ref.key ?: throw IllegalStateException("Failed to generate key")
        ref.setValue(product.copy(id = id)).await()
        return id
    }

    suspend fun updateProduct(uid: String, product: Product) {
        productsRef(uid).child(product.id).setValue(product).await()
    }

    suspend fun decrementStock(uid: String, productId: String, quantity: Int) {
        val ref = productsRef(uid).child(productId).child("stock")
        val snapshot = ref.get().await()
        val current = snapshot.getValue(Int::class.java) ?: 0
        ref.setValue(maxOf(0, current - quantity)).await()
    }

    suspend fun deleteProduct(uid: String, productId: String) {
        productsRef(uid).child(productId).removeValue().await()
    }

    fun observeSales(uid: String): Flow<List<Sale>> = callbackFlow {
        val ref = salesRef(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sales = snapshot.children.mapNotNull { child ->
                    child.getValue(Sale::class.java)?.copy(id = child.key ?: "")
                }.sortedByDescending { it.timestamp }
                trySend(sales)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addSale(uid: String, sale: Sale): String {
        val ref = salesRef(uid).push()
        val id = ref.key ?: throw IllegalStateException("Failed to generate key")
        ref.setValue(sale.copy(id = id)).await()
        return id
    }

    suspend fun isSeeded(uid: String): Boolean {
        val snap = seededFlagRef(uid).get().await()
        return snap.getValue(Boolean::class.java) == true
    }

    suspend fun markSeeded(uid: String) {
        seededFlagRef(uid).setValue(true).await()
    }

    suspend fun seedDefaultProducts(uid: String) {
        val seeds = listOf(
            Product(name = "Banana Fiber Bag", color = "Natural", pricePerUnit = 250.0, stock = 12, iconName = "ShoppingBag"),
            Product(name = "Banana Fiber Bag", color = "Red", pricePerUnit = 280.0, stock = 8, iconName = "ShoppingBag"),
            Product(name = "Banana Fiber Bag", color = "Blue", pricePerUnit = 280.0, stock = 2, iconName = "ShoppingBag"),
            Product(name = "Keychain", color = "Multicolor", pricePerUnit = 50.0, stock = 30, iconName = "VpnKey"),
            Product(name = "Pouch", color = "Green", pricePerUnit = 120.0, stock = 15, iconName = "Backpack"),
            Product(name = "Pouch", color = "Yellow", pricePerUnit = 120.0, stock = 4, iconName = "Backpack"),
            Product(name = "Coaster Set", color = "Brown", pricePerUnit = 80.0, stock = 20, iconName = "Spa")
        )
        seeds.forEach { addProduct(uid, it) }
        markSeeded(uid)
    }
}