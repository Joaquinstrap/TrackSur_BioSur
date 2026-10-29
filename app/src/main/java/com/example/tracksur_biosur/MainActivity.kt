package com.example.tracksur_biosur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tracksur_biosur.data.local.database.AppDataBase
import com.example.tracksur_biosur.data.repository.FieldReportRepositoryImpl
import com.example.tracksur_biosur.ui.navigation.Screen
import com.example.tracksur_biosur.ui.screens.CreateReportScreen
import com.example.tracksur_biosur.ui.screens.HistoryScreen
import com.example.tracksur_biosur.ui.screens.RegistryScreen
import com.example.tracksur_biosur.ui.theme.TrackSur_BioSurTheme
import com.example.tracksur_biosur.ui.viewmodel.ReportViewModel
import com.example.tracksur_biosur.ui.screens.ReportDetailScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicialización de Room y Capa de Datos
        val database = AppDataBase.getDataBase(applicationContext)
        val repository = FieldReportRepositoryImpl(database.fieldReportDao())

        // 2. ViewModel con Repositorio
        val viewModel = ReportViewModel(repository)

        setContent {
            TrackSur_BioSurTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Registry.route
                    ) {
                        composable(Screen.Registry.route) {
                            RegistryScreen(
                                viewModel = viewModel,
                                onNavigateToCreate = {
                                    navController.navigate(Screen.CreateReport.route)
                                },
                                onNavigateToHistory = {
                                    navController.navigate(Screen.History.route)
                                },
                                onNavigateToDetail = {
                                    navController.navigate(Screen.ReportDetail.route)
                                }
                            )
                        }

                        composable(Screen.CreateReport.route) {
                            CreateReportScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Screen.History.route) {
                            HistoryScreen(
                                viewModel = viewModel,
                                onNavigateToRegistry = {
                                    navController.navigate(Screen.Registry.route) {
                                        popUpTo(Screen.Registry.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.ReportDetail.route) {
                            val selectedReport = viewModel.uiState.collectAsState().value.selectedReportForDetail
                            selectedReport?.let {report ->
                                ReportDetailScreen(
                                    report = report,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }



                    }
                }
            }
        }
    }
}

