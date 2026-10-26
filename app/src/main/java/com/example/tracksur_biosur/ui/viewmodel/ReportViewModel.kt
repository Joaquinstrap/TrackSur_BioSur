package com.example.tracksur_biosur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracksur_biosur.domain.model.FieldReport
import com.example.tracksur_biosur.domain.repository.FieldReportRepository
import com.example.tracksur_biosur.domain.usecase.ValidateRunUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportViewModel(
    private val repository: FieldReportRepository,
    private val validateRunUseCase: ValidateRunUseCase = ValidateRunUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            repository.getAllReports().collect { reports ->
                _uiState.update { currentState ->
                    currentState.copy(reports = reports)
                }
            }
        }
    }

    fun onCategoryChanged(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onOperatorRunChanged(run: String) {
        if (run.isBlank()) {
            _uiState.update {
                it.copy(
                    operatorRun = run,
                    isRunValid = true,
                    runErrorMessage = null
                )
            }
            return
        }

        val result = validateRunUseCase.execute(run)
        val isValid = result is ValidateRunUseCase.Result.Success
        val errorMessage = (result as? ValidateRunUseCase.Result.Error)?.message

        _uiState.update {
            it.copy(
                operatorRun = run,
                isRunValid = isValid,
                runErrorMessage = errorMessage
            )
        }
    }

    fun saveReport(isDraft: Boolean) {
        val currentState = _uiState.value

        if (!isDraft && (!currentState.isRunValid || currentState.operatorRun.isBlank())) {
            _uiState.update {
                it.copy(
                    isRunValid = false,
                    runErrorMessage = "Debe ingresar un RUN válido para firmar el reporte"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val report = FieldReport(
                categoria = currentState.category,
                descripcion = currentState.description,
                timestamp = System.currentTimeMillis(),
                photoPath = currentState.photoPath,
                extractedTextOcr = currentState.extractedTextOcr,
                operatorRun = currentState.operatorRun,
                isDraft = isDraft
            )

            repository.saveReport(report)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSaveSuccess = true,
                    category = "",
                    description = "",
                    operatorRun = "",
                    photoPath = null,
                    extractedTextOcr = null
                )
            }
        }
    }
}
