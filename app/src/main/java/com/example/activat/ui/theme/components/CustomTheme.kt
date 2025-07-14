package com.example.activat.ui.theme.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.*

// === COMPONENTES CON IDENTIDAD FITNESS ===

@Composable
fun FitnessGradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = FitnessGradients.PrimaryGradient,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = colors,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                    )
                )
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 12.dp,
    progressColor: Color = FitnessGreen60,
    backgroundColor: Color = NeutralGray90,
    animationDurationMs: Int = 1000
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = animationDurationMs,
            easing = EaseOutCubic
        ),
        label = "progress_ring"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Círculo de fondo
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(size),
            color = backgroundColor,
            strokeWidth = strokeWidth,
            trackColor = Color.Transparent,
        )

        // Círculo de progreso
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(size),
            color = progressColor,
            strokeWidth = strokeWidth,
            trackColor = Color.Transparent,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Contenido central
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
        }
    }
}

@Composable
fun FitnessMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    color: Color = FitnessGreen60,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick ?: {},
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono con fondo circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
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
fun PulsingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = FitnessGreen60
    ),
    content: @Composable RowScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        enabled = enabled,
        colors = colors,
        shape = RoundedCornerShape(32.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        content = content
    )
}

@Composable
fun HealthStatusIndicator(
    status: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (status.lowercase()) {
        "excelente" -> HealthMetrics.IMC_Normal to "🟢"
        "bueno" -> HealthMetrics.Steps_Good to "🟡"
        "regular" -> HealthMetrics.Steps_Fair to "🟠"
        "bajo" -> HealthMetrics.Steps_Poor to "🔴"
        else -> NeutralGray60 to "⚫"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )

        Column {
            Text(
                text = status,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineLarge,
    color: Color = MaterialTheme.colorScheme.primary,
    suffix: String = ""
) {
    var currentValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        if (targetValue > currentValue) {
            for (i in currentValue..targetValue) {
                currentValue = i
                kotlinx.coroutines.delay(50)
            }
        } else {
            currentValue = targetValue
        }
    }

    Text(
        text = "$currentValue$suffix",
        modifier = modifier,
        style = textStyle,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun FloatingMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// === EXTENSIONES DE UTILIDAD ===

fun getProgressColor(percentage: Float): Color {
    return when {
        percentage >= 0.9f -> HealthMetrics.Steps_Excellent
        percentage >= 0.7f -> HealthMetrics.Steps_Good
        percentage >= 0.4f -> HealthMetrics.Steps_Fair
        else -> HealthMetrics.Steps_Poor
    }
}

fun getActivityLevelColor(level: String): Color {
    return when (level.lowercase()) {
        "muy activo" -> ActivityColors.VeryActive
        "activo" -> ActivityColors.Active
        "moderado" -> ActivityColors.Moderate
        "ligero" -> ActivityColors.Light
        "sedentario" -> ActivityColors.Sedentary
        else -> NeutralGray60
    }
}