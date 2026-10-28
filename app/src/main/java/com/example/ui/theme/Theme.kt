package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFF48FB1),
  onPrimary = DarkPlum,
  primaryContainer = Color(0xFF67143A),
  onPrimaryContainer = Color(0xFFFFD9E4),
  secondary = ChampagneGold,
  onSecondary = Color(0xFF382300),
  secondaryContainer = Color(0xFF553805),
  onSecondaryContainer = Color(0xFFFFE0B2),
  tertiary = Color(0xFFE29578),
  background = PlumDark,
  surface = PlumDark,
  surfaceVariant = PlumCardDark,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = BorderSubtleDark
)

private val LightColorScheme = lightColorScheme(
  primary = RoseWine,
  onPrimary = Color.White,
  primaryContainer = SoftBlush,
  onPrimaryContainer = RoseWineDark,
  secondary = ChampagneGold,
  onSecondary = Color.White,
  secondaryContainer = WarmSand,
  onSecondaryContainer = Color(0xFF4A3416),
  tertiary = Color(0xFFB85D43),
  background = CreamWhite,
  surface = CreamWhite,
  surfaceVariant = Color(0xFFF9F3F6),
  onBackground = TextPrimaryLight,
  onSurface = TextPrimaryLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = BorderSubtleLight
)

@Composable
fun GlowSwapTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our branded luxury theme consistently
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
