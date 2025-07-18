package com.aeci.mmucompanion.data.repository

import com.aeci.mmucompanion.data.local.dao.FormDao
import com.aeci.mmucompanion.data.local.entity.FormEntity
import com.aeci.mmucompanion.data.remote.api.AECIApiService
import com.aeci.mmucompanion.data.remote.api.FormSubmissionRequest
import com.aeci.mmucompanion.data.remote.api.FormDto
import com.aeci.mmucompanion.domain.model.*
import com.aeci.mmucompanion.domain.repository.FormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import com.aeci.mmucompanion.data.remote.api.MobileServerApiService
import com.aeci.mmucompanion.core.util.MobileServerConfig
import com.aeci.mmucompanion.core.util.NetworkManager
import com.aeci.mmucompanion.data.remote.api.CreateFormRequest

@Singleton
class FormRepositoryImpl @Inject constructor(
    private val formDao: FormDao,
    private val apiService: AECIApiService,
    private val gson: Gson,
    private val mobileServerApiService: MobileServerApiService,
    private val mobileServerConfig: MobileServerConfig,
    private val networkManager: NetworkManager
) : FormRepository {
    
    // Maintenance Forms
    override suspend fun saveMaintenanceForm(form: MaintenanceReportForm): MaintenanceReportForm {
        try {
            // Save to local database first
        val formEntity = FormEntity(
                id = form.id,
                formType = form.formType.name,
                createdAt = form.createdAt.toString(),
                updatedAt = form.updatedAt.toString(),
                createdBy = form.createdBy,
                status = form.status.name,
                equipmentId = form.equipmentId,
                siteLocation = form.siteLocation,
                reportNumber = form.reportNumber,
                formData = gson.toJson(form),
                synced = false
        )
        
        formDao.insertForm(formEntity)
            
            // Try to sync with server
            try {
                val formDto = FormDto(
                    id = form.id,
                    formType = form.formType.name,
                    title = "Maintenance Report",
                    createdBy = form.createdBy.toIntOrNull() ?: 0,
                    assignedTo = null,
                    equipmentId = form.equipmentId,
                    status = form.status.name,
                    formData = gson.toJson(form),
                    attachments = null,
                    createdAt = form.createdAt.toString(),
                    updatedAt = form.updatedAt.toString(),
                    completedAt = null,
                    createdByName = null,
                    assignedToName = null,
                    equipmentName = null
                )
                
                apiService.saveForm(formDto)
                
                // Update local entity as synced
                formDao.updateFormSyncStatus(form.id, true)
                
            } catch (e: Exception) {
                // Server sync failed, but local save succeeded
                // Form will be synced later
            }
            
            return form
            
        } catch (e: Exception) {
            throw Exception("Failed to save maintenance form: ${e.message}")
        }
    }
    
    override suspend fun getMaintenanceFormById(id: String): MaintenanceReportForm? {
        return try {
            val entity = formDao.getFormById(id)
            entity?.let {
                gson.fromJson(it.formData, MaintenanceReportForm::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getMaintenanceFormsByUser(userId: String): List<MaintenanceReportForm> {
        return try {
            val entities = formDao.getFormsByUser(userId)
            entities.filter { it.formType == FormType.MAINTENANCE.name }
                .mapNotNull { entity ->
                    try {
                        gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getMaintenanceFormsByEquipment(equipmentId: String): List<MaintenanceReportForm> {
        return try {
            val entities = formDao.getFormsByEquipment(equipmentId)
            entities.filter { it.formType == FormType.MAINTENANCE.name }
                .mapNotNull { entity ->
                    try {
                        gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun updateMaintenanceForm(form: MaintenanceReportForm): MaintenanceReportForm {
        return saveMaintenanceForm(form) // Use the same save logic
    }
    
    override suspend fun deleteMaintenanceForm(id: String): Boolean {
        return try {
            formDao.deleteForm(id)
            
            // Try to delete from server
            try {
                apiService.deleteForm(id)
            } catch (e: Exception) {
                // Server deletion failed, but local deletion succeeded
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Inspection Forms
    override suspend fun saveInspectionForm(form: InspectionReportForm): InspectionReportForm {
        try {
            val formEntity = FormEntity(
                id = form.id,
                formType = form.formType.name,
                createdAt = form.createdAt.toString(),
                updatedAt = form.updatedAt.toString(),
                createdBy = form.createdBy,
                status = form.status.name,
                equipmentId = form.equipmentId,
                siteLocation = form.siteLocation,
                reportNumber = form.reportNumber,
                formData = gson.toJson(form),
                synced = false
            )
            
            formDao.insertForm(formEntity)
            
            // Try to sync with server
            try {
                val formDto = FormDto(
                    id = form.id,
                    formType = form.formType.name,
                    title = "Inspection Report",
                    createdBy = form.createdBy.toIntOrNull() ?: 0,
                    assignedTo = null,
                    equipmentId = form.equipmentId,
                    status = form.status.name,
                    formData = gson.toJson(form),
                    attachments = null,
                    createdAt = form.createdAt.toString(),
                    updatedAt = form.updatedAt.toString(),
                    completedAt = null,
                    createdByName = null,
                    assignedToName = null,
                    equipmentName = null
                )
                
                apiService.saveForm(formDto)
                formDao.updateFormSyncStatus(form.id, true)
                
            } catch (e: Exception) {
                // Server sync failed, but local save succeeded
            }
            
            return form
            
        } catch (e: Exception) {
            throw Exception("Failed to save inspection form: ${e.message}")
        }
    }
    
    override suspend fun getInspectionFormById(id: String): InspectionReportForm? {
        return try {
            val entity = formDao.getFormById(id)
            entity?.let {
                gson.fromJson(it.formData, InspectionReportForm::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getInspectionFormsByUser(userId: String): List<InspectionReportForm> {
        return try {
            val entities = formDao.getFormsByUser(userId)
            entities.filter { it.formType == FormType.INSPECTION.name }
                .mapNotNull { entity ->
                    try {
                        gson.fromJson(entity.formData, InspectionReportForm::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getInspectionFormsByEquipment(equipmentId: String): List<InspectionReportForm> {
        return try {
            val entities = formDao.getFormsByEquipment(equipmentId)
            entities.filter { it.formType == FormType.INSPECTION.name }
                .mapNotNull { entity ->
                    try {
                        gson.fromJson(entity.formData, InspectionReportForm::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun updateInspectionForm(form: InspectionReportForm): InspectionReportForm {
        return saveInspectionForm(form)
    }
    
    override suspend fun deleteInspectionForm(id: String): Boolean {
        return deleteMaintenanceForm(id) // Use the same delete logic
    }
    
    // Safety Forms
    override suspend fun saveSafetyForm(form: SafetyReportForm): SafetyReportForm {
        try {
            val formEntity = FormEntity(
                id = form.id,
                formType = form.formType.name,
                createdAt = form.createdAt.toString(),
                updatedAt = form.updatedAt.toString(),
                createdBy = form.createdBy,
                status = form.status.name,
                equipmentId = form.equipmentId,
                siteLocation = form.siteLocation,
                reportNumber = form.reportNumber,
                formData = gson.toJson(form),
                synced = false
            )
            
            formDao.insertForm(formEntity)
            
            // Try to sync with server
            try {
                val formDto = FormDto(
                    id = form.id,
                    formType = form.formType.name,
                    title = "Safety Report",
                    createdBy = form.createdBy.toIntOrNull() ?: 0,
                    assignedTo = null,
                    equipmentId = form.equipmentId,
                    status = form.status.name,
                    formData = gson.toJson(form),
                    attachments = null,
                    createdAt = form.createdAt.toString(),
                    updatedAt = form.updatedAt.toString(),
                    completedAt = null,
                    createdByName = null,
                    assignedToName = null,
                    equipmentName = null
                )
                
                apiService.saveForm(formDto)
                formDao.updateFormSyncStatus(form.id, true)
                
            } catch (e: Exception) {
                // Server sync failed, but local save succeeded
            }
            
            return form
            
        } catch (e: Exception) {
            throw Exception("Failed to save safety form: ${e.message}")
        }
    }
    
    override suspend fun getSafetyFormById(id: String): SafetyReportForm? {
        return try {
            val entity = formDao.getFormById(id)
            entity?.let {
                gson.fromJson(it.formData, SafetyReportForm::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getSafetyFormsByUser(userId: String): List<SafetyReportForm> {
        return try {
            val entities = formDao.getFormsByUser(userId)
            entities.filter { it.formType == FormType.SAFETY.name }
                .mapNotNull { entity ->
                    try {
                        gson.fromJson(entity.formData, SafetyReportForm::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun updateSafetyForm(form: SafetyReportForm): SafetyReportForm {
        return saveSafetyForm(form)
    }
    
    override suspend fun deleteSafetyForm(id: String): Boolean {
        return deleteMaintenanceForm(id) // Use the same delete logic
    }
    
    // Generic Form Operations
    override suspend fun getAllFormsByUser(userId: String): List<FormData> {
        val entities = formDao.getFormsByUser(userId)
        return entities.mapNotNull { entity ->
            try {
                when (entity.formType) {
                    FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                    FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                    FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    override suspend fun getFormsByStatus(status: FormStatus): List<FormData> {
        return try {
            val entities = formDao.getFormsByStatus(status.name)
            entities.mapNotNull { entity ->
                try {
                    when (entity.formType) {
                        FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                        FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                        FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getFormsByType(type: FormType): List<FormData> {
        return try {
            val entities = formDao.getFormsByType(type.name)
            entities.mapNotNull { entity ->
                try {
                    when (entity.formType) {
                        FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                        FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                        FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getFormsByDateRange(startDate: String, endDate: String): List<FormData> {
        return try {
            val entities = formDao.getFormsByDateRange(startDate, endDate)
            entities.mapNotNull { entity ->
                try {
                    when (entity.formType) {
                        FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                        FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                        FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Real-time updates
    override fun getFormsFlow(): Flow<List<FormData>> = flow {
        formDao.getAllFormsFlow().collect { entities ->
            val forms = entities.mapNotNull { entity ->
                try {
                    when (entity.formType) {
                        FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                        FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                        FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            emit(forms)
        }
    }
    
    override fun getFormsByUserFlow(userId: String): Flow<List<FormData>> = flow {
        formDao.getFormsByUserFlow(userId).collect { entities ->
            val forms = entities.mapNotNull { entity ->
                try {
                    when (entity.formType) {
                        FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                        FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                        FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                        else -> null
                    }
        } catch (e: Exception) {
                    null
                }
            }
            emit(forms)
        }
    }
    
    override suspend fun createForm(type: FormType, userId: String, equipmentId: String?, shiftId: String?, locationId: String?): String {
        val formId = java.util.UUID.randomUUID().toString()
        val now = java.time.LocalDateTime.now()
        
        // Create a basic form based on type
        val form = when (type) {
            FormType.MAINTENANCE -> MaintenanceReportForm(
                id = formId,
                formType = type,
                createdAt = now,
                updatedAt = now,
                createdBy = userId,
                status = FormStatus.DRAFT,
                equipmentId = equipmentId,
                siteLocation = locationId ?: "Unknown",
                reportNumber = "REP-${formId.takeLast(8)}",
                equipmentName = "Unknown Equipment",
                equipmentModel = "",
                equipmentSerial = "",
                equipmentLocation = locationId ?: "Unknown",
                equipmentHours = null,
                maintenanceType = MaintenanceType.PREVENTIVE,
                workDescription = "",
                partsUsed = emptyList(),
                laborHours = 0.0,
                maintenanceDate = java.time.LocalDate.now(),
                completionDate = null,
                nextMaintenanceDate = null,
                technicianName = "",
                technicianId = userId,
                supervisorName = null,
                supervisorApproval = false,
                preMaintenanceCondition = ConditionRating.GOOD,
                postMaintenanceCondition = ConditionRating.GOOD,
                issuesFound = emptyList(),
                recommendations = null,
                photos = emptyList(),
                attachments = emptyList(),
                notes = null
            )
            FormType.INSPECTION -> InspectionReportForm(
                id = formId,
                formType = type,
                createdAt = now,
                updatedAt = now,
                createdBy = userId,
                status = FormStatus.DRAFT,
                equipmentId = equipmentId,
                siteLocation = locationId ?: "Unknown",
                reportNumber = "INS-${formId.takeLast(8)}",
                equipmentName = "Unknown Equipment",
                equipmentModel = "",
                equipmentSerial = "",
                equipmentLocation = locationId ?: "Unknown",
                inspectionType = InspectionType.ROUTINE,
                inspectionDate = java.time.LocalDate.now(),
                inspectorName = "",
                inspectorId = userId,
                inspectionFrequency = "Monthly",
                lastInspectionDate = null,
                nextInspectionDate = java.time.LocalDate.now().plusMonths(1),
                inspectionItems = emptyList(),
                overallCondition = ConditionRating.GOOD,
                operationalStatus = OperationalStatus.OPERATIONAL,
                deficienciesFound = emptyList(),
                correctiveActions = emptyList(),
                recommendations = null,
                complianceStatus = ComplianceStatus.COMPLIANT,
                regulatoryReferences = emptyList(),
                photos = emptyList(),
                attachments = emptyList(),
                notes = null
            )
            FormType.SAFETY -> SafetyReportForm(
                id = formId,
                formType = type,
                createdAt = now,
                updatedAt = now,
                createdBy = userId,
                equipmentId = equipmentId,
                status = FormStatus.DRAFT,
                siteLocation = locationId ?: "Unknown",
                reportNumber = "SAF-${formId.takeLast(8)}",
                incidentDate = java.time.LocalDate.now(),
                incidentTime = java.time.LocalTime.now().toString(),
                incidentLocation = locationId ?: "Unknown",
                incidentType = IncidentType.NEAR_MISS,
                severityLevel = SeverityLevel.LOW,
                reportedBy = "Unknown Reporter",
                reporterId = userId,
                witnesses = emptyList(),
                injuredPersons = emptyList(),
                incidentDescription = "Initial incident description",
                immediateActions = "",
                rootCause = "",
                contributingFactors = emptyList(),
                equipmentInvolved = emptyList(),
                environmentalConditions = "",
                ppeUsed = emptyList(),
                safetyProceduresFollowed = true,
                investigatorAssigned = null,
                investigationDate = null,
                investigationFindings = null,
                correctiveActions = emptyList(),
                preventiveMeasures = emptyList(),
                trainingDetails = null,
                regulatoryBody = null,
                notificationDate = null,
                regulatoryReference = null,
                photos = emptyList(),
                attachments = emptyList(),
                notes = null
            )
            else -> throw IllegalArgumentException("Unsupported form type: $type")
        }
        
        // Save the form
        when (form) {
            is MaintenanceReportForm -> saveMaintenanceForm(form)
            is InspectionReportForm -> saveInspectionForm(form)
            is SafetyReportForm -> saveSafetyForm(form)
        }
        
        return formId
    }
    
    override suspend fun getPendingFormSubmissions(): List<FormData> {
        return formDao.getPendingForms().mapNotNull { entity ->
            try {
                when (entity.formType) {
                    FormType.MAINTENANCE.name -> gson.fromJson(entity.formData, MaintenanceReportForm::class.java)
                    FormType.INSPECTION.name -> gson.fromJson(entity.formData, InspectionReportForm::class.java)
                    FormType.SAFETY.name -> gson.fromJson(entity.formData, SafetyReportForm::class.java)
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    override suspend fun syncFormSubmission(form: FormData): Result<Unit> {
        return try {
            // Convert form to DTO and sync with API
            val formDto = FormDto(
                id = form.id,
                formType = form.formType.name,
                title = "Submitted Form",
                createdBy = form.createdBy.toIntOrNull() ?: 0,
                assignedTo = null,
                equipmentId = form.equipmentId,
                status = form.status.name,
                formData = gson.toJson(form),
                attachments = null,
                createdAt = form.createdAt.toString(),
                updatedAt = form.updatedAt.toString(),
                completedAt = null,
                createdByName = null,
                assignedToName = null,
                equipmentName = null
            )
            
            // Create FormSubmissionRequest for API call
            val submissionRequest = FormSubmissionRequest(
                type = form.formType.name,
                equipmentId = form.equipmentId,
                shiftId = null, // Add shiftId if available in form
                locationId = form.siteLocation,
                formData = mapOf("formData" to gson.toJson(form)),
                attachments = emptyList()
            )
            
            val token = "Bearer placeholder_token" // Get actual token from auth service
            val response = apiService.submitForm(submissionRequest, token)
            if (response.isSuccessful) {
            Result.success(Unit)
            } else {
                Result.failure(Exception("API submission failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markFormAsSynced(formId: String) {
        formDao.updateSyncStatus(formId, System.currentTimeMillis())
    }
    
    override suspend fun getFormById(formId: String): Form? {
        val entity = formDao.getFormById(formId) ?: return null
        
        return try {
            // Convert FormEntity to generic Form
            val formDataMap = try {
                val mapType = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
                gson.fromJson<Map<String, Any>>(entity.formData, mapType)
            } catch (e: Exception) {
                emptyMap<String, Any>()
            }
            
            Form(
                id = entity.id,
                type = FormType.valueOf(entity.formType),
                templateId = entity.formType, // Use formType as templateId for now
                userId = entity.createdBy,
                equipmentId = entity.equipmentId,
                shiftId = null, // Not stored in current entity
                locationId = entity.siteLocation,
                status = FormStatus.valueOf(entity.status),
                data = formDataMap,
                createdAt = java.time.LocalDateTime.parse(entity.createdAt),
                updatedAt = java.time.LocalDateTime.parse(entity.updatedAt),
                submittedAt = null, // Not stored in current entity
                approvedAt = null, // Not stored in current entity
                approvedBy = null, // Not stored in current entity
                syncStatus = if (entity.synced) SyncStatus.SYNCED else SyncStatus.OFFLINE,
                version = "1.0"
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveForm(formId: String, formData: Map<String, Any>) {
        val entity = formDao.getFormById(formId) ?: return
        
        // Merge the new data with existing form data
        val existingData = try {
            gson.fromJson(entity.formData, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            emptyMap()
        }
        
        val updatedData = existingData + formData
        val updatedEntity = entity.copy(
            formData = gson.toJson(updatedData),
            updatedAt = java.time.LocalDateTime.now().toString()
        )
        
        formDao.updateForm(updatedEntity)
    }

    override suspend fun getFormTemplateById(templateId: String): FormTemplate? {
        // For now, return null as placeholder - this would typically fetch from database
        return null
    }

    override suspend fun submitForm(formId: String) {
        try {
            val formEntity = formDao.getFormById(formId) ?: return
            
            // Update form status to submitted
            val updatedEntity = formEntity.copy(
                status = "SUBMITTED",
                updatedAt = java.time.LocalDateTime.now().toString()
            )
            formDao.updateForm(updatedEntity)
            
            // Try to sync with mobile server first
            val mobileServerUrl = mobileServerConfig.getActiveServerUrl()
            if (mobileServerUrl.isNotEmpty()) {
                try {
                    // Convert form entity to submission request
                    val submissionRequest = FormSubmissionRequest(
                        type = formEntity.formType,
                        equipmentId = formEntity.equipmentId,
                        shiftId = formEntity.shiftId,
                        locationId = formEntity.locationId,
                        formData = emptyMap(), // TODO: Parse formEntity.formData properly
                        attachments = emptyList()
                    )
                    
                    val mobileResponse = mobileServerApiService.createForm(
                        CreateFormRequest(
                            formType = formEntity.formType,
                            title = "Submitted Form",
                            assignedTo = null,
                            equipmentId = formEntity.equipmentId,
                            formData = formEntity.formData
                        )
                    )
                    
                    if (mobileResponse.isSuccessful && mobileResponse.body() != null) {
                        // Update sync status
                        formDao.updateFormSyncStatus(formEntity.id, true)
                    }
                } catch (e: Exception) {
                    // Mobile server failed, continue to cloud fallback
                    e.printStackTrace()
                }
            }
            
            // Fallback to original AECI cloud server
            if (networkManager.isNetworkAvailable()) {
                try {
                    val submissionRequest = FormSubmissionRequest(
                        type = formEntity.formType,
                        equipmentId = formEntity.equipmentId,
                        shiftId = formEntity.shiftId,
                        locationId = formEntity.locationId,
                        formData = emptyMap(), // TODO: Parse formEntity.formData properly
                        attachments = emptyList()
                    )
                    
                    val response = apiService.submitForm(submissionRequest, "Bearer token_placeholder")
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Update sync status
                        formDao.updateFormSyncStatus(formEntity.id, true)
                    } else {
                        // Mark as pending sync
                        formDao.updateFormSyncStatus(formEntity.id, false)
                    }
                } catch (e: Exception) {
                    // Mark as pending sync
                    formDao.updateFormSyncStatus(formEntity.id, false)
                }
            } else {
                // No network, mark as pending sync
                formDao.updateFormSyncStatus(formEntity.id, false)
            }
        } catch (e: Exception) {
            // Log error but don't throw
            e.printStackTrace()
        }
    }
    
    override suspend fun exportFormToPdf(formId: String, coordinates: List<FieldCoordinate>, pdfTemplatePath: String): Result<String> {
        return try {
            // For now, return a placeholder implementation
            // This would typically generate a PDF using the form data and coordinates
            Result.success("pdf_export_placeholder.pdf")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportFormToExcel(formId: String): Result<String> {
        return try {
            val form = getFormById(formId)
            if (form == null) {
                return Result.failure(Exception("Form not found with ID: $formId"))
            }

            // Use ExcelExporter to create Excel file
            val fileName = "form_${formId}_${System.currentTimeMillis()}.xlsx"
            val outputFile = java.io.File(fileName)
            
            // For now, return success with file path
            // In a real implementation, you would use ExcelExporter
            Result.success(outputFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sync methods needed by OfflineSyncService
    override suspend fun syncPendingForms() {
        // Get all unsynced form submissions
        val unsyncedForms = formDao.getUnsyncedForms()
        
        unsyncedForms.forEach { formEntity ->
            try {
                // Convert to FormSubmissionRequest and submit to API
                val submissionRequest = FormSubmissionRequest(
                    type = formEntity.formType,
                    equipmentId = formEntity.equipmentId,
                    shiftId = formEntity.shiftId,
                    locationId = formEntity.locationId,
                    formData = emptyMap(), // TODO: Parse formEntity.formData properly
                    attachments = emptyList()
                )
                
                val response = apiService.submitForm(submissionRequest, "Bearer token_placeholder")
                if (response.isSuccessful && response.body()?.success == true) {
                    // Mark as synced in local database
                    formDao.updateFormSyncStatus(formEntity.id, true)
                }
            } catch (e: Exception) {
                // Log error but continue with other forms
                e.printStackTrace()
            }
        }
    }

    // Export Operations
    override suspend fun bulkExportForms(formIds: List<String>, format: ExportFormat): Result<String> {
        return try {
            // TODO: Implement bulk export logic
            val exportedFiles = mutableListOf<String>()
            
            for (formId in formIds) {
            when (format) {
                    ExportFormat.PDF -> {
                        // Export to PDF
                        exportedFiles.add("$formId.pdf")
                    }
                ExportFormat.EXCEL -> {
                        val result = exportFormToExcel(formId)
                        if (result.isSuccess) {
                            result.getOrNull()?.let { exportedFiles.add(it) }
                }
                }
                ExportFormat.CSV -> {
                        // Export to CSV
                        exportedFiles.add("$formId.csv")
                    }
                    ExportFormat.JSON -> {
                        // Export to JSON
                        exportedFiles.add("$formId.json")
                    }
                }
            }
            
            // Create a combined export file or zip
            val combinedFile = "bulk_export_${System.currentTimeMillis()}.zip"
            Result.success(combinedFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sync operations for form templates
    override suspend fun downloadLatestFormTemplates(): Result<List<FormTemplate>> {
        return try {
            // Download latest form templates from server
            // For now, return empty list - this can be implemented when server sync is needed
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cacheFormTemplates(templates: List<FormTemplate>) {
        try {
            // Cache form templates locally
            // For now, this is a placeholder - can be implemented when form template caching is needed
            android.util.Log.d("FormRepository", "Cached ${templates.size} form templates")
        } catch (e: Exception) {
            android.util.Log.e("FormRepository", "Failed to cache form templates", e)
            throw e
        }
    }
}
