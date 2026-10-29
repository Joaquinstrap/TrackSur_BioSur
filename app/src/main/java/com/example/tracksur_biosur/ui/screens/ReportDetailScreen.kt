package com.example.tracksur_biosur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale


import com.example.tracksur_biosur.domain.model.FieldReport
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
//Esta etiqueta es como dar mi consentimiento de que estoy usando componentes
//Que actualmente estan en posibilidad de cambiar en futuras versiones
//En este caso las funciones son TopAppBar o ExposedDropdownMenuBox



//Que un archivo tenga el composable hace referencia a que forma parte
//De Jetpack Compose
@Composable
//Esta anotacion le indica al compilador
//Que la aplicacion su objetivo es dibujar cosas en pantalla
//No realizar el calculo o obtenciond e cualquier valor


fun ReportDetailScreen(
    report: FieldReport,
    onNavigateBack: () -> Unit,
) {
    // Color de fondo verde oscuro corporativo (tomado de tu imagen)
    val darkGreenBg = Color(0xFF083F29)

    val formattedDate = remember(report.timestamp) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(report.timestamp))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de reporte",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkGreenBg)
            )
        },
        containerColor = darkGreenBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta blanca central con la información en letras oscuras
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Estado
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFA5D6A7)
                    ) {
                        Text(
                            text = "Entregado Oficialmente",
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Categoría
                    Column {
                        Text(
                            text = "Carpeta de Servicio",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = report.categoria.ifBlank { "Servicio sin especificar" },
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Descripción
                    Column {
                        Text(
                            text = "Descripción",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = report.descripcion.ifBlank { "Sin descripción" },
                            fontSize = 14.sp,
                            color = Color(0xFF212121)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // RUN Firma Operador
                    Column {
                        Text(
                            text = "RUN Firma Operador",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = report.operatorRun.orEmpty().ifBlank() {"No registrado" },
                            fontSize = 14.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // Fecha y hora
                    Column {
                        Text(
                            text = "Fecha de Entrega",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 13.sp,
                            color = Color(0xFF424242)
                        )
                    }

                    val photoList = remember(report.photoPath) {
                        report.photoPath?.split("|")?.filter {it.isNotEmpty() } ?:emptyList()
                    }

                    if (photoList.isNotEmpty()) {
                        HorizontalDivider(color = Color(0xFFE0E0E0))

                        Column {
                            Text(
                                text = "Respaldo de Terreno (${photoList.size} evidencias)",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ){
                                items(photoList){ path ->
                                    val bitma = remember(path){
                                        try{
                                            BitmapFactory.decodeFile(path)
                                        }catch (e: Exception){
                                            null
                                        }
                                    }

                                    bitma?.let{
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "Evidencia Fotografica",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                }
                            }
                        }
                    }



                }
            }
        }
    }




}