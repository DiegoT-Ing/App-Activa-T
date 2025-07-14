package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.activat.ui.theme.components.IndicadorMetaPasos
import com.example.activat.ui.theme.components.SessionMetricsCard
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: ActivaTViewModel
) {
    // Observar estados del ViewModel
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()
    val pasosTotalesDelDia by viewModel.pasosTotalesDelDia.collectAsStateWithLifecycle()
    val ultimaSesion by viewModel.ultimaSesion.collectAsStateWithLifecycle()

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

        Spacer(modifier = Modifier.height(32.dp))

        // Indicador de progreso con datos reales
        IndicadorMetaPasos(
            currentSteps = pasosTotalesDelDia.toFloat(),
            metaPasos = usuarioData.metaPasosDiarios.toFloat()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto informativo de la meta
        Text(
            text = "Meta diaria: ${usuarioData.metaPasosDiarios} pasos",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón principal
        Button(
            onClick = {
                viewModel.iniciarCaminata()
                navController.navigate("caminata?autostart=true")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Iniciar caminata",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta del último registro usando componente reutilizable
        if (ultimaSesion != null) {
            SessionMetricsCard(
                title = "Último registro:",
                pasos = ultimaSesion!!.pasos,
                tiempo = ultimaSesion!!.tiempoFormateado(),
                distancia = ultimaSesion!!.distanciaKm
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚶",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Inicia tu primera caminata!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tus estadísticas aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información adicional
        Text(
            text = "Pasos hoy: $pasosTotalesDelDia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}