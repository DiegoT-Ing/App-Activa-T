package com.example.activat.ui.theme.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object HapticFeedbackManager {

    fun triggerImpact(context: Context, intensity: HapticIntensity = HapticIntensity.MEDIUM) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        vibrator?.let {
            val effect = when (intensity) {
                HapticIntensity.LIGHT -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticIntensity.MEDIUM -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticIntensity.STRONG -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            it.vibrate(effect)
        }
    }

    fun triggerSuccess(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        vibrator?.let {
            val pattern = VibrationEffect.createWaveform(
                longArrayOf(0, 100, 50, 100),
                intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE),
                -1
            )
            it.vibrate(pattern)
        }
    }

    fun triggerStart(context: Context) {
        triggerImpact(context, HapticIntensity.MEDIUM)
    }

    fun triggerStop(context: Context) {
        triggerImpact(context, HapticIntensity.STRONG)
    }
}

enum class HapticIntensity {
    LIGHT, MEDIUM, STRONG
}

@Composable
fun rememberHapticFeedback(): HapticFeedbackHelper {
    val context = LocalContext.current
    return HapticFeedbackHelper(context)
}

class HapticFeedbackHelper(private val context: Context) {
    fun light() = HapticFeedbackManager.triggerImpact(context, HapticIntensity.LIGHT)
    fun medium() = HapticFeedbackManager.triggerImpact(context, HapticIntensity.MEDIUM)
    fun strong() = HapticFeedbackManager.triggerImpact(context, HapticIntensity.STRONG)
    fun success() = HapticFeedbackManager.triggerSuccess(context)
    fun start() = HapticFeedbackManager.triggerStart(context)
    fun stop() = HapticFeedbackManager.triggerStop(context)
}