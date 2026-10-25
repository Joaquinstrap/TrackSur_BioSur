package com.example.tracksur_biosur.domain.model

import android.view.inputmethod.ExtractedText

data class FieldReport(
    //Identificador unico que tendra cada reporte en la base de dato
    val id: Int = 0,

    //Guardara la categoria seleccionada por el operario
    val categoria: String,

    //Texto que guardara los antecedentes e informacion escrita en el formulario
    val descripcion: String,

    //Guardara la fecha y hora de creacion exacta  en milisegundos
    val timestamp: Long = System.currentTimeMillis(),

    //Guardara la ruta de imagenes o fotos adjuntadas. y si no hay foto sera null
    val photoPath: String? = null,

    //Guardara el texto escaneado por la camara(OCR)
    val extractedTextOcr: String? = null,

    //Guarda el run del trabajador cuando este firme y confirme el envio
    val operatorRun: String? = null,

    //Este valor es un booleano que verificara si el formulario permanece como borrador o fue enviado
    //Siendo true si es borrador o false si ya fue enviado
    val isDraft: Boolean = false,
)
