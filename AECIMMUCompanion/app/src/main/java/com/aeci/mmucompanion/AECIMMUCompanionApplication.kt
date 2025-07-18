package com.aeci.mmucompanion

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.aeci.mmucompanion.domain.repository.UserRepository
import com.aeci.mmucompanion.core.util.ServerConnectionService

@HiltAndroidApp
class AECIMMUCompanionApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var userRepository: UserRepository
    
    override fun onCreate() {
        super.onCreate()
        // WorkManager is automatically initialized when using Configuration.Provider
        
        // Start automatic server connection service
        ServerConnectionService.start(this)
        
        // Create default admin user if none exists
        CoroutineScope(Dispatchers.IO).launch {
            createDefaultAdminUser()
        }
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    
    private suspend fun createDefaultAdminUser() {
        try {
            // Check if any admin users exist
            val adminCount = userRepository.getAdminUserCount()
            if (adminCount == 0) {
                // Create default admin user
                val defaultAdmin = com.aeci.mmucompanion.domain.model.User(
                    id = "admin-default-001",
                    username = "admin",
                    fullName = "System Administrator",
                    email = "admin@aeci.com",
                    role = com.aeci.mmucompanion.domain.model.UserRole.ADMIN,
                    department = "IT Administration",
                    shiftPattern = "Day Shift",
                    permissions = listOf(
                        com.aeci.mmucompanion.domain.model.Permission.SYSTEM_ADMIN,
                        com.aeci.mmucompanion.domain.model.Permission.MANAGE_USERS,
                        com.aeci.mmucompanion.domain.model.Permission.VIEW_USERS,
                        com.aeci.mmucompanion.domain.model.Permission.MANAGE_EQUIPMENT,
                        com.aeci.mmucompanion.domain.model.Permission.VIEW_EQUIPMENT,
                        com.aeci.mmucompanion.domain.model.Permission.VIEW_FORMS,
                        com.aeci.mmucompanion.domain.model.Permission.CREATE_FORMS,
                        com.aeci.mmucompanion.domain.model.Permission.EDIT_FORMS,
                        com.aeci.mmucompanion.domain.model.Permission.DELETE_FORMS,
                        com.aeci.mmucompanion.domain.model.Permission.SUBMIT_FORMS,
                        com.aeci.mmucompanion.domain.model.Permission.APPROVE_FORMS,
                        com.aeci.mmucompanion.domain.model.Permission.EXPORT_DATA,
                        com.aeci.mmucompanion.domain.model.Permission.VIEW_REPORTS
                    ),
                    isActive = true,
                    lastLoginAt = null,
                    biometricEnabled = false
                )
                
                // Default password: AECIAdmin2025! (should be changed on first login)
                userRepository.createUserWithPassword(defaultAdmin, "AECIAdmin2025!")
                android.util.Log.i("AECIApp", "Default admin user created with username: admin, password: AECIAdmin2025!")
            }
        } catch (e: Exception) {
            android.util.Log.e("AECIApp", "Error creating default admin user", e)
        }
    }
}
