package com.example.activat.ui.theme.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activat.data.SesionCaminata
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun StepsChart(
    sesiones: List<SesionCaminata>,
    periodo: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    // Animación de entrada
    LaunchedEffect(sesiones) {
        isVisible = false
        kotlinx.coroutines.delay(200)
        isVisible = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chart_animation"
    )

    // SOLO LA GRÁFICA - SIN TÍTULO NI ESTADÍSTICAS
    if (sesiones.isNotEmpty()) {
        // Preparar datos para la gráfica
        val dataPoints = prepareChartData(sesiones, periodo)
        val maxSteps = dataPoints.maxOfOrNull { it.steps } ?: 1000

        // Canvas para la gráfica - SIN CARD WRAPPER
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            drawChart(
                dataPoints = dataPoints,
                maxSteps = maxSteps,
                animationProgress = animatedProgress,
                primaryColor = Color(0xFF2E7D32), // FitnessGreen60
                backgroundColor = Color(0xFFE0E0E0), // NeutralGray80
                surfaceColor = Color(0xFFFFFFFF) // Blanco
            )
        }
    } else {
        // Estado vacío simple sin card
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📈",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sin datos para graficar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Función para preparar datos de la gráfica
private fun prepareChartData(
    sesiones: List<SesionCaminata>,
    periodo: String
): List<ChartDataPoint> {
    return when (periodo) {
        "Día" -> {
            // Agrupar por hora del día
            sesiones.groupBy { it.fecha.hour }
                .map { (hour, sessions) ->
                    ChartDataPoint(
                        label = "${hour}h",
                        steps = sessions.sumOf { it.pasos },
                        index = hour
                    )
                }
                .sortedBy { it.index }
        }
        "Semana" -> {
            // Agrupar por día de la semana
            sesiones.groupBy { it.fecha.toLocalDate() }
                .map { (date, sessions) ->
                    ChartDataPoint(
                        label = date.format(DateTimeFormatter.ofPattern("dd/MM")),
                        steps = sessions.sumOf { it.pasos },
                        index = date.dayOfYear
                    )
                }
                .sortedBy { it.index }
                .takeLast(7)
        }
        "Mes" -> {
            // Agrupar por semana del mes
            sesiones.groupBy { it.fecha.toLocalDate().dayOfMonth / 7 }
                .map { (week, sessions) ->
                    ChartDataPoint(
                        label = "S${week + 1}",
                        steps = sessions.sumOf { it.pasos },
                        index = week
                    )
                }
                .sortedBy { it.index }
        }
        else -> emptyList()
    }
}

private fun DrawScope.drawChart(
    dataPoints: List<ChartDataPoint>,
    maxSteps: Int,
    animationProgress: Float,
    primaryColor: Color,
    backgroundColor: Color,
    surfaceColor: Color
) {
    if (dataPoints.isEmpty()) return

    val width = size.width
    val height = size.height
    val padding = 40f
    val chartWidth = width - 2 * padding
    val chartHeight = height - 2 * padding

    // Dibujar fondo del gráfico más sutil
    drawRoundRect(
        color = backgroundColor.copy(alpha = 0.05f),
        topLeft = Offset(padding, padding),
        size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
    )

    // Calcular puntos de la línea
    val points = dataPoints.mapIndexed { index, dataPoint ->
        val x = padding + (index.toFloat() / (dataPoints.size - 1).coerceAtLeast(1)) * chartWidth
        val y = padding + chartHeight - (dataPoint.steps.toFloat() / maxSteps) * chartHeight * animationProgress
        Offset(x, y)
    }

    if (points.size > 1) {
        // Dibujar área bajo la curva (gradiente sutil)
        val path = Path().apply {
            moveTo(points.first().x, padding + chartHeight)
            points.forEach { point ->
                lineTo(point.x, point.y)
            }
            lineTo(points.last().x, padding + chartHeight)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.2f),
                    primaryColor.copy(alpha = 0.05f)
                ),
                startY = padding,
                endY = padding + chartHeight
            )
        )

        // Dibujar línea principal
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
        }

        drawPath(
            path = linePath,
            color = primaryColor,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Dibujar puntos más pequeños
        points.forEach { point ->
            drawCircle(
                color = surfaceColor,
                radius = 6f,
                center = point
            )
            drawCircle(
                color = primaryColor,
                radius = 4f,
                center = point
            )
        }
    }

    // Dibujar líneas de cuadrícula horizontales muy sutiles
    repeat(3) { i ->
        val y = padding + (chartHeight / 3) * (i + 1)
        drawLine(
            color = backgroundColor.copy(alpha = 0.2f),
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1f
        )
    }

    // Dibujar etiquetas de ejes si hay datos
    if (dataPoints.isNotEmpty()) {
        // Etiquetas en X (simplificadas)
        dataPoints.take(3).forEachIndexed { index, dataPoint ->
            val x = padding + (index.toFloat() / 2) * chartWidth
            drawContext.canvas.nativeCanvas.drawText(
                dataPoint.label,
                x,
                padding + chartHeight + 20f,
                android.graphics.Paint().apply {
                    color = backgroundColor.copy(alpha = 0.7f).toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // Etiqueta Y máxima
        drawContext.canvas.nativeCanvas.drawText(
            "$maxSteps",
            padding - 30f,
            padding + 10f,
            android.graphics.Paint().apply {
                color = backgroundColor.copy(alpha = 0.7f).toArgb()
                textSize = 20f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
        )
    }
}

private data class ChartDataPoint(
    val label: String,
    val steps: Int,
    val index: Int
)