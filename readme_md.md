# 📱 Bitácora de Servicios (BioSur)

![Android Studio](https://img.shields.io/badge/Android%20Studio-Hedgehog%20%7C%20Iguana-green?logo=androidstudio)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen?logo=jetpackcompose)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange)
![Duoc UC](https://img.shields.io/badge/Evaluación-Parcial%202%20(DSY1105)-darkblue)

**Bitácora de Servicios** (anteriormente TrackSur) es una aplicación móvil nativa en Android diseñada para operarios e inspectores de terreno de la empresa **BioSur**. La aplicación permite registrar, respaldar con múltiples evidencias fotográficas, escanear documentos mediante OCR y validar firmas de servicios industriales y ambientales en terreno.

---

## 👥 Integrantes del Equipo

* **Estudiante 1:** [Nombre y Apellido] - *[RUT / Usuario GitHub]*
* **Estudiante 2:** [Nombre y Apellido] - *[RUT / Usuario GitHub]*
* **Sección:** [Número de Sección]
* **Docente:** [Nombre del Profesor]
* **Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105) - Duoc UC

---

## 🚀 Funcionalidades Principales

### 1. Gestión de Formularios y Carpetas de Servicios
* **Fichas Técnicas:** Categorización de servicios específicos (Mantención de Baños Químicos, Plantas Elevadoras, Tratamiento de Aguas, Inspección de Ductos, Muestreos, entre otros).
* **Validación de RUN (Módulo 11):** Algoritmo de validación del Digito Verificador del RUN del operador en tiempo real antes de permitir la firma y envío oficial del reporte.
* **Control de Crecimiento de UI:** Cuadros de texto optimizados con límites fijos de altura (`140.dp`) y desplazamiento vertical (*scroll*) interno para proteger la estructura visual.

### 2. Reconocimiento Óptico de Caracteres (OCR - ML Kit)
* **Escáner de Texto Nativo:** Módulo con **Google ML Kit Text Recognition** que permite tomar una fotografía a una orden de trabajo e insertar automáticamente el texto leído dentro de la casilla de descripción, sin mezclar la imagen en el carrusel de evidencias.

### 3. Captura y Carga Multimedia (Respaldo de Terreno)
* **Múltiples Evidencias:** Permite adjuntar múltiples fotografías de respaldo a través de la **Cámara Nativa** o seleccionando imágenes en lote desde la **Galería del dispositivo** (`GetMultipleContents`).
* **Carrusel Interactivo:** Visualización en horizontal (`LazyRow`) con botones de eliminación individual de fotos antes de guardar.

### 4. Persistencia Local y Estado Offline
* **Base de Datos Room:** Almacenamiento estructurado en la memoria local con las tablas y entidades necesarias (`FieldReport`).
* **Soporte Offline:** Manejo automático de falta de conectividad a Internet. Si no hay señal, la app advierte al usuario y guarda automáticamente el registro en estado **Borrador** para su posterior sincronización.

---

## 🛠️ Arquitectura y Tecnologías Utilizadas

La aplicación está construida siguiendo los principios de la **Clean Architecture** y el patrón de diseño **MVVM (Model-View-ViewModel)** recomendado por Google:

* **Lenguaje:** Kotlin
* **Interfaz de Usuario (UI):** Jetpack Compose (Declarativa, adaptativa e interactiva)
* **Gestión de Estado:** `StateFlow`, `collectAsState`, `rememberSaveable`
* **Persistencia Local:** Room Database (SQLite)
* **Recursos Nativos:**
  1. **Cámara Nativa:** Captura de fotografías mediante `FileProvider` e `ActivityResultContracts.TakePicture`.
  2. **Galería / Almacenamiento:** Acceso multiselección a través de `ActivityResultContracts.GetMultipleContents`.
* **Procesamiento de Imágenes / IA:** Google ML Kit Vision Text Recognition

---

## 📂 Estructura del Proyecto

```text
com.example.tracksur_biosur/
├── data/
│   ├── local/              # Room Database, DAOs y Entidades
│   └── repository/         # Implementación del Repositorio de datos
├── domain/
│   ├── model/              # Modelos de dominio (FieldReport)
│   ├── repository/         # Interfaces del repositorio
│   └── usecase/            # Casos de uso (ValidateRunUseCase)
├── ui/
│   ├── screens/            # Pantallas Compose (Registry, CreateReport, ReportDetail)
│   ├── viewmodel/          # ReportViewModel y ReportUiState
│   └── theme/              # Colores, Tipografías y Temas corporativos
└── util/                   # Utilidades de conectividad y formateo
```

---

## 📋 Requisitos de la Pauta Cumplidos (DSY1105)

| Indicador de Logro | Funcionalidad Implementada |
| :--- | :--- |
| **IL 2.1 (UI/UX y Navegación)** | Diseño adaptativo con TopAppBar, NavigationBar, diálogos emergentes de confirmación y flujo de navegación entre la Bitácora, Formulario y Detalle. |
| **IL 2.2 (Lógica y Estado)** | Estado desacoplado de la UI mediante `ReportViewModel` y `ReportUiState`. Validación Módulo 11 en `ValidateRunUseCase`. |
| **IL 2.3 (Modularidad y Room)** | Estructura por capas (Data, Domain, UI) y persistencia local Room guardando reportes, borradores e imágenes unificadas. |
| **IL 2.4 (Recursos Nativos)** | Integración exitosa con la Cámara (Captura + OCR) y Galería/Almacenamiento del dispositivo. |

---

## ⚙️ Instrucciones de Instalación y Ejecución

1. Clona este repositorio usando Git:
   ```bash
   git clone https://github.com/TU_USUARIO/TrackSur_BioSur.git
   ```
2. Abre **Android Studio** (Hedgehog, Iguana o superior).
3. Selecciona `Open` y busca la carpeta raíz del proyecto clonado.
4. Espera a que la sincronización de **Gradle** finalice correctamente.
5. Ejecuta la aplicación en un dispositivo físico con Android 8.0 (API 26) o superior (recomendado para probar funciones de cámara y permisos nativos) o en un Emulador.

---

## 📌 Planificación del Proyecto

La organización y distribución de tareas de este encargo se gestionaron a través de **Trello**:
* 🔗 [Enlace al Tablero de Trello del Equipo](https://trello.com) *(Reemplazar con la URL pública de su tablero)*