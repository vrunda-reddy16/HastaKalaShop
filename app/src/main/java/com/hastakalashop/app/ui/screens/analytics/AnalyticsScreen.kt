package com.hastakalashop.app.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hastakalashop.app.data.repository.TimeRange
import com.hastakalashop.app.ui.components.BarItem
import com.hastakalashop.app.ui.components.ButtonSize
import com.hastakalashop.app.ui.components.ButtonVariant
import com.hastakalashop.app.ui.components.PieSlice
import com.hastakalashop.app.ui.components.ShadcnBarChart
import com.hastakalashop.app.ui.components.ShadcnButton
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

@Composable
fun AnalyticsScreen(viewModel: HastaKalaViewModel = hiltViewModel()) {
    val productSales by viewModel.productSalesForRange.collectAsState()
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
        Text(
            text = "Best Seller",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "What's flying off the shelf",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimeRange.values().forEach { r ->
                ShadcnButton(
                    text = r.label,
                    onClick = { viewModel.setRange(r) },
                    variant = if (r == range)
                        ButtonVariant.Default else ButtonVariant.Outline,
                    size = ButtonSize.Sm
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnCard(modifier = Modifier.fillMaxWidth()) {
            ShadcnCardHeader(
                title = "Sales Distribution",
                description = "Share by product & color • ${range.label}"
            )
            val slices = productSales.take(6).map {
                PieSlice(
                    label = "${it.productName} (${it.color})",
                    value = it.totalQuantity.toFloat()
                )
            }
            ShadcnPieChart(
                slices = slices,
                colors = chartColors,
                centerText = if (productSales.isEmpty()) ""
                else "${productSales.sumOf { it.totalQuantity }}\nUnits"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnCard(modifier = Modifier.fillMaxWidth()) {
            ShadcnCardHeader(
                title = "Revenue by Product",
                description = "Top earners • ${range.label}"
            )
            val bars = productSales
                .sortedByDescending { it.totalRevenue }
                .take(5)
                .map {
                    BarItem(
                        "${it.productName.take(8)}-${it.color.take(3)}",
                        it.totalRevenue.toFloat()
                    )
                }
            ShadcnBarChart(bars = bars, barColor = Chart1.toArgb())
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShadcnCard(modifier = Modifier.fillMaxWidth()) {
            ShadcnCardHeader(
                title = "Ranked Performance",
                description = "Make more of what's selling"
            )
            if (productSales.isEmpty()) {
                Text(
                    text = "No data for ${range.label.lowercase()} yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                productSales.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "#${index + 1}  ${item.productName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${item.color} • ${item.totalQuantity} sold",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "₹${item.totalRevenue.toInt()}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}