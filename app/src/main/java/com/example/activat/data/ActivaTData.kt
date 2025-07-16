package com.example.activat.data

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Datos del usuario para persistencia local
 */
data class UsuarioData(
    val edad: Int = 0,
    val estatura: Float = 0f, // en cm
    val peso: Float = 0f, // en kg
    val metaPasosDiarios: Int = 7000
)

/**
 * Configuración de salud del usuario
 */
data class ConfiguracionSalud(
    val actividadFisica: String = "Sedentario",
    val objetivoSalud: String = "Mantener peso",
    val ultimaActualizacion: LocalDateTime = LocalDateTime.now()
)

/**
 * Sesión individual de caminata
 */
data class SesionCaminata(
    val id: String = "",
    val fecha: LocalDateTime = LocalDateTime.now(),
    val pasos: Int = 0,
    val tiempoSegundos: Long = 0L,
    val distanciaKm: Float = 0f
) {

    /**
     * Formato legible del tiempo
     */
    fun tiempoFormateado(): String {
        val minutos = tiempoSegundos / 60
        val segundos = tiempoSegundos % 60
        return "%02d:%02d".format(minutos, segundos)
    }
}

/**
 * Datos acumulados del día actual
 */
data class DatosDelDia(
    val fecha: LocalDate = LocalDate.now(),
    val pasosAcumulados: Int = 0,
    val sesionesRealizadas: Int = 0
)

