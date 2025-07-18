package com.aeci.mmucompanion.domain.model

import java.time.LocalDateTime
import java.time.LocalDate

// Generic Form class for the application
data class Form(
    val id: String,
    val type: FormType,
    val templateId: String,
    val userId: String,
    val equipmentId: String?,
    val shiftId: String?,
    val locationId: String?,
    val status: FormStatus,
    val data: Map<String, Any>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val submittedAt: LocalDateTime? = null,
    val approvedAt: LocalDateTime? = null,
    val approvedBy: String? = null,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE,
    val version: String = "1.0"
)

// Base form interface
interface FormData {
    val id: String
    val formType: FormType
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
    val createdBy: String
    val status: FormStatus
    val equipmentId: String?
    val siteLocation: String
    val reportNumber: String
}

enum class FormType(val displayName: String, val templateFile: String) {
    MAINTENANCE("Maintenance Report", "maintenance_template.pdf"),
    INSPECTION("Inspection Report", "inspection_template.pdf"),
    SAFETY("Safety Report", "safety_template.pdf"),
    INCIDENT("Incident Report", "incident_template.pdf"),
    EQUIPMENT_CHECK("Equipment Check", "equipment_check_template.pdf"),
    WORK_ORDER("Work Order", "work_order_template.pdf"),
    
    // MMU Forms
    MMU_DAILY_LOG("MMU Daily Log", "mmu_daily_log_template.pdf"),
    MMU_QUALITY_REPORT("MMU Quality Report", "mmu_quality_report_template.pdf"),
    MMU_HANDOVER_CERTIFICATE("MMU Handover Certificate", "mmu_handover_certificate_template.pdf"),
    MMU_CHASSIS_MAINTENANCE("MMU Chassis Maintenance", "mmu_chassis_maintenance_template.pdf"),
    ON_BENCH_MMU_INSPECTION("On Bench MMU Inspection", "on_bench_mmu_inspection_template.pdf"),
    
    // Pump Forms
    PUMP_90_DAY_INSPECTION("90 Day Pump Inspection", "pump_90_day_inspection_template.pdf"),
    PUMP_WEEKLY_CHECK("Pump Weekly Check", "pump_weekly_check_template.pdf"),
    PC_PUMP_PRESSURE_TEST("PC Pump Pressure Test", "pc_pump_pressure_test_template.pdf"),
    
    // Other Forms
    AVAILABILITY_UTILIZATION("Availability & Utilization", "availability_utilization_template.pdf"),
    BLAST_HOLE_LOG("Blast Hole Log", "blast_hole_log_template.pdf"),
    PRETASK_SAFETY("Pre-task Safety", "pretask_safety_template.pdf"),
    FIRE_EXTINGUISHER_INSPECTION("Fire Extinguisher Inspection", "fire_extinguisher_inspection_template.pdf"),
    MONTHLY_PROCESS_MAINTENANCE("Monthly Process Maintenance", "monthly_process_maintenance_template.pdf"),
    JOB_CARD("Job Card", "job_card_template.pdf"),
    TIMESHEET("Timesheet", "timesheet_template.pdf"),
    UOR_REPORT("UOR Report", "uor_report_template.pdf"),
    PRETASK("Pre-task", "pretask_template.pdf")
}

enum class FormStatus {
    DRAFT, IN_PROGRESS, SUBMITTED, APPROVED, REJECTED, COMPLETED
}

// Maintenance Report Form
data class MaintenanceReportForm(
    override val id: String,
    override val formType: FormType = FormType.MAINTENANCE,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    override val createdBy: String,
    override val status: FormStatus,
    override val equipmentId: String?,
    override val siteLocation: String,
    override val reportNumber: String,
    
    // Equipment Information
    val equipmentName: String,
    val equipmentModel: String,
    val equipmentSerial: String,
    val equipmentLocation: String,
    val equipmentHours: Double?,
    
    // Maintenance Details
    val maintenanceType: MaintenanceType,
    val workDescription: String,
    val partsUsed: List<PartUsed>,
    val laborHours: Double,
    val maintenanceDate: LocalDate,
    val completionDate: LocalDate?,
    val nextMaintenanceDate: LocalDate?,
    
    // Technician Information
    val technicianName: String,
    val technicianId: String,
    val supervisorName: String?,
    val supervisorApproval: Boolean = false,
    
    // Condition Assessment
    val preMaintenanceCondition: ConditionRating,
    val postMaintenanceCondition: ConditionRating,
    val issuesFound: List<String>,
    val recommendations: String?,
    
    // Documentation
    val photos: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val notes: String?
) : FormData

// Inspection Report Form
data class InspectionReportForm(
    override val id: String,
    override val formType: FormType = FormType.INSPECTION,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    override val createdBy: String,
    override val status: FormStatus,
    override val equipmentId: String?,
    override val siteLocation: String,
    override val reportNumber: String,
    
    // Equipment Information
    val equipmentName: String,
    val equipmentModel: String,
    val equipmentSerial: String,
    val equipmentLocation: String,
    
    // Inspection Details
    val inspectionType: InspectionType,
    val inspectionDate: LocalDate,
    val inspectorName: String,
    val inspectorId: String,
    val inspectionFrequency: String,
    val lastInspectionDate: LocalDate?,
    val nextInspectionDate: LocalDate,
    
    // Inspection Items
    val inspectionItems: List<InspectionItem>,
    val overallCondition: ConditionRating,
    val operationalStatus: OperationalStatus,
    
    // Findings
    val deficienciesFound: List<DeficiencyItem>,
    val correctiveActions: List<CorrectiveAction>,
    val recommendations: String?,
    
    // Compliance
    val complianceStatus: ComplianceStatus,
    val regulatoryReferences: List<String>,
    val certificationRequired: Boolean = false,
    
    // Documentation
    val photos: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val notes: String?
) : FormData

// Safety Report Form
data class SafetyReportForm(
    override val id: String,
    override val formType: FormType = FormType.SAFETY,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    override val createdBy: String,
    override val status: FormStatus,
    override val equipmentId: String?,
    override val siteLocation: String,
    override val reportNumber: String,
    
    // Incident Information
    val incidentDate: LocalDate,
    val incidentTime: String,
    val incidentLocation: String,
    val incidentType: IncidentType,
    val severityLevel: SeverityLevel,
    
    // Personnel Involved
    val reportedBy: String,
    val reporterId: String,
    val witnessesPresent: Boolean = false,
    val witnesses: List<Witness>,
    val injuredPersons: List<InjuredPerson>,
    
    // Incident Details
    val incidentDescription: String,
    val immediateActions: String,
    val rootCause: String?,
    val contributingFactors: List<String>,
    
    // Equipment/Environment
    val equipmentInvolved: List<String>,
    val environmentalConditions: String,
    val ppeUsed: List<String>,
    val safetyProceduresFollowed: Boolean,
    
    // Investigation
    val investigationRequired: Boolean = false,
    val investigatorAssigned: String?,
    val investigationDate: LocalDate?,
    val investigationFindings: String?,
    
    // Corrective Actions
    val correctiveActions: List<CorrectiveAction>,
    val preventiveMeasures: List<String>,
    val trainingRequired: Boolean = false,
    val trainingDetails: String?,
    
    // Regulatory
    val regulatoryNotification: Boolean = false,
    val regulatoryBody: String?,
    val notificationDate: LocalDate?,
    val regulatoryReference: String?,
    
    // Documentation
    val photos: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val notes: String?
) : FormData

// Supporting Data Classes
data class PartUsed(
    val partNumber: String,
    val partName: String,
    val quantity: Int,
    val unitCost: Double,
    val supplier: String?
)

data class InspectionItem(
    val itemName: String,
    val checkPoints: List<String>,
    val condition: ConditionRating,
    val notes: String?,
    val actionRequired: Boolean = false
)

data class DeficiencyItem(
    val description: String,
    val severity: SeverityLevel,
    val location: String,
    val identifiedBy: String,
    val identifiedDate: LocalDate,
    val photoReference: String?
)

data class CorrectiveAction(
    val description: String,
    val assignedTo: String,
    val dueDate: LocalDate,
    val priority: Priority,
    val status: ActionStatus,
    val completedDate: LocalDate?
)

data class Witness(
    val name: String,
    val id: String,
    val contactInfo: String,
    val statement: String?
)

data class InjuredPerson(
    val name: String,
    val id: String,
    val injuryType: String,
    val injuryDescription: String,
    val medicalAttention: Boolean,
    val hospitalName: String?
)

// Enums
enum class MaintenanceType {
    PREVENTIVE, CORRECTIVE, EMERGENCY, SCHEDULED, PREDICTIVE
}

enum class InspectionType {
    ROUTINE, ANNUAL, REGULATORY, SPECIAL, PRE_OPERATION, POST_OPERATION
}

enum class IncidentType {
    INJURY, NEAR_MISS, PROPERTY_DAMAGE, ENVIRONMENTAL, SECURITY, FIRE
}

enum class ConditionRating {
    EXCELLENT, GOOD, FAIR, POOR, CRITICAL
}

enum class OperationalStatus {
    OPERATIONAL, LIMITED, NON_OPERATIONAL, UNDER_REPAIR
}

enum class ComplianceStatus {
    COMPLIANT, NON_COMPLIANT, PARTIALLY_COMPLIANT, PENDING_REVIEW
}

enum class SeverityLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class ActionStatus {
    PENDING, IN_PROGRESS, COMPLETED, OVERDUE, CANCELLED
}

// PDF Field Mapping
data class PDFFieldMapping(
    val formField: String,
    val pdfField: String,
    val fieldType: PDFFieldType,
    val format: String? = null,
    val required: Boolean = false
)

enum class PDFFieldType {
    TEXT, NUMBER, DATE, BOOLEAN, IMAGE, TABLE, SIGNATURE
}

// Template Configuration
data class FormTemplate(
    val id: String,
    val name: String,
    val description: String?,
    val formType: FormType,
    val templateFile: String,
    val pdfTemplate: String?,
    val fieldMappings: List<PDFFieldMapping>,
    val sections: List<FormSection> = emptyList(),
    val fields: List<FormField>,
    val version: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
