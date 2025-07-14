package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.pow

@Composable
fun SaludScreen(viewModel: ActivaTViewModel) {
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()
    val configuracionSalud by viewModel.configuracionSalud.collectAsStateWithLifecycle()

    // Estados locales sincronizados con el ViewModel
    var actividadFisica by remember { mutableStateOf("Sedentario") }
    var objetivoSalud by remember { mutableStateOf("Mantener peso") }

    // Sincronizar con datos del ViewModel
    LaunchedEffect(configuracionSalud) {
        actividadFisica = configuracionSalud.actividadFisica
        objetivoSalud = configuracionSalud.objetivoSalud
    }

    // Guardar cambios automáticamente
    LaunchedEffect(actividadFisica, objetivoSalud) {
        if (actividadFisica != configuracionSalud.actividadFisica ||
            objetivoSalud != configuracionSalud.objetivoSalud) {
            viewModel.actualizarConfiguracionSalud(actividadFisica, objetivoSalud)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Centro de Salud",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // IMC Calculator
        item {
            IMCCard(
                estatura = usuarioData.estatura,
                peso = usuarioData.peso
            )
        }

        // Metabolismo Basal
        item {
            MetabolismoBasalCard(
                edad = usuarioData.edad,
                estatura = usuarioData.estatura,
                peso = usuarioData.peso,
                actividadFisica = actividadFisica,
                onActividadChange = { actividadFisica = it }
            )
        }

        // Agua Diaria
        item {
            AguaDiariaCard(peso = usuarioData.peso)
        }

        // Objetivos de Salud
        item {
            ObjetivosSaludCard(
                objetivoSalud = objetivoSalud,
                onObjetivoChange = { objetivoSalud = it },
                pesoActual = usuarioData.peso
            )
        }
    }
}

@Composable
private fun IMCCard(estatura: Float, peso: Float) {
    val imc = if (estatura > 0 && peso > 0) {
        peso / (estatura / 100).pow(2)
    } else 0f

    val categoria = when {
        imc < 18.5 -> "Bajo peso" to MaterialTheme.colorScheme.primary
        imc < 25 -> "Normal" to MaterialTheme.colorScheme.tertiary
        imc < 30 -> "Sobrepeso" to MaterialTheme.colorScheme.secondary
        else -> "Obesidad" to MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonitorWeight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Índice de Masa Corporal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (imc > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "%.1f".format(imc),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = categoria.second
                        )
                        Text(
                            text = "IMC",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = categoria.first,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = categoria.second
                        )
                        Text(
                            text = "Categoría",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "⚠️ Completa tu peso y estatura en Usuario",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetabolismoBasalCard(
    edad: Int,
    estatura: Float,
    peso: Float,
    actividadFisica: String,
    onActividadChange: (String) -> Unit
) {
    // Fórmula Harris-Benedict aproximada (hombre por defecto)
    val tmb = if (edad > 0 && estatura > 0 && peso > 0) {
        88.362 + (13.397 * peso) + (4.799 * estatura) - (5.677 * edad)
    } else 0.0

    val factorActividad = when (actividadFisica) {
        "Sedentario" -> 1.2
        "Ligero" -> 1.375
        "Moderado" -> 1.55
        "Activo" -> 1.725
        "Muy Activo" -> 1.9
        else -> 1.2
    }

    val caloriasTotal = tmb * factorActividad

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Metabolismo Basal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tmb > 0) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${tmb.toInt()}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "TMB (kcal/día)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${caloriasTotal.toInt()}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Total diario",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Nivel de actividad:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val opciones = listOf("Sedentario", "Ligero", "Moderado", "Activo", "Muy Activo")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        opciones.take(3).forEach { opcion ->
                            FilterChip(
                                onClick = { onActividadChange(opcion) },
                                label = { Text(opcion, style = MaterialTheme.typography.labelSmall) },
                                selected = actividadFisica == opcion,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "⚠️ Completa tus datos en Usuario",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun AguaDiariaCard(peso: Float) {
    // Recomendación: 35ml por kg de peso corporal
    val aguaRecomendada = if (peso > 0) (peso * 35) / 1000 else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Water,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Hidratación Diaria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (aguaRecomendada > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "%.1f L".format(aguaRecomendada),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Agua diaria",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${(aguaRecomendada * 4).toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Vasos aprox.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            } else {
                Text(
                    text = "⚠️ Ingresa tu peso en Usuario",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun ObjetivosSaludCard(
    objetivoSalud: String,
    onObjetivoChange: (String) -> Unit,
    pesoActual: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Objetivos de Salud",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val objetivos = listOf("Perder peso", "Mantener peso", "Ganar masa")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                objetivos.forEach { objetivo ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = objetivoSalud == objetivo,
                            onClick = { onObjetivoChange(objetivo) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = objetivo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            if (pesoActual > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💡 ${getRecomendacion(objetivoSalud)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

private fun getRecomendacion(objetivo: String): String {
    return when (objetivo) {
        "Perder peso" -> "Déficit calórico de 300-500 kcal + ejercicio regular"
        "Mantener peso" -> "Mantén tu nivel actual de actividad y alimentación"
        "Ganar masa" -> "Superávit calórico + entrenamiento de fuerza"
        else -> "Consulta con un profesional de la salud"
    }
}