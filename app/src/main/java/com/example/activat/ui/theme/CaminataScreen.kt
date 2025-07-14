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
import kotlinx.coroutines.delay

@Composable
fun CaminataScreen(autoStart: Boolean = false, onFinalizar: () -> Unit) {
    val context = LocalContext.current

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

    var isWalking by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    var stepsAtStart by remember { mutableFloatStateOf(0f) }
    var currentSteps by remember { mutableFloatStateOf(0f) }
    var elapsedTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(autoStart) {
        if (autoStart && !isWalking) {
            stepsAtStart = 0f
            currentSteps = 0f
            elapsedTime = 0L
            isWalking = true
        }
    }

    // Sensor setup
    DisposableEffect(isWalking) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isWalking && !isPaused) {
                    if (stepsAtStart == 0f) {
                        stepsAtStart = event.values[0]
                    }
                    currentSteps = event.values[0] - stepsAtStart
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (permissionGranted && stepSensor != null) {
            sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Toast.makeText(context, "Sensor de pasos no disponible o sin permisos", Toast.LENGTH_LONG).show()
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // Tiempo
    LaunchedEffect(isWalking, isPaused) {
        while (isWalking) {
            if (!isPaused) {
                delay(1000L)
                elapsedTime += 1
            } else {
                delay(100L)
            }
        }
    }

    val metaPasos = 6000f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pasos: ${currentSteps.toInt()}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tiempo: ${formatTime(elapsedTime)}", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))
        IndicadorMetaPasos(currentSteps = currentSteps, metaPasos = metaPasos)
        Spacer(modifier = Modifier.height(32.dp))

        if (!isWalking) {
            Button(onClick = {
                stepsAtStart = 0f
                currentSteps = 0f
                elapsedTime = 0L
                isPaused = false
                isWalking = true
            }) {
                Text("Iniciar caminata")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { isPaused = !isPaused }) {
                    Text(if (isPaused) "Reanudar" else "Pausar")
                }

                Button(onClick = { showDialog = true }) {
                    Text("Detener")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    isWalking = false
                    showDialog = false
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
            text = { Text("¿Estás seguro de que deseas finalizar esta sesión?") }
        )
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}
