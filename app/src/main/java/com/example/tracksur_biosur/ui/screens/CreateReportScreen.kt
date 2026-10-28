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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts


import android.net.Uri
import android.os.Environment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import android.content.Intent
import android.provider.MediaStore

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

    //Guarda la ruta del archivo temporal para llamar a la camara
    //De esta variable solo puede existir una en el codigo
    var tempPhotoPath by remember { mutableStateOf<String?>(null) }




    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ){isSuccess ->
        if(isSuccess && tempPhotoPath != null) {
            viewModel.onPhotoCaptured(tempPhotoPath!!)
        }
    }


    val categories = listOf(
        "Mantención / Limpieza de Baños Químicos",
        "Mantención de Sistemas Sanitarios",
        "Disposición / Retiro de Aguas Servidas",
        "Gestión y Transporte de Residuos",
        "Inspección de Infraestructura Sanitaria",
        "Otro"
    )

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

    val context = LocalContext.current







    fun launchCamera() {
        try {
            // 1. Obtener la carpeta de almacenamiento e integrar verificación de existencia
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs()
            }

            // 2. Crear el archivo temporal .jpg
            val photoFile = File.createTempFile(
                "biosur_${System.currentTimeMillis()}_",
                ".jpg",
                storageDir
            )

            // 3. Generar la URI del FileProvider usando la ID exacta de la app
            val authority = "${context.applicationContext.packageName}.fileprovider"
            val photoUri: Uri = FileProvider.getUriForFile(
                context,
                authority,
                photoFile
            )

            // 4. Guardar la ruta local e invocar la cámara nativa
            tempPhotoPath = photoFile.absolutePath
            cameraLauncher.launch(photoUri)

        } catch (e: Exception) {
            // El try-catch evita que la app colapse si ocurre algún problema de permisos o archivo
            android.util.Log.e("CAMERA_ERROR", "Error al iniciar la cámara: ${e.message}", e)
        }
    }

    // Lanzador para pedir el permiso de cámara al usuario
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            android.util.Log.e("CAMERA_TEST", "El usuario denegó el permiso de cámara")
        }
    }

    // Función que verifica si ya existe el permiso antes de abrir la cámara
    fun checkAndLaunchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "A que categoría pertenecen sus antecedentes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White
                        ),
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
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedContainerColor = Color(0xFFFFF3E0),
                        unfocusedContainerColor = Color(0xFFFFF8E1),
                        focusedBorderColor = Color(0xFFFFB74D),
                        unfocusedBorderColor = Color(0xFFFFE0B2)
                    )
                )

                // 3. Campo RUN del Operador con validación Módulo 11 en tiempo real
                OutlinedTextField(
                    value = uiState.operatorRun,
                    onValueChange = { viewModel.onOperatorRunChanged(it) },
                    label = { Text("RUN Firma Operador", color = Color.DarkGray) },
                    placeholder = { Text("Ej: 12345678-K", color= Color.DarkGray) },
                    isError = !uiState.isRunValid,
                    supportingText = {
                        uiState.runErrorMessage?.let { msg ->
                            Text(text = msg, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Color(0xFF2E7D32),
                        unfocusedLabelColor = Color.DarkGray,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    ),


                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // 4. Barra de Acciones Inferior (Escaner e Insertar)
            // 4. Barra de Acciones Inferior (Escáner e Insertar)
            // 4. Barra de Acciones Inferior (Escáner, Borrador e Insertar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Escáner
                OutlinedButton(
                    onClick = {checkAndLaunchCamera() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
                ) {
                    Text(if(uiState.photoPath != null) "Foto Adjunta" else "Escáner")
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

                // <-- AQUÍ VA EL BLOQUE MEJORADO:
                val isFormValid = uiState.category.isNotBlank() &&
                        uiState.description.isNotBlank() &&
                        uiState.isRunValid &&
                        uiState.operatorRun.isNotBlank()

                Button(
                    onClick = {
                        viewModel.saveReport(isDraft = false)
                        onNavigateBack()
                    },
                    enabled = isFormValid, // Se deshabilita si faltan datos por rellenar
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        disabledContainerColor = Color(0xFFA5D6A7)
                    ),
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