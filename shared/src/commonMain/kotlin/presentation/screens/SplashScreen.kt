package presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.icon
import flashlight.shared.generated.resources.splashScreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(onAnimationDone: () -> Unit) {
    // Анимация прозрачности: 0f (темнота) -> 1f (горит) -> 0f (плавное затухание)
    val splashAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "neon_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        // 1. Плавно проявляем логотип из темноты
        splashAlpha.animateTo(1f, tween(750, easing = LinearOutSlowInEasing))

        // 2. Ждем, пока WarmupActivity сделает свою работу в onStop (600 мс)
        // В это время логотип продолжает красиво и плавно пульсировать!
        delay(300)

        // 3. ПЛАВНОЕ ЗАТУХАНИЕ: уводим прозрачность всего экрана в 0 за 400 мс
        splashAlpha.animateTo(0f, tween(750, easing = FastOutLinearInEasing))

        // 4. Только теперь, когда всё полностью растворилось в темноте, переключаем экран
        onAnimationDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // Применяем общую прозрачность ко всему экрану заставки для эффекта Fade Out
            .graphicsLayer { alpha = splashAlpha.value },
        contentAlignment = Alignment.Center
    ) {
        // 1. ЗАДНИЙ ФОН: Неоновый шлейф (свечение) на весь экран
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { this.blendMode = BlendMode.Screen }
                .scale(pulseScale * 1.8f)
                .blur(60.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.splashScreen),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center).size(300.dp),
                alpha = 0.5f // Держим постоянную мягкость, затухание сделает графический слой выше
            )
        }

        // 2. ПЕРЕДНИЙ ФОН: Сам четкий логотип по центру
        Image(
            painter = painterResource(Res.drawable.splashScreen),
            contentDescription = "Focus Logo",
            modifier = Modifier
                .size(280.dp)
                .scale(pulseScale)
                .graphicsLayer { this.blendMode = BlendMode.Screen }
        )
    }
}