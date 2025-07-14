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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.components.StepsChart
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.format.DateTimeFormatter

@Composable
fun HistorialScreen(viewModel: ActivaTViewModel) {
    // Estados del ViewModel
    val historialSesiones by viewModel.historialSesiones.collectAsStateWithLifecycle()
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()

    // Selector de período
    var periodoSeleccionado by remember { mutableStateOf("Semana") }
    val opciones = listOf("Día", "Semana", "Mes")

    // Filtrar sesiones según el período seleccionado
    val sesionesFiltradas = remember(historialSesiones, periodoSeleccionado) {
        viewModel.obtenerSesionesPorPeriodo(periodoSeleccionado)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Historial de Actividad",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Selector de período con nuevo diseño
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Período de análisis:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        opciones.forEach { opcion ->
                            FilterChip(
                                onClick = { periodoSeleccionado = opcion },
                                label = { Text(opcion) },
                                selected = (opcion == periodoSeleccionado),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // CORREGIDO: Mostrar gráfica siempre, con estado vacío si no hay datos
        item {
            StepsChart(
                sesiones = sesionesFiltradas,
                periodo = periodoSeleccionado
            )
        }

        // Resumen estadístico mejorado - Solo si hay datos
        if (sesionesFiltradas.isNotEmpty()) {
            item {
                val totalPasos = sesionesFiltradas.sumOf { it.pasos }
                val totalTiempo = sesionesFiltradas.sumOf { it.tiempoSegundos }
                val totalDistancia = sesionesFiltradas.sumOf { it.distanciaKm.toDouble() }.toFloat()
                val promedioPasos = totalPasos / sesionesFiltradas.size

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = FitnessGreen60.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "📊 Resumen del ${periodoSeleccionado.lowercase()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FitnessGreen60
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid de estadísticas CORREGIDO
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatsCard(
                                    title = "Total",
                                    value = "$totalPasos",
                                    subtitle = "pasos",
                                    modifier = Modifier.weight(1f),
                                    color = FitnessGreen60
                                )
                                StatsCard(
                                    title = "Promedio",
                                    value = "$promedioPasos",
                                    subtitle = "pasos/día",
                                    modifier = Modifier.weight(1f),
                                    color = TechBlue60
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatsCard(
                                    title = "Distancia",
                                    value = "%.1f".format(totalDistancia),
                                    subtitle = "km",
                                    modifier = Modifier.weight(1f),
                                    color = EnergyOrange60
                                )
                                StatsCard(
                                    title = "Sesiones",
                                    value = "${sesionesFiltradas.size}",
                                    subtitle = "completadas",
                                    modifier = Modifier.weight(1f),
                                    color = MotivationPurple60
                                )
                            }
                        }
                    }
                }
            }

            // Lista de sesiones individuales mejorada
            item {
                Text(
                    text = "Sesiones detalladas:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            items(sesionesFiltradas.sortedByDescending { it.fecha }) { sesion ->
                SessionCard(
                    sesion = sesion,
                    metaDiaria = usuarioData.metaPasosDiarios
                )
            }
        } else {
            // CORREGIDO: Estado vacío mejorado cuando no hay sesiones
            item {
                EmptyHistoryState()
            }
        }
    }
}

@Composable
private fun StatsCard(
    title: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SessionCard(
    sesion: com.example.activat.data.SesionCaminata,
    metaDiaria: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🏃‍♂️ ${sesion.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = sesion.fecha.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Métricas principales - CORREGIDO
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricColumn(
                        value = "${sesion.pasos}",
                        label = "pasos",
                        icon = "👣",
                        color = FitnessGreen60
                    )
                    MetricColumn(
                        value = sesion.tiempoFormateado(),
                        label = "tiempo",
                        icon = "⏱️",
                        color = TechBlue60
                    )
                    MetricColumn(
                        value = "%.2f".format(sesion.distanciaKm),
                        label = "km",
                        icon = "📍",
                        color = EnergyOrange60
                    )
                }
            }

            // Barra de progreso hacia la meta
            if (metaDiaria > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                val progreso = (sesion.pasos.toFloat() / metaDiaria.toFloat()).coerceIn(0f, 1f)

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso hacia meta diaria",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progreso * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = getProgressColor(progreso)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth(),
                        color = getProgressColor(progreso),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(
    value: String,
    label: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyHistoryState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sin historial aún",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia tu primera caminata para ver gráficas y estadísticas detalladas aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Función helper para obtener color según progreso
private fun getProgressColor(progress: Float): androidx.compose.ui.graphics.Color {
    return when {
        progress >= 0.9f -> FitnessGreen40
        progress >= 0.7f -> FitnessGreen60
        progress >= 0.4f -> EnergyOrange60
        else -> HealthCritical
    }
}