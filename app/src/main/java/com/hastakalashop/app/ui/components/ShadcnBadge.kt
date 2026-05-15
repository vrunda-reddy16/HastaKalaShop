package com.hastakalashop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BadgeVariant { Default, Secondary, Outline, Destructive }

@Composable
fun ShadcnBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default
) {
    val (bg, fg, border) = when (variant) {
        BadgeVariant.Default -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary,
            Color.Transparent
        )
        BadgeVariant.Secondary -> Triple(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary,
            Color.Transparent
        )
        BadgeVariant.Outline -> Triple(
            Color.Transparent,
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.outline
        )
        BadgeVariant.Destructive -> Triple(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError,
            Color.Transparent
        )
    }
    Text(
        text = text,
        modifier = modifier
            .background(color = bg, shape = RoundedCornerShape(6.dp))
            .border(width = 1.dp, color = border, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 2.dp),
        color = fg,
        style = MaterialTheme.typography.labelSmall
    )
}