package com.example.tracksur_biosur.data.local.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tracksur_biosur.data.local.entity.FieldReportEntity
import kotlinx.coroutines.flow.Flow

//Esta anotacion marca esta interfaz como un objeto de acceso a datos
@Dao
interface FieldReportDao {
    @Query("SELECT * FROM field_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<FieldReportEntity>>
    //Flow es como una herramienta que permite emitir, procesar y cosumir
    //multiples valores de manera asincornica. Que en este caso procesa los reportes

    @Query("SELECT * FROM field_reports WHERE id = :id")
    suspend fun getReportById(id: Int): FieldReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: FieldReportEntity): Long

    @Update
    suspend fun updateReport(report: FieldReportEntity)

    @Delete
    suspend fun deleteReport(report: FieldReportEntity)

}