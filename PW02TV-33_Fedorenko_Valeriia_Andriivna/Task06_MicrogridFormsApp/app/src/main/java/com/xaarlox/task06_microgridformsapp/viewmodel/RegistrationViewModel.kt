package com.xaarlox.task06_microgridformsapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task06_microgridformsapp.domain.FieldError
import com.xaarlox.task06_microgridformsapp.domain.FormField
import com.xaarlox.task06_microgridformsapp.domain.FormValidator
import com.xaarlox.task06_microgridformsapp.domain.RegistrationResult
import com.xaarlox.task06_microgridformsapp.domain.SourceType
import com.xaarlox.task06_microgridformsapp.domain.StationRepository
import kotlinx.coroutines.launch

/**
 * Registration screen state
 *
 * @property sourceType energy source type (solar or wind)
 * @property values entered field values
 * @property errors validation errors for fields
 * @property isLoading true while processing after a button click
 * @property isSuccess true upon successful registration
 */
data class RegistrationUiState(
    val sourceType: SourceType = SourceType.SOLAR,
    val values: Map<FormField, String> = emptyMap(),
    val errors: Map<FormField, FieldError> = emptyMap(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    fun valueOf(field: FormField): String = values[field].orEmpty()
}

/**
 * Registration form ViewModel: holds state (survives screen rotation),
 * performs validation, and simulates registration
 */
class RegistrationViewModel : ViewModel() {
    private val repository = StationRepository()

    var uiState by mutableStateOf(RegistrationUiState())
        private set

    fun onSourceTypeChange(type: SourceType) {
        if (uiState.isLoading) return
        uiState = uiState.copy(sourceType = type, isSuccess = false)
    }

    fun onFieldChange(field: FormField, value: String) {
        val state = uiState
        val error = FormValidator.validate(field, value)
        uiState = state.copy(
            values = state.values + (field to value),
            errors = if (error == null) state.errors - field else state.errors + (field to error),
            isSuccess = false
        )
    }

    /** Full form validation, followed by registration with a loading indicator */
    fun submit() {
        val state = uiState
        if (state.isLoading) return

        val errors = FormValidator.validateAll(state.values)
        if (errors.isNotEmpty()) {
            uiState = state.copy(errors = errors, isSuccess = false)
            return
        }

        uiState = state.copy(errors = emptyMap(), isLoading = true, isSuccess = false)
        viewModelScope.launch {
            when (repository.register(state.valueOf(FormField.EMAIL))) {
                RegistrationResult.SUCCESS ->
                    uiState = uiState.copy(isLoading = false, isSuccess = true)

                RegistrationResult.EMAIL_TAKEN ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errors = mapOf(FormField.EMAIL to FieldError.EMAIL_TAKEN)
                    )
            }
        }
    }
}