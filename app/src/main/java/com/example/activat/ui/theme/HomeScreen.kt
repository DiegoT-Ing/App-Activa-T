package com.example.activat.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.activat.ui.theme.components.*
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
    val porcentajeMetaAlcanzado by viewModel.porcentajeMetaAlcanzado.collectAsStateWithLifecycle()

    // Estados para animaciones (más sutiles)
    var isVisible by remember { mutableStateOf(false) }

    // Feedback háptico
    val haptic = rememberHapticFeedback()

    // Animaciones de entrada más suaves
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fondo limpio blanco
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado más compacto
    ) {

        // Header limpio sin gradiente de fondo
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400) // Animación más rápida
            ) + fadeIn(animationSpec = tween(400))
        ) {
            CleanWelcomeHeader()
        }

        // Card de Progreso Principal - MUCHO más limpia
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 100))
        ) {
            CleanCard(
                backgroundColor = MaterialTheme.colorScheme.surface,
                borderColor = FitnessGreen60
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Progreso del Día",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row { // <-- ¡Modificado! Ya NO lleva 'verticalAlignment = Alignment.Baseline'
                            AnimatedCounter(
                                targetValue = pasosTotalesDelDia,
                                textStyle = MaterialTheme.typography.headlineMedium,
                                color = FitnessGreen60,
                                modifier = Modifier.alignByBaseline() // <-- ¡Añadido!
                            )
                            Text(
                                text = " pasos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Meta: ${usuarioData.metaPasosDiarios} pasos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    ProgressRing(
                        progress = porcentajeMetaAlcanzado,
                        size = 80.dp, // Más pequeño
                        strokeWidth = 6.dp,
                        progressColor = FitnessGreen60,
                        backgroundColor = NeutralGray90
                    )
                }
            }
        }

        // Botón Principal limpio sin pulsación
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(600, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
        ) {
            PrimaryActionButton(
                text = "Iniciar Caminata",
                onClick = {
                    haptic.start()
                    viewModel.iniciarCaminata()
                    navController.navigate("caminata?autostart=true")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // Métricas rápidas - Layout más limpio
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(700, delayMillis = 300)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Hoy
                CleanCard(
                    modifier = Modifier.weight(1f),
                    borderColor = FitnessGreen60,
                    onClick = { haptic.light() }
                ) {
                    CompactMetric(
                        label = "Hoy",
                        value = "$pasosTotalesDelDia",
                        color = FitnessGreen60
                    )
                }

                // Card Meta
                CleanCard(
                    modifier = Modifier.weight(1f),
                    borderColor = TechBlue60,
                    onClick = { haptic.light() }
                ) {
                    CompactMetric(
                        label = "Meta",
                        value = "${usuarioData.metaPasosDiarios}",
                        color = TechBlue60
                    )
                }

                // Card Restante
                CleanCard(
                    modifier = Modifier.weight(1f),
                    borderColor = EnergyOrange60,
                    onClick = { haptic.light() }
                ) {
                    CompactMetric(
                        label = "Restante",
                        value = "${(usuarioData.metaPasosDiarios - pasosTotalesDelDia).coerceAtLeast(0)}",
                        color = EnergyOrange60
                    )
                }
            }
        }

        // Última Sesión Card - Diseño mucho más limpio
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800, delayMillis = 400)
            ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
        ) {
            CleanCard(
                onClick = { haptic.light() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Última Actividad",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (ultimaSesion != null) {
                        Surface(
                            color = FitnessGreen60.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "✓",
                                modifier = Modifier.padding(6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = FitnessGreen60
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (ultimaSesion != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CleanCard(
                            modifier = Modifier.weight(1f),
                            backgroundColor = FitnessGreen60.copy(alpha = 0.05f),
                            borderColor = FitnessGreen60
                        ) {
                            CompactMetric(
                                label = "Pasos",
                                value = "${ultimaSesion!!.pasos}",
                                color = FitnessGreen60
                            )
                        }

                        CleanCard(
                            modifier = Modifier.weight(1f),
                            backgroundColor = TechBlue60.copy(alpha = 0.05f),
                            borderColor = TechBlue60
                        ) {
                            CompactMetric(
                                label = "Tiempo",
                                value = ultimaSesion!!.tiempoFormateado(),
                                color = TechBlue60
                            )
                        }

                        CleanCard(
                            modifier = Modifier.weight(1f),
                            backgroundColor = EnergyOrange60.copy(alpha = 0.05f),
                            borderColor = EnergyOrange60
                        ) {
                            CompactMetric(
                                label = "Distancia",
                                value = "%.1f km".format(ultimaSesion!!.distanciaKm),
                                color = EnergyOrange60
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌟",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Tu primera aventura te espera!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = FitnessGreen60
                        )
                        Text(
                            text = "Cada paso cuenta hacia una vida más saludable",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Tip motivacional más sutil
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(900, delayMillis = 500)
            ) + fadeIn(animationSpec = tween(900, delayMillis = 500))
        ) {
            CleanMotivationalTip()
        }
    }
}

@Composable
private fun CleanWelcomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "¡Hola! 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "¿Listo para activarte?",
                style = MaterialTheme.typography.titleMedium,
                color = FitnessGreen60,
                fontWeight = FontWeight.Medium
            )
        }

        // Icono simple sin animaciones
        Surface(
            color = FitnessGreen60.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "🏃‍♂️",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun CleanMotivationalTip() {
    val tips = listOf(
        "💡 Caminar 30 min al día reduce el riesgo cardíaco en 35%",
        "🌟 Cada paso es una victoria hacia tu mejor versión",
        "⚡ La constancia es más importante que la intensidad",
        "🎯 Pequeños cambios generan grandes resultados",
        "🏃‍♂️ Tu cuerpo es tu templo, manténlo activo"
    )

    val randomTip = remember { tips.random() }

    CleanCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        borderColor = MotivationPurple60
    ) {
        Text(
            text = randomTip,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}