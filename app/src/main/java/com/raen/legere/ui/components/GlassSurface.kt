package com.raen.legere.ui.components

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import com.raen.legere.utils.defaultSharedPreferences
import com.raen.legere.utils.getPrefBoolean
import com.raen.legere.utils.getPrefInt
import com.raen.legere.utils.getPrefString

val LocalHazeState = staticCompositionLocalOf { HazeState() }

@Immutable
data class GlassStyle(
    val containerAlpha: Float,
    val borderAlpha: Float,
    val tonalElevation: Dp,
    val shadowElevation: Dp,
)

object GlassDefaults {
    val shape: Shape = RoundedCornerShape(24.dp)

    @Composable
    fun subtleStyle(): GlassStyle = GlassStyle(
        containerAlpha = 0.72f,
        borderAlpha = 0.18f,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    )

    @Composable
    fun regularStyle(): GlassStyle = GlassStyle(
        containerAlpha = 0.82f,
        borderAlpha = 0.24f,
        tonalElevation = 0.dp,
        shadowElevation = 6.dp,
    )

    @Composable
    fun prominentStyle(): GlassStyle = GlassStyle(
        containerAlpha = 0.88f,
        borderAlpha = 0.30f,
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
    )
}

@Composable
fun <T> rememberPreferenceState(key: String, defaultValue: T, getValue: (String, T) -> T): MutableState<T> {
    val context = LocalContext.current
    val state = remember(key) { mutableStateOf(getValue(key, defaultValue)) }
    DisposableEffect(key) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                state.value = getValue(key, defaultValue)
            }
        }
        context.defaultSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            context.defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    style: GlassStyle = GlassDefaults.regularStyle(),
    shape: Shape = GlassDefaults.shape,
    dialogSurface: Boolean = false,
    isReaderSurface: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val context = LocalContext.current
    val isGlassEnabled by rememberPreferenceState("kisara_frosted_glass", true) { k, d ->
        context.getPrefBoolean(k, d)
    }
    val hazeOpacity by rememberPreferenceState(if (isReaderSurface) "reader_app_bar_opacity" else "bottom_bar_opacity", 80) { k, d ->
        context.getPrefInt(k, d)
    }
    val hazeBlur by rememberPreferenceState("bottom_bar_blur", 24) { k, d ->
        context.getPrefInt(k, d)
    }
    val mixColorTypeStr by rememberPreferenceState("kisara_glass_color_type", "0") { k, d ->
        context.getPrefString(k, d) ?: d
    }
    val mixColorType = mixColorTypeStr.toIntOrNull() ?: 0
    val mixColorRatioVal by rememberPreferenceState(if (isReaderSurface) "reader_app_bar_mix" else "kisara_glass_color_mix", 0) { k, d ->
        context.getPrefInt(k, d)
    }
    val mixCustomColorVal by rememberPreferenceState("kisara_glass_custom_color", Color.White.toArgb()) { k, d ->
        context.getPrefInt(k, d)
    }

    val hazeState = LocalHazeState.current
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = colorScheme.background.luminance() < 0.5f

    val mixRatio = mixColorRatioVal.coerceIn(0, 100) / 100f
    val mixColor = when (mixColorType) {
        1 -> colorScheme.primary
        2 -> colorScheme.surface
        3 -> Color.Black
        4 -> Color.White
        5 -> Color(mixCustomColorVal)
        else -> null
    }

    val preferenceAlpha = (hazeOpacity.coerceIn(0, 100)) / 100f
    val effectiveContainerAlpha = (preferenceAlpha * style.containerAlpha).coerceIn(0f, 1f)
    val adjustedContainerAlpha = if (mixColor != null) {
        effectiveContainerAlpha + (0.95f - effectiveContainerAlpha) * mixRatio
    } else {
        effectiveContainerAlpha
    }

    // Base color formula
    val baseColor = when {
        adjustedContainerAlpha >= 0.86f -> colorScheme.surfaceVariant
        adjustedContainerAlpha >= 0.80f -> colorScheme.surfaceVariant
        else -> colorScheme.surfaceVariant
    }.let { candidate ->
        if (isDarkTheme) lerp(candidate, colorScheme.inverseOnSurface, 0.16f) else candidate
    }.let { candidate ->
        if (mixColor != null && mixRatio > 0f) lerp(candidate, mixColor, mixRatio) else candidate
    }
    val containerColor = baseColor.copy(alpha = adjustedContainerAlpha)

    // Base blur radius formula
    val baseBlurRadius = when {
        style.shadowElevation >= 10.dp -> 28.dp
        style.shadowElevation >= 6.dp -> 24.dp
        else -> 18.dp
    }
    val blurRadius = hazeBlur.coerceIn(0, 80).dp
        .takeIf { it > 0.dp }
        ?: baseBlurRadius

    // Tint alpha
    val tintAlpha = ((preferenceAlpha * 0.22f) + (style.containerAlpha * 0.14f)).coerceIn(0.18f, 0.38f)
        .let { alpha ->
            if (isDarkTheme) (alpha + 0.10f).coerceAtMost(0.50f) else alpha
        }
    val adjustedTintAlpha = if (mixColor != null) {
        tintAlpha + (0.75f - tintAlpha) * mixRatio
    } else {
        tintAlpha
    }
    val baseTintColor = baseColor.copy(alpha = adjustedTintAlpha)

    val border = BorderStroke(
        width = 1.dp,
        color = colorScheme.outlineVariant.copy(
            alpha = if (isDarkTheme) style.borderAlpha.coerceIn(0.16f, 0.28f) else style.borderAlpha.coerceAtMost(0.18f),
        ),
    )

    val useRuntimeHaze = isGlassEnabled

    val hazeStyle = remember(blurRadius, baseTintColor, containerColor, dialogSurface) {
        HazeStyle(
            backgroundColor = Color.Transparent,
            tint = HazeDefaults.tint(baseTintColor),
            blurRadius = blurRadius,
            noiseFactor = 0.03f, // premium frosted feel noise
        )
    }

    val hazeBackgroundColor = if (dialogSurface) Color.Transparent else containerColor
    val surfaceColor = if (useRuntimeHaze && !dialogSurface) Color.Transparent else containerColor

    CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
        Surface(
            modifier = if (useRuntimeHaze) {
                modifier
                    .clip(shape)
                    .hazeChild(hazeState, hazeStyle) {
                        backgroundColor = hazeBackgroundColor
                        blurredEdgeTreatment = BlurredEdgeTreatment(shape)
                        clipToAreasBounds = false
                        expandLayerBounds = false
                        forceInvalidateOnPreDraw = true
                    }
            } else {
                modifier
            },
            shape = shape,
            color = surfaceColor,
            contentColor = colorScheme.onSurface,
            tonalElevation = style.tonalElevation,
            shadowElevation = if (useRuntimeHaze && !isReaderSurface) 0.dp else style.shadowElevation,
            border = if (useRuntimeHaze && !isReaderSurface) null else border,
        ) {
            Box(content = content)
        }
    }
}
