@file:OptIn(ExperimentalMaterial3Api::class)

package com.aeci.mmucompanion.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aeci.mmucompanion.R
import com.aeci.mmucompanion.presentation.component.AECIIcons
import com.aeci.mmucompanion.presentation.viewmodel.AuthViewModel
import androidx.compose.material3.Divider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AECIMMUCompanionApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    
    if (authState.isAuthenticated) {
        MainScreen(navController = navController, authViewModel = authViewModel)
    } else {
        LoginScreen(
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.aeci_logo),
                            contentDescription = "AECI Logo",
                            modifier = Modifier.height(36.dp).padding(end = 8.dp)
                        )
                        Text("AECI MMU Companion")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Menu button
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Dropdown menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        // User Profile submenu
                        DropdownMenuItem(
                            text = { Text("User Profile") },
                            onClick = { 
                                navController.navigate("profile")
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        
                        // Dashboard Options submenu
                        if (authState.currentUser?.role?.name in listOf("ADMIN", "SUPERVISOR")) {
                            DropdownMenuItem(
                                text = { Text("Admin Dashboard") },
                                onClick = { 
                                    navController.navigate("admin_dashboard")
                                    showMenu = false 
                                },
                                leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) }
                            )
                        }
                        
                        if (authState.currentUser?.role?.name in listOf("MAINTENANCE", "SUPERVISOR")) {
                            DropdownMenuItem(
                                text = { Text("Millwright Dashboard") },
                                onClick = { 
                                    navController.navigate("millwright_dashboard")
                                    showMenu = false 
                                },
                                leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) }
                            )
                        }
                        
                        Divider()
                        
                        // Export Data
                        DropdownMenuItem(
                            text = { Text("Export Data") },
                            onClick = { 
                                navController.navigate("export")
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null) }
                        )
                        
                        // Settings
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { 
                                navController.navigate("settings")
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                        
                        // Logout
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = { 
                                authViewModel.logout()
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Dashboard") },
                    selected = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Forms") },
                    selected = currentRoute?.startsWith("forms") == true,
                    onClick = { navController.navigate("forms") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    label = { Text("Technician") },
                    selected = currentRoute?.startsWith("technician") == true,
                    onClick = { navController.navigate("technician_dashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Storage, contentDescription = null) },
                    label = { Text("Equipment") },
                    selected = currentRoute?.startsWith("equipment") == true,
                    onClick = { navController.navigate("equipment") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text("Reports") },
                    selected = currentRoute?.startsWith("reports") == true,
                    onClick = { navController.navigate("reports") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(navController = navController)
            }
            composable("forms") {
                FormsListScreen(navController = navController)
            }
            composable("forms/category/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                FormsCategoryScreen(
                    navController = navController,
                    category = category ?: "production"
                )
            }
            composable("forms/create/{formType}") { backStackEntry ->
                val formType = backStackEntry.arguments?.getString("formType")
                WorkingFormCreationScreen(
                    navController = navController,
                    formType = formType
                )
            }
            composable("forms/edit/{formId}") { backStackEntry ->
                val formId = backStackEntry.arguments?.getString("formId")
                FormEditScreen(
                    navController = navController,
                    formId = formId
                )
            }
            composable("equipment") {
                EquipmentStatusScreen()
            }
            composable("equipment/list") {
                EquipmentListScreen(navController = navController)
            }
            composable("equipment/{equipmentId}") { backStackEntry ->
                val equipmentId = backStackEntry.arguments?.getString("equipmentId")
                EquipmentDetailsScreen(
                    navController = navController,
                    equipmentId = equipmentId
                )
            }
            composable("reports") {
                ReportsScreen(navController = navController)
            }
            composable("export") {
                ExportScreen(navController = navController)
            }
            composable("password_change") {
                PasswordChangeScreen(
                    navController = navController,
                    isFirstLogin = false
                )
            }
            composable("profile") {
                UserProfileScreen(navController = navController)
            }
            composable("admin_dashboard") {
                AdminDashboardScreen(navController = navController)
            }
            composable("millwright_dashboard") {
                MillwrightDashboardScreen(
                    onNavigateToEquipmentDetails = { equipmentId ->
                        navController.navigate("equipment/$equipmentId")
                    }
                )
            }
            composable("technician_dashboard") {
                TechnicianDashboardScreen(navController = navController)
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
