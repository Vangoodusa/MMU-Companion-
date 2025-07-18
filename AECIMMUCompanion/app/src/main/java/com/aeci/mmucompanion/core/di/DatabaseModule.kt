package com.aeci.mmucompanion.core.di

import android.content.Context
import androidx.room.Room
import com.aeci.mmucompanion.data.local.MMUDatabase
import com.aeci.mmucompanion.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideMMUDatabase(@ApplicationContext context: Context): MMUDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MMUDatabase::class.java,
            "mmu_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideFormDao(database: MMUDatabase): FormDao = database.formDao()
    
    @Provides
    fun provideUserDao(database: MMUDatabase): UserDao = database.userDao()
    
    @Provides
    fun provideEquipmentDao(database: MMUDatabase): EquipmentDao = database.equipmentDao()
    
    @Provides
    fun provideShiftDao(database: MMUDatabase): ShiftDao = database.shiftDao()
    
    @Provides
    fun provideReportDao(database: MMUDatabase): ReportDao = database.reportDao()
    
    @Provides
    fun provideJobCardDao(database: MMUDatabase): JobCardDao = database.jobCardDao()
}
