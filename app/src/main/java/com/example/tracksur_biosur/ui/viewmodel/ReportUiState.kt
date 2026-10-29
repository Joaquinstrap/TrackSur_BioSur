package com.example.tracksur_biosur.ui.viewmodel
import com.example.tracksur_biosur.domain.model.FieldReport
data class ReportUiState(
    val reports: List<FieldReport> = emptyList(),
    val category: String = "",
    val description: String = "",
    val operatorRun: String = "",
    val photoPath: List<String> = emptyList(),
    val extractedTextOcr: String? = null,
    val isRunValid: Boolean = true,
    val runErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val selectedReportForDetail: FieldReport? = null,


)
