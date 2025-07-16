package com.example.activat.ui.theme.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activat.data.SesionCaminata
import com.example.activat.ui.theme.FitnessGreen60
import com.example.activat.ui.theme.NeutralGray80
import java.time.format.DateTimeFormatter

@Composable
fun StepsChart(
    sesiones: List<SesionCaminata>,
    periodo: String,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()
    LocalDensity.current

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

    if (sesiones.isNotEmpty()) {
        // Preparar datos para la gráfica
        val dataPoints = prepareChartData(sesiones, periodo)
        val maxSteps = dataPoints.maxOfOrNull { it.steps } ?: 1000

        // Canvas para la gráfica
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
                primaryColor = FitnessGreen60,
                backgroundColor = NeutralGray80,
                surfaceColor = Color.White,
                textMeasurer = textMeasurer
            )
        }
    } else {
        // Estado vacío
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
    surfaceColor: Color,
    textMeasurer: TextMeasurer
) {
    if (dataPoints.isEmpty()) return

    val width = size.width
    val height = size.height
    val padding = 50f
    val chartWidth = width - 2 * padding
    val chartHeight = height - 2 * padding

    // Dibujar fondo del gráfico
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
        // Dibujar área bajo la curva
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

        // Dibujar puntos
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

    // Dibujar líneas de cuadrícula horizontales
    repeat(3) { i ->
        val y = padding + (chartHeight / 3) * (i + 1)
        drawLine(
            color = backgroundColor.copy(alpha = 0.2f),
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1f
        )
    }

    // Dibujar etiquetas usando TextMeasurer de Compose
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = backgroundColor.copy(alpha = 0.7f),
        fontWeight = FontWeight.Normal
    )

    // Etiquetas en X (solo algunas para evitar sobreposición)
    if (dataPoints.isNotEmpty()) {
        val labelsToShow = when {
            dataPoints.size <= 3 -> dataPoints.indices.toList()
            dataPoints.size <= 7 -> listOf(0, dataPoints.size / 2, dataPoints.size - 1)
            else -> listOf(0, dataPoints.size / 3, 2 * dataPoints.size / 3, dataPoints.size - 1)
        }

        labelsToShow.forEach { index ->
            if (index < dataPoints.size && index < points.size) {
                val dataPoint = dataPoints[index]
                val textLayoutResult = textMeasurer.measure(
                    text = dataPoint.label,
                    style = textStyle
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        points[index].x - textLayoutResult.size.width / 2,
                        padding + chartHeight + 8f
                    )
                )
            }
        }

        // Etiqueta Y máxima
        val maxStepsText = textMeasurer.measure(
            text = "$maxSteps",
            style = textStyle
        )

        drawText(
            textLayoutResult = maxStepsText,
            topLeft = Offset(
                padding - maxStepsText.size.width - 8f,
                padding - maxStepsText.size.height / 2
            )
        )

        // Etiqueta Y media
        val midStepsText = textMeasurer.measure(
            text = "${maxSteps / 2}",
            style = textStyle
        )

        drawText(
            textLayoutResult = midStepsText,
            topLeft = Offset(
                padding - midStepsText.size.width - 8f,
                padding + chartHeight / 2 - midStepsText.size.height / 2
            )
        )

        // Etiqueta Y cero
        val zeroText = textMeasurer.measure(
            text = "0",
            style = textStyle
        )

        drawText(
            textLayoutResult = zeroText,
            topLeft = Offset(
                padding - zeroText.size.width - 8f,
                padding + chartHeight - zeroText.size.height / 2
            )
        )
    }
}

private data class ChartDataPoint(
    val label: String,
    val steps: Int,
    val index: Int
)