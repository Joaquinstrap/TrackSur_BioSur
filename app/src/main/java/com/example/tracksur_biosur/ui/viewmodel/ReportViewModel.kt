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

    // ID del borrador en edición (0 si es un reporte nuevo desde cero)
    private var editingReportId: Int = 0

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

    fun onPhotoCaptured(path:String){
        _uiState.update{currentState ->
            currentState.copy(photoPath = currentState.photoPath + path)
        }
    }

    fun onPhotoRemoved(path: String) {
        _uiState.update { currentState ->
            currentState.copy(photoPath = currentState.photoPath - path)
        }
    }



    // 1. Cargar borrador existente en el formulario para editarlo
    fun loadDraftForEdit(report: FieldReport) {
        editingReportId = report.id
        val path = report.photoPath?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
        _uiState.update { currentState ->
            currentState.copy(
                category = report.categoria,
                description = report.descripcion,
                operatorRun = report.operatorRun ?: "",
                photoPath = path,
                extractedTextOcr = report.extractedTextOcr,
                isRunValid = true,
                runErrorMessage = null
            )
        }
    }

    // 2. Limpiar el formulario para crear un reporte nuevo desde cero
    fun prepareNewReport() {
        editingReportId = 0
        _uiState.update { currentState ->
            currentState.copy(
                category = "",
                description = "",
                operatorRun = "",
                photoPath = emptyList(),
                extractedTextOcr = null,
                isRunValid = true,
                runErrorMessage = null,
                isSaveSuccess = false
            )
        }
    }

    // 3. Función unificada para guardar (Borrador u Oficial)
    fun saveReport(isDraft: Boolean) {
        val currentState = _uiState.value

        // Validación si se intenta firmar un reporte oficial
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

            val joinedPhotos = currentState.photoPath.joinToString("|").ifBlank { null }

            val report = FieldReport(
                id = editingReportId, // Conserva el ID existente para actualizar, o 0 para crear uno nuevo
                categoria = currentState.category,
                descripcion = currentState.description,
                timestamp = System.currentTimeMillis(),
                photoPath = joinedPhotos,
                extractedTextOcr = currentState.extractedTextOcr,
                operatorRun = currentState.operatorRun.ifBlank { null },
                isDraft = isDraft
            )

            repository.saveReport(report)
            prepareNewReport()
        }
    }

    // Elimina cualquier reporte seleccionado desde la lista principal
    fun deleteReport(report: FieldReport) {
        viewModelScope.launch {
            repository.deleteReport(report)
        }
    }


    // Asigna o limpia el reporte a visualizar en la pantalla de detalle
    fun selectReportForDetail(report: FieldReport?) {
        _uiState.update { it.copy(selectedReportForDetail = report) }
    }


}