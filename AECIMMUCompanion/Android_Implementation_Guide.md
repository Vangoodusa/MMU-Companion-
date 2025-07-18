# Android Development Implementation Guide

## Overview
This document provides detailed Android implementation guidelines for the AECI MMU Companion app, focusing on architecture patterns, best practices, and avoiding common development pitfalls.

## Project Setup and Configuration

### Gradle Configuration
```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.aeci.mmu.companion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aeci.mmu.companion"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
```

### Dependencies Management
```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // PDF Generation
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:html2pdf:4.0.5")
    
    // Excel Export
    implementation("org.apache.poi:poi:5.2.4")
    implementation("org.apache.poi:poi-ooxml:5.2.4")
    
    // Image Processing
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    
    // Camera & Gallery
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    
    // Signature Capture
    implementation("com.github.gcacace:signature-pad:1.3.1")
    
    // Date/Time Picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // WorkManager for Background Tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

## Clean Architecture Implementation

### Data Layer Structure

#### Entity Models
```kotlin
// data/model/Equipment.kt
@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey val id: String,
    val name: String,
    val model: String,
    val serialNumber: String,
    val siteId: String,
    val category: EquipmentCategory,
    val status: EquipmentStatus,
    val lastServiceDate: Long?,
    val nextServiceDate: Long?,
    val operatingHours: Double,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class EquipmentCategory {
    PUMP, DRILL, GENERATOR, COMPRESSOR, VEHICLE, OTHER
}

enum class EquipmentStatus {
    OPERATIONAL, MAINTENANCE_DUE, BREAKDOWN, OUT_OF_SERVICE
}
```

#### Database Configuration
```kotlin
// data/database/AppDatabase.kt
@Database(
    entities = [
        Equipment::class,
        User::class,
        FormSubmission::class,
        MaintenanceRecord::class,
        ProductionLog::class,
        InspectionRecord::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun userDao(): UserDao
    abstract fun formDao(): FormDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun productionDao(): ProductionDao
    abstract fun inspectionDao(): InspectionDao
}

// data/database/Converters.kt
class Converters {
    @TypeConverter
    fun fromEquipmentCategory(category: EquipmentCategory): String = category.name
    
    @TypeConverter
    fun toEquipmentCategory(category: String): EquipmentCategory = 
        EquipmentCategory.valueOf(category)
    
    @TypeConverter
    fun fromEquipmentStatus(status: EquipmentStatus): String = status.name
    
    @TypeConverter
    fun toEquipmentStatus(status: String): EquipmentStatus = 
        EquipmentStatus.valueOf(status)
    
    @TypeConverter
    fun fromStringList(value: List<String>): String = Gson().toJson(value)
    
    @TypeConverter
    fun toStringList(value: String): List<String> = 
        Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
}
```

#### Repository Pattern
```kotlin
// data/repository/EquipmentRepository.kt
@Singleton
class EquipmentRepository @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val apiService: ApiService,
    private val syncManager: SyncManager
) {
    
    suspend fun getEquipmentBySite(siteId: String): Flow<List<Equipment>> {
        return equipmentDao.getEquipmentBySite(siteId)
            .onStart { 
                try {
                    syncEquipmentFromServer(siteId)
                } catch (e: Exception) {
                    // Continue with local data if sync fails
                }
            }
    }
    
    suspend fun updateEquipmentStatus(
        equipmentId: String, 
        status: EquipmentStatus
    ): Result<Unit> {
        return try {
            val equipment = equipmentDao.getEquipmentById(equipmentId)
            val updatedEquipment = equipment.copy(
                status = status,
                updatedAt = System.currentTimeMillis()
            )
            equipmentDao.updateEquipment(updatedEquipment)
            
            // Queue for sync
            syncManager.queueEquipmentUpdate(updatedEquipment)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun syncEquipmentFromServer(siteId: String) {
        if (NetworkUtils.isConnected()) {
            val serverEquipment = apiService.getEquipmentBySite(siteId)
            equipmentDao.insertAll(serverEquipment.map { it.toEntity() })
        }
    }
}
```

### Domain Layer Implementation

#### Use Cases
```kotlin
// domain/usecase/GetEquipmentBySiteUseCase.kt
class GetEquipmentBySiteUseCase @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(siteId: String): Flow<List<Equipment>> {
        return equipmentRepository.getEquipmentBySite(siteId)
    }
}

// domain/usecase/SubmitFormUseCase.kt
class SubmitFormUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val pdfGenerator: PdfGenerator,
    private val validator: FormValidator
) {
    suspend operator fun invoke(
        formData: FormData,
        formType: FormType
    ): Result<FormSubmissionResult> {
        return try {
            // Validate form data
            val validationResult = validator.validate(formData, formType)
            if (!validationResult.isValid) {
                return Result.failure(
                    ValidationException(validationResult.errors)
                )
            }
            
            // Save to local database
            val submission = FormSubmission(
                id = UUID.randomUUID().toString(),
                formType = formType,
                formData = formData,
                status = SubmissionStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )
            
            formRepository.saveFormSubmission(submission)
            
            // Generate PDF
            val pdfResult = pdfGenerator.generatePdf(formData, formType)
            
            Result.success(
                FormSubmissionResult(
                    submissionId = submission.id,
                    pdfPath = pdfResult.filePath,
                    isValid = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Presentation Layer Architecture

#### ViewModels
```kotlin
// presentation/dashboard/MillwrightDashboardViewModel.kt
@HiltViewModel
class MillwrightDashboardViewModel @Inject constructor(
    private val getEquipmentBySiteUseCase: GetEquipmentBySiteUseCase,
    private val getUserSiteUseCase: GetUserSiteUseCase,
    private val updateEquipmentStatusUseCase: UpdateEquipmentStatusUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MillwrightDashboardUiState())
    val uiState: StateFlow<MillwrightDashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val userSite = getUserSiteUseCase()
                getEquipmentBySiteUseCase(userSite.siteId)
                    .collect { equipment ->
                        _uiState.value = _uiState.value.copy(
                            equipment = equipment,
                            isLoading = false,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun updateEquipmentStatus(equipmentId: String, status: EquipmentStatus) {
        viewModelScope.launch {
            updateEquipmentStatusUseCase(equipmentId, status)
                .fold(
                    onSuccess = { loadDashboardData() },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            error = e.message
                        )
                    }
                )
        }
    }
    
    fun refreshData() {
        loadDashboardData()
    }
}

data class MillwrightDashboardUiState(
    val equipment: List<Equipment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedEquipment: Equipment? = null
)
```

#### Compose UI Components
```kotlin
// presentation/dashboard/MillwrightDashboardScreen.kt
@Composable
fun MillwrightDashboardScreen(
    viewModel: MillwrightDashboardViewModel = hiltViewModel(),
    onNavigateToForm: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Section
        DashboardHeader(
            title = "Millwright Dashboard",
            onRefresh = { viewModel.refreshData() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Equipment Status Summary
        EquipmentStatusSummary(
            equipment = uiState.equipment
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Equipment List
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error,
                    onRetry = { viewModel.refreshData() }
                )
            }
            else -> {
                EquipmentGrid(
                    equipment = uiState.equipment,
                    onEquipmentClick = { equipment ->
                        onNavigateToForm(equipment.id, equipment.category.name)
                    },
                    onStatusUpdate = { equipmentId, status ->
                        viewModel.updateEquipmentStatus(equipmentId, status)
                    }
                )
            }
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: Equipment,
    onCardClick: () -> Unit,
    onStatusUpdate: (EquipmentStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Equipment Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = equipment.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                EquipmentStatusBadge(status = equipment.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Equipment Details
            EquipmentDetailsRow(
                label = "Model",
                value = equipment.model
            )
            
            EquipmentDetailsRow(
                label = "Serial Number",
                value = equipment.serialNumber
            )
            
            EquipmentDetailsRow(
                label = "Operating Hours",
                value = "${equipment.operatingHours} hrs"
            )
            
            equipment.nextServiceDate?.let { nextService ->
                EquipmentDetailsRow(
                    label = "Next Service",
                    value = formatDate(nextService)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { onCardClick() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Details")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { onCardClick() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Service")
                }
            }
        }
    }
}

@Composable
fun EquipmentStatusBadge(status: EquipmentStatus) {
    val (color, text) = when (status) {
        EquipmentStatus.OPERATIONAL -> Color(0xFF4CAF50) to "Operational"
        EquipmentStatus.MAINTENANCE_DUE -> Color(0xFFFF9800) to "Due"
        EquipmentStatus.BREAKDOWN -> Color(0xFFF44336) to "Breakdown"
        EquipmentStatus.OUT_OF_SERVICE -> Color(0xFF9E9E9E) to "Out of Service"
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
```

## Form Implementation System

### Dynamic Form Engine
```kotlin
// presentation/forms/FormEngine.kt
@Composable
fun DynamicFormScreen(
    formConfig: FormConfiguration,
    onSubmit: (FormData) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var formData by remember { mutableStateOf(FormData()) }
    var validationErrors by remember { mutableStateOf(emptyMap<String, String>()) }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Header
        item {
            FormHeader(
                title = formConfig.title,
                subtitle = formConfig.subtitle,
                logo = formConfig.logoResource
            )
        }
        
        // Dynamic Form Fields
        items(formConfig.sections) { section ->
            FormSection(
                section = section,
                formData = formData,
                validationErrors = validationErrors,
                onFieldChange = { fieldName, value ->
                    formData = formData.copy(
                        fields = formData.fields.toMutableMap().apply {
                            put(fieldName, value)
                        }
                    )
                    
                    // Clear validation error when field is updated
                    validationErrors = validationErrors.toMutableMap().apply {
                        remove(fieldName)
                    }
                }
            )
        }
        
        // Form Actions
        item {
            FormActions(
                onSubmit = {
                    val errors = validateForm(formData, formConfig)
                    if (errors.isEmpty()) {
                        onSubmit(formData)
                    } else {
                        validationErrors = errors
                    }
                },
                onCancel = onCancel,
                isSubmitEnabled = formData.isValid()
            )
        }
    }
}

@Composable
fun FormSection(
    section: FormSection,
    formData: FormData,
    validationErrors: Map<String, String>,
    onFieldChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Header
            Text(
                text = section.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Section Fields
            section.fields.forEach { field ->
                FormField(
                    field = field,
                    value = formData.fields[field.name] ?: "",
                    error = validationErrors[field.name],
                    onValueChange = { newValue ->
                        onFieldChange(field.name, newValue)
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun FormField(
    field: FieldConfiguration,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    when (field.type) {
        FieldType.TEXT -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(field.label) },
                placeholder = { Text(field.placeholder ?: "") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                supportingText = error?.let { { Text(it) } }
            )
        }
        
        FieldType.NUMBER -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(field.label) },
                placeholder = { Text(field.placeholder ?: "") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = error != null,
                supportingText = error?.let { { Text(it) } }
            )
        }
        
        FieldType.DATE -> {
            DatePickerField(
                value = value,
                onValueChange = onValueChange,
                label = field.label,
                placeholder = field.placeholder,
                error = error
            )
        }
        
        FieldType.CHECKBOX -> {
            CheckboxField(
                checked = value.toBoolean(),
                onCheckedChange = { onValueChange(it.toString()) },
                label = field.label,
                error = error
            )
        }
        
        FieldType.DROPDOWN -> {
            DropdownField(
                value = value,
                onValueChange = onValueChange,
                label = field.label,
                options = field.options ?: emptyList(),
                error = error
            )
        }
        
        FieldType.SIGNATURE -> {
            SignatureField(
                value = value,
                onValueChange = onValueChange,
                label = field.label,
                error = error
            )
        }
        
        FieldType.MULTILINE_TEXT -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(field.label) },
                placeholder = { Text(field.placeholder ?: "") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4,
                isError = error != null,
                supportingText = error?.let { { Text(it) } }
            )
        }
    }
}
```

## PDF Generation System

### PDF Export Engine
```kotlin
// core/pdf/PdfGenerator.kt
@Singleton
class PdfGenerator @Inject constructor(
    private val context: Context,
    private val templateManager: PdfTemplateManager
) {
    
    suspend fun generatePdf(
        formData: FormData,
        formType: FormType
    ): PdfGenerationResult = withContext(Dispatchers.IO) {
        try {
            val template = templateManager.getTemplate(formType)
            val coordinates = CoordinateMapProvider.getCoordinateMap(formType)
            
            // Load template PDF
            val templateStream = context.assets.open("pdf_templates/${template.fileName}")
            val pdfDocument = PdfDocument(PdfReader(templateStream))
            
            // Create output document
            val outputFile = createOutputFile(formData, formType)
            val pdfWriter = PdfWriter(FileOutputStream(outputFile))
            val outputDocument = PdfDocument(pdfWriter)
            
            // Copy template to output
            val page = pdfDocument.getFirstPage()
            val copiedPage = page.copyTo(outputDocument)
            outputDocument.addPage(copiedPage)
            
            // Apply form data overlays
            applyFormDataOverlays(outputDocument, formData, coordinates)
            
            // Add signatures and photos
            addSignatures(outputDocument, formData, coordinates)
            addPhotos(outputDocument, formData, coordinates)
            
            // Add AECI branding
            addBranding(outputDocument)
            
            // Close documents
            outputDocument.close()
            pdfDocument.close()
            
            PdfGenerationResult.Success(outputFile.absolutePath)
            
        } catch (e: Exception) {
            PdfGenerationResult.Error(e.message ?: "PDF generation failed")
        }
    }
    
    private fun applyFormDataOverlays(
        document: PdfDocument,
        formData: FormData,
        coordinates: List<FieldCoordinate>
    ) {
        val canvas = PdfCanvas(document.getFirstPage())
        
        coordinates.forEach { coordinate ->
            val fieldValue = formData.fields[coordinate.fieldName]
            if (!fieldValue.isNullOrEmpty()) {
                when (coordinate.fieldType) {
                    FieldType.TEXT, FieldType.NUMBER, FieldType.DATE -> {
                        canvas.beginText()
                            .setFontAndSize(PdfFontFactory.createFont(), 10f)
                            .moveText(coordinate.x, coordinate.y)
                            .showText(fieldValue)
                            .endText()
                    }
                    
                    FieldType.CHECKBOX -> {
                        if (fieldValue.toBoolean()) {
                            drawCheckmark(canvas, coordinate.x, coordinate.y)
                        }
                    }
                    
                    FieldType.MULTILINE_TEXT -> {
                        drawMultilineText(canvas, fieldValue, coordinate)
                    }
                }
            }
        }
    }
    
    private fun addSignatures(
        document: PdfDocument,
        formData: FormData,
        coordinates: List<FieldCoordinate>
    ) {
        val canvas = PdfCanvas(document.getFirstPage())
        
        coordinates.filter { it.fieldType == FieldType.SIGNATURE }
            .forEach { coordinate ->
                val signatureData = formData.fields[coordinate.fieldName]
                if (!signatureData.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(signatureData, Base64.DEFAULT)
                    val imageData = ImageDataFactory.create(imageBytes)
                    val image = Image(imageData)
                    
                    image.setFixedPosition(
                        coordinate.x,
                        coordinate.y,
                        coordinate.width
                    )
                    
                    document.getFirstPage().add(image)
                }
            }
    }
    
    private fun createOutputFile(
        formData: FormData,
        formType: FormType
    ): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val fileName = "${formData.siteCode}_${formType.name}_${timestamp}_${formData.equipmentId}.pdf"
        
        val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports")
        if (!documentsDir.exists()) {
            documentsDir.mkdirs()
        }
        
        return File(documentsDir, fileName)
    }
}

sealed class PdfGenerationResult {
    data class Success(val filePath: String) : PdfGenerationResult()
    data class Error(val message: String) : PdfGenerationResult()
}
```

## Data Synchronization Strategy

### Sync Manager Implementation
```kotlin
// core/sync/SyncManager.kt
@Singleton
class SyncManager @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val workManager: WorkManager,
    private val connectivityManager: ConnectivityManager
) {
    
    fun scheduleSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(syncRequest)
    }
    
    suspend fun syncPendingData(): SyncResult {
        if (!isConnected()) {
            return SyncResult.NoConnection
        }
        
        return try {
            // Sync pending form submissions
            syncFormSubmissions()
            
            // Sync equipment updates
            syncEquipmentUpdates()
            
            // Sync user data
            syncUserData()
            
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }
    
    private suspend fun syncFormSubmissions() {
        val pendingSubmissions = database.formDao().getPendingSubmissions()
        
        pendingSubmissions.forEach { submission ->
            try {
                val response = apiService.submitForm(submission.toApiModel())
                if (response.isSuccessful) {
                    database.formDao().updateSubmissionStatus(
                        submission.id,
                        SubmissionStatus.SYNCED
                    )
                }
            } catch (e: Exception) {
                // Log error but continue with other submissions
                Timber.e(e, "Failed to sync submission ${submission.id}")
            }
        }
    }
    
    private fun isConnected(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val syncResult = syncManager.syncPendingData()
            when (syncResult) {
                is SyncResult.Success -> Result.success()
                is SyncResult.NoConnection -> Result.retry()
                is SyncResult.Error -> Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): SyncWorker
    }
}
```

## Error Handling and Validation

### Comprehensive Error Handling
```kotlin
// core/error/ErrorHandler.kt
@Singleton
class ErrorHandler @Inject constructor(
    private val crashAnalytics: CrashAnalytics
) {
    
    fun handleError(error: Throwable, context: String = ""): ErrorResult {
        // Log error for debugging
        Timber.e(error, "Error in $context")
        
        // Send to crash analytics
        crashAnalytics.recordException(error)
        
        return when (error) {
            is ValidationException -> ErrorResult.ValidationError(error.errors)
            is NetworkException -> ErrorResult.NetworkError(error.message)
            is DatabaseException -> ErrorResult.DatabaseError(error.message)
            is AuthenticationException -> ErrorResult.AuthError(error.message)
            else -> ErrorResult.UnknownError(error.message ?: "Unknown error occurred")
        }
    }
}

sealed class ErrorResult {
    data class ValidationError(val errors: List<String>) : ErrorResult()
    data class NetworkError(val message: String) : ErrorResult()
    data class DatabaseError(val message: String) : ErrorResult()
    data class AuthError(val message: String) : ErrorResult()
    data class UnknownError(val message: String) : ErrorResult()
}

// Form validation system
class FormValidator @Inject constructor() {
    
    fun validate(formData: FormData, formType: FormType): ValidationResult {
        val errors = mutableListOf<String>()
        val rules = ValidationRules.getRules(formType)
        
        rules.forEach { rule ->
            val fieldValue = formData.fields[rule.fieldName]
            val validationError = validateField(fieldValue, rule)
            if (validationError != null) {
                errors.add(validationError)
            }
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    private fun validateField(value: String?, rule: ValidationRule): String? {
        return when (rule.type) {
            ValidationType.REQUIRED -> {
                if (value.isNullOrBlank()) "Field ${rule.fieldName} is required"
                else null
            }
            
            ValidationType.MIN_LENGTH -> {
                val minLength = rule.parameters["minLength"] as Int
                if (value != null && value.length < minLength) {
                    "Field ${rule.fieldName} must be at least $minLength characters"
                } else null
            }
            
            ValidationType.NUMERIC_RANGE -> {
                val min = rule.parameters["min"] as Double
                val max = rule.parameters["max"] as Double
                val numValue = value?.toDoubleOrNull()
                
                when {
                    numValue == null -> "Field ${rule.fieldName} must be a number"
                    numValue < min -> "Field ${rule.fieldName} must be at least $min"
                    numValue > max -> "Field ${rule.fieldName} must be at most $max"
                    else -> null
                }
            }
            
            ValidationType.EMAIL -> {
                if (value != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    "Field ${rule.fieldName} must be a valid email"
                } else null
            }
            
            ValidationType.EQUIPMENT_ID -> {
                if (value != null && !value.matches(Regex("^[A-Z]{2,3}[0-9]{3,4}$"))) {
                    "Field ${rule.fieldName} must be a valid equipment ID"
                } else null
            }
        }
    }
}
```

This comprehensive Android implementation guide provides the foundation for building a robust, maintainable AECI MMU Companion app with proper architecture patterns, error handling, and best practices to avoid common development pitfalls.
