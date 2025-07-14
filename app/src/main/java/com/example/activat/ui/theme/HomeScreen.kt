package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.activat.ui.theme.components.IndicadorMetaPasos

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a ActivaT",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            navController.navigate("caminata?autostart=true")
        }) {
            Text("Iniciar caminata")
        }

        Spacer(modifier = Modifier.height(32.dp))

        val metaPasos = 6000f
        val pasosDelDia = 3240f // Simulado; luego será real

        IndicadorMetaPasos(currentSteps = pasosDelDia, metaPasos = metaPasos)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Último registro:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pasos: ---")
                Text("Tiempo: --:--")
                Text("Distancia: -- km")
            }
        }
    }
}
