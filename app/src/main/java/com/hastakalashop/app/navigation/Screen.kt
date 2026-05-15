package com.hastakalashop.app.navigation

sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "Home")
    object QuickBill : Screen("quick_bill", "Quick Bill")
    object Analytics : Screen("analytics", "Best Seller")
    object IncomeLog : Screen("income_log", "Income Log")
    object AiInsights : Screen("ai_insights", "AI Coach")
    object ManageProducts : Screen("manage_products", "Manage")
}