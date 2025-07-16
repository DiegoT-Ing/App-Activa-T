package com.example.activat.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.activat.ui.theme.components.CleanCard
import com.example.activat.ui.theme.components.PrimaryActionButton
import com.example.activat.ui.theme.components.rememberHapticFeedback
import com.example.activat.viewmodel.ActivaTViewModel

@Composable
fun UsuarioScreen(viewModel: ActivaTViewModel) {
    // Estados del ViewModel
    val usuarioData by viewModel.usuarioData.collectAsStateWithLifecycle()

    // Feedback háptico
    val haptic = rememberHapticFeedback()

    // Estados locales para los inputs
    var edad by remember { mutableStateOf("") }
    var estatura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var metaPasos by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    // Animación de entrada más rápida
    LaunchedEffect(Unit) {
        isVisible = true
    }

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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header limpio
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        ) {
            Column {
                Text(
                    text = "PERFIL DE USUARIO",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Personaliza tu experiencia Activa-T",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Formulario limpio sin gradientes
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 100))
        ) {
            CleanCard(
                borderColor = FitnessGreen60
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Datos Personales",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Edad Input
                    OutlinedTextField(
                        value = edad,
                        onValueChange = { edad = it },
                        label = {
                            Text(
                                "Edad",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FitnessGreen60,
                            focusedLabelColor = FitnessGreen60,
                            cursorColor = FitnessGreen60
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Estatura Input
                    OutlinedTextField(
                        value = estatura,
                        onValueChange = { estatura = it },
                        label = {
                            Text(
                                "Estatura [cm]",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechBlue60,
                            focusedLabelColor = TechBlue60,
                            cursorColor = TechBlue60
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Peso Input
                    OutlinedTextField(
                        value = peso,
                        onValueChange = { peso = it },
                        label = {
                            Text(
                                "Peso [kg]",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnergyOrange60,
                            focusedLabelColor = EnergyOrange60,
                            cursorColor = EnergyOrange60
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Meta Input
                    OutlinedTextField(
                        value = metaPasos,
                        onValueChange = { metaPasos = it },
                        label = {
                            Text(
                                "Meta diaria de pasos",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MotivationPurple60,
                            focusedLabelColor = MotivationPurple60,
                            cursorColor = MotivationPurple60
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de guardar limpio
                    PrimaryActionButton(
                        text = "Guardar datos",
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

                                haptic.success()
                                showSuccessMessage = true
                            } catch (_: Exception) {
                                haptic.strong()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Text(
                                text = "💾",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
            }
        }

        // Mensaje de éxito más limpio
        AnimatedVisibility(
            visible = showSuccessMessage,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            CleanCard(
                backgroundColor = FitnessGreen60.copy(alpha = 0.05f),
                borderColor = FitnessGreen60
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✅",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "¡Datos guardados exitosamente!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FitnessGreen60
                    )
                }
            }
        }

        // Ocultar mensaje después de 3 segundos
        LaunchedEffect(showSuccessMessage) {
            if (showSuccessMessage) {
                kotlinx.coroutines.delay(3000)
                showSuccessMessage = false
            }
        }
    }
}