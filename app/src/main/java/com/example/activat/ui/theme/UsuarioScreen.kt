package com.example.activat.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.activat.ui.theme.components.*
import com.example.activat.viewmodel.ActivaTViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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

    // Animación de entrada
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header mejorado
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Column {
                Text(
                    text = "👤 Perfil de Usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = FitnessGreen60
                )
                Text(
                    text = "Personaliza tu experiencia ActivaT",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Formulario con nueva identidad
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(700, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(700, delayMillis = 100))
        ) {
            FitnessGradientCard(
                colors = FitnessGradients.PrimaryGradient
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "📝 Datos Personales",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Edad Input
                    OutlinedTextField(
                        value = edad,
                        onValueChange = { edad = it },
                        label = { Text("🎂 Edad", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Estatura Input
                    OutlinedTextField(
                        value = estatura,
                        onValueChange = { estatura = it },
                        label = { Text("📏 Estatura (cm)", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Peso Input
                    OutlinedTextField(
                        value = peso,
                        onValueChange = { peso = it },
                        label = { Text("⚖️ Peso (kg)", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Meta Input
                    OutlinedTextField(
                        value = metaPasos,
                        onValueChange = { metaPasos = it },
                        label = { Text("🎯 Meta diaria de pasos", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de guardar mejorado
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

                                haptic.success()
                                showSuccessMessage = true
                            } catch (e: Exception) {
                                haptic.strong()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "💾 Guardar Datos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Mensaje de éxito mejorado
        AnimatedVisibility(
            visible = showSuccessMessage,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = FitnessGreen60.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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

        // Información actual mejorada
        if (usuarioData.edad > 0 || usuarioData.estatura > 0 || usuarioData.peso > 0) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { haptic.light() }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "📊 Resumen Actual",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid de datos actuales
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (usuarioData.edad > 0) {
                                    DataCard(
                                        icon = "🎂",
                                        label = "Edad",
                                        value = "${usuarioData.edad} años",
                                        color = FitnessGreen60,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (usuarioData.estatura > 0) {
                                    DataCard(
                                        icon = "📏",
                                        label = "Estatura",
                                        value = "${usuarioData.estatura} cm",
                                        color = TechBlue60,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (usuarioData.peso > 0) {
                                    DataCard(
                                        icon = "⚖️",
                                        label = "Peso",
                                        value = "${usuarioData.peso} kg",
                                        color = EnergyOrange60,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                DataCard(
                                    icon = "🎯",
                                    label = "Meta",
                                    value = "${usuarioData.metaPasosDiarios} pasos",
                                    color = MotivationPurple60,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
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

@Composable
private fun DataCard(
    icon: String,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}