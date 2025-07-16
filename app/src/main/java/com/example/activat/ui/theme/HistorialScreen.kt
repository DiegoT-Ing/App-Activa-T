package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.components.*
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.activat.data.SesionCaminata
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
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado más compacto
    ) {
        item {
            Column {
                Text(
                    text = "HISTORIAL DE ACTIVIDAD",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Revisa tu progreso y tendencias",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Selector de período con gráfica integrada - SIN REDUNDANCIAS
        item {
            CleanCard(
                backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                content = {
                Column {
                    // Header con selector integrado - MÁS ESPACIO PARA BOTONES
                    Column {
                        Text(
                            text = "📈 Tendencia de pasos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Selector con más espacio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            opciones.forEach { opcion ->
                                FilterChip(
                                    onClick = { periodoSeleccionado = opcion },
                                    label = {
                                        Text(
                                            text = opcion,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    selected = (opcion == periodoSeleccionado),
                                    modifier = Modifier.weight(1f), // IGUAL ESPACIO PARA TODOS
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FitnessGreen60.copy(alpha = 0.15f),
                                        selectedLabelColor = FitnessGreen60
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gráfica SIN TÍTULO REPETIDO
                    if (sesionesFiltradas.isNotEmpty()) {
                        StepsChart(
                            sesiones = sesionesFiltradas,
                            periodo = periodoSeleccionado
                        )

                        // SOLO información de sesiones, SIN PERIODO REPETIDO
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${sesionesFiltradas.size} sesión${if(sesionesFiltradas.size != 1) "es" else ""} registrada${if(sesionesFiltradas.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Estado vacío integrado
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📈",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sin datos para mostrar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Inicia sesiones para ver tendencias",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            })
        }

        // Resumen estadístico SOLO si hay datos - SIN REDUNDANCIAS NI REPETICIONES
        if (sesionesFiltradas.isNotEmpty()) {
            item {
                val totalPasos = sesionesFiltradas.sumOf { it.pasos }
                val promedioPasos = totalPasos / sesionesFiltradas.size
                val mayorSesion = sesionesFiltradas.maxByOrNull { it.pasos }

                CleanCard(
                    content = {
                        Column {
                            Text(
                                text = "📊 Resumen",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Solo métricas importantes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CleanStatsCard(
                                    title = "Total",
                                    value = "$totalPasos",
                                    subtitle = "pasos",
                                    modifier = Modifier.weight(1f),
                                    color = FitnessGreen60
                                )
                                CleanStatsCard(
                                    title = "Promedio",
                                    value = "$promedioPasos",
                                    subtitle = "por sesión",
                                    modifier = Modifier.weight(1f),
                                    color = TechBlue60
                                )
                                CleanStatsCard(
                                    title = "Mejor día",
                                    value = "${mayorSesion?.pasos ?: 0}",
                                    subtitle = "pasos máx",
                                    modifier = Modifier.weight(1f),
                                    color = EnergyOrange60
                                )
                            }
                        }
                    }
                )
            }

            // SIEMPRE mostrar las últimas 5 sesiones para que el usuario vea su actividad
            item {
                Text(
                    text = "Sesiones recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(sesionesFiltradas.sortedByDescending { it.fecha }.take(5)) { sesion ->
                CleanSessionCard(
                    sesion = sesion,
                    metaDiaria = usuarioData.metaPasosDiarios
                )
            }

            // Si hay más de 5 sesiones, mostrar indicador
            if (sesionesFiltradas.size > 5) {
                item {
                    CleanCard(
                        backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        content = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "📋 ${sesionesFiltradas.size - 5} sesión${if(sesionesFiltradas.size - 5 != 1) "es" else ""} más registrada${if(sesionesFiltradas.size - 5 != 1) "s" else ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanStatsCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    CleanCard(
        modifier = modifier,
        borderColor = color,
        backgroundColor = color.copy(alpha = 0.05f),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
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
    )
}

@Composable
private fun CleanSessionCard(
    sesion: SesionCaminata,
    metaDiaria: Int,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
) {
    CleanCard(
        backgroundColor = backgroundColor,
        content = {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🏃‍♂️ ${sesion.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = sesion.fecha.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Métricas principales en layout limpio
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CleanMetricColumn(
                        value = "${sesion.pasos}",
                        label = "pasos",
                        icon = "👣",
                        color = FitnessGreen60
                    )
                    CleanMetricColumn(
                        value = sesion.tiempoFormateado(),
                        label = "tiempo",
                        icon = "⏱️",
                        color = TechBlue60
                    )
                    CleanMetricColumn(
                        value = "%.2f".format(sesion.distanciaKm),
                        label = "km",
                        icon = "📍",
                        color = EnergyOrange60
                    )
                }
            }

            // Barra de progreso hacia la meta más limpia
            if (metaDiaria > 0) {
                Spacer(modifier = Modifier.height(16.dp))
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
                            fontWeight = FontWeight.SemiBold,
                            color = getProgressColorClean(progreso)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth(),
                        color = getProgressColorClean(progreso),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    })
}

@Composable
private fun CleanMetricColumn(
    value: String,
    label: String,
    icon: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(2.dp))
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

// Función helper para obtener color según progreso
private fun getProgressColorClean(progress: Float): Color {
    return when {
        progress >= 0.9f -> FitnessGreen40
        progress >= 0.7f -> FitnessGreen60
        progress >= 0.4f -> EnergyOrange60
        else -> HealthCritical
    }
}