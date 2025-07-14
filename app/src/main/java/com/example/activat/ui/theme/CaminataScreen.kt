package com.example.activat.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.activat.ui.theme.components.*
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

@Composable
fun CaminataScreen(
    autoStart: Boolean = false,
    viewModel: ActivaTViewModel,
    onFinalizar: () -> Unit
) {
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()

    // Estados del ViewModel
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()
    val pasosTotalesDelDia by viewModel.pasosTotalesDelDia.collectAsStateWithLifecycle()
    val pasosEnSesionActual by viewModel.pasosEnSesionActual.collectAsStateWithLifecycle()
    val tiempoSesionActual by viewModel.tiempoSesionActual.collectAsStateWithLifecycle()
    val caminataActiva by viewModel.caminataActiva.collectAsStateWithLifecycle()
    val porcentajeMetaAlcanzado by viewModel.porcentajeMetaAlcanzado.collectAsStateWithLifecycle()

    // Estados locales para la UI
    var isPaused by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var stepsAtStart by remember { mutableFloatStateOf(0f) }
    var isVisible by remember { mutableStateOf(false) }

    // Animaciones de entrada
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val permissionGranted = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // Autostart effect
    LaunchedEffect(autoStart) {
        if (autoStart && !caminataActiva) {
            stepsAtStart = 0f
            viewModel.iniciarCaminata()
            haptic.start()
        }
    }

    // Sensor setup
    DisposableEffect(caminataActiva) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (caminataActiva && !isPaused) {
                    if (stepsAtStart == 0f) {
                        stepsAtStart = event.values[0]
                    }
                    val pasosEnSesion = (event.values[0] - stepsAtStart).toInt()
                    viewModel.actualizarPasosSesion(pasosEnSesion)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (permissionGranted && stepSensor != null && caminataActiva) {
            sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else if (!permissionGranted) {
            Toast.makeText(context, "Sensor de pasos no disponible o sin permisos", Toast.LENGTH_LONG).show()
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // Timer effect
    LaunchedEffect(caminataActiva, isPaused) {
        while (caminataActiva) {
            if (!isPaused) {
                delay(1000L)
                viewModel.actualizarTiempoSesion(tiempoSesionActual + 1)
            } else {
                delay(100L)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        if (caminataActiva && !isPaused) FitnessGreen80.copy(alpha = 0.15f) else NeutralGray95,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Título dinámico con animación
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (caminataActiva) {
                        if (isPaused) "⏸️ Sesión Pausada" else "🏃‍♂️ ¡Caminando!"
                    } else {
                        "🚀 Listo para Caminar"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (caminataActiva && !isPaused) {
                        FitnessGreen60
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                if (caminataActiva) {
                    Text(
                        text = if (isPaused) "Toca Reanudar para continuar" else "¡Sigue así!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Indicador de progreso principal con nueva identidad
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(700, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 200))
        ) {
            FitnessGradientCard(
                colors = if (caminataActiva && !isPaused) {
                    FitnessGradients.SuccessGradient
                } else {
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progreso Total del Día",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProgressRing(
                        progress = porcentajeMetaAlcanzado,
                        size = 140.dp,
                        strokeWidth = 12.dp,
                        progressColor = Color.White,
                        backgroundColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$pasosTotalesDelDia pasos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Meta: ${usuarioData.metaPasosDiarios} pasos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Métricas de la sesión actual
        if (caminataActiva) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 300))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPaused) {
                            EnergyOrange60.copy(alpha = 0.1f)
                        } else {
                            FitnessGreen60.copy(alpha = 0.1f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sesión Actual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "👣",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                AnimatedCounter(
                                    targetValue = pasosEnSesionActual,
                                    textStyle = MaterialTheme.typography.headlineLarge,
                                    color = FitnessGreen60
                                )
                                Text(
                                    text = "Pasos",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "⏱️",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(
                                    text = formatTime(tiempoSesionActual),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TechBlue60
                                )
                                Text(
                                    text = "Tiempo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Botones de control con nueva identidad
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(900, delayMillis = 400)
            ) + fadeIn(animationSpec = tween(900, delayMillis = 400))
        ) {
            if (!caminataActiva) {
                PulsingButton(
                    onClick = {
                        haptic.start()
                        stepsAtStart = 0f
                        viewModel.iniciarCaminata()
                        isPaused = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitnessGreen60
                    )
                ) {
                    Text(
                        text = "🚀 Iniciar Caminata",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            haptic.medium()
                            isPaused = !isPaused
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaused) FitnessGreen60 else EnergyOrange60
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = if (isPaused) "▶️ Reanudar" else "⏸️ Pausar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            haptic.strong()
                            showDialog = true
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HealthCritical
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "⏹️ Detener",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Información de estado mejorada
        if (caminataActiva && isPaused) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + fadeIn()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = EnergyOrange60.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏸️",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sesión Pausada",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EnergyOrange60
                            )
                            Text(
                                text = "Presiona Reanudar para continuar tu progreso",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog de confirmación mejorado
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.success()
                        viewModel.detenerCaminata()
                        showDialog = false
                        isPaused = false
                        onFinalizar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitnessGreen60
                    )
                ) {
                    Text("✓ Sí, guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Continuar")
                }
            },
            title = {
                Text(
                    text = "🏁 ¿Finalizar sesión?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = FitnessGreen60.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tu progreso se guardará automáticamente:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "👣 $pasosEnSesionActual pasos",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = FitnessGreen60
                            )
                            Text(
                                text = "⏱️ ${formatTime(tiempoSesionActual)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = TechBlue60
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}