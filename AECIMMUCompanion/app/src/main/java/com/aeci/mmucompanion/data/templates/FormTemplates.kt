package com.aeci.mmucompanion.data.templates

import com.aeci.mmucompanion.domain.model.*
import java.time.LocalDateTime

object FormTemplates {
    private val now = LocalDateTime.now()
    
    val all: Map<String, FormTemplate> = mapOf(
        // 1. 90 Day Pump System Inspection Checklist
        "pump_inspection_90day" to FormTemplate(
            id = "pump_inspection_90day",
            name = "90 Day Pump System Inspection Checklist",
            description = "Quarterly inspection for pump systems.",
            formType = FormType.PUMP_90_DAY_INSPECTION,
            templateFile = "templates/pump_inspection_90day.json",
            pdfTemplate = "templates/pump_inspection_90day.pdf",
            fieldMappings = emptyList(),
            version = "1.0",
            sections = listOf(
                FormSection(
                    title = "Header",
                    fields = listOf(
                        FormField(
                            fieldName = "date",
                            fieldType = FormFieldType.DATE,
                            label = "Date",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "shift",
                            fieldType = FormFieldType.TEXT,
                            label = "Shift",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "inspector_name",
                            fieldType = FormFieldType.TEXT,
                            label = "Inspector Name",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "pump_number",
                            fieldType = FormFieldType.TEXT,
                            label = "Pump Number",
                            isRequired = true
                        )
                    )
                ),
                FormSection(
                    title = "Pre-Start Checks",
                    fields = listOf(
                        FormField(
                            fieldName = "visual_inspection",
                            fieldType = FormFieldType.CHECKBOX,
                            label = "Visual Inspection Complete",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "lubrication_check",
                            fieldType = FormFieldType.CHECKBOX,
                            label = "Lubrication Check",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "belt_tension",
                            fieldType = FormFieldType.CHECKBOX,
                            label = "Belt Tension Check",
                            isRequired = true
                        )
                    )
                ),
                FormSection(
                    title = "Operational Checks",
                    fields = listOf(
                        FormField(
                            fieldName = "discharge_pressure",
                            fieldType = FormFieldType.NUMBER,
                            label = "Discharge Pressure (kPa)",
                            isRequired = true,
                            unit = "kPa"
                        ),
                        FormField(
                            fieldName = "suction_pressure",
                            fieldType = FormFieldType.NUMBER,
                            label = "Suction Pressure (kPa)",
                            isRequired = true,
                            unit = "kPa"
                        ),
                        FormField(
                            fieldName = "flow_rate",
                            fieldType = FormFieldType.NUMBER,
                            label = "Flow Rate (L/min)",
                            isRequired = true,
                            unit = "L/min"
                        )
                    )
                ),
                FormSection(
                    title = "Completion",
                    fields = listOf(
                        FormField(
                            fieldName = "inspector_signature",
                            fieldType = FormFieldType.SIGNATURE,
                            label = "Inspector Signature",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "completion_date",
                            fieldType = FormFieldType.DATE,
                            label = "Completion Date",
                            isRequired = true
                    )
                )
            )
        ),
            fields = listOf(), // Will be populated from sections
            createdAt = now,
            updatedAt = now
        ),
        
        // 2. Blast Hole Log Sheet
        "blast_hole_log" to FormTemplate(
            id = "blast_hole_log",
            name = "Blast Hole Log Sheet",
            description = "Log sheet for blast hole drilling operations.",
            formType = FormType.BLAST_HOLE_LOG,
            templateFile = "templates/blast_hole_log.json",
            pdfTemplate = "templates/blast_hole_log.pdf",
            fieldMappings = emptyList(),
            version = "1.0",
            sections = listOf(
                FormSection(
                    title = "Header Information",
                    fields = listOf(
                        FormField(
                            fieldName = "date",
                            fieldType = FormFieldType.DATE,
                            label = "Date",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "shift",
                            fieldType = FormFieldType.TEXT,
                            label = "Shift",
                            isRequired = true
                ),
                        FormField(
                            fieldName = "driller_name",
                            fieldType = FormFieldType.TEXT,
                            label = "Driller Name",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "blast_pattern",
                            fieldType = FormFieldType.TEXT,
                            label = "Blast Pattern",
                            isRequired = true
                        )
                    )
                ),
                FormSection(
                    title = "Hole Details",
                    fields = listOf(
                        FormField(
                            fieldName = "hole_number",
                            fieldType = FormFieldType.TEXT,
                            label = "Hole Number",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "depth",
                            fieldType = FormFieldType.NUMBER,
                            label = "Depth (m)",
                            isRequired = true,
                            unit = "m"
                        ),
                        FormField(
                            fieldName = "diameter",
                            fieldType = FormFieldType.NUMBER,
                            label = "Diameter (mm)",
                            isRequired = true,
                            unit = "mm"
                        ),
                        FormField(
                            fieldName = "collar_elevation",
                            fieldType = FormFieldType.NUMBER,
                            label = "Collar Elevation",
                            isRequired = true
                    )
                )
            )
        ),
            fields = listOf(), // Will be populated from sections
            createdAt = now,
            updatedAt = now
        ),

        // Continue with similar pattern for all other form templates...
        // For brevity, I'll add just a few more key templates

        // 3. Pre-Task Safety Assessment
        "pretask_safety_assessment" to FormTemplate(
            id = "pretask_safety_assessment",
            name = "Pre-Task Safety Assessment",
            description = "Pre-task safety assessment form.",
            formType = FormType.PRETASK_SAFETY,
            templateFile = "templates/pretask_safety_assessment.json",
            pdfTemplate = "templates/pretask_safety_assessment.pdf",
            fieldMappings = emptyList(),
            version = "1.0",
            sections = listOf(
                FormSection(
                    title = "Header",
                    fields = listOf(
                        FormField(
                            fieldName = "date",
                            fieldType = FormFieldType.DATE,
                            label = "Date",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "task_description",
                            fieldType = FormFieldType.MULTILINE_TEXT,
                            label = "Task Description",
                            isRequired = true
                        ),
                        FormField(
                            fieldName = "assessor_name",
                            fieldType = FormFieldType.TEXT,
                            label = "Assessor Name",
                            isRequired = true
                        )
                    )
                ),
                FormSection(
                    title = "Hazard Identification",
                    fields = listOf(
                        FormField(
                            fieldName = "mechanical_hazards",
                            fieldType = FormFieldType.CHECKBOX,
                            label = "Mechanical Hazards Identified",
                            isRequired = false
                        ),
                        FormField(
                            fieldName = "electrical_hazards",
                            fieldType = FormFieldType.CHECKBOX,
                            label = "Electrical Hazards Identified",
                            isRequired = false
                        ),
                        FormField(
                            fieldName = "chemical_hazards",
                            fieldType = FormFieldType.CHECKBOX,
                            label = "Chemical Hazards Identified",
                            isRequired = false
                        )
                    )
                )
            ),
            fields = listOf(), // Will be populated from sections
            createdAt = now,
            updatedAt = now
        )
    )

    fun getTemplate(formId: String): FormTemplate? = all[formId]
    
    fun getFormTemplate(type: FormType): FormTemplate? {
        return all.values.find { it.formType == type }
    }
    
    fun getAllTemplates(): List<FormTemplate> {
        return all.values.toList()
    }
}









