package com.example.activat.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.activat.viewmodel.ActivaTViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: ActivaTViewModel
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("caminata?autostart={autostart}") { backStackEntry ->
            val autoStartArg = backStackEntry.arguments?.getString("autostart") == "true"
            CaminataScreen(
                autoStart = autoStartArg,
                viewModel = viewModel,
                onFinalizar = {
                    navController.popBackStack("home", false)
                }
            )
        }
        composable("historial") {
            HistorialScreen(viewModel = viewModel)
        }
        composable("usuario") {
            UsuarioScreen(viewModel = viewModel)
        }
    }
}