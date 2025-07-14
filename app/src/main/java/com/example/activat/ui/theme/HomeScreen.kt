package com.example.activat.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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

    // Estados para animaciones
    var isVisible by remember { mutableStateOf(false) }

    // Feedback háptico
    val haptic = rememberHapticFeedback()

    // Animaciones de entrada
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FitnessGreen80.copy(alpha = 0.1f),
                        TechBlue80.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Header con saludo y logo animado
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(600, delayMillis = 100))
        ) {
            FitnessWelcomeHeader()
        }

        // Card de Progreso Principal con anillo animado
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(700, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
        ) {
            FitnessGradientCard(
                colors = FitnessGradients.PrimaryGradient
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
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AnimatedCounter(
                            targetValue = pasosTotalesDelDia,
                            textStyle = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            suffix = " pasos"
                        )
                        Text(
                            text = "Meta: ${usuarioData.metaPasosDiarios} pasos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    ProgressRing(
                        progress = porcentajeMetaAlcanzado,
                        size = 100.dp,
                        strokeWidth = 8.dp,
                        progressColor = Color.White,
                        backgroundColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // Botón Principal con pulsación y gradiente
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800, delayMillis = 300)
            ) + fadeIn(animationSpec = tween(800, delayMillis = 300))
        ) {
            PulsingButton(
                onClick = {
                    haptic.start()
                    viewModel.iniciarCaminata()
                    navController.navigate("caminata?autostart=true")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FitnessGreen60
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Iniciar Caminata",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Métricas rápidas con nuevos componentes
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(900, delayMillis = 400)
            ) + fadeIn(animationSpec = tween(900, delayMillis = 400))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FitnessMetricCard(
                    title = "Hoy",
                    value = "$pasosTotalesDelDia",
                    subtitle = "pasos",
                    icon = "👣",
                    color = FitnessGreen60,
                    modifier = Modifier.weight(1f),
                    onClick = { haptic.light() }
                )

                FitnessMetricCard(
                    title = "Meta",
                    value = "${usuarioData.metaPasosDiarios}",
                    subtitle = "objetivo",
                    icon = "🎯",
                    color = TechBlue60,
                    modifier = Modifier.weight(1f),
                    onClick = { haptic.light() }
                )

                FitnessMetricCard(
                    title = "Restante",
                    value = "${(usuarioData.metaPasosDiarios - pasosTotalesDelDia).coerceAtLeast(0)}",
                    subtitle = "pasos",
                    icon = "⚡",
                    color = EnergyOrange60,
                    modifier = Modifier.weight(1f),
                    onClick = { haptic.light() }
                )
            }
        }

        // Última Sesión Card mejorada
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1000, delayMillis = 500)
            ) + fadeIn(animationSpec = tween(1000, delayMillis = 500))
        ) {
            EnhancedLastSessionCard(ultimaSesion = ultimaSesion, haptic = haptic)
        }

        // Motivational Quote o Tip del día
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1100, delayMillis = 600)
            ) + fadeIn(animationSpec = tween(1100, delayMillis = 600))
        ) {
            MotivationalTipCard()
        }
    }
}

@Composable
private fun FitnessWelcomeHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

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

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FitnessGreen60.copy(alpha = pulseAlpha),
                            TechBlue60.copy(alpha = pulseAlpha * 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🏃‍♂️",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
private fun EnhancedLastSessionCard(
    ultimaSesion: com.example.activat.data.SesionCaminata?,
    haptic: HapticFeedbackHelper
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = { haptic.light() }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Última Actividad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (ultimaSesion != null) {
                    Surface(
                        color = FitnessGreen60.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "✓",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = FitnessGreen60,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ultimaSesion != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingMetric(
                        label = "Pasos",
                        value = "${ultimaSesion.pasos}",
                        modifier = Modifier.weight(1f),
                        backgroundColor = FitnessGreen60.copy(alpha = 0.1f),
                        contentColor = FitnessGreen60
                    )

                    FloatingMetric(
                        label = "Tiempo",
                        value = ultimaSesion.tiempoFormateado(),
                        modifier = Modifier.weight(1f),
                        backgroundColor = TechBlue60.copy(alpha = 0.1f),
                        contentColor = TechBlue60
                    )

                    FloatingMetric(
                        label = "Distancia",
                        value = "%.1f km".format(ultimaSesion.distanciaKm),
                        modifier = Modifier.weight(1f),
                        backgroundColor = EnergyOrange60.copy(alpha = 0.1f),
                        contentColor = EnergyOrange60
                    )
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
                        fontWeight = FontWeight.Bold,
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
}

@Composable
private fun MotivationalTipCard() {
    val tips = listOf(
        "💡 Caminar 30 min al día reduce el riesgo cardíaco en 35%",
        "🌟 Cada paso es una victoria hacia tu mejor versión",
        "⚡ La constancia es más importante que la intensidad",
        "🎯 Pequeños cambios generan grandes resultados",
        "🏃‍♂️ Tu cuerpo es tu templo, manténlo activo"
    )

    val randomTip = remember { tips.random() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MotivationPurple60.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = randomTip,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MotivationPurple60,
            fontWeight = FontWeight.Medium
        )
    }
}