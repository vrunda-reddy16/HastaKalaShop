package com.hastakalashop.app.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val color: String = "",
    val pricePerUnit: Double = 0.0,
    val stock: Int = 0,
    val iconName: String = "ShoppingBag"
)