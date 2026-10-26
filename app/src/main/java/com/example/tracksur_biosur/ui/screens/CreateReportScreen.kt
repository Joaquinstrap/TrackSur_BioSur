package com.example.tracksur_biosur.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    viewModel: com.example.tracksur_biosur.ui.viewmodel.ReportViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado local para el diálogo de confirmación al salir
    var showExitDialog by remember { mutableStateOf(false) }

    // Estado del menú desplegable de categorías
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Inspección Biológica", "Muestreo de Agua", "Control de Plagas", "Auditoría Ambiental", "Otro")

    // Comprueba si el usuario ha escrito algo antes de salir
    val isFormDirty = uiState.category.isNotBlank() || uiState.description.isNotBlank() || uiState.operatorRun.isNotBlank()

    fun handleBackPress() {
        if (isFormDirty) {
            showExitDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Intercepta el botón físico de atrás en Android
    BackHandler {
        handleBackPress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "A que categoría pertenecen sus antecedentes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F9FC))
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Selector de Categoría (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = uiState.category.ifBlank { "Seleccione una categoría..." },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.onCategoryChanged(category)
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. Área de texto para la Descripción
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChanged(it) },
                    placeholder = { Text("Ingrese información...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFF3E0), // Naranja pastel suave similar al boceto
                        unfocusedContainerColor = Color(0xFFFFF8E1),
                        focusedBorderColor = Color(0xFFFFB74D),
                        unfocusedBorderColor = Color(0xFFFFE0B2)
                    )
                )

                // 3. Campo RUN del Operador con validación Módulo 11 en tiempo real
                OutlinedTextField(
                    value = uiState.operatorRun,
                    onValueChange = { viewModel.onOperatorRunChanged(it) },
                    label = { Text("RUN Firma Operador") },
                    placeholder = { Text("Ej: 12345678-K") },
                    isError = !uiState.isRunValid,
                    supportingText = {
                        uiState.runErrorMessage?.let { msg ->
                            Text(text = msg, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // 4. Barra de Acciones Inferior (Escaner e Insertar)
            // 4. Barra de Acciones Inferior (Escáner e Insertar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Escáner (solo texto nativo)
                OutlinedButton(
                    onClick = { /* Lógica futura para abrir cámara/OCR */ },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
                ) {
                    Text("Escáner")
                }

                // Botón Guardar Borrador
                OutlinedButton(
                    onClick = {
                        viewModel.saveReport(isDraft = true)
                        onNavigateBack()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Borrador")
                }

                // Botón Firmar / Insertar
                Button(
                    onClick = {
                        viewModel.saveReport(isDraft = false)
                        if (uiState.isRunValid && uiState.operatorRun.isNotBlank()) {
                            onNavigateBack()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Insertar")
                }
            }
        }
    }

    // Pop-up superior de confirmación ("Lo que no haya guardado se borrara...")
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Desea salir?") },
            text = { Text("Lo que no haya guardado se borrará...") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Sí, salir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}