package com.example.activat.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.components.FitnessGradientCard
import com.example.activat.ui.theme.components.AnimatedCounter
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
    var isVisible by remember { mutableStateOf(false) }

    // Animación de entrada
    LaunchedEffect(Unit) {
        isVisible = true
    }

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
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Text(
                    text = "💊 Centro de Salud",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitnessGreen60
                )
            }
        }

        // IMC Calculator con nueva identidad
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 100)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 100))
            ) {
                IMCCard(
                    estatura = usuarioData.estatura,
                    peso = usuarioData.peso
                )
            }
        }

        // Metabolismo Basal
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
            ) {
                MetabolismoBasalCard(
                    edad = usuarioData.edad,
                    estatura = usuarioData.estatura,
                    peso = usuarioData.peso,
                    actividadFisica = actividadFisica,
                    onActividadChange = { actividadFisica = it }
                )
            }
        }

        // Agua Diaria
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(900, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(900, delayMillis = 300))
            ) {
                AguaDiariaCard(peso = usuarioData.peso)
            }
        }

        // Objetivos de Salud
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1000, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 400))
            ) {
                ObjetivosSaludCard(
                    objetivoSalud = objetivoSalud,
                    onObjetivoChange = { objetivoSalud = it },
                    pesoActual = usuarioData.peso
                )
            }
        }
    }
}

@Composable
private fun IMCCard(estatura: Float, peso: Float) {
    val imc = if (estatura > 0 && peso > 0) {
        peso / (estatura / 100).pow(2)
    } else 0f

    val (categoria, color) = when {
        imc < 18.5 -> "Bajo peso" to TechBlue60
        imc < 25 -> "Normal" to FitnessGreen60
        imc < 30 -> "Sobrepeso" to EnergyOrange60
        else -> "Obesidad" to HealthCritical
    }

    FitnessGradientCard(
        colors = listOf(color.copy(alpha = 0.8f), color)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "⚖️ Índice de Masa Corporal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            if (imc > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    AnimatedCounter(
                        targetValue = imc.toInt(),
                        textStyle = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        suffix = ".${((imc % 1) * 10).toInt()}"
                    )
                    Text(
                        text = "IMC",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            } else {
                Text(
                    text = "⚠️ Completa tu peso y estatura",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
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

    FitnessGradientCard(
        colors = FitnessGradients.EnergyGradient
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Metabolismo Basal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tmb > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        AnimatedCounter(
                            targetValue = tmb.toInt(),
                            textStyle = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                        Text(
                            text = "TMB (kcal/día)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        AnimatedCounter(
                            targetValue = caloriasTotal.toInt(),
                            textStyle = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Total diario",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nivel de actividad:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                val opciones = listOf("Sedentario", "Ligero", "Moderado", "Activo", "Muy Activo")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    opciones.chunked(3).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chunk.forEach { opcion ->
                                FilterChip(
                                    onClick = { onActividadChange(opcion) },
                                    label = {
                                        Text(
                                            opcion,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    selected = actividadFisica == opcion,
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color.White.copy(alpha = 0.2f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "⚠️ Completa tus datos en Usuario",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
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
            containerColor = TechBlue60.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💧",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Hidratación Diaria",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TechBlue60
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (aguaRecomendada > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Este es el bloque que debes modificar
                        Row { // <-- Quita 'verticalAlignment = Alignment.Baseline' de aquí
                            AnimatedCounter(
                                targetValue = aguaRecomendada.toInt(),
                                textStyle = MaterialTheme.typography.displayMedium,
                                color = TechBlue60,
                                modifier = Modifier.alignByBaseline() // <-- Añade este modificador
                            )
                            Text(
                                text = ".${((aguaRecomendada % 1) * 10).toInt()} L",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TechBlue60,
                                modifier = Modifier.alignByBaseline() // <-- Añade este modificador
                            )
                        }
                        Text(
                            text = "Agua diaria recomendada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        AnimatedCounter(
                            targetValue = (aguaRecomendada * 4).toInt(),
                            textStyle = MaterialTheme.typography.displaySmall,
                            color = TechBlue60
                        )
                        Text(
                            text = "Vasos aprox.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "⚠️ Ingresa tu peso en Usuario",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            containerColor = MotivationPurple60.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Objetivos de Salud",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MotivationPurple60
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val objetivos = listOf(
                "Perder peso" to "🔽",
                "Mantener peso" to "⚖️",
                "Ganar masa" to "🔼"
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                objetivos.forEach { (objetivo, emoji) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (objetivoSalud == objetivo) {
                                MotivationPurple60.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        onClick = { onObjetivoChange(objetivo) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = objetivoSalud == objetivo,
                                onClick = { onObjetivoChange(objetivo) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MotivationPurple60
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = emoji,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = objetivo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (objetivoSalud == objetivo) FontWeight.Bold else FontWeight.Normal,
                                color = if (objetivoSalud == objetivo) MotivationPurple60 else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (pesoActual > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MotivationPurple60.copy(alpha = 0.05f)
                    )
                ) {
                    Text(
                        text = "💡 ${getRecomendacion(objetivoSalud)}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MotivationPurple60,
                        fontWeight = FontWeight.Medium
                    )
                }
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