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

    // Animaciones de entrada más suaves
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
            .background(MaterialTheme.colorScheme.background) // Fondo limpio blanco
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título dinámico más limpio
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (caminataActiva) {
                        if (isPaused) "Sesión pausada" else "¡Caminando!"
                    } else {
                        "🚀 Listo para Caminar"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Indicador de progreso principal - CENTRADO CORRECTAMENTE
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 100))
        ) {
            CleanCard(
                borderColor = if (caminataActiva && !isPaused) FitnessGreen60 else TechBlue60,
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Progreso total del día",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProgressRing(
                            progress = porcentajeMetaAlcanzado,
                            size = 120.dp,
                            strokeWidth = 10.dp,
                            progressColor = if (caminataActiva && !isPaused) FitnessGreen60 else TechBlue60,
                            backgroundColor = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row { // <-- ¡Modificado! Ya NO lleva 'verticalAlignment = Alignment.Baseline'
                                AnimatedCounter(
                                    targetValue = pasosTotalesDelDia,
                                    textStyle = MaterialTheme.typography.headlineMedium,
                                    color = if (caminataActiva && !isPaused) FitnessGreen60 else TechBlue60,
                                    modifier = Modifier.alignByBaseline() // <-- ¡Añadido!
                                )
                                Text(
                                    text = " pasos",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Meta: ${usuarioData.metaPasosDiarios} pasos",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }

        // Métricas de la sesión actual - CENTRADO CORRECTAMENTE
        if (caminataActiva) {
            CleanCard(
                borderColor = if (isPaused) EnergyOrange60 else TechBlue60,
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sesión actual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "👣",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
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

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⏱️",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
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
            )
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
            ) {
            }
        }

        // Botones de control limpios
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(700, delayMillis = 300)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 300))
        ) {
            if (!caminataActiva) {
                PrimaryActionButton(
                    text = "Iniciar caminata",
                    onClick = {
                        haptic.start()
                        stepsAtStart = 0f
                        viewModel.iniciarCaminata()
                        isPaused = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            haptic.medium()
                            isPaused = !isPaused
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaused) FitnessGreen60 else EnergyOrange60
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isPaused) "▶️ Reanudar" else "⏸️ Pausar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            haptic.strong()
                            showDialog = true
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HealthCritical
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "⏹️ Detener",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Información de estado cuando está pausado - más limpia
        if (caminataActiva && isPaused) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + fadeIn()
            ) {
                CleanCard(
                    borderColor = EnergyOrange60,
                    backgroundColor = EnergyOrange60.copy(alpha = 0.05f),
                    content = {
                        Row(
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
                                    fontWeight = FontWeight.SemiBold,
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
                )
            }
        }
    }

    // Dialog de confirmación más limpio
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
                    ),
                    shape = RoundedCornerShape(8.dp)
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
                // Dialog de confirmación más limpio
                CleanCard(
                    borderColor = FitnessGreen60,
                    backgroundColor = FitnessGreen60.copy(alpha = 0.05f),
                    content = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Tu progreso se guardará automáticamente:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$pasosEnSesionActual",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FitnessGreen60
                                    )
                                    Text(
                                        text = "pasos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatTime(tiempoSesionActual),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TechBlue60
                                    )
                                    Text(
                                        text = "tiempo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                )
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}