@file:OptIn(ExperimentalMaterial3Api::class)

package com.aeci.mmucompanion.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeci.mmucompanion.domain.model.Form
import com.aeci.mmucompanion.presentation.component.AECIIcons
import androidx.navigation.NavHostController
import com.aeci.mmucompanion.R
import com.aeci.mmucompanion.domain.model.Equipment
import com.aeci.mmucompanion.domain.model.EquipmentStatus
import com.aeci.mmucompanion.domain.model.EquipmentType
import com.aeci.mmucompanion.presentation.component.DynamicFormRenderer
import com.aeci.mmucompanion.presentation.viewmodel.FormViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image

// Placeholder screens - These would be fully implemented based on the requirements

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormsListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    val tabs = listOf("All Forms", "My Forms", "Drafts", "Submitted")
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search forms...") },
            leadingIcon = { Icon(AECIIcons.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> AllFormsContent(navController, searchQuery)
            1 -> MyFormsContent(navController, searchQuery)
            2 -> DraftsContent(navController, searchQuery)
            3 -> SubmittedContent(navController, searchQuery)
        }
    }
}

@Composable
private fun AllFormsContent(
    navController: NavHostController,
    searchQuery: String
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Create Section
        item {
            Text(
                text = "Quick Create",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val quickForms = listOf(
                    "Daily Log" to "MMU_DAILY_LOG",
                    "Quality Report" to "MMU_QUALITY_REPORT",
                    "Pump Check" to "PUMP_90_DAY_INSPECTION",
                    "Maintenance" to "MMU_CHASSIS_MAINTENANCE"
                )
                
                items(quickForms) { (title, formType) ->
                    QuickCreateCard(
                        title = title,
                        onClick = { navController.navigate("forms/create/$formType") }
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "All Form Types",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // All available form types
        val allFormTypes = listOf(
            FormTypeItem("MMU Daily Production Log", "Track daily production metrics", "MMU_DAILY_LOG", AECIIcons.Today),
            FormTypeItem("MMU Quality Report", "Quality control inspection", "MMU_QUALITY_REPORT", AECIIcons.Assessment),
            FormTypeItem("Pump Weekly Checklist", "Weekly pump maintenance", "PUMP_WEEKLY_CHECK", AECIIcons.Construction),
            FormTypeItem("90-Day Pump Inspection", "Quarterly pump inspection", "PUMP_90_DAY_INSPECTION", AECIIcons.Schedule),
            FormTypeItem("Fire Extinguisher Check", "Monthly safety inspection", "FIRE_EXTINGUISHER_INSPECTION", AECIIcons.FireExtinguisher),
            FormTypeItem("MMU Handover Certificate", "Shift handover documentation", "MMU_HANDOVER_CERTIFICATE", AECIIcons.Assignment),
            FormTypeItem("Pre-task Safety Check", "Pre-work safety assessment", "PRETASK_SAFETY", AECIIcons.Security),
            FormTypeItem("Blast Hole Log", "Drilling operation record", "BLAST_HOLE_LOG", AECIIcons.Construction),
            FormTypeItem("Job Card", "Work order documentation", "JOB_CARD", AECIIcons.Work),
            FormTypeItem("MMU Chassis Maintenance", "Chassis inspection record", "MMU_CHASSIS_MAINTENANCE", AECIIcons.Construction),
            FormTypeItem("On-Bench MMU Inspection", "Bench inspection checklist", "ON_BENCH_MMU_INSPECTION", AECIIcons.Checklist),
            FormTypeItem("PC Pump Pressure Test", "High/Low pressure testing", "PC_PUMP_PRESSURE_TEST", AECIIcons.Speed),
            FormTypeItem("Monthly Process Record", "Monthly process documentation", "MONTHLY_PROCESS_MAINTENANCE", AECIIcons.CalendarMonth),
            FormTypeItem("Availability & Utilization", "Equipment availability report", "AVAILABILITY_UTILIZATION", AECIIcons.Analytics)
        )
        
        items(allFormTypes) { formType ->
            FormTypeCard(
                formType = formType,
                onClick = { navController.navigate("forms/create/${formType.id}") }
            )
        }
    }
}

@Composable
private fun MyFormsContent(
    navController: NavHostController,
    searchQuery: String
) {
    // Mock data for demonstration
    val myForms = remember {
        listOf(
            FormListItem("Daily Log - Zone A", "mmu_daily_log", "COMPLETED", System.currentTimeMillis() - 3600000),
            FormListItem("Pump Check - Unit 002", "pump_inspection", "IN_PROGRESS", System.currentTimeMillis() - 7200000),
            FormListItem("Quality Report - Batch 145", "quality_report", "DRAFT", System.currentTimeMillis() - 86400000)
        )
    }
    
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (myForms.isEmpty()) {
            item {
                EmptyStateCard("No forms assigned to you")
            }
        } else {
            items(myForms) { form ->
                FormListCard(
                    form = form,
                    onClick = { navController.navigate("forms/edit/${form.id}") }
                )
            }
        }
    }
}

@Composable
private fun DraftsContent(
    navController: NavHostController,
    searchQuery: String
) {
    // Mock draft forms
    val drafts = remember {
        listOf(
            FormListItem("Daily Log - Zone B", "mmu_daily_log", "DRAFT", System.currentTimeMillis() - 86400000),
            FormListItem("Maintenance Record - MMU003", "maintenance_record", "DRAFT", System.currentTimeMillis() - 172800000)
        )
    }
    
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (drafts.isEmpty()) {
            item {
                EmptyStateCard("No draft forms")
            }
        } else {
            items(drafts) { form ->
                FormListCard(
                    form = form,
                    onClick = { navController.navigate("forms/edit/${form.id}") }
                )
            }
        }
    }
}

@Composable
private fun SubmittedContent(
    navController: NavHostController,
    searchQuery: String
) {
    // Mock submitted forms
    val submittedForms = remember {
        listOf(
            FormListItem("Daily Log - Zone A", "mmu_daily_log", "COMPLETED", System.currentTimeMillis() - 3600000),
            FormListItem("Quality Report - Batch 144", "quality_report", "COMPLETED", System.currentTimeMillis() - 90000000)
        )
    }
    
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (submittedForms.isEmpty()) {
            item {
                EmptyStateCard("No submitted forms")
            }
        } else {
            items(submittedForms) { form ->
                FormListCard(
                    form = form,
                    onClick = { navController.navigate("forms/edit/${form.id}") }
                )
            }
        }
    }
}

@Composable
private fun QuickCreateCard(
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(80.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FormTypeCard(
    formType: FormTypeItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                formType.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formType.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                AECIIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FormListCard(
    form: FormListItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = form.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = form.type.replace("_", " ").uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = when (form.status) {
                        "COMPLETED" -> MaterialTheme.colorScheme.primaryContainer
                        "IN_PROGRESS" -> MaterialTheme.colorScheme.tertiaryContainer
                        "DRAFT" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = form.status.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Modified: ${placeholderFormatDate(form.lastModified)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class FormTypeItem(
    val title: String,
    val description: String,
    val id: String,
    val icon: ImageVector
)

data class FormListItem(
    val title: String,
    val type: String,
    val status: String,
    val lastModified: Long,
    val id: String = "$type-${System.currentTimeMillis()}"
)

@Composable
fun FormCreationScreen(
    navController: NavHostController,
    formType: String?,
    modifier: Modifier = Modifier
) {
    val formViewModel: FormViewModel = hiltViewModel()
    val uiState by formViewModel.uiState.collectAsState()
    
    LaunchedEffect(formType) {
        formType?.let {
            formViewModel.initializeForm(it)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // App Bar
        Surface(
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Create Form",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Row {
                    TextButton(
                        onClick = { formViewModel.saveDraft() }
                    ) {
                        Text(
                            text = "Save Draft",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Button(
                        onClick = { formViewModel.submitForm() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
        
        // Form Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.formTemplate != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Form Header
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = uiState.formTemplate?.name ?: "Form",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (!uiState.formTemplate?.description.isNullOrBlank()) {
                                Text(
                                    text = uiState.formTemplate?.description ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
                
                // Dynamic Form Fields
                item {
                    DynamicFormRenderer(
                        template = uiState.formTemplate!!,
                        formData = uiState.formData,
                        onFieldValueChange = { fieldId, value ->
                            formViewModel.updateField(fieldId, value)
                        },
                        onValidationError = { fieldId, error ->
                            formViewModel.setFieldError(fieldId, error)
                        }
                    )
                }
                
                // Form Actions
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { formViewModel.saveDraft() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(AECIIcons.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Draft")
                        }
                        
                        Button(
                            onClick = { formViewModel.submitForm() },
                            modifier = Modifier.weight(1f),
                            enabled = true // TODO: Add form validation
                        ) {
                            Icon(AECIIcons.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
    
    // Handle state changes
    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            navController.navigateUp()
        }
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
        }
    }
}

@Composable
fun FormEditScreen(
    navController: NavHostController,
    formId: String?,
    modifier: Modifier = Modifier
) {
    val formViewModel: FormViewModel = hiltViewModel()
    val uiState by formViewModel.uiState.collectAsState()
    
    LaunchedEffect(formId) {
        formId?.let {
            formViewModel.loadForm(it)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // App Bar
        Surface(
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "Edit Form",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "ID: $formId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Row {
                    IconButton(
                        onClick = { 
                            // Share/Export functionality
                            formViewModel.exportToPdf()
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Export",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    if (uiState.currentForm?.status == com.aeci.mmucompanion.domain.model.FormStatus.DRAFT) {
                        TextButton(
                            onClick = { formViewModel.saveDraft() }
                        ) {
                            Text(
                                text = "Save",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Button(
                            onClick = { formViewModel.submitForm() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
        
        // Form Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.currentForm != null && uiState.formTemplate != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo at the top
                item {
                    Image(
                        painter = painterResource(id = R.drawable.aeci_logo),
                        contentDescription = "AECI Logo",
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        alignment = Alignment.Center
                    )
                }
                
                // Form Status Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (uiState.currentForm!!.status) {
                                com.aeci.mmucompanion.domain.model.FormStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                                com.aeci.mmucompanion.domain.model.FormStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiaryContainer
                                com.aeci.mmucompanion.domain.model.FormStatus.DRAFT -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = uiState.formTemplate!!.name,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = "Created: ${uiState.currentForm!!.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (uiState.currentForm!!.equipmentId != null) {
                                        Text(
                                            text = "Equipment: ${uiState.currentForm!!.equipmentId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = uiState.currentForm!!.status.name.replace("_", " "),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Dynamic Form Fields (read-only if completed)
                item {
                    DynamicFormRenderer(
                        template = uiState.formTemplate!!,
                        formData = uiState.formData,
                        onFieldValueChange = if (uiState.currentForm!!.status == com.aeci.mmucompanion.domain.model.FormStatus.DRAFT) {
                            { fieldId, value -> formViewModel.updateField(fieldId, value) }
                        } else null, // Read-only for completed forms
                        onValidationError = { fieldId, error ->
                            formViewModel.setFieldError(fieldId, error)
                        },
                        readOnly = uiState.currentForm!!.status != com.aeci.mmucompanion.domain.model.FormStatus.DRAFT
                    )
                }
                
                // Actions (only for draft forms)
                if (uiState.currentForm!!.status == com.aeci.mmucompanion.domain.model.FormStatus.DRAFT) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { formViewModel.saveDraft() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(AECIIcons.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Changes")
                            }
                            
                            Button(
                                onClick = { formViewModel.submitForm() },
                                modifier = Modifier.weight(1f),
                                enabled = true // TODO: Add form validation
                            ) {
                                Icon(AECIIcons.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit")
                            }
                        }
                    }
                } else {
                    // Export options for completed forms
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { formViewModel.exportToPdf() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(AECIIcons.PictureAsPdf, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export PDF")
                            }
                            
                            OutlinedButton(
                                onClick = { formViewModel.exportToExcel() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(AECIIcons.TableChart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export Excel")
                            }
                        }
                    }
                }
            }
        } else {
            // Error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        AECIIcons.Error,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Form not found",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigateUp() }
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
    
    // Handle state changes
    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            navController.navigateUp()
        }
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
        }
    }
}

@Composable
fun EquipmentListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    
    // Mock data - this would come from ViewModel
    val equipmentList = remember {
        listOf(
            Equipment(
                id = "MMU001",
                name = "Mobile Mining Unit 001",
                type = EquipmentType.MMU,
                model = "MMU-2024",
                serialNumber = "SN001234",
                location = "Zone A",
                status = EquipmentStatus.OPERATIONAL,
                manufacturer = "AECI",
                installationDate = System.currentTimeMillis() - 31536000000L, // 1 year ago
                lastMaintenanceDate = System.currentTimeMillis() - 86400000L, // 1 day ago
                nextMaintenanceDate = System.currentTimeMillis() + 604800000L, // 7 days from now
                specifications = mapOf("capacity" to "500 t/h", "power" to "200 kW"),
                operatingParameters = mapOf("speed" to "1800 rpm", "temperature" to "65°C")
            ),
            Equipment(
                id = "PUMP002",
                name = "Hydraulic Pump 002", 
                type = EquipmentType.PUMP,
                model = "HP-2023",
                serialNumber = "SN005678",
                location = "Zone B",
                status = EquipmentStatus.MAINTENANCE,
                manufacturer = "PumpCorp",
                installationDate = System.currentTimeMillis() - 15768000000L, // 6 months ago
                lastMaintenanceDate = System.currentTimeMillis() - 172800000L, // 2 days ago
                nextMaintenanceDate = System.currentTimeMillis() - 86400000L, // Overdue
                specifications = mapOf("capacity" to "200 L/min", "pressure" to "350 bar"),
                operatingParameters = mapOf("flow_rate" to "180 L/min", "pressure" to "320 bar")
            ),
            Equipment(
                id = "EXTINGUISHER003",
                name = "Fire Extinguisher 003",
                type = EquipmentType.OTHER,
                model = "FE-ABC-2024",
                serialNumber = "SN009012",
                location = "Zone C",
                status = EquipmentStatus.OPERATIONAL,
                manufacturer = "SafetyFirst",
                installationDate = System.currentTimeMillis() - 7776000000L, // 3 months ago
                lastMaintenanceDate = System.currentTimeMillis() - 259200000L, // 3 days ago
                nextMaintenanceDate = System.currentTimeMillis() + 2592000000L, // 30 days from now
                specifications = mapOf("type" to "ABC Dry Chemical", "capacity" to "9 kg"),
                operatingParameters = mapOf("pressure" to "12 bar", "weight" to "8.5 kg")
            )
        )
    }
    
    val filteredEquipment = equipmentList.filter { equipment ->
        (selectedFilter == "All" || equipment.status.name == selectedFilter) &&
        (searchQuery.isBlank() || equipment.name.contains(searchQuery, ignoreCase = true) || 
         equipment.id.contains(searchQuery, ignoreCase = true))
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search equipment...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All", "OPERATIONAL", "MAINTENANCE", "BREAKDOWN")
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.replace("_", " ")) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Equipment List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredEquipment) { equipment ->
                PlaceholderEquipmentCard(
                    equipment = equipment,
                    onClick = { navController.navigate("equipment/${equipment.id}") }
                )
            }
            
            if (filteredEquipment.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "No equipment found" else "No equipment matches your search",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderEquipmentCard(
    equipment: Equipment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = equipment.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${equipment.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Location: ${equipment.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = when (equipment.status) {
                        EquipmentStatus.OPERATIONAL -> MaterialTheme.colorScheme.primaryContainer
                        EquipmentStatus.MAINTENANCE -> MaterialTheme.colorScheme.tertiaryContainer  
                        EquipmentStatus.BREAKDOWN -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = equipment.status.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (equipment.status) {
                            EquipmentStatus.OPERATIONAL -> MaterialTheme.colorScheme.onPrimaryContainer
                            EquipmentStatus.MAINTENANCE -> MaterialTheme.colorScheme.onTertiaryContainer
                            EquipmentStatus.BREAKDOWN -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Last Inspection",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = placeholderFormatDate(equipment.lastMaintenanceDate ?: 0),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column {
                    Text(
                        text = "Next Maintenance", 
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = placeholderFormatDate(equipment.nextMaintenanceDate ?: 0),
                        style = MaterialTheme.typography.bodySmall,
                        color = if ((equipment.nextMaintenanceDate ?: 0) < System.currentTimeMillis()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}

private fun placeholderFormatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Not set"
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(timestamp))
}

@Composable
fun EquipmentDetailsScreen(
    navController: NavHostController,
    equipmentId: String?,
    modifier: Modifier = Modifier
) {
    // Mock equipment data - this would come from ViewModel
    val equipment = remember {
        Equipment(
            id = equipmentId ?: "MMU001",
            name = "Mobile Mining Unit 001",
            type = EquipmentType.MMU,
            status = EquipmentStatus.OPERATIONAL,
            location = "Zone A",
            model = "Caterpillar 994K",
            serialNumber = "CAT994K-2023-001",
            manufacturer = "Caterpillar Inc.",
            installationDate = System.currentTimeMillis() - 31536000000L, // 1 year ago
            lastMaintenanceDate = System.currentTimeMillis() - 86400000L, // 1 day ago
            nextMaintenanceDate = System.currentTimeMillis() + 604800000L, // 7 days from now
            operatingParameters = mapOf(
                "Engine Power" to "850 HP",
                "Operating Weight" to "102,000 kg",
                "Bucket Capacity" to "12.3 m³",
                "Max Speed" to "65 km/h"
            ),
            specifications = mapOf(
                "Engine Power" to "850 HP",
                "Operating Weight" to "102,000 kg",
                "Bucket Capacity" to "12.3 m³",
                "Max Speed" to "65 km/h"
            )
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Maintenance", "Inspections", "Documents")

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = equipment.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "ID: ${equipment.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Surface(
                        color = when (equipment.status) {
                            EquipmentStatus.OPERATIONAL -> MaterialTheme.colorScheme.primary
                            EquipmentStatus.MAINTENANCE -> MaterialTheme.colorScheme.tertiary
                            EquipmentStatus.BREAKDOWN -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = equipment.status.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Placeholder for image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
                    Icon(
                        imageVector = AECIIcons.Image,
                        contentDescription = "Equipment Image",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Tab Content
        when (selectedTab) {
            0 -> OverviewTab(equipment)
            1 -> MaintenanceTab(equipment)
            2 -> InspectionsTab(equipment)
            3 -> DocumentsTab(equipment)
        }
    }
}

@Composable
private fun OverviewTab(equipment: Equipment) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Uptime",
                    value = "98.5%",
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.primary
                )
                QuickStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Last Service",
                    value = "2 days ago",
                    icon = Icons.Default.Build,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        
        // Equipment Details
        item {
            Text(
                text = "Equipment Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    PlaceholderDetailRow("Equipment ID", equipment.id)
                    PlaceholderDetailRow("Model", equipment.model ?: "N/A")
                    PlaceholderDetailRow("Manufacturer", equipment.manufacturer ?: "N/A")
                    PlaceholderDetailRow("Installation Date", placeholderFormatDate(equipment.installationDate ?: 0))
                }
            }
        }
        
        // Specifications
        item {
            Text(
                text = "Specifications",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    equipment.specifications?.forEach { (key, value) ->
                        PlaceholderDetailRow(key, value.toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceTab(equipment: Equipment) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Next Maintenance
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            AECIIcons.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Next Scheduled Maintenance",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = placeholderFormatDate(equipment.nextMaintenanceDate ?: 0),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    
                    val daysUntil = ((equipment.nextMaintenanceDate ?: 0) - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
                    Text(
                        text = if (daysUntil > 0) "$daysUntil days remaining" else "${-daysUntil} days overdue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        // Maintenance History
        item {
            Text(
                text = "Maintenance History",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        items(3) { index ->
            MaintenanceHistoryCard(
                date = System.currentTimeMillis() - (index + 1) * 2592000000L, // Monthly intervals
                type = when (index) {
                    0 -> "Routine Service"
                    1 -> "Oil Change"
                    else -> "Belt Replacement"
                },
                technician = "John Smith",
                notes = "Completed successfully. All systems operational."
            )
        }
    }
}

@Composable
private fun InspectionsTab(equipment: Equipment) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Create New Inspection Button
        item {
            Button(
                onClick = { /* Navigate to inspection form */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Inspection")
            }
        }
        
        // Recent Inspections
        item {
            Text(
                text = "Recent Inspections",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        items(5) { index ->
            InspectionCard(
                date = System.currentTimeMillis() - index * 86400000L, // Daily intervals
                inspector = "Jane Doe",
                type = "Daily Inspection",
                status = if (index == 0) "COMPLETED" else "PENDING",
                findings = if (index == 1) "Minor oil leak detected" else "No issues found"
            )
        }
    }
}

@Composable 
private fun DocumentsTab(equipment: Equipment) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Equipment Documents",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        val documents = listOf(
            "Operator Manual" to "PDF",
            "Maintenance Schedule" to "PDF", 
            "Warranty Certificate" to "PDF",
            "Installation Photos" to "Images",
            "Safety Guidelines" to "PDF"
        )
        
        items(documents) { (name, type) ->
            DocumentCard(
                name = name,
                type = type,
                onClick = { /* Open document */ }
            )
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String, 
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlaceholderDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium
        )
        Text(text = value)
    }
}

@Composable
private fun MaintenanceHistoryCard(
    date: Long,
    type: String,
    technician: String,
    notes: String
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = placeholderFormatDate(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Technician: $technician",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun InspectionCard(
    date: Long,
    inspector: String,
    type: String,
    status: String,
    findings: String
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Inspector: $inspector",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = placeholderFormatDate(date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = if (status == "COMPLETED") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            if (findings.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Findings: $findings",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun DocumentCard(
    name: String,
    type: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                when (type) {
                    "PDF" -> AECIIcons.PictureAsPdf
                    "Images" -> AECIIcons.Image
                    else -> AECIIcons.InsertDriveFile
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                AECIIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReportsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Analytics", "Export", "Trends", "Compliance")
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "Reports & Analytics",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Tab Content
        when (selectedTab) {
            0 -> AnalyticsTab()
            1 -> ExportTab(navController = navController)
            2 -> TrendsTab()
            3 -> ComplianceTab()
        }
    }
}

@Composable
private fun AnalyticsTab() {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stats
        item {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnalyticsCard(
                        title = "Forms This Month",
                        value = "245",
                        change = "+12%",
                        isPositive = true,
                        icon = AECIIcons.Assignment
                    )
                }
                item {
                    AnalyticsCard(
                        title = "Equipment Uptime",
                        value = "96.8%",
                        change = "+2.1%",
                        isPositive = true,
                        icon = AECIIcons.Speed
                    )
                }
                item {
                    AnalyticsCard(
                        title = "Compliance Score",
                        value = "94%",
                        change = "-1.5%",
                        isPositive = false,
                        icon = AECIIcons.Shield
                    )
                }
            }
        }
        
        // Form Completion Chart
        item {
            Text(
                text = "Form Completion Trends",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Last 7 Days",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Simple chart representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val chartData = listOf(12, 15, 8, 20, 18, 25, 22)
                        chartData.forEachIndexed { index, value ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height((value * 4).dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                ) {}
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Equipment Status Distribution
        item {
            Text(
                text = "Equipment Status",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    PlaceholderEquipmentStatusRow("Operational", 85, MaterialTheme.colorScheme.primary)
                    PlaceholderEquipmentStatusRow("Maintenance", 12, MaterialTheme.colorScheme.tertiary)
                    PlaceholderEquipmentStatusRow("Out of Service", 3, MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun ExportTab(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Export Reports",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Date Range Selector
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Date Range",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = "2024-01-01",
                            onValueChange = { },
                            label = { Text("From") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            trailingIcon = {
                                Icon(AECIIcons.CalendarToday, contentDescription = null)
                            }
                        )
                        OutlinedTextField(
                            value = "2024-01-31",
                            onValueChange = { },
                            label = { Text("To") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            trailingIcon = {
                                Icon(AECIIcons.CalendarToday, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
        
        // Export Options
        item {
            Text(
                text = "Available Reports",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val exportOptions = listOf(
                ExportOption("Form Summary Report", "Complete overview of all forms", "PDF, Excel"),
                ExportOption("Equipment Report", "Equipment status and maintenance", "PDF, Excel"), 
                ExportOption("Compliance Report", "Safety and compliance metrics", "PDF"),
                ExportOption("Production Report", "Daily production statistics", "Excel"),
                ExportOption("Maintenance Schedule", "Upcoming maintenance tasks", "PDF, Excel")
            )
            
            exportOptions.forEach { option ->
                ExportOptionCard(
                    option = option,
                    onExportClick = { navController.navigate("export") }
                )
            }
        }
    }
}

@Composable
private fun TrendsTab() {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Trend Analysis",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Production Trends
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Production Trends",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TrendIndicator("This Month", "1,245 tons", "+8.5%", true)
                        TrendIndicator("This Week", "312 tons", "+12.1%", true)
                        TrendIndicator("Yesterday", "67 tons", "-3.2%", false)
                    }
                }
            }
        }
        
        // Safety Trends
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Safety Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TrendIndicator("Incidents", "0", "0%", true)
                        TrendIndicator("Safety Score", "98%", "+1.2%", true)
                        TrendIndicator("Compliance", "96%", "-0.8%", false)
                    }
                }
            }
        }
        
        // Efficiency Trends
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Efficiency Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TrendIndicator("Equipment Utilization", "87%", "+3.5%", true)
                        TrendIndicator("Form Completion Rate", "94%", "+2.1%", true)
                        TrendIndicator("Response Time", "12 min", "-15%", true)
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplianceTab() {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Compliance Overview",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Overall Compliance Score
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Overall Compliance Score",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "94%",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Excellent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        // Compliance Categories
        item {
            Text(
                text = "By Category",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val complianceCategories = listOf(
                ComplianceCategory("Safety Inspections", 98, MaterialTheme.colorScheme.primary),
                ComplianceCategory("Equipment Maintenance", 92, MaterialTheme.colorScheme.tertiary),
                ComplianceCategory("Documentation", 96, MaterialTheme.colorScheme.secondary),
                ComplianceCategory("Training Records", 89, MaterialTheme.colorScheme.error)
            )
            
            complianceCategories.forEach { category ->
                ComplianceCategoryCard(category)
            }
        }
        
        // Recent Issues
        item {
            Text(
                text = "Recent Issues",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val issues = listOf(
                "MMU-003: Overdue maintenance documentation",
                "PUMP-007: Missing safety inspection record",
                "Zone-B: Fire extinguisher check not completed"
            )
            
            if (issues.isEmpty()) {
                Card {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No compliance issues",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            } else {
                issues.forEach { issue ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = issue,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.width(150.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = change,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PlaceholderEquipmentStatusRow(
    label: String,
    percentage: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        LinearProgressIndicator(
            progress = percentage / 100f,
            color = color,
            modifier = Modifier
                .width(100.dp)
                .height(8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExportOptionCard(
    option: ExportOption,
    onExportClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = option.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Formats: ${option.formats}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onExportClick,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text("Export")
                }
            }
        }
    }
}

@Composable
private fun TrendIndicator(
    label: String,
    value: String,
    change: String,
    isPositive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = change,
            style = MaterialTheme.typography.labelSmall,
            color = if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ComplianceCategoryCard(category: ComplianceCategory) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall
                )
                LinearProgressIndicator(
                    progress = category.score / 100f,
                    color = category.color,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${category.score}%",
                style = MaterialTheme.typography.titleMedium,
                color = category.color
            )
        }
    }
}

data class ExportOption(
    val title: String,
    val description: String,
    val formats: String
)

data class ComplianceCategory(
    val name: String,
    val score: Int,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: com.aeci.mmucompanion.presentation.viewmodel.AuthViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { authViewModel.logout() }
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun FormsCategoryScreen(
    navController: NavHostController,
    category: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "${category.capitalize()} Forms",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Form Types for the category
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getFormTypesForCategory(category)) { formType ->
                FormTypeCard(
                    formType = formType,
                    onClick = { 
                        navController.navigate("forms/create/${formType.formType}")
                    }
                )
            }
        }
    }
}

@Composable
private fun FormTypeCard(
    formType: FormTypeInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = formType.icon,
                contentDescription = formType.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formType.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Icon(
                imageVector = AECIIcons.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

data class FormTypeInfo(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val formType: String
)

fun getFormTypesForCategory(category: String): List<FormTypeInfo> {
    return when (category.lowercase()) {
        "production" -> listOf(
            FormTypeInfo(
                "MMU Daily Log",
                "Record daily mining unit operations and activities",
                AECIIcons.Today,
                "MMU_DAILY_LOG"
            ),
            FormTypeInfo(
                "MMU Quality Report",
                "Document quality control checks and results",
                AECIIcons.Assessment,
                "MMU_QUALITY_REPORT"
            ),
            FormTypeInfo(
                "MMU Handover Certificate",
                "Record shift handover information and status",
                AECIIcons.Assignment,
                "MMU_HANDOVER_CERTIFICATE"
            ),
            FormTypeInfo(
                "Availability & Utilization",
                "Track equipment availability and utilization metrics",
                AECIIcons.Analytics,
                "AVAILABILITY_UTILIZATION"
            ),
            FormTypeInfo(
                "Blast Hole Log",
                "Document blast hole drilling operations",
                AECIIcons.Construction,
                "BLAST_HOLE_LOG"
            ),
            FormTypeInfo(
                "Pre-task Safety",
                "Complete safety checks before task execution",
                AECIIcons.Security,
                "PRETASK_SAFETY"
            )
        )
        "maintenance" -> listOf(
            FormTypeInfo(
                "MMU Chassis Maintenance",
                "Record maintenance activities on MMU chassis",
                AECIIcons.Construction,
                "MMU_CHASSIS_MAINTENANCE"
            ),
            FormTypeInfo(
                "On Bench MMU Inspection",
                "Perform detailed inspection of bench-mounted equipment",
                AECIIcons.Assessment,
                "ON_BENCH_MMU_INSPECTION"
            ),
            FormTypeInfo(
                "90 Day Pump Inspection",
                "Quarterly comprehensive pump system inspection",
                AECIIcons.Schedule,
                "PUMP_90_DAY_INSPECTION"
            ),
            FormTypeInfo(
                "Pump Weekly Check",
                "Weekly routine pump maintenance checks",
                AECIIcons.Checklist,
                "PUMP_WEEKLY_CHECK"
            ),
            FormTypeInfo(
                "PC Pump Pressure Test",
                "Test pump pressure systems and safety mechanisms",
                AECIIcons.Speed,
                "PC_PUMP_PRESSURE_TEST"
            ),
            FormTypeInfo(
                "Fire Extinguisher Inspection",
                "Monthly fire safety equipment inspection",
                AECIIcons.FireExtinguisher,
                "FIRE_EXTINGUISHER_INSPECTION"
            ),
            FormTypeInfo(
                "Monthly Process Maintenance",
                "Monthly maintenance of process equipment",
                AECIIcons.CalendarMonth,
                "MONTHLY_PROCESS_MAINTENANCE"
            ),
            FormTypeInfo(
                "Job Card",
                "Create and track maintenance work orders",
                AECIIcons.Work,
                "JOB_CARD"
            )
        )
        else -> emptyList()
    }
}
