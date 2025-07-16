package com.example.activat.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.activat.ui.theme.components.CleanCard
import com.example.activat.ui.theme.components.CompactMetric
import com.example.activat.ui.theme.components.PrimaryActionButton
import com.example.activat.ui.theme.components.ProgressRing
import com.example.activat.ui.theme.components.rememberHapticFeedback
import com.example.activat.viewmodel.ActivaTViewModel

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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                borderColor = FitnessGreen60,
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Progreso del día",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        ProgressRing(
                            progress = porcentajeMetaAlcanzado,
                            size = 100.dp,
                            strokeWidth = 6.dp,
                            progressColor = MaterialTheme.colorScheme.onSurface,
                            backgroundColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        }

        // Botón Principal
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(600, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
        ) {
            PrimaryActionButton(
                text = "Iniciar caminata",
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
                    borderColor = EnergyOrange60,
                    onClick = { haptic.light() },
                    content = {
                        CompactMetric(
                            label = "Hoy",
                            value = "$pasosTotalesDelDia",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )

                // Card Meta
                CleanCard(
                    modifier = Modifier.weight(1f),
                    borderColor = EnergyOrange60,
                    onClick = { haptic.light() },
                    content = {
                        CompactMetric(
                            label = "Meta",
                            value = "${usuarioData.metaPasosDiarios}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )

                // Card Restante
                CleanCard(
                    modifier = Modifier.weight(1f),
                    borderColor = EnergyOrange60,
                    onClick = { haptic.light() },
                    content = {
                        CompactMetric(
                            label = "Restante",
                            value = "${(usuarioData.metaPasosDiarios - pasosTotalesDelDia).coerceAtLeast(0)}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
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
                onClick = { haptic.light() },
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Última actividad",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (ultimaSesion != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CleanCard(
                                modifier = Modifier.weight(1f),
                                borderColor = TechBlue60
                            ) {
                                CompactMetric(
                                    label = "Pasos",
                                    value = "${ultimaSesion!!.pasos}",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            CleanCard(
                                modifier = Modifier.weight(1f),
                                borderColor = TechBlue60
                            ) {
                                CompactMetric(
                                    label = "Tiempo",
                                    value = ultimaSesion!!.tiempoFormateado(),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            CleanCard(
                                modifier = Modifier.weight(1f),
                                borderColor = TechBlue60
                            ) {
                                CompactMetric(
                                    label = "Distancia",
                                    value = "%.1f km".format(ultimaSesion!!.distanciaKm),
                                    color = MaterialTheme.colorScheme.onSurface
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
            )
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Activa - T",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
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
        borderColor = MotivationPurple60,
        content = {
            Text(
                text = randomTip,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    )
}