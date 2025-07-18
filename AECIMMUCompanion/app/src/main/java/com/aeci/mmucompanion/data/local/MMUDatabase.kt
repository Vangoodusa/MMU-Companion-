package com.aeci.mmucompanion.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.aeci.mmucompanion.data.local.dao.*
import com.aeci.mmucompanion.data.local.entity.*

@Database(
    entities = [
        FormEntity::class,
        UserEntity::class,
        EquipmentEntity::class,
        ShiftEntity::class,
        JobCardEntity::class,
        ReportEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MMUDatabase : RoomDatabase() {
    abstract fun formDao(): FormDao
    abstract fun userDao(): UserDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun shiftDao(): ShiftDao
    abstract fun jobCardDao(): JobCardDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: MMUDatabase? = null

        fun getDatabase(context: Context): MMUDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MMUDatabase::class.java,
                    "mmu_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
