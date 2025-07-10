package com.example.activat.ui.theme

import android.Manifest
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableFloatStateOf
import android.widget.Toast
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.core.content.ContextCompat

@Composable
fun CaminataScreen() {
    val context = LocalContext.current
    val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true // Para versiones menores a Android 10
    }

    var isWalking by remember { mutableStateOf(false) }
    var stepsAtStart by remember { mutableFloatStateOf(0f) }
    var currentSteps by remember { mutableFloatStateOf(0f) }
    var elapsedTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isWalking) {
                    if (stepsAtStart == 0f) stepsAtStart = event.values[0]
                    currentSteps = event.values[0] - stepsAtStart
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (!permissionGranted) {
            // No hacer nada
        } else if (stepSensor != null) {
            try {
                sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al activar sensor: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Sensor de pasos no disponible en este dispositivo.", Toast.LENGTH_LONG).show()
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    LaunchedEffect(isWalking) {
        if (isWalking) {
            elapsedTime = 0L
            while (isWalking) {
                delay(1000L)
                elapsedTime += 1L
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
        Text("Pasos: ${currentSteps.toInt()}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tiempo: ${formatTime(elapsedTime)}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (isWalking) {
                    isWalking = false
                } else {
                    stepsAtStart = 0f
                    currentSteps = 0f
                    isWalking = true
                }
            }
        ) {
            Text(if (isWalking) "Detener caminata" else "Iniciar caminata")
        }
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}
