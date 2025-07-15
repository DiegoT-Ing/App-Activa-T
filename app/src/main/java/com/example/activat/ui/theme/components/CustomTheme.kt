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

// === COMPONENTES LIMPIOS Y PROFESIONALES ===

@Composable
fun FitnessGradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(FitnessGreen60, Color.White), // Gradiente más sutil
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevación reducida
        shape = RoundedCornerShape(16.dp) // Bordes menos redondeados
    ) {
        Column(
            modifier = Modifier
                .background(
                    // Gradiente mucho más sutil
                    if (colors.size > 1) {
                        Brush.verticalGradient(
                            colors = listOf(
                                colors[0].copy(alpha = 0.95f), // Menos opacidad
                                colors[0].copy(alpha = 0.85f)  // Gradiente más sutil
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(colors[0], colors[0])
                        )
                    }
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
    strokeWidth: androidx.compose.ui.unit.Dp = 8.dp, // Grosor reducido
    progressColor: Color = FitnessGreen60,
    backgroundColor: Color = NeutralGray90,
    animationDurationMs: Int = 800 // Animación más rápida
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

        // Contenido central más limpio
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge, // Tipografía menos agresiva
            fontWeight = FontWeight.SemiBold, // Menos bold
            color = progressColor.copy(alpha = 0.9f) // Menos saturado
        )
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
            containerColor = MaterialTheme.colorScheme.surface // Fondo blanco limpio
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Elevación mínima
        shape = RoundedCornerShape(12.dp), // Bordes más suaves
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.2f) // Borde sutil del color
        ),
        onClick = onClick ?: {},
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono sin fondo circular
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold, // Menos bold
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
    // ELIMINAMOS la animación de pulsación constante - ahora es un botón limpio
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = RoundedCornerShape(12.dp), // Bordes más suaves
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp, // Elevación mínima
            pressedElevation = 4.dp
        ),
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
        "excelente" -> HealthMetrics.IMC_Normal to "●"  // Círculo simple en lugar de emoji
        "bueno" -> HealthMetrics.Steps_Good to "●"
        "regular" -> HealthMetrics.Steps_Fair to "●"
        "bajo" -> HealthMetrics.Steps_Poor to "●"
        else -> NeutralGray60 to "●"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )

        Column {
            Text(
                text = status,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold, // Menos bold
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
            // Animación más rápida y sutil
            for (i in currentValue..targetValue) {
                currentValue = i
                kotlinx.coroutines.delay(30) // Más rápido
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
        fontWeight = FontWeight.SemiBold // Menos bold
    )
}

@Composable
fun FloatingMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp), // Bordes más suaves
        shadowElevation = 2.dp, // Elevación mínima
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// === NUEVA FUNCIÓN: Card Limpia y Simple ===
@Composable
fun CleanCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = borderColor?.let {
            androidx.compose.foundation.BorderStroke(1.dp, it.copy(alpha = 0.3f))
        },
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// === NUEVA FUNCIÓN: Botón de Acción Principal ===
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = FitnessGreen60,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// === NUEVA FUNCIÓN: Métrica Compacta ===
@Composable
fun CompactMetric(
    label: String,
    value: String,
    color: Color = FitnessGreen60,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// === EXTENSIONES DE UTILIDAD ACTUALIZADAS ===

fun getProgressColor(percentage: Float): Color {
    return when {
        percentage >= 0.9f -> FitnessGreen40
        percentage >= 0.7f -> FitnessGreen60
        percentage >= 0.4f -> EnergyOrange60
        else -> HealthCritical
    }
}

fun getActivityLevelColor(level: String): Color {
    return when (level.lowercase()) {
        "muy activo" -> FitnessGreen40
        "activo" -> FitnessGreen60
        "moderado" -> EnergyOrange60
        "ligero" -> EnergyOrange80
        "sedentario" -> NeutralGray60
        else -> NeutralGray60
    }
}

/**
 * Extensión para aplicar opacidad de forma más legible
 */
fun Color.withAlpha(alpha: Float): Color {
    return this.copy(alpha = alpha)
}