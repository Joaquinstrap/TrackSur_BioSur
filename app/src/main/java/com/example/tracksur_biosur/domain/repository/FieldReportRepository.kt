package com.example.tracksur_biosur.domain.repository

import com.example.tracksur_biosur.domain.model.FieldReport
import kotlinx.coroutines.flow.Flow


interface FieldReportRepository {

    fun getAllReports(): Flow<List<FieldReport>>

    suspend fun getReportById(id: Int): FieldReport?

    suspend fun saveReport(report: FieldReport): Long

    suspend fun updateReport(report: FieldReport)

    suspend fun deleteReport(report: FieldReport)
}