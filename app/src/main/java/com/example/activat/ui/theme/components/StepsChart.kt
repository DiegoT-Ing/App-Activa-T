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

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
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
                    text = "Tendencia de Pasos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Indicador del período
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = periodo,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (sesiones.isNotEmpty()) {
                // Preparar datos para la gráfica
                val dataPoints = prepareChartData(sesiones, periodo)
                val maxSteps = dataPoints.maxOfOrNull { it.steps } ?: 1000

                // Canvas para la gráfica
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    drawChart(
                        dataPoints = dataPoints,
                        maxSteps = maxSteps,
                        animationProgress = animatedProgress,
                        primaryColor = androidx.compose.ui.graphics.Color(0xFF6750A4),
                        backgroundColor = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
                        surfaceColor = androidx.compose.ui.graphics.Color(0xFFFFFBFE)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estadísticas rápidas
                StatsRow(sesiones = sesiones, animationProgress = animatedProgress)

            } else {
                // Estado vacío con animación
                EmptyChartState(modifier = Modifier.height(200.dp))
            }
        }
    }
}

@Composable
private fun StatsRow(
    sesiones: List<SesionCaminata>,
    animationProgress: Float
) {
    val totalSteps = sesiones.sumOf { it.pasos }
    val avgSteps = if (sesiones.isNotEmpty()) totalSteps / sesiones.size else 0
    val bestDay = sesiones.maxByOrNull { it.pasos }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatItem(
            label = "Total",
            value = "${(totalSteps * animationProgress).toInt()}",
            color = MaterialTheme.colorScheme.primary
        )

        StatItem(
            label = "Promedio",
            value = "${(avgSteps * animationProgress).toInt()}",
            color = MaterialTheme.colorScheme.secondary
        )

        StatItem(
            label = "Mejor día",
            value = "${((bestDay?.pasos ?: 0) * animationProgress).toInt()}",
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyChartState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
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
                text = "Gráfica aparecerá aquí",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Inicia sesiones para ver tendencias",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

    // Dibujar fondo del gráfico
    drawRoundRect(
        color = backgroundColor.copy(alpha = 0.1f),
        topLeft = Offset(padding, padding),
        size = androidx.compose.ui.geometry.Size(chartWidth, chartHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
    )

    // Calcular puntos de la línea
    val points = dataPoints.mapIndexed { index, dataPoint ->
        val x = padding + (index.toFloat() / (dataPoints.size - 1).coerceAtLeast(1)) * chartWidth
        val y = padding + chartHeight - (dataPoint.steps.toFloat() / maxSteps) * chartHeight * animationProgress
        Offset(x, y)
    }

    if (points.size > 1) {
        // Dibujar área bajo la curva (gradiente)
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
                    primaryColor.copy(alpha = 0.3f),
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
                width = 4f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Dibujar puntos
        points.forEach { point ->
            drawCircle(
                color = surfaceColor,
                radius = 8f,
                center = point
            )
            drawCircle(
                color = primaryColor,
                radius = 5f,
                center = point
            )
        }
    }

    // Dibujar líneas de cuadrícula horizontales
    repeat(4) { i ->
        val y = padding + (chartHeight / 4) * (i + 1)
        drawLine(
            color = backgroundColor.copy(alpha = 0.3f),
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1f
        )
    }
}

private data class ChartDataPoint(
    val label: String,
    val steps: Int,
    val index: Int
)