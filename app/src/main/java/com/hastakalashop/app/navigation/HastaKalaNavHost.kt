package com.hastakalashop.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hastakalashop.app.ui.screens.aiinsights.AiInsightsScreen
import com.hastakalashop.app.ui.screens.analytics.AnalyticsScreen
import com.hastakalashop.app.ui.screens.home.HomeScreen
import com.hastakalashop.app.ui.screens.incomelog.IncomeLogScreen
import com.hastakalashop.app.ui.screens.quickbill.QuickBillScreen
import com.hastakalashop.app.ui.screens.manageproducts.ManageProductsScreen
import androidx.compose.material.icons.outlined.Inventory

private data class NavItem(val screen: Screen, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Screen.Home, Icons.Outlined.Home),
    NavItem(Screen.QuickBill, Icons.Outlined.ShoppingCart),
    NavItem(Screen.Analytics, Icons.Outlined.BarChart),
    NavItem(Screen.IncomeLog, Icons.Outlined.Receipt),
    NavItem(Screen.AiInsights, Icons.Outlined.AutoAwesome),
    NavItem(Screen.ManageProducts, Icons.Outlined.Inventory)
)

@Composable
fun HastaKalaNavHost(
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {}
)  {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.screen.route ||
                            backStackEntry?.destination?.hierarchy
                                ?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.screen.label)
                        },
                        label = {
                            Text(
                                item.screen.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(isDarkMode = isDarkMode,onToggleTheme = onToggleTheme) }
            composable(Screen.QuickBill.route) { QuickBillScreen() }
            composable(Screen.Analytics.route) { AnalyticsScreen() }
            composable(Screen.IncomeLog.route) { IncomeLogScreen() }
            composable(Screen.AiInsights.route) { AiInsightsScreen() }
            composable(Screen.ManageProducts.route) { ManageProductsScreen() }
        }
    }
}