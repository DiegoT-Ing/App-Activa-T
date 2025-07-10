package com.example.activat.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("caminata") { CaminataScreen() }
        composable("historial") { HistorialScreen() }
        composable("usuario") { UsuarioScreen() }
    }
}
