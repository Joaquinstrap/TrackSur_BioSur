package com.example.tracksur_biosur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun RegistryScreen(
    viewModel: ReportViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Filtro de reportes según su estado
    val drafts = remember(uiState.reports) { uiState.reports.filter { it.isDraft } }
    val submitted = remember(uiState.reports) { uiState.reports.filter { !it.isDraft } }

    // Control de apertura/cierre de acordeones
    var isDraftsExpanded by remember { mutableStateOf(true) }
    var isSubmittedExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registro",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1D1F)
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F9FC)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2D3B2D), // Verde oscuro elegante según boceto
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Ya estamos en Registro */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", tint = Color.White) },
                    label = { Text("Inicio", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.Star, contentDescription = "Historial", tint = Color.LightGray) },
                    label = { Text("Historial", color = Color.LightGray) }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Color(0xFFD4E157), // Tono lima/verde claro sutil
                contentColor = Color(0xFF1B5E20),
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo") },
                text = { Text("Nuevo", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Sección 1: Borradores
            item {
                SectionHeader(
                    title = "Borradores (${drafts.size})",
                    isExpanded = isDraftsExpanded,
                    onToggle = { isDraftsExpanded = !isDraftsExpanded },
                    headerColor = Color(0xFFFFF3E0) // Fondo naranja sutil
                )
            }

            item {
                AnimatedVisibility(visible = isDraftsExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (drafts.isEmpty()) {
                            EmptyStateMessage("No hay borradores pendientes.")
                        } else {
                            drafts.forEach { report ->
                                ModernReportCard(report = report, isDraft = true)
                            }
                        }
                    }
                }
            }

            // Sección 2: Antecedentes Entregados
            item {
                SectionHeader(
                    title = "Antecedentes entregados (${submitted.size})",
                    isExpanded = isSubmittedExpanded,
                    onToggle = { isSubmittedExpanded = !isSubmittedExpanded },
                    headerColor = Color(0xFFE8F5E9) // Fondo verde sutil
                )
            }

            item {
                AnimatedVisibility(visible = isSubmittedExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (submitted.isEmpty()) {
                            EmptyStateMessage("No hay antecedentes registrados.")
                        } else {
                            submitted.forEach { report ->
                                ModernReportCard(report = report, isDraft = false)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // Espacio para no solapar el FAB
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    headerColor: Color
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(12.dp),
        color = headerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Plegar" else "Desplegar",
                tint = Color(0xFF546E7A)
            )
        }
    }
}

@Composable
private fun ModernReportCard(
    report: FieldReport,
    isDraft: Boolean
) {
    val cardBg = if (isDraft) Color(0xFFFFE0B2) else Color(0xFFC8E6C9)
    val badgeColor = if (isDraft) Color(0xFFE65100) else Color(0xFF2E7D32)
    val formattedDate = remember(report.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(report.timestamp))
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "- ${report.categoria.ifBlank { "Sin Asunto" }}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                )
                Text(
                    text = if (isDraft) "Borrador" else "Entregado",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor
                )
            }

            if (report.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF37474F)),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = Color(0xFF616161)
                )
            )
        }
    }
}

@Composable
private fun EmptyStateMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)
    )
}