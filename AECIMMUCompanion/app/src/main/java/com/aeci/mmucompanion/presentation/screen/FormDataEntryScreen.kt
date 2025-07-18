package com.aeci.mmucompanion.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeci.mmucompanion.domain.model.FormField
import com.aeci.mmucompanion.domain.model.FormFieldType
import com.aeci.mmucompanion.presentation.viewmodel.FormDataEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDataEntryScreen(
    formType: String,
    viewModel: FormDataEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(formType) {
        viewModel.loadForm(formType)
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
                text = uiState.formTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                Button(
                    onClick = { viewModel.saveForm() },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { viewModel.exportPDF() },
                    enabled = !uiState.isLoading && uiState.canExport
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Export PDF",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export PDF")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Form Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Form sections
                uiState.formSections.forEach { section ->
                    item {
                        FormSectionHeader(section.title)
                    }
                    
                    items(section.fields) { field ->
                        FormFieldComponent(
                            field = field,
                            value = uiState.formData[field.fieldName] ?: "",
                            onValueChange = { newValue ->
                                viewModel.updateFieldValue(field.fieldName, newValue)
                            },
                            error = uiState.fieldErrors[field.fieldName]
                        )
                    }
                }
            }
        }
    }
    
    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
        }
    }
}

@Composable
private fun FormSectionHeader(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormFieldComponent(
    field: FormField,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (field.fieldType) {
            FormFieldType.TEXT -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.MULTILINE_TEXT -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    minLines = 3,
                    maxLines = 5,
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.NUMBER -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.INTEGER -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.DATE -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text("DD/MM/YYYY") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Open date picker */ }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.TIME -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text("HH:MM") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Open time picker */ }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Select Time")
                        }
                    },
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.DATETIME -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text("DD/MM/YYYY HH:MM") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Open datetime picker */ }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date & Time")
                        }
                    },
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.CHECKBOX -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = value.toBoolean(),
                        onCheckedChange = { onValueChange(it.toString()) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(field.label)
                }
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            FormFieldType.RADIO -> {
                Column {
                    Text(
                        text = field.label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    field.options?.forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = value == option,
                                onClick = { onValueChange(option) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                    if (error != null) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            FormFieldType.DROPDOWN -> {
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(field.label) },
                        placeholder = { Text(field.placeholder ?: "Select option") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = error != null,
                        supportingText = if (error != null) {
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        } else null
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        field.options?.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            FormFieldType.SIGNATURE -> {
                Button(
                    onClick = { /* TODO: Open signature capture */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Draw, contentDescription = "Capture Signature")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capture Signature")
                }
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            FormFieldType.PHOTO -> {
                Button(
                    onClick = { /* TODO: Open camera */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo")
                }
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            FormFieldType.BARCODE -> {
                Button(
                    onClick = { /* TODO: Open barcode scanner */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Barcode")
                }
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            FormFieldType.EQUIPMENT_ID -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "Enter equipment ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Open equipment picker */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Select Equipment")
                        }
                    },
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.SITE_CODE -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "Enter site code") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Open site picker */ }) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Select Site")
                        }
                    },
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
            
            FormFieldType.EMPLOYEE_ID -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder ?: "Enter employee ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Open employee picker */ }) {
                            Icon(Icons.Default.Person, contentDescription = "Select Employee")
                        }
                    },
                    supportingText = if (error != null) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }
        }
    }
} 