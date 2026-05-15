package com.hastakalashop.app.data.model

data class Sale(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val color: String = "",
    val quantity: Int = 0,
    val totalAmount: Double = 0.0,
    val timestamp: Long = 0L
)

data class ProductSalesAggregate(
    val productName: String,
    val color: String,
    val totalQuantity: Int,
    val totalRevenue: Double
)