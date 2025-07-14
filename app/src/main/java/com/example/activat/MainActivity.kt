package com.example.activat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.example.activat.ui.theme.ActivaTTheme
import com.example.activat.ui.theme.NavigationGraph
import com.example.activat.viewmodel.ActivaTViewModel
import com.example.activat.viewmodel.ActivaTViewModelFactory

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableIntStateOf
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Aquí puedes hacer algo con la respuesta si quieres (por ahora no necesario)
        }

        setContent {
            ActivaTTheme {
                // Crear ViewModel una sola vez para toda la app
                val viewModel: ActivaTViewModel = viewModel(
                    factory = ActivaTViewModelFactory(this@MainActivity)
                )
                ActivaTApp(viewModel = viewModel)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
}

@Composable
fun ActivaTApp(viewModel: ActivaTViewModel) {
    val navController = rememberNavController()
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    val items = listOf(
        NavItem("Home", Icons.Default.Home, "home"),
        NavItem("Historial", Icons.Default.History, "historial"),
        NavItem("Usuario", Icons.Default.Person, "usuario")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector, val route: String)