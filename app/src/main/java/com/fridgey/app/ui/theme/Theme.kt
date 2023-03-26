package com.fridgey.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.fridgey.app.R

private val DarkColorPalette = darkColors(
        primary = FridgeyBlue,
        primaryVariant = FridgeyAccent,
        secondary = Teal200
)

private val LightColorPalette = lightColors(
        primary = FridgeyBlue,
        primaryVariant = FridgeyAccent,
        secondary = Teal200

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun FridgeyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}

val caviarFamily = FontFamily(
    Font(R.font.caviardreams, FontWeight.Normal),
    Font(R.font.caviardreams_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.caviardreams_bold, FontWeight.Bold)
)

@Composable
fun FridgeyText(t : String, modifier: Modifier = Modifier, isBold : Boolean = false) {
    if (isBold)
        Text(text = t, fontSize = 22.sp, fontFamily = caviarFamily, fontWeight = FontWeight.Bold, modifier = modifier)
    else
        Text(text = t, fontSize = 22.sp, fontFamily = caviarFamily, modifier = modifier)
}

@Composable
fun HomeText(t : String, isBold : Boolean = false, fSize : Int = 19, align: TextAlign = TextAlign.Center) {
    if (isBold)
        Text(text = t, fontSize = fSize.sp, fontFamily = caviarFamily, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = align)
    else
        Text(text = t, fontSize = fSize.sp, fontFamily = caviarFamily, color = Color.Black, textAlign = align)
}