package com.aeci.mmucompanion.domain.usecase

import com.aeci.mmucompanion.domain.model.FieldCoordinate
import com.aeci.mmucompanion.domain.model.Form
import com.aeci.mmucompanion.domain.model.FormType
import com.aeci.mmucompanion.domain.repository.FormRepository
import javax.inject.Inject

class CreateFormUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend operator fun invoke(
        type: FormType,
        userId: String,
        equipmentId: String? = null,
        shiftId: String? = null,
        locationId: String? = null
    ): Result<String> {
        return try {
            val formId = formRepository.createForm(type, userId, equipmentId, shiftId, locationId)
            Result.success(formId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SaveFormUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend operator fun invoke(formId: String, formData: Map<String, Any>): Result<Unit> {
        return try {
            formRepository.saveForm(formId, formData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SubmitFormUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend operator fun invoke(formId: String): Result<Unit> {
        return try {
            formRepository.submitForm(formId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetFormUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend operator fun invoke(formId: String): Result<Form?> {
        return try {
            val form = formRepository.getFormById(formId)
            Result.success(form)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ExportFormUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend fun exportToPdf(formId: String): Result<String> {
        return try {
            val form = formRepository.getFormById(formId)
                ?: return Result.failure(Exception("Form not found"))

            val template = formRepository.getFormTemplateById(form.templateId)
                ?: return Result.failure(Exception("Form template not found for type: ${form.type}"))
            
            val pdfTemplatePath = template.pdfTemplate
                ?: return Result.failure(Exception("PDF template path not defined for form: ${template.name}"))

            val coordinates = template.fields.map { field ->
                FieldCoordinate(
                    fieldName = field.fieldName,
                    x = field.x.toInt(),
                    y = field.y.toInt(),
                    width = field.width.toInt(),
                    height = field.height.toInt(),
                    fieldType = field.fieldType.name,
                    isRequired = field.isRequired,
                    placeholder = field.placeholder ?: ""
                )
            }

            formRepository.exportFormToPdf(formId, coordinates, pdfTemplatePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun exportToExcel(formId: String): Result<String> {
        return formRepository.exportFormToExcel(formId)
    }
}

class SyncFormsUseCase @Inject constructor(
    private val formRepository: FormRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            formRepository.syncPendingForms()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
