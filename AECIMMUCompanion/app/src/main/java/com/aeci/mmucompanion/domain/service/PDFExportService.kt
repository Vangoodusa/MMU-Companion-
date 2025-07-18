package com.aeci.mmucompanion.domain.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.aeci.mmucompanion.domain.model.FormField
import com.aeci.mmucompanion.domain.model.FormFieldType
import com.aeci.mmucompanion.domain.model.FormSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PDFExportService @Inject constructor(
    private val context: Context
) {

    suspend fun exportFormToPDF(
        formId: String,
        formTitle: String,
        formSections: List<FormSection>,
        formData: Map<String, String>,
        outputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create() // Letter size
            val page = pdfDocument.startPage(pageInfo)
            
            val canvas = page.canvas
            val paint = Paint()
            paint.textSize = 12f
            paint.isAntiAlias = true
            
            // Draw form title
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText(formTitle, 50f, 50f, paint)
            
            // Reset paint for form fields
            paint.textSize = 12f
            paint.isFakeBoldText = false
            
            // Draw form data based on field coordinates
            drawFormData(canvas, paint, formSections, formData)
            
            pdfDocument.finishPage(page)
            
            // Save to file
            val fileOutputStream = FileOutputStream(outputPath)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.close()
            pdfDocument.close()
            
            Result.success(outputPath)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun drawFormData(
        canvas: Canvas,
        paint: Paint,
        sections: List<FormSection>,
        formData: Map<String, String>
    ) {
        var currentY = 100f
        
        sections.forEach { section ->
            // Draw section header
            paint.isFakeBoldText = true
            canvas.drawText(section.title, 50f, currentY, paint)
            currentY += 30f
            
            // Reset paint for fields
            paint.isFakeBoldText = false
            
            section.fields.forEach { field ->
                val value = formData[field.fieldName] ?: ""
                
                // Use field coordinates if available, otherwise use default positioning
                val x = if (field.x > 0) field.x else 70f
                val y = if (field.y > 0) field.y else currentY
                
                when (field.fieldType) {
                    FormFieldType.TEXT,
                    FormFieldType.MULTILINE_TEXT,
                    FormFieldType.NUMBER,
                    FormFieldType.INTEGER,
                    FormFieldType.DATE,
                    FormFieldType.TIME,
                    FormFieldType.EQUIPMENT_ID,
                    FormFieldType.SITE_CODE,
                    FormFieldType.EMPLOYEE_ID -> {
                        drawTextValue(canvas, paint, field.label, value, x, y)
                    }
                    
                    FormFieldType.CHECKBOX -> {
                        drawCheckboxValue(canvas, paint, field.label, value, x, y)
                    }
                    
                    FormFieldType.DROPDOWN -> {
                        drawDropdownValue(canvas, paint, field.label, value, x, y)
                    }
                    
                    FormFieldType.SIGNATURE -> {
                        drawSignaturePlaceholder(canvas, paint, field.label, x, y)
                    }
                    
                    FormFieldType.PHOTO -> {
                        drawPhotoPlaceholder(canvas, paint, field.label, x, y)
                    }
                    
                    else -> {
                        // Handle other field types as needed
                        drawTextValue(canvas, paint, field.label, value, x, y)
                    }
                }
                
                currentY += 25f
            }
            
            currentY += 20f // Space between sections
        }
    }

    private fun drawTextValue(
        canvas: Canvas,
        paint: Paint,
        label: String,
        value: String,
        x: Float,
        y: Float
    ) {
        // Draw label
        paint.isFakeBoldText = true
        canvas.drawText("$label:", x, y, paint)
        
        // Draw value
        paint.isFakeBoldText = false
        val labelWidth = paint.measureText("$label: ")
        canvas.drawText(value, x + labelWidth, y, paint)
    }

    private fun drawCheckboxValue(
        canvas: Canvas,
        paint: Paint,
        label: String,
        value: String,
        x: Float,
        y: Float
    ) {
        // Draw checkbox
        val checkboxSize = 15f
        paint.style = Paint.Style.STROKE
        canvas.drawRect(x, y - checkboxSize, x + checkboxSize, y, paint)
        
        // Draw check mark if checked
        if (value.toBoolean()) {
            paint.style = Paint.Style.FILL
            canvas.drawLine(x + 3f, y - 8f, x + 7f, y - 4f, paint)
            canvas.drawLine(x + 7f, y - 4f, x + 12f, y - 12f, paint)
        }
        
        // Draw label
        paint.style = Paint.Style.FILL
        canvas.drawText(label, x + checkboxSize + 5f, y - 3f, paint)
    }

    private fun drawDropdownValue(
        canvas: Canvas,
        paint: Paint,
        label: String,
        value: String,
        x: Float,
        y: Float
    ) {
        drawTextValue(canvas, paint, label, value, x, y)
    }

    private fun drawSignaturePlaceholder(
        canvas: Canvas,
        paint: Paint,
        label: String,
        x: Float,
        y: Float
    ) {
        // Draw signature line
        paint.style = Paint.Style.STROKE
        canvas.drawLine(x, y + 5f, x + 150f, y + 5f, paint)
        
        // Draw label
        paint.style = Paint.Style.FILL
        canvas.drawText(label, x, y - 5f, paint)
    }

    private fun drawPhotoPlaceholder(
        canvas: Canvas,
        paint: Paint,
        label: String,
        x: Float,
        y: Float
    ) {
        // Draw photo placeholder box
        paint.style = Paint.Style.STROKE
        canvas.drawRect(x, y - 30f, x + 100f, y + 30f, paint)
        
        // Draw label
        paint.style = Paint.Style.FILL
        canvas.drawText("$label: [Photo]", x, y - 35f, paint)
    }

    suspend fun getFormCoordinates(formId: String): Map<String, FormField> = withContext(Dispatchers.IO) {
        // Return coordinate mappings for the specific form
        // This would ideally come from a database or configuration file
        when (formId) {
            "pump_inspection_90day" -> getPumpInspectionCoordinates()
            "availability_utilization" -> getAvailabilityUtilizationCoordinates()
            "blast_hole_log" -> getBlastHoleLogCoordinates()
            "bowie_weekly" -> getBowieWeeklyCoordinates()
            "fire_extinguisher" -> getFireExtinguisherCoordinates()
            "job_card" -> getJobCardCoordinates()
            "chassis_maintenance" -> getChassisMaintenanceCoordinates()
            "handover_certificate" -> getHandoverCertificateCoordinates()
            "production_log" -> getProductionLogCoordinates()
            "quality_report" -> getQualityReportCoordinates()
            "monthly_maintenance" -> getMonthlyMaintenanceCoordinates()
            "bench_inspection" -> getBenchInspectionCoordinates()
            "pressure_trip_test" -> getPressureTripTestCoordinates()
            "pretask_assessment" -> getPretaskAssessmentCoordinates()
            else -> emptyMap()
        }
    }

    private fun getPumpInspectionCoordinates(): Map<String, FormField> {
        // Based on the PDF coordinate maps from the markdown file
        return mapOf(
            "inspection_date" to FormField(
                fieldName = "inspection_date",
                label = "Inspection Date",
                fieldType = FormFieldType.DATE,
                x = 450f, y = 85f, width = 120f, height = 25f
            ),
            "inspector_name" to FormField(
                fieldName = "inspector_name",
                label = "Inspector Name",
                fieldType = FormFieldType.TEXT,
                x = 150f, y = 110f, width = 200f, height = 25f
            ),
            "equipment_id" to FormField(
                fieldName = "equipment_id",
                label = "Equipment ID",
                fieldType = FormFieldType.EQUIPMENT_ID,
                x = 450f, y = 110f, width = 150f, height = 25f
            ),
            "serial_number" to FormField(
                fieldName = "serial_number",
                label = "Serial Number",
                fieldType = FormFieldType.TEXT,
                x = 150f, y = 135f, width = 200f, height = 25f
            ),
            "pump_location" to FormField(
                fieldName = "pump_location",
                label = "Pump Location",
                fieldType = FormFieldType.TEXT,
                x = 450f, y = 135f, width = 150f, height = 25f
            ),
            "service_hours" to FormField(
                fieldName = "service_hours",
                label = "Service Hours",
                fieldType = FormFieldType.NUMBER,
                x = 150f, y = 160f, width = 100f, height = 25f
            )
        )
    }

    // Add other coordinate mapping functions as needed
    private fun getAvailabilityUtilizationCoordinates(): Map<String, FormField> = emptyMap()
    private fun getBlastHoleLogCoordinates(): Map<String, FormField> = emptyMap()
    private fun getBowieWeeklyCoordinates(): Map<String, FormField> = emptyMap()
    private fun getFireExtinguisherCoordinates(): Map<String, FormField> = emptyMap()
    private fun getJobCardCoordinates(): Map<String, FormField> = emptyMap()
    private fun getChassisMaintenanceCoordinates(): Map<String, FormField> = emptyMap()
    private fun getHandoverCertificateCoordinates(): Map<String, FormField> = emptyMap()
    private fun getProductionLogCoordinates(): Map<String, FormField> = emptyMap()
    private fun getQualityReportCoordinates(): Map<String, FormField> = emptyMap()
    private fun getMonthlyMaintenanceCoordinates(): Map<String, FormField> = emptyMap()
    private fun getBenchInspectionCoordinates(): Map<String, FormField> = emptyMap()
    private fun getPressureTripTestCoordinates(): Map<String, FormField> = emptyMap()
    private fun getPretaskAssessmentCoordinates(): Map<String, FormField> = emptyMap()
} 