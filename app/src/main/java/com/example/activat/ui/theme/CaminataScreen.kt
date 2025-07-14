package com.example.activat.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.activat.ui.theme.components.IndicadorMetaPasos
import com.example.activat.ui.theme.components.LiveSessionMetrics
import com.example.activat.ui.theme.components.rememberHapticFeedback
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

    // Estados locales para la UI
    var isPaused by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var stepsAtStart by remember { mutableFloatStateOf(0f) }

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
            haptic.start() // Feedback háptico al iniciar
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
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título dinámico
        Text(
            text = if (caminataActiva) {
                if (isPaused) "Sesión pausada" else "¡Caminando!"
            } else {
                "Listo para caminar"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = if (caminataActiva && !isPaused) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Métricas de la sesión usando componente reutilizable
        if (caminataActiva) {
            LiveSessionMetrics(
                pasosEnSesion = pasosEnSesionActual,
                tiempoSesion = tiempoSesionActual
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Indicador de progreso total del día
        IndicadorMetaPasos(
            currentSteps = pasosTotalesDelDia.toFloat(),
            metaPasos = usuarioData.metaPasosDiarios.toFloat()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Progreso total del día: $pasosTotalesDelDia pasos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de control
        if (!caminataActiva) {
            Button(
                onClick = {
                    haptic.start()
                    stepsAtStart = 0f
                    viewModel.iniciarCaminata()
                    isPaused = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar caminata")
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
                    modifier = Modifier.weight(1f),
                    colors = if (isPaused) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                ) {
                    Text(if (isPaused) "Reanudar" else "Pausar")
                }

                Button(
                    onClick = {
                        haptic.strong()
                        showDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Detener")
                }
            }
        }

        // Información de estado
        if (caminataActiva && isPaused) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "⏸️ Sesión pausada - Presiona Reanudar para continuar",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }

    // Dialog de confirmación optimizado
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    haptic.success()
                    viewModel.detenerCaminata()
                    showDialog = false
                    isPaused = false
                    onFinalizar()
                }) {
                    Text("Sí, guardar y finalizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Continuar caminando")
                }
            },
            title = { Text("¿Finalizar sesión?") },
            text = {
                Column {
                    Text("Tu progreso se guardará automáticamente:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• $pasosEnSesionActual pasos\n• ${com.example.activat.ui.theme.components.formatTime(tiempoSesionActual)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}