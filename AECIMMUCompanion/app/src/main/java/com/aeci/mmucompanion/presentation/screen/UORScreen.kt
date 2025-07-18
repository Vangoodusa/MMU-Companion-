@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.aeci.mmucompanion.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeci.mmucompanion.presentation.viewmodel.FormViewModel
import com.aeci.mmucompanion.domain.model.FormType
import com.aeci.mmucompanion.presentation.component.DynamicFormRenderer
import com.aeci.mmucompanion.presentation.component.DynamicFormRendererWithValidation

@Composable
fun UORScreen(
    formViewModel: FormViewModel = hiltViewModel()
) {
    val uiState by formViewModel.uiState.collectAsState()
    
    // Initialize form if not already done
    if (uiState.formTemplate == null && !uiState.isLoading) {
        formViewModel.initializeForm(FormType.UOR_REPORT.name)
    }

    uiState.formTemplate?.let { template ->
        DynamicFormRendererWithValidation(
            sections = template.sections,
            formData = uiState.formData,
            validationErrors = uiState.validationErrors,
            onFieldValueChanged = { fieldId, value ->
                formViewModel.updateField(fieldId, value)
            },
            onPhotoCapture = { fieldId -> 
                // TODO: Implement photo capture
            },
            onDatePicker = { fieldId, date ->
                // TODO: Implement date picker
            },
            onTimePicker = { fieldId, time ->
                // TODO: Implement time picker
            }
        )
    }
} 