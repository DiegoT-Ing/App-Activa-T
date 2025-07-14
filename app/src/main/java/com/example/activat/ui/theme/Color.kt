package com.example.activat.ui.theme

import androidx.compose.ui.graphics.Color

// === PALETA FITNESS MODERNA ===

// Verdes Actividad (principales)
val FitnessGreen80 = Color(0xFF81C784)      // Verde suave claro
val FitnessGreen60 = Color(0xFF66BB6A)      // Verde principal
val FitnessGreen40 = Color(0xFF4CAF50)      // Verde intenso
val FitnessGreen20 = Color(0xFF388E3C)      // Verde oscuro

// Azules Tecnología (secundarios)
val TechBlue80 = Color(0xFF64B5F6)          // Azul suave claro
val TechBlue60 = Color(0xFF42A5F5)          // Azul principal
val TechBlue40 = Color(0xFF2196F3)          // Azul intenso
val TechBlue20 = Color(0xFF1976D2)          // Azul oscuro

// Naranjas Energía (acentos)
val EnergyOrange80 = Color(0xFFFFB74D)      // Naranja suave
val EnergyOrange60 = Color(0xFFFF9800)      // Naranja principal
val EnergyOrange40 = Color(0xFFEF6C00)      // Naranja intenso

// Púrpuras Motivación (destacados)
val MotivationPurple80 = Color(0xFFBA68C8)  // Púrpura suave
val MotivationPurple60 = Color(0xFF9C27B0)  // Púrpura principal
val MotivationPurple40 = Color(0xFF7B1FA2)  // Púrpura intenso

// Grises Neutrales
val NeutralGray95 = Color(0xFFF5F5F5)       // Casi blanco
val NeutralGray90 = Color(0xFFE0E0E0)       // Gris muy claro
val NeutralGray80 = Color(0xFFBDBDBD)       // Gris claro
val NeutralGray60 = Color(0xFF757575)       // Gris medio
val NeutralGray40 = Color(0xFF424242)       // Gris oscuro
val NeutralGray20 = Color(0xFF212121)       // Casi negro

// === COLORES SEMÁNTICOS ===

// Estados de progreso
val ProgressLow = Color(0xFFFF5722)         // Rojo para bajo progreso
val ProgressMedium = EnergyOrange60         // Naranja para progreso medio
val ProgressHigh = FitnessGreen60           // Verde para buen progreso
val ProgressExcellent = Color(0xFF4CAF50)   // Verde intenso para excelente

// Estados de salud
val HealthExcellent = FitnessGreen40        // Verde para salud excelente
val HealthGood = FitnessGreen60             // Verde claro para buena salud
val HealthWarning = EnergyOrange60          // Naranja para advertencia
val HealthCritical = Color(0xFFE53935)      // Rojo para crítico

// === ESQUEMAS LEGACY (para compatibilidad) ===
val Purple80 = MotivationPurple80
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = MotivationPurple60
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// === COLORES ESPECIALES PARA COMPONENTES ===

// Gradientes predefinidos (se usan en CustomTheme.kt)
object FitnessGradients {
    val PrimaryGradient = listOf(FitnessGreen60, TechBlue60)
    val EnergyGradient = listOf(EnergyOrange60, EnergyOrange80)
    val MotivationGradient = listOf(MotivationPurple60, MotivationPurple80)
    val SuccessGradient = listOf(FitnessGreen40, FitnessGreen80)
}

// Colores para estado de actividad
object ActivityColors {
    val Sedentary = NeutralGray60
    val Light = EnergyOrange80
    val Moderate = EnergyOrange60
    val Active = FitnessGreen60
    val VeryActive = FitnessGreen40
}

// Colores para métricas de salud
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

// Colores para estados de la app
object AppStateColors {
    val Success = FitnessGreen60
    val Warning = EnergyOrange60
    val Error = ProgressLow
    val Info = TechBlue60
    val Loading = MotivationPurple60
}