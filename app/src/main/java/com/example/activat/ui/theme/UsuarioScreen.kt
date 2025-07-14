package com.example.activat.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UsuarioScreen(viewModel: ActivaTViewModel) {
    // Estados del ViewModel
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()

    // Estados locales para los inputs
    var edad by remember { mutableStateOf("") }
    var estatura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var metaPasos by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Cargar datos cuando cambien en el ViewModel
    LaunchedEffect(usuarioData) {
        if (usuarioData.edad > 0) edad = usuarioData.edad.toString()
        if (usuarioData.estatura > 0) estatura = usuarioData.estatura.toString()
        if (usuarioData.peso > 0) peso = usuarioData.peso.toString()
        metaPasos = usuarioData.metaPasosDiarios.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Datos del Usuario",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = estatura,
            onValueChange = { estatura = it },
            label = { Text("Estatura (cm)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = metaPasos,
            onValueChange = { metaPasos = it },
            label = { Text("Meta diaria de pasos") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                try {
                    val edadInt = edad.toIntOrNull() ?: 0
                    val estaturaFloat = estatura.toFloatOrNull() ?: 0f
                    val pesoFloat = peso.toFloatOrNull() ?: 0f
                    val metaPasosInt = metaPasos.toIntOrNull() ?: 6000

                    viewModel.actualizarUsuario(
                        edad = edadInt,
                        estatura = estaturaFloat,
                        peso = pesoFloat,
                        metaPasos = metaPasosInt
                    )

                    showSuccessMessage = true
                } catch (e: Exception) {
                    // Manejar errores de conversión
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar datos")
        }

        if (showSuccessMessage) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "✓ Datos guardados exitosamente",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Ocultar mensaje después de 3 segundos
            LaunchedEffect(showSuccessMessage) {
                kotlinx.coroutines.delay(3000)
                showSuccessMessage = false
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Información actual
        if (usuarioData.edad > 0 || usuarioData.estatura > 0 || usuarioData.peso > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Datos actuales:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (usuarioData.edad > 0) {
                        Text("Edad: ${usuarioData.edad} años")
                    }
                    if (usuarioData.estatura > 0) {
                        Text("Estatura: ${usuarioData.estatura} cm")
                    }
                    if (usuarioData.peso > 0) {
                        Text("Peso: ${usuarioData.peso} kg")
                    }
                    Text("Meta diaria: ${usuarioData.metaPasosDiarios} pasos")
                }
            }
        }
    }
}