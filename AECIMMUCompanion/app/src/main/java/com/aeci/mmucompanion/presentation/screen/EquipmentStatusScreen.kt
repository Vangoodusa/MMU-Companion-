package com.aeci.mmucompanion.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeci.mmucompanion.domain.model.Equipment
import com.aeci.mmucompanion.domain.model.EquipmentStatusIndicator
import com.aeci.mmucompanion.presentation.component.EquipmentStatusCard
import com.aeci.mmucompanion.presentation.viewmodel.EquipmentViewModel

@Composable
fun EquipmentStatusScreen(
    viewModel: EquipmentViewModel = hiltViewModel(),
    currentUserId: String = "default_user" // This should come from auth context
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val equipmentList by viewModel.equipmentList.collectAsStateWithLifecycle()
    
    // Show success message
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            // You can implement a snackbar here
            viewModel.clearSuccessMessage()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Equipment Status",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Status Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EquipmentStatusLegendItemRenamed(
                    color = Color(0xFF4CAF50),
                    label = "Good"
                )
                EquipmentStatusLegendItemRenamed(
                    color = Color(0xFFFF9800),
                    label = "Moderate"
                )
                EquipmentStatusLegendItemRenamed(
                    color = Color(0xFFF44336),
                    label = "Critical"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        // Error state
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        // Equipment list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(equipmentList) { equipment ->
                EquipmentStatusCard(
                    equipment = equipment,
                    onStatusChange = { statusIndicator, conditionDescription ->
                        viewModel.updateEquipmentStatus(
                            equipmentId = equipment.id,
                            statusIndicator = statusIndicator,
                            conditionDescription = conditionDescription,
                            modifiedBy = currentUserId
                        )
                    }
                )
            }
        }
        
        // Show updating overlay
        if (uiState.isUpdating) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Updating equipment status...",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentStatusLegendItemRenamed(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
