package com.example.activat.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.components.*
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

    // Animación de entrada más rápida
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "CENTRO DE SALUD",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Conoce tu estado de salud actual",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // IMC Card - Diseño limpio
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500, delayMillis = 100)
                ) + fadeIn(animationSpec = tween(500, delayMillis = 100))
            ) {
                CleanIMCCard(
                    estatura = usuarioData.estatura,
                    peso = usuarioData.peso
                )
            }
        }

        // Metabolismo Basal - Diseño limpio
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
            ) {
                CleanMetabolismoCard(
                    edad = usuarioData.edad,
                    estatura = usuarioData.estatura,
                    peso = usuarioData.peso,
                    actividadFisica = actividadFisica,
                    onActividadChange = { actividadFisica = it }
                )
            }
        }

        // Hidratación - Diseño limpio
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
            ) {
                CleanHidratacionCard(peso = usuarioData.peso)
            }
        }

        // Objetivos de Salud - Diseño limpio
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                CleanObjetivosCard(
                    objetivoSalud = objetivoSalud,
                    onObjetivoChange = { objetivoSalud = it },
                    pesoActual = usuarioData.peso
                )
            }
        }
    }
}

@Composable
private fun CleanIMCCard(estatura: Float, peso: Float) {
    val imc = if (estatura > 0 && peso > 0) {
        peso / (estatura / 100).pow(2)
    } else 0f

    val (categoria, color) = when {
        imc < 18.5 -> "Bajo peso" to TechBlue60
        imc < 25 -> "Normal" to FitnessGreen60
        imc < 30 -> "Sobrepeso" to EnergyOrange60
        else -> "Obesidad" to HealthCritical
    }

    CleanCard(
        borderColor = FitnessGreen60,
        modifier = Modifier.fillMaxWidth(),
        content = {
            Column { // Contenedor principal de la tarjeta, apilando elementos verticalmente
                Row( // Contiene el título/categoría y el valor del IMC (si está disponible)
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) { // Columna para el título y categoría
                        Text(
                            text = "Indice de masa corporal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // La categoría se muestra siempre, incluso si el IMC es 0 (ej: "Bajo peso" con IMC=0)
                        Text(
                            text = categoria,
                            style = MaterialTheme.typography.titleMedium,
                            color = FitnessGreen60,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (imc > 0) { // Muestra el valor del IMC si es calculable
                        Column(horizontalAlignment = Alignment.End) {
                            Row {
                                AnimatedCounter(
                                    targetValue = imc.toInt(),
                                    textStyle = MaterialTheme.typography.displaySmall,
                                    color = color,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Text(
                                    text = ".${((imc % 1) * 10).toInt()}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                    modifier = Modifier.alignByBaseline()
                                )
                            }
                            Text(
                                text = "IMC",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (imc <= 0) { // Muestra la indicación si el IMC no es calculable
                    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el título y la indicación
                    Text(
                        text = "Completa tu peso y estatura para calcular tu IMC",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
private fun CleanMetabolismoCard(
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

    CleanCard(
        borderColor = EnergyOrange60,
        modifier = Modifier.fillMaxWidth(), // ¡Ajuste clave aquí!
        content = {
            Column {
                Text(
                    text = "Metabolismo basal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (tmb > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround // Distribuye el espacio entre ellas
                    ) {
                        Column(modifier = Modifier.weight(1f)) { // Ocupa la mitad del espacio
                            AnimatedCounter(
                                targetValue = tmb.toInt(),
                                textStyle = MaterialTheme.typography.headlineLarge,
                                color = EnergyOrange60
                            )
                            Text(
                                text = "TMB (kcal/día)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f), // Ocupa la otra mitad del espacio
                            horizontalAlignment = Alignment.End
                        ) {
                            AnimatedCounter(
                                targetValue = caloriasTotal.toInt(),
                                textStyle = MaterialTheme.typography.headlineLarge,
                                color = EnergyOrange60
                            )
                            Text(
                                text = "Total diario",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Nivel de actividad:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        selected = actividadFisica == opcion,
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EnergyOrange60.copy(alpha = 0.15f),
                                            selectedLabelColor = EnergyOrange60
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Completa tus datos en la sección Usuario",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
private fun CleanHidratacionCard(peso: Float) {
    // Recomendación: 35ml por kg de peso corporal
    val aguaRecomendada = if (peso > 0) (peso * 35) / 1000 else 0f

    CleanCard(
        borderColor = TechBlue60,
        modifier = Modifier.fillMaxWidth(), // ¡Ajuste clave aquí!
        content = {
            Column {
                Text(
                    text = "Hidratación diaria",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (aguaRecomendada > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) { // Se expande para ocupar el espacio
                            Row {
                                AnimatedCounter(
                                    targetValue = aguaRecomendada.toInt(),
                                    textStyle = MaterialTheme.typography.displayMedium,
                                    color = TechBlue60,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Text(
                                    text = ".${((aguaRecomendada % 1) * 10).toInt()} L",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TechBlue60,
                                    modifier = Modifier.alignByBaseline()
                                )
                            }
                            Text(
                                text = "Agua diaria recomendada",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) { // Se ajusta a su contenido, sin weight
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
                        text = "Ingresa tu peso en la sección Usuario",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
private fun CleanObjetivosCard(
    objetivoSalud: String,
    onObjetivoChange: (String) -> Unit,
    pesoActual: Float
) {
    CleanCard(
        borderColor = MotivationPurple60,
        content = {
            Column {
                Text(
                    text = "Objetivos de salud",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                val objetivos = listOf(
                    "Perder peso" to "\uD83D\uDD25",
                    "Mantener peso" to "\uD83D\uDC96",
                    "Ganar masa" to "\uD83D\uDCAA"
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    objetivos.forEach { (objetivo, emoji) ->
                        CleanCard(
                            backgroundColor = if (objetivoSalud == objetivo) {
                                MotivationPurple60.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            borderColor = if (objetivoSalud == objetivo) {
                                MotivationPurple60
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            },
                            onClick = { onObjetivoChange(objetivo) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                    CleanCard(
                        backgroundColor = MotivationPurple60.copy(alpha = 0.05f),
                        borderColor = MotivationPurple60.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "💡 ${getRecomendacion(objetivoSalud)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MotivationPurple60,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    )
}

private fun getRecomendacion(objetivo: String): String {
    return when (objetivo) {
        "Perder peso" -> "Déficit calórico de 300-500 kcal + ejercicio regular"
        "Mantener peso" -> "Mantén tu nivel actual de actividad y alimentación"
        "Ganar masa" -> "Superávit calórico + entrenamiento de fuerza"
        else -> "Consulta con un profesional de la salud"
    }
}