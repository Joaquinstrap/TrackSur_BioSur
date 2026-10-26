package com.example.tracksur_biosur.ui.navigation


sealed class Screen(val route: String) { // <-- ¡IMPORTANTE poner 'val' aquí!
    object Registry : Screen("registry")
    object CreateReport : Screen("create_report")
    object History : Screen("history")
}