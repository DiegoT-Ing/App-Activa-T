package com.example.activat.ui.theme.components

/**
 * Función helper para formatear tiempo
 */
fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}