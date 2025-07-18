package com.aeci.mmucompanion.domain.repository

import com.aeci.mmucompanion.domain.model.*
import kotlinx.coroutines.flow.Flow

interface FormRepository {
    
    // Generic Form Operations (for use cases)
    suspend fun createForm(type: FormType, userId: String, equipmentId: String?, shiftId: String?, locationId: String?): String
    suspend fun getFormById(formId: String): Form?
    suspend fun saveForm(formId: String, formData: Map<String, Any>)
    suspend fun submitForm(formId: String)
    suspend fun getFormTemplateById(templateId: String): FormTemplate?
    suspend fun exportFormToPdf(formId: String, coordinates: List<FieldCoordinate>, pdfTemplatePath: String): Result<String>
    suspend fun exportFormToExcel(formId: String): Result<String>
    suspend fun syncPendingForms()
    
    // Maintenance Forms
    suspend fun saveMaintenanceForm(form: MaintenanceReportForm): MaintenanceReportForm
    suspend fun getMaintenanceFormById(id: String): MaintenanceReportForm?
    suspend fun getMaintenanceFormsByUser(userId: String): List<MaintenanceReportForm>
    suspend fun getMaintenanceFormsByEquipment(equipmentId: String): List<MaintenanceReportForm>
    suspend fun updateMaintenanceForm(form: MaintenanceReportForm): MaintenanceReportForm
    suspend fun deleteMaintenanceForm(id: String): Boolean
    
    // Inspection Forms
    suspend fun saveInspectionForm(form: InspectionReportForm): InspectionReportForm
    suspend fun getInspectionFormById(id: String): InspectionReportForm?
    suspend fun getInspectionFormsByUser(userId: String): List<InspectionReportForm>
    suspend fun getInspectionFormsByEquipment(equipmentId: String): List<InspectionReportForm>
    suspend fun updateInspectionForm(form: InspectionReportForm): InspectionReportForm
    suspend fun deleteInspectionForm(id: String): Boolean
    
    // Safety Forms
    suspend fun saveSafetyForm(form: SafetyReportForm): SafetyReportForm
    suspend fun getSafetyFormById(id: String): SafetyReportForm?
    suspend fun getSafetyFormsByUser(userId: String): List<SafetyReportForm>
    suspend fun updateSafetyForm(form: SafetyReportForm): SafetyReportForm
    suspend fun deleteSafetyForm(id: String): Boolean
    
    // Generic Form Operations
    suspend fun getAllFormsByUser(userId: String): List<FormData>
    suspend fun getFormsByStatus(status: FormStatus): List<FormData>
    suspend fun getFormsByType(type: FormType): List<FormData>
    suspend fun getFormsByDateRange(startDate: String, endDate: String): List<FormData>
    
    // Sync Operations
    suspend fun getPendingFormSubmissions(): List<FormData>
    suspend fun syncFormSubmission(form: FormData): Result<Unit>
    suspend fun markFormAsSynced(formId: String)
    
    // Real-time updates
    fun getFormsFlow(): Flow<List<FormData>>
    fun getFormsByUserFlow(userId: String): Flow<List<FormData>>
    
    // Export operations
    suspend fun bulkExportForms(formIds: List<String>, format: ExportFormat): Result<String>
    
    // Sync operations for form templates
    suspend fun downloadLatestFormTemplates(): Result<List<FormTemplate>>
    suspend fun cacheFormTemplates(templates: List<FormTemplate>)
}

enum class SyncStatus {
    PENDING,
    SYNCED,
    FAILED
}
