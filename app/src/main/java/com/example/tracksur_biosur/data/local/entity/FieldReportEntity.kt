package com.example.tracksur_biosur.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_report")
data class FieldReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoria: String,
    val descripcion: String,
    val timestamp: Long = System.currentTimeMillis(),
    val photoPath: String? = null,
    val extractedTextOcr: String? = null,
    val operatorRun: String? = null,
    val isDraft: Boolean = false,


)
