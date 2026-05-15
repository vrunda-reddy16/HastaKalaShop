package com.hastakalashop.app.ui.screens.aiinsights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hastakalashop.app.ui.components.AiSuggestionCard
import com.hastakalashop.app.ui.components.ShadcnButton
import com.hastakalashop.app.viewmodel.HastaKalaViewModel

@Composable
fun AiInsightsScreen(viewModel: HastaKalaViewModel = hiltViewModel()) {
    val planState by viewModel.aiPlanState.collectAsState()
    val bestSellerState by viewModel.aiBestSellerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI Coach",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Powered by Gemini — smart suggestions from your sales",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        AiSuggestionCard(
            modifier = Modifier.fillMaxWidth(),
            title = "What to make next week",
            body = planState.suggestion,
            isLoading = planState.isLoading,
            error = planState.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShadcnButton(
            text = if (planState.suggestion == null)
                "Get AI Suggestion" else "Refresh Suggestion",
            leadingIcon = Icons.Outlined.AutoAwesome,
            onClick = { viewModel.fetchAiProductionPlan() },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        AiSuggestionCard(
            modifier = Modifier.fillMaxWidth(),
            title = "About your top product",
            body = bestSellerState.suggestion,
            isLoading = bestSellerState.isLoading,
            error = bestSellerState.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShadcnButton(
            text = if (bestSellerState.suggestion == null)
                "Analyze Best Seller" else "Refresh Insight",
            leadingIcon = Icons.Outlined.AutoAwesome,
            onClick = { viewModel.fetchAiBestSellerInsight() },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}