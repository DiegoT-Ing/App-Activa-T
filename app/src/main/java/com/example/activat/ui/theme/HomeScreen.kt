package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
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
            // Aquí después se lanzará la caminata directa
        }) {
            Text("Iniciar caminata")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Último registro:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pasos: ---")
        Text("Tiempo: --:--")
        Text("Distancia: -- km")
    }
}
