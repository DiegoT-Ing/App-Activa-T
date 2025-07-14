package com.example.activat.ui.theme.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.*
import kotlinx.coroutines.delay

// === COMPONENTES DE CARGA PRINCIPALES ===

@Composable
fun PulsingDots(
    modifier: Modifier = Modifier,
    color: Color = FitnessGreen60,
    dotCount: Int = 3,
    animationDuration: Int = 1200
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_animation")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        easing = EaseInOutSine,
                        delayMillis = index * 200
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_scale_$index"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun WaveLoader(
    modifier: Modifier = Modifier,
    color: Color = TechBlue60,
    waveCount: Int = 4
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_animation")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(waveCount) { index ->
            val height by infiniteTransition.animateFloat(
                initialValue = 20f,
                targetValue = 40f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800,
                        easing = EaseInOutSine,
                        delayMillis = index * 100
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_height_$index"
            )

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun SpinnerLoader(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 4.dp,
    color: Color = FitnessGreen60
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner_animation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_rotation"
    )

    CircularProgressIndicator(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation },
        strokeWidth = strokeWidth,
        color = color,
        trackColor = color.copy(alpha = 0.2f)
    )
}

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    shimmerColor: Color = NeutralGray90,
    highlightColor: Color = Color.White.copy(alpha = 0.8f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_animation")

    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        shimmerColor,
                        highlightColor,
                        shimmerColor
                    ),
                    start = androidx.compose.ui.geometry.Offset(shimmerTranslateAnim - 200f, 0f),
                    end = androidx.compose.ui.geometry.Offset(shimmerTranslateAnim, 100f)
                )
            )
    )
}

// === ESTADOS DE CARGA ESPECÍFICOS ===

@Composable
fun LoadingSessionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpinnerLoader(size = 60.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Iniciando sesión...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            PulsingDots()
        }
    }
}

@Composable
fun LoadingSensorData() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Conectando sensores...",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Detectando movimiento",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        WaveLoader()
    }
}

@Composable
fun LoadingHistoryData() {
    LazyColumn {
        items(3) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeletonLoader(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        SkeletonLoader(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SkeletonLoader(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(12.dp)
                        )
                    }
                }
            }
        }
    }
}

// === TRANSICIONES ENTRE ESTADOS ===

@Composable
fun <T> StatefulContent(
    state: LoadingState<T>,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    errorContent: @Composable (String) -> Unit = { DefaultErrorContent(it, onRetry) },
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "stateful_content"
    ) { currentState ->
        when (currentState) {
            is LoadingState.Loading -> loadingContent()
            is LoadingState.Error -> errorContent(currentState.message)
            is LoadingState.Success -> content(currentState.data)
        }
    }
}

@Composable
private fun DefaultLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SpinnerLoader(size = 64.dp)
            Text(
                text = "Cargando...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DefaultErrorContent(
    message: String,
    onRetry: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

// === CLASES DE ESTADO ===

sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}

// === EXTENSIONES DE UTILIDAD ===

@Composable
fun LazyListScope.loadingItem() {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            PulsingDots()
        }
    }
}