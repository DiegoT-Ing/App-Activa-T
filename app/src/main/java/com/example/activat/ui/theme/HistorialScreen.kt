package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.format.DateTimeFormatter

@Composable
fun HistorialScreen(viewModel: ActivaTViewModel) {
    // Estados del ViewModel
    val historialSesiones by viewModel.historialSesiones.collectAsStateWithLifecycle()
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()

    // Selector de período
    var periodoSeleccionado by remember { mutableStateOf("Día") }
    val opciones = listOf("Día", "Semana", "Mes")

    // Filtrar sesiones según el período seleccionado
    val sesionesFiltradas = remember(historialSesiones, periodoSeleccionado) {
        viewModel.obtenerSesionesPorPeriodo(periodoSeleccionado)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Historial de Caminatas",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de período
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ver:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    opciones.forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = (opcion == periodoSeleccionado),
                                    onClick = { periodoSeleccionado = opcion },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (opcion == periodoSeleccionado),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = opcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estadísticas del período
        if (sesionesFiltradas.isNotEmpty()) {
            val totalPasos = sesionesFiltradas.sumOf { it.pasos }
            val totalTiempo = sesionesFiltradas.sumOf { it.tiempoSegundos }
            val totalDistancia = sesionesFiltradas.sumOf { it.distanciaKm.toDouble() }.toFloat()
            val promedioPasos = totalPasos / sesionesFiltradas.size

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen del ${periodoSeleccionado.lowercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total pasos:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$totalPasos",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column {
                            Text(
                                text = "Promedio:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$promedioPasos pasos",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column {
                            Text(
                                text = "Distancia:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${"%.2f".format(totalDistancia)} km",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sesiones realizadas: ${sesionesFiltradas.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de sesiones
        if (sesionesFiltradas.isNotEmpty()) {
            Text(
                text = "Sesiones individuales:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sesionesFiltradas.sortedByDescending { it.fecha }) { sesion ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sesion.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = sesion.fecha.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${sesion.pasos} pasos",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = sesion.tiempoFormateado(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${"%.2f".format(sesion.distanciaKm)} km",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Barra de progreso hacia la meta
                            if (usuarioData.metaPasosDiarios > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val progreso = (sesion.pasos.toFloat() / usuarioData.metaPasosDiarios.toFloat()).coerceIn(0f, 1f)

                                LinearProgressIndicator(
                                    progress = { progreso },
                                    modifier = Modifier.fillMaxWidth(),
                                )

                                Text(
                                    text = "${(progreso * 100).toInt()}% de la meta diaria",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Estado vacío
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay sesiones registradas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Comienza tu primera caminata para ver estadísticas aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}