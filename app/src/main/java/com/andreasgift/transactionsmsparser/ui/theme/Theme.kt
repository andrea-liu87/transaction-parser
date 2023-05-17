package live.onedata.vo.tablet.ptt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val DarkColorScheme = lightColorScheme(
    primary = PrimaryMain,
    onPrimary = Color.White,
    secondary = SecondaryMain,
    onSecondary = OnSecondaryMain,
    background = PrimaryMain,
    onBackground = PrimaryMain,
    surface = SurfaceMain,
    onSurface = Color.Black,
    error = Error,
    onError = Color.White
)

@Composable
fun TransactionParserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}