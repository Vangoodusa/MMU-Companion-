package com.aeci.mmucompanion.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeci.mmucompanion.core.util.FormTemplateProvider
import com.aeci.mmucompanion.domain.model.*
import com.aeci.mmucompanion.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FormUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val currentForm: Form? = null,
    val formTemplate: FormTemplate? = null,
    val formData: Map<String, Any> = emptyMap(),
    val fieldErrors: Map<String, String> = emptyMap(),
    val validationErrors: Map<String, String> = emptyMap(),
    val isDraftSaved: Boolean = false,
    val isSubmitted: Boolean = false,
    val exportPath: String? = null,
    val error: String? = null
)

@HiltViewModel
class FormViewModel @Inject constructor(
    private val templateProvider: FormTemplateProvider,
    private val createFormUseCase: CreateFormUseCase,
    private val saveFormUseCase: SaveFormUseCase,
    private val submitFormUseCase: SubmitFormUseCase,
    private val getFormUseCase: GetFormUseCase,
    private val formRepository: com.aeci.mmucompanion.domain.repository.FormRepository
) : ViewModel() {

    private val _formTemplateState = mutableStateOf<FormTemplate?>(null)
    val formTemplateState: State<FormTemplate?> = _formTemplateState

    private val _formDataState = mutableStateOf<Map<String, Any>>(emptyMap())
    val formDataState: State<Map<String, Any>> = _formDataState
    
    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()
    
    // Mock current user ID - in real app, get from auth service
    private val currentUserId = "current_user_id"
    private var currentFormId: String? = null

    fun loadFormTemplate(formType: FormType) {
        viewModelScope.launch {
            _formTemplateState.value = templateProvider.getFormTemplate(formType)
            // Initialize form data with default values if any
            _formDataState.value = _formTemplateState.value?.fields?.associate {
                it.fieldName to (it.defaultValue ?: "")
            } ?: emptyMap()
        }
    }

    fun updateFormData(fieldId: String, value: Any) {
        _formDataState.value = _formDataState.value.toMutableMap().apply {
            this[fieldId] = value
        }
    }

    fun initializeForm(formType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val formTypeEnum = FormType.valueOf(formType.uppercase())
                val template = templateProvider.getFormTemplate(formTypeEnum)
                
                // Create new form
                val formId = createFormUseCase(
                    type = formTypeEnum,
                    userId = currentUserId
                ).getOrThrow()
                
                currentFormId = formId
                
                val initialData = template?.fields?.associate {
                    it.fieldName to (it.defaultValue ?: "")
                } ?: emptyMap()
                
                _uiState.update { 
                    it.copy(
                            isLoading = false,
                            formTemplate = template,
                        formData = initialData,
                        fieldErrors = emptyMap(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to initialize form"
                    )
                }
            }
        }
    }

    fun updateField(fieldId: String, value: Any) {
        _uiState.update { 
            it.copy(
                formData = it.formData.toMutableMap().apply {
                    this[fieldId] = value
                },
                fieldErrors = it.fieldErrors.toMutableMap().apply {
                    remove(fieldId) // Clear error when field is updated
                }
            )
        }
    }

    fun setFieldError(fieldId: String, error: String) {
        _uiState.update { 
            it.copy(
                fieldErrors = it.fieldErrors.toMutableMap().apply {
                    this[fieldId] = error
                },
                validationErrors = it.validationErrors.toMutableMap().apply {
                    this[fieldId] = error
                }
            )
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            currentFormId?.let { formId ->
                try {
                    saveFormUseCase(formId, _uiState.value.formData).getOrThrow()
                    _uiState.update { 
                        it.copy(
                            isDraftSaved = true,
                            error = null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            error = e.message ?: "Failed to save draft"
                        )
                    }
                }
            }
        }
    }

    fun submitForm() {
        viewModelScope.launch {
            currentFormId?.let { formId ->
                try {
                    _uiState.update { it.copy(isSubmitting = true) }
                    
                    // First save the current data
                    saveFormUseCase(formId, _uiState.value.formData).getOrThrow()
                    
                    // Then submit the form
                    submitFormUseCase(formId).getOrThrow()
                    
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            isSubmitted = true,
                            error = null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isSubmitting = false,
                            error = e.message ?: "Failed to submit form"
                        )
                    }
            }
        }
    }
    }

    fun loadForm(formId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val form = getFormUseCase(formId).getOrThrow()
                val template = form?.let { templateProvider.getFormTemplate(it.type) }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        currentForm = form,
                        formTemplate = template,
                        formData = form?.data ?: emptyMap(),
                        error = null
                    )
                }
                
                currentFormId = formId
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load form"
                    )
                }
            }
        }
    }
    
    fun exportToPdf() {
        viewModelScope.launch {
            currentFormId?.let { formId ->
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    
                    // TODO: Implement PDF export with proper coordinates
                    val result = formRepository.exportFormToPdf(
                        formId = formId,
                        coordinates = emptyList(), // TODO: Get coordinates from template
                        pdfTemplatePath = "" // TODO: Get template path
                    )
                    
                    result.fold(
                        onSuccess = { path: String ->
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    exportPath = path,
                                    error = null
                                )
                            }
                        },
                        onFailure = { error: Throwable ->
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: "Export failed"
                                )
                            }
                        }
                    )
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Export failed"
                        )
                    }
                }
            }
        }
    }

    fun exportToExcel() {
        viewModelScope.launch {
            currentFormId?.let { formId ->
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    
                    val result = formRepository.exportFormToExcel(formId)
                    
                    result.fold(
                        onSuccess = { path: String ->
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    exportPath = path,
                                    error = null
                                )
                            }
                        },
                        onFailure = { error: Throwable ->
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: "Excel export failed"
                                )
                            }
                        }
                    )
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Excel export failed"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSubmissionState() {
        _uiState.update { 
            it.copy(
                isSubmitted = false,
                isDraftSaved = false
            )
        }
    }
}
