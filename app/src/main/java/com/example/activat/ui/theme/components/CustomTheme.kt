package com.example.activat.ui.theme.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.*

// === COMPONENTES LIMPIOS Y PROFESIONALES ===

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
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineLarge,
    color: Color = MaterialTheme.colorScheme.primary,
    suffix: String = ""
) {
    var currentValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        currentValue = targetValue // Valor inmediato, sin animación
    }

    Text(
        text = "$currentValue$suffix",
        modifier = modifier,
        style = textStyle,
        color = color,
        fontWeight = FontWeight.SemiBold // Menos bold
    )
}

// === NUEVA FUNCIÓN: Card Limpia y Simple ===
@Composable
fun CleanCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    backgroundColor: Color = borderColor?.copy(alpha = 0.05f) ?: MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor // Este usará el color calculado arriba
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Mantenemos 0.dp para no tener sombras
        shape = RoundedCornerShape(12.dp),
        border = borderColor?.let {
            // Mantenemos el borde sólido y definido como lo dejamos
            androidx.compose.foundation.BorderStroke(1.dp, it)
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
        // Añade .fillMaxWidth() aquí para que la columna ocupe
        modifier = modifier.fillMaxWidth(), // <-- ¡MODIFICACIÓN AQUÍ!
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
