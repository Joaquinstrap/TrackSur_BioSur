package com.example.tracksur_biosur.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tracksur_biosur.data.local.dao.FieldReportDao
import com.example.tracksur_biosur.data.local.entity.FieldReportEntity

@Database(
    entities = [FieldReportEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun fieldReportDao(): FieldReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "biorsur_database"
                ).build()
                INSTANCE = instance
                instance
            }


        }
    }
}