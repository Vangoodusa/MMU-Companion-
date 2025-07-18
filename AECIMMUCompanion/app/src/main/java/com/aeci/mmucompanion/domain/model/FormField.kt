package com.aeci.mmucompanion.domain.model

enum class FormFieldType {
    TEXT,
    MULTILINE_TEXT,
    NUMBER,
    INTEGER,
    DATE,
    TIME,
    DATETIME,
    CHECKBOX,
    RADIO,
    DROPDOWN,
    SIGNATURE,
    PHOTO,
    BARCODE,
    EQUIPMENT_ID,
    SITE_CODE,
    EMPLOYEE_ID
}

data class FormField(
    val fieldName: String,
    val label: String,
    val fieldType: FormFieldType,
    val isRequired: Boolean = false,
    val placeholder: String? = null,
    val options: List<String>? = null,
    val validation: ValidationRule? = null,
    val dependencies: List<String> = emptyList(),
    val defaultValue: String? = null,
    val unit: String? = null,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f
)

data class FormSection(
    val title: String,
    val description: String? = null,
    val fields: List<FormField>
)

// FormTemplate is defined in FormModels.kt

data class ValidationRule(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val customMessage: String? = null
) 