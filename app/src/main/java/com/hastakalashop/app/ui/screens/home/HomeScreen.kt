package com.hastakalashop.app.ui.screens.home

import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hastakalashop.app.ui.components.BadgeVariant
import com.hastakalashop.app.ui.components.PieSlice
import com.hastakalashop.app.ui.components.ShadcnBadge
import com.hastakalashop.app.ui.components.ShadcnCard
import com.hastakalashop.app.ui.components.ShadcnCardHeader
import com.hastakalashop.app.ui.components.ShadcnPieChart
import com.hastakalashop.app.ui.theme.Chart1
import com.hastakalashop.app.ui.theme.Chart2
import com.hastakalashop.app.ui.theme.Chart3
import com.hastakalashop.app.ui.theme.Chart4
import com.hastakalashop.app.ui.theme.Chart5
import com.hastakalashop.app.ui.theme.Chart6
import com.hastakalashop.app.viewmodel.HastaKalaViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(viewModel: HastaKalaViewModel = hiltViewModel(), isDarkMode: Boolean = false, onToggleTheme: () -> Unit ={}) {
    val totalRevenue by viewModel.totalRevenueForRange.collectAsState()
    val saleCount by viewModel.saleCountForRange.collectAsState()
    val lowStock by viewModel.lowStockProducts.collectAsState()
    val productSales by viewModel.productSalesAllTime.collectAsState()
    val range by viewModel.selectedRange.collectAsState()
    val chartColors = listOf(
        Chart1, Chart2, Chart3, Chart4, Chart5, Chart6
    ).map { it.toArgb() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hasta-Kala Shop",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Your micro-sales dashboard",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkMode)
                        Icons.Outlined.LightMode
                    else
                        Icons.Outlined.DarkMode,
                    contentDescription = "Toggle theme",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Revenue (${range.label})",
                value = formatCurrency(totalRevenue),
                icon = Icons.Outlined.AttachMoney
            )
            StatTile(
                modifier = Modifier.weight(1f),
                title = "Sales (${range.label})",
                value = saleCount.toString(),
                icon = Icons.Outlined.ReceiptLong
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnCard(modifier = Modifier.fillMaxWidth()) {
            ShadcnCardHeader(
                title = "Best Sellers",
                description = "Your sales breakdown — all time"
            )
            if (productSales.isEmpty()) {
                Text(
                    text = "Log your first sale in Quick Bill to see analytics.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val slices = productSales.take(6).map {
                    PieSlice(
                        label = "${it.productName} (${it.color})",
                        value = it.totalQuantity.toFloat()
                    )
                }
                ShadcnPieChart(
                    slices = slices,
                    colors = chartColors,
                    centerText = "${productSales.sumOf { it.totalQuantity }}\nUnits"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Stock Alerts", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Items running low (≤ 5 units)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (lowStock.isEmpty()) {
                Text(
                    text = "All your items are well stocked. ✓",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                lowStock.forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = product.color,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        ShadcnBadge(
                            text = "Only ${product.stock} left",
                            variant = if (product.stock <= 2)
                                BadgeVariant.Destructive else BadgeVariant.Outline
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    ShadcnCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatCurrency(amount: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    nf.maximumFractionDigits = 0
    return nf.format(amount)
}