package com.hastakalashop.app.ui.screens.incomelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hastakalashop.app.data.model.Sale
import com.hastakalashop.app.data.repository.TimeRange
import com.hastakalashop.app.ui.components.ButtonSize
import com.hastakalashop.app.ui.components.ButtonVariant
import com.hastakalashop.app.ui.components.ShadcnBadge
import com.hastakalashop.app.ui.components.ShadcnButton
import com.hastakalashop.app.ui.components.ShadcnCard
import com.hastakalashop.app.viewmodel.HastaKalaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncomeLogScreen(viewModel: HastaKalaViewModel = hiltViewModel()) {
    val sales by viewModel.salesForRange.collectAsState()
    val totalRevenue by viewModel.totalRevenueForRange.collectAsState()
    val saleCount by viewModel.saleCountForRange.collectAsState()
    val range by viewModel.selectedRange.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Income Log",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Every sale, in order",
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Income — ${range.label}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${totalRevenue.toInt()}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                ShadcnBadge(text = "$saleCount sales")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (sales.isEmpty()) {
            ShadcnCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No sales for ${range.label.lowercase()} yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sales) { sale -> SaleRow(sale = sale) }
            }
        }
    }
}

@Composable
private fun SaleRow(sale: Sale) {
    ShadcnCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.productName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${sale.color} • Qty ${sale.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(sale.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "+ ₹${sale.totalAmount.toInt()}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}