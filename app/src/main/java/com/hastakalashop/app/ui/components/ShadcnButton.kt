package com.hastakalashop.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ButtonVariant { Default, Secondary, Outline, Ghost, Destructive }
enum class ButtonSize { Default, Sm, Lg, Icon }

@Composable
fun ShadcnButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val height = when (size) {
        ButtonSize.Sm -> 36.dp
        ButtonSize.Lg -> 44.dp
        ButtonSize.Icon -> 40.dp
        ButtonSize.Default -> 40.dp
    }
    val padding = when (size) {
        ButtonSize.Sm -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ButtonSize.Lg -> PaddingValues(horizontal = 32.dp, vertical = 8.dp)
        ButtonSize.Icon -> PaddingValues(0.dp)
        ButtonSize.Default -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    }
    val shape = RoundedCornerShape(8.dp)

    when (variant) {
        ButtonVariant.Default -> Button(
            onClick = onClick, modifier = modifier.height(height), enabled = enabled,
            shape = shape, contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) { ButtonContent(text, leadingIcon) }

        ButtonVariant.Secondary -> Button(
            onClick = onClick, modifier = modifier.height(height), enabled = enabled,
            shape = shape, contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) { ButtonContent(text, leadingIcon) }

        ButtonVariant.Outline -> OutlinedButton(
            onClick = onClick, modifier = modifier.height(height), enabled = enabled,
            shape = shape, contentPadding = padding,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) { ButtonContent(text, leadingIcon) }

        ButtonVariant.Ghost -> TextButton(
            onClick = onClick, modifier = modifier.height(height), enabled = enabled,
            shape = shape, contentPadding = padding,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) { ButtonContent(text, leadingIcon) }

        ButtonVariant.Destructive -> Button(
            onClick = onClick, modifier = modifier.height(height), enabled = enabled,
            shape = shape, contentPadding = padding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) { ButtonContent(text, leadingIcon) }
    }
}

@Composable
private fun ButtonContent(text: String, leadingIcon: ImageVector?) {
    Row {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}