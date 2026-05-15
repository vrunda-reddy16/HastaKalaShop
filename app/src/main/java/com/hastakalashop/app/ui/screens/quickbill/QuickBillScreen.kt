package com.hastakalashop.app.ui.screens.quickbill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hastakalashop.app.data.model.Product
import com.hastakalashop.app.ui.components.BadgeVariant
import com.hastakalashop.app.ui.components.ButtonVariant
import com.hastakalashop.app.ui.components.ShadcnBadge
import com.hastakalashop.app.ui.components.ShadcnButton
import com.hastakalashop.app.ui.components.ShadcnCard
import com.hastakalashop.app.viewmodel.HastaKalaViewModel
import kotlinx.coroutines.launch

@Composable
fun QuickBillScreen(viewModel: HastaKalaViewModel = hiltViewModel()) {
    val products by viewModel.products.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Bill",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Tap a product to log a sale",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    ProductTile(
                        product = product,
                        selected = selectedProduct?.id == product.id,
                        onClick = {
                            selectedProduct = product
                            quantity = 1
                        }
                    )
                }
            }
            if (selectedProduct != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ShadcnCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedProduct!!.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${selectedProduct!!.color} • ₹${selectedProduct!!.pricePerUnit.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Quantity:",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                        ShadcnButton(
                            text = "−",
                            onClick = { if (quantity > 1) quantity-- },
                            variant = ButtonVariant.Outline
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ShadcnButton(
                            text = "+",
                            onClick = {
                                if (quantity < selectedProduct!!.stock) quantity++
                            },
                            variant = ButtonVariant.Outline
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ₹${(selectedProduct!!.pricePerUnit * quantity).toInt()}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        ShadcnButton(
                            text = "Save Sale",
                            leadingIcon = Icons.Outlined.CheckCircle,
                            onClick = {
                                val product = selectedProduct!!
                                viewModel.recordSale(product, quantity)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Logged $quantity × ${product.name}"
                                    )
                                }
                                selectedProduct = null
                                quantity = 1
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductTile(
    product: Product,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outline
    val borderWidth = if (selected) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = product.stock > 0) { onClick() }
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = iconFor(product.iconName),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = product.color,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${product.pricePerUnit.toInt()}",
                    style = MaterialTheme.typography.labelLarge
                )
                ShadcnBadge(
                    text = "${product.stock} left",
                    variant = if (product.stock <= 5)
                        BadgeVariant.Destructive
                    else
                        BadgeVariant.Secondary
                )
            }
        }
    }
}

private fun iconFor(name: String): ImageVector = when (name) {
    "ShoppingBag" -> Icons.Outlined.ShoppingBag
    "VpnKey" -> Icons.Outlined.VpnKey
    "Backpack" -> Icons.Outlined.Backpack
    "Spa" -> Icons.Outlined.Spa
    else -> Icons.Outlined.ShoppingBag
}