package com.hastakalashop.app.ui.screens.manageproducts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
fun ManageProductsScreen(viewModel: HastaKalaViewModel = hiltViewModel()) {

    val products by viewModel.products.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showStockDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Manage Products",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Add, edit, update stock or delete",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ShadcnButton(
                    text = "Add New",
                    leadingIcon = Icons.Outlined.Add,
                    onClick = { showAddDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product list
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(products) { product ->
                    ProductManageCard(
                        product = product,
                        onEdit = {
                            selectedProduct = product
                            showEditDialog = true
                        },
                        onUpdateStock = {
                            selectedProduct = product
                            showStockDialog = true
                        },
                        onDelete = {
                            selectedProduct = product
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // ===== ADD PRODUCT DIALOG =====
    if (showAddDialog) {
        AddEditProductDialog(
            title = "Add New Product",
            product = null,
            onConfirm = { name, color, price, stock ->
                viewModel.addProduct(
                    Product(
                        name = name,
                        color = color,
                        pricePerUnit = price,
                        stock = stock,
                        iconName = "ShoppingBag"
                    )
                )
                scope.launch {
                    snackbarHostState.showSnackbar("Product added successfully")
                }
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // ===== EDIT PRODUCT DIALOG =====
    if (showEditDialog && selectedProduct != null) {
        AddEditProductDialog(
            title = "Edit Product",
            product = selectedProduct,
            onConfirm = { name, color, price, stock ->
                viewModel.updateProduct(
                    selectedProduct!!.copy(
                        name = name,
                        color = color,
                        pricePerUnit = price,
                        stock = stock
                    )
                )
                scope.launch {
                    snackbarHostState.showSnackbar("Product updated successfully")
                }
                showEditDialog = false
                selectedProduct = null
            },
            onDismiss = {
                showEditDialog = false
                selectedProduct = null
            }
        )
    }

    // ===== UPDATE STOCK DIALOG =====
    if (showStockDialog && selectedProduct != null) {
        UpdateStockDialog(
            product = selectedProduct!!,
            onConfirm = { newStock ->
                viewModel.updateStock(selectedProduct!!.id, newStock)
                scope.launch {
                    snackbarHostState.showSnackbar("Stock updated to $newStock")
                }
                showStockDialog = false
                selectedProduct = null
            },
            onDismiss = {
                showStockDialog = false
                selectedProduct = null
            }
        )
    }

    // ===== DELETE CONFIRM DIALOG =====
    if (showDeleteDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Product",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete " +
                            "${selectedProduct!!.name} (${selectedProduct!!.color})?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                ShadcnButton(
                    text = "Delete",
                    variant = ButtonVariant.Destructive,
                    onClick = {
                        viewModel.deleteProduct(selectedProduct!!.id)
                        scope.launch {
                            snackbarHostState.showSnackbar("Product deleted")
                        }
                        showDeleteDialog = false
                        selectedProduct = null
                    }
                )
            },
            dismissButton = {
                ShadcnButton(
                    text = "Cancel",
                    variant = ButtonVariant.Outline,
                    onClick = {
                        showDeleteDialog = false
                        selectedProduct = null
                    }
                )
            }
        )
    }
}

// ===== PRODUCT CARD =====
@Composable
private fun ProductManageCard(
    product: Product,
    onEdit: () -> Unit,
    onUpdateStock: () -> Unit,
    onDelete: () -> Unit
) {
    ShadcnCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${product.color} • ₹${product.pricePerUnit.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ShadcnBadge(
                text = "Stock: ${product.stock}",
                variant = if (product.stock <= 5)
                    BadgeVariant.Destructive else BadgeVariant.Secondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ShadcnButton(
                text = "Edit",
                leadingIcon = Icons.Outlined.Edit,
                onClick = onEdit,
                variant = ButtonVariant.Outline
            )
            ShadcnButton(
                text = "Stock",
                onClick = onUpdateStock,
                variant = ButtonVariant.Secondary
            )
            ShadcnButton(
                text = "Delete",
                leadingIcon = Icons.Outlined.Delete,
                onClick = onDelete,
                variant = ButtonVariant.Destructive
            )
        }
    }
}

// ===== ADD / EDIT DIALOG =====
@Composable
private fun AddEditProductDialog(
    title: String,
    product: Product?,
    onConfirm: (String, String, Double, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var color by remember { mutableStateOf(product?.color ?: "") }
    var price by remember { mutableStateOf(product?.pricePerUnit?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price per unit (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            ShadcnButton(
                text = "Save",
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    val s = stock.toIntOrNull() ?: 0
                    if (name.isNotBlank() && color.isNotBlank()) {
                        onConfirm(name, color, p, s)
                    }
                }
            )
        },
        dismissButton = {
            ShadcnButton(
                text = "Cancel",
                variant = ButtonVariant.Outline,
                onClick = onDismiss
            )
        }
    )
}

// ===== UPDATE STOCK DIALOG =====
@Composable
private fun UpdateStockDialog(
    product: Product,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var newStock by remember { mutableStateOf(product.stock) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Stock",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "${product.name} (${product.color})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShadcnButton(
                        text = "−",
                        onClick = { if (newStock > 0) newStock-- },
                        variant = ButtonVariant.Outline
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = newStock.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    ShadcnButton(
                        text = "+",
                        onClick = { newStock++ },
                        variant = ButtonVariant.Outline
                    )
                }
            }
        },
        confirmButton = {
            ShadcnButton(
                text = "Update",
                onClick = { onConfirm(newStock) }
            )
        },
        dismissButton = {
            ShadcnButton(
                text = "Cancel",
                variant = ButtonVariant.Outline,
                onClick = onDismiss
            )
        }
    )
}