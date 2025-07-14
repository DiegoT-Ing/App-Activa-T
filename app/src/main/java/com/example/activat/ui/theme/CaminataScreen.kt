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

    // Estados del ViewModel
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()
    val porcentajeMetaAlcanzado by viewModel.porcentajeMetaAlcanzado.collectAsStateWithLifecycle()
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
        Text(
            text = "Sesión activa",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Métricas de la sesión actual
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pasos en sesión: $pasosEnSesionActual",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tiempo: ${formatTime(tiempoSesionActual)}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Indicador de progreso total del día
        IndicadorMetaPasos(
            currentSteps = pasosTotalesDelDia.toFloat(),
            metaPasos = usuarioData.metaPasosDiarios.toFloat()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Progreso total del día",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de control
        if (!caminataActiva) {
            Button(
                onClick = {
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
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isPaused) "Reanudar" else "Pausar")
                }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Detener")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información adicional
        Text(
            text = "Total del día: $pasosTotalesDelDia pasos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    // Dialog de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.detenerCaminata()
                    showDialog = false
                    isPaused = false
                    onFinalizar()
                }) {
                    Text("Sí, detener")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("¿Detener caminata?") },
            text = { Text("¿Estás seguro de que deseas finalizar esta sesión? Se guardará automáticamente.") }
        )
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}