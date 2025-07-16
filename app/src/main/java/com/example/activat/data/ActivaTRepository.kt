package com.example.activat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "activat_settings")

class ActivaTRepository(private val context: Context) {

    // Keys para DataStore
    private object PreferencesKeys {
        // Usuario
        val EDAD = intPreferencesKey("edad")
        val ESTATURA = floatPreferencesKey("estatura")
        val PESO = floatPreferencesKey("peso")
        val META_PASOS = intPreferencesKey("meta_pasos")

        // Datos del día
        val FECHA_ACTUAL = stringPreferencesKey("fecha_actual")
        val PASOS_ACUMULADOS = intPreferencesKey("pasos_acumulados")
        val SESIONES_REALIZADAS = intPreferencesKey("sesiones_realizadas")

        // Configuración Salud
        val ACTIVIDAD_FISICA = stringPreferencesKey("actividad_fisica")
        val OBJETIVO_SALUD = stringPreferencesKey("objetivo_salud")

        // Sesiones (formato JSON simplificado)
        val SESIONES_JSON = stringPreferencesKey("sesiones_json")
    }

    // Flow para datos del usuario
    val usuarioDataFlow: Flow<UsuarioData> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            UsuarioData(
                edad = preferences[PreferencesKeys.EDAD] ?: 0,
                estatura = preferences[PreferencesKeys.ESTATURA] ?: 0f,
                peso = preferences[PreferencesKeys.PESO] ?: 0f,
                metaPasosDiarios = preferences[PreferencesKeys.META_PASOS] ?: 6000
            )
        }

    // Flow para configuración de salud
    val configuracionSaludFlow: Flow<ConfiguracionSalud> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            ConfiguracionSalud(
                actividadFisica = preferences[PreferencesKeys.ACTIVIDAD_FISICA] ?: "Sedentario",
                objetivoSalud = preferences[PreferencesKeys.OBJETIVO_SALUD] ?: "Mantener peso"
            )
        }

    // Flow para datos del día actual
    val datosDelDiaFlow: Flow<DatosDelDia> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val fechaGuardada = preferences[PreferencesKeys.FECHA_ACTUAL]
            val fechaActual = LocalDate.now()

            // Si cambió el día, reiniciar contadores
            if (fechaGuardada != fechaActual.toString()) {
                DatosDelDia(
                    fecha = fechaActual,
                    pasosAcumulados = 0,
                    sesionesRealizadas = 0
                )
            } else {
                DatosDelDia(
                    fecha = fechaActual,
                    pasosAcumulados = preferences[PreferencesKeys.PASOS_ACUMULADOS] ?: 0,
                    sesionesRealizadas = preferences[PreferencesKeys.SESIONES_REALIZADAS] ?: 0
                )
            }
        }

    // Guardar datos del usuario
    suspend fun guardarUsuario(usuario: UsuarioData) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EDAD] = usuario.edad
            preferences[PreferencesKeys.ESTATURA] = usuario.estatura
            preferences[PreferencesKeys.PESO] = usuario.peso
            preferences[PreferencesKeys.META_PASOS] = usuario.metaPasosDiarios
        }
    }

    // Guardar configuración de salud
    suspend fun guardarConfiguracionSalud(configuracion: ConfiguracionSalud) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVIDAD_FISICA] = configuracion.actividadFisica
            preferences[PreferencesKeys.OBJETIVO_SALUD] = configuracion.objetivoSalud
        }
    }

    // Guardar nueva sesión
    suspend fun guardarSesion(sesion: SesionCaminata) {
        context.dataStore.edit { preferences ->
            // Actualizar contadores del día
            val fechaActual = LocalDate.now().toString()
            val pasosActuales = preferences[PreferencesKeys.PASOS_ACUMULADOS] ?: 0
            val sesionesActuales = preferences[PreferencesKeys.SESIONES_REALIZADAS] ?: 0

            preferences[PreferencesKeys.FECHA_ACTUAL] = fechaActual
            preferences[PreferencesKeys.PASOS_ACUMULADOS] = pasosActuales + sesion.pasos
            preferences[PreferencesKeys.SESIONES_REALIZADAS] = sesionesActuales + 1

            // Guardar sesión en historial (implementación simplificada)
            val sesionesExistentes = preferences[PreferencesKeys.SESIONES_JSON] ?: ""
            val nuevaSesionJson = "${sesion.fecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}|${sesion.pasos}|${sesion.tiempoSegundos}|${sesion.distanciaKm}"
            val sesionesActualizadas = if (sesionesExistentes.isEmpty()) {
                nuevaSesionJson
            } else {
                "$sesionesExistentes;$nuevaSesionJson"
            }
            preferences[PreferencesKeys.SESIONES_JSON] = sesionesActualizadas
        }
    }

    // Obtener historial de sesiones
    fun obtenerHistorialSesiones(): Flow<List<SesionCaminata>> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val sesionesJson = preferences[PreferencesKeys.SESIONES_JSON] ?: ""
            if (sesionesJson.isEmpty()) {
                emptyList()
            } else {
                sesionesJson.split(";").mapNotNull { sesionStr ->
                    try {
                        val partes = sesionStr.split("|")
                        SesionCaminata(
                            fecha = LocalDateTime.parse(partes[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            pasos = partes[1].toInt(),
                            tiempoSegundos = partes[2].toLong(),
                            distanciaKm = partes[3].toFloat()
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        }

}