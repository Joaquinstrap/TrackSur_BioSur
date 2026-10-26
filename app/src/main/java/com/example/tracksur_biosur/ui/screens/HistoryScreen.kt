package com.example.tracksur_biosur.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tracksur_biosur.domain.model.FieldReport
import com.example.tracksur_biosur.ui.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ReportViewModel,
    onNavigateToRegistry: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial de antecedentes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1D1F)
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F9FC))
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2D3B2D),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToRegistry,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", tint = Color.LightGray) },
                    label = { Text("Inicio", color = Color.LightGray) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Ya estamos en Historial */ },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Historial", tint = Color.White) },
                    label = { Text("Historial", color = Color.White) }
                )
            }
        },
        containerColor = Color(0xFFF7F9FC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (uiState.reports.isEmpty()) {
                item {
                    Text(
                        text = "No hay registros en el historial.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(uiState.reports) { report ->
                    HistoryItemCard(report = report)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun HistoryItemCard(report: FieldReport) {
    val formattedDate = remember(report.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(report.timestamp))
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "- ${report.categoria.ifBlank { "Sin Asunto" }}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            )

            if (report.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF37474F))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                )

                Text(
                    text = if (report.isDraft) "Borrador" else "Firmado (RUN: ${report.operatorRun})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (report.isDraft) Color(0xFFE65100) else Color(0xFF2E7D32)
                    )
                )
            }
        }
    }
}