package com.example.activat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activat.data.ActivaTRepository
import com.example.activat.data.DatosDelDia
import com.example.activat.data.SesionCaminata
import com.example.activat.data.UsuarioData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
// Factory para crear el ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context

class ActivaTViewModel(private val repository: ActivaTRepository) : ViewModel() {

    // Estados observables
    private val _usuarioData = MutableStateFlow(UsuarioData())
    val usuarioData: StateFlow<UsuarioData> = _usuarioData.asStateFlow()

    private val _datosDelDia = MutableStateFlow(DatosDelDia())
    val datosDelDia: StateFlow<DatosDelDia> = _datosDelDia.asStateFlow()

    private val _historialSesiones = MutableStateFlow<List<SesionCaminata>>(emptyList())
    val historialSesiones: StateFlow<List<SesionCaminata>> = _historialSesiones.asStateFlow()

    private val _ultimaSesion = MutableStateFlow<SesionCaminata?>(null)
    val ultimaSesion: StateFlow<SesionCaminata?> = _ultimaSesion.asStateFlow()

    // Estados para caminata activa
    private val _caminataActiva = MutableStateFlow(false)
    val caminataActiva: StateFlow<Boolean> = _caminataActiva.asStateFlow()

    private val _pasosEnSesionActual = MutableStateFlow(0)
    val pasosEnSesionActual: StateFlow<Int> = _pasosEnSesionActual.asStateFlow()

    private val _tiempoSesionActual = MutableStateFlow(0L)
    val tiempoSesionActual: StateFlow<Long> = _tiempoSesionActual.asStateFlow()

    // Estado derivado: porcentaje de meta alcanzado
    val porcentajeMetaAlcanzado: StateFlow<Float> = combine(
        _datosDelDia,
        _usuarioData,
        _pasosEnSesionActual
    ) { datosDelDia, usuario, pasosSesion ->
        val pasosTotal = datosDelDia.pasosAcumulados + pasosSesion
        if (usuario.metaPasosDiarios > 0) {
            (pasosTotal.toFloat() / usuario.metaPasosDiarios.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    // Pasos totales del día (incluyendo sesión actual si está activa)
    val pasosTotalesDelDia: StateFlow<Int> = combine(
        _datosDelDia,
        _pasosEnSesionActual
    ) { datosDelDia, pasosSesion ->
        datosDelDia.pasosAcumulados + pasosSesion
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            // Cargar datos del usuario
            repository.usuarioDataFlow.collect { userData ->
                _usuarioData.value = userData
            }
        }

        viewModelScope.launch {
            // Cargar datos del día
            repository.datosDelDiaFlow.collect { datosDelDia ->
                _datosDelDia.value = datosDelDia
            }
        }

        viewModelScope.launch {
            // Cargar historial
            repository.obtenerHistorialSesiones().collect { sesiones ->
                _historialSesiones.value = sesiones
                _ultimaSesion.value = sesiones.maxByOrNull { it.fecha }
            }
        }
    }

    // Funciones para manejar datos del usuario
    fun actualizarUsuario(
        edad: Int,
        estatura: Float,
        peso: Float,
        metaPasos: Int
    ) {
        viewModelScope.launch {
            val nuevoUsuario = UsuarioData(
                edad = edad,
                estatura = estatura,
                peso = peso,
                metaPasosDiarios = metaPasos
            )
            repository.guardarUsuario(nuevoUsuario)
        }
    }

    // Funciones para manejar caminata
    fun iniciarCaminata() {
        _caminataActiva.value = true
        _pasosEnSesionActual.value = 0
        _tiempoSesionActual.value = 0L
    }

    fun pausarCaminata() {
        // Por ahora solo cambiamos el estado
        // La lógica de pausa/reanudación se maneja en la UI
    }

    fun detenerCaminata() {
        viewModelScope.launch {
            val sesion = SesionCaminata(
                id = UUID.randomUUID().toString(),
                fecha = LocalDateTime.now(),
                pasos = _pasosEnSesionActual.value,
                tiempoSegundos = _tiempoSesionActual.value,
                distanciaKm = calcularDistancia(_pasosEnSesionActual.value)
            )

            // Guardar sesión
            repository.guardarSesion(sesion)

            // Resetear estados
            _caminataActiva.value = false
            _pasosEnSesionActual.value = 0
            _tiempoSesionActual.value = 0L
        }
    }

    // Actualizar pasos durante caminata activa
    fun actualizarPasosSesion(pasos: Int) {
        _pasosEnSesionActual.value = pasos
    }

    // Actualizar tiempo de sesión
    fun actualizarTiempoSesion(tiempo: Long) {
        _tiempoSesionActual.value = tiempo
    }

    // Calcular distancia basada en estatura del usuario
    private fun calcularDistancia(pasos: Int): Float {
        val estatura = _usuarioData.value.estatura
        return if (estatura > 0) {
            val zancadaCm = estatura * 0.415f
            (pasos * zancadaCm) / 100000f
        } else {
            // Usar estatura promedio si no hay datos
            val zancadaPromedio = 70f // cm aproximado
            (pasos * zancadaPromedio) / 100000f
        }
    }

    // Función para obtener sesiones filtradas por período
    fun obtenerSesionesPorPeriodo(periodo: String): List<SesionCaminata> {
        val ahora = LocalDateTime.now()
        return when (periodo) {
            "Día" -> _historialSesiones.value.filter {
                it.fecha.toLocalDate() == ahora.toLocalDate()
            }
            "Semana" -> _historialSesiones.value.filter {
                it.fecha.isAfter(ahora.minusDays(7))
            }
            "Mes" -> _historialSesiones.value.filter {
                it.fecha.isAfter(ahora.minusDays(30))
            }
            else -> _historialSesiones.value
        }
    }
}


class ActivaTViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivaTViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivaTViewModel(ActivaTRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}