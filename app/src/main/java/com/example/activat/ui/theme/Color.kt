package com.example.activat.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// === NUEVA PALETA CLEAN HEALTH (manteniendo nombres existentes) ===

// Verdes Actividad (actualizados a tonos más profesionales)
val FitnessGreen80 = Color(0xFF4CAF50)      // Verde suave claro (menos saturado)
val FitnessGreen60 = Color(0xFF2E7D32)      // Verde principal (más profesional)
val FitnessGreen40 = Color(0xFF1B5E20)      // Verde intenso (más sobrio)
val FitnessGreen20 = Color(0xFF0F3510)      // Verde oscuro

// Azules Tecnología (actualizados a tonos más suaves)
val TechBlue80 = Color(0xFF42A5F5)          // Azul suave claro (menos vibrante)
val TechBlue60 = Color(0xFF1976D2)          // Azul principal (más profesional)
val TechBlue40 = Color(0xFF0D47A1)          // Azul intenso
val TechBlue20 = Color(0xFF0A2E6B)          // Azul oscuro

// Naranjas Energía (tonos más suaves)
val EnergyOrange80 = Color(0xFFFFB74D)      // Naranja suave (sin cambios)
val EnergyOrange60 = Color(0xFFFF8F00)      // Naranja principal (menos saturado)
val EnergyOrange40 = Color(0xFFE65100)      // Naranja intenso

// Púrpuras Motivación (tonos más profesionales)
val MotivationPurple80 = Color(0xFF9C27B0)  // Púrpura suave (menos saturado)
val MotivationPurple60 = Color(0xFF7B1FA2)  // Púrpura principal
val MotivationPurple40 = Color(0xFF4A148C)  // Púrpura intenso

// Grises Neutrales (sistema más coherente)
val NeutralGray95 = Color(0xFFFAFAFA)       // Casi blanco (más cálido)
val NeutralGray90 = Color(0xFFF5F5F5)       // Gris muy claro
val NeutralGray80 = Color(0xFFE0E0E0)       // Gris claro (menos saturado)
val NeutralGray60 = Color(0xFF9E9E9E)       // Gris medio (más sutil)
val NeutralGray40 = Color(0xFF616161)       // Gris oscuro
val NeutralGray20 = Color(0xFF212121)       // Casi negro

// === COLORES SEMÁNTICOS (actualizados) ===

// Estados de progreso (menos agresivos)
val ProgressLow = Color(0xFFD32F2F)         // Rojo para bajo progreso (menos vibrante)
val ProgressMedium = EnergyOrange60         // Naranja para progreso medio
val ProgressHigh = FitnessGreen60           // Verde para buen progreso
val ProgressExcellent = FitnessGreen40      // Verde intenso para excelente

// Estados de salud (más profesionales)
val HealthExcellent = FitnessGreen40        // Verde para salud excelente
val HealthGood = FitnessGreen60             // Verde claro para buena salud
val HealthWarning = EnergyOrange60          // Naranja para advertencia
val HealthCritical = Color(0xFFD32F2F)      // Rojo para crítico (menos agresivo)

// === ESQUEMAS LEGACY (mantenemos compatibilidad) ===
val Purple80 = MotivationPurple80
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = MotivationPurple60
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// === GRADIENTES ACTUALIZADOS (menos saturados) ===
object FitnessGradients {
    val PrimaryGradient = listOf(FitnessGreen60, FitnessGreen80)
    val EnergyGradient = listOf(EnergyOrange60, EnergyOrange80)
    val MotivationGradient = listOf(MotivationPurple60, MotivationPurple80)
    val SuccessGradient = listOf(FitnessGreen40, FitnessGreen80)

    // Nuevos gradientes más sutiles
    val SubtlePrimary = listOf(
        FitnessGreen60.copy(alpha = 0.1f),
        FitnessGreen60.copy(alpha = 0.05f)
    )
    val SubtleSecondary = listOf(
        TechBlue60.copy(alpha = 0.1f),
        TechBlue60.copy(alpha = 0.05f)
    )
}

// === COLORES PARA ESTADO DE ACTIVIDAD (actualizados) ===
object ActivityColors {
    val Sedentary = NeutralGray60
    val Light = EnergyOrange80
    val Moderate = EnergyOrange60
    val Active = FitnessGreen60
    val VeryActive = FitnessGreen40
}

// === COLORES PARA MÉTRICAS DE SALUD (menos agresivos) ===
object HealthMetrics {
    val IMC_Underweight = TechBlue60
    val IMC_Normal = FitnessGreen60
    val IMC_Overweight = EnergyOrange60
    val IMC_Obese = ProgressLow

    val Steps_Excellent = FitnessGreen40
    val Steps_Good = FitnessGreen60
    val Steps_Fair = EnergyOrange60
    val Steps_Poor = ProgressLow
}

// === COLORES PARA ESTADOS DE LA APP (más sutiles) ===
object AppStateColors {
    val Success = FitnessGreen60
    val Warning = EnergyOrange60
    val Error = ProgressLow
    val Info = TechBlue60
    val Loading = MotivationPurple60
}

// === NUEVOS TOKENS DE ESPACIADO ===
object HealthSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp

    // Espaciados específicos para componentes
    val cardPadding = 20.dp
    val screenPadding = 24.dp
    val sectionSpacing = 20.dp
}

// === FUNCIONES DE UTILIDAD (mejoradas) ===

/**
 * Obtiene color de progreso más sutil
 */
fun getProgressColorSubtle(percentage: Float): Color {
    return when {
        percentage >= 0.9f -> FitnessGreen40
        percentage >= 0.7f -> FitnessGreen60
        percentage >= 0.4f -> EnergyOrange60
        else -> ProgressLow
    }
}

/**
 * Aplica opacidad a cualquier color
 */
fun Color.withOpacity(opacity: Float): Color {
    return this.copy(alpha = opacity)
}