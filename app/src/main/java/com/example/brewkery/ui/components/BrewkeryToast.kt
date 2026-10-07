package com.example.brewkery.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.brewkery.ui.theme.BrewkeryTheme
import com.example.brewkery.ui.theme.Espresso
import com.example.brewkery.ui.theme.WarmBorder
import kotlinx.coroutines.delay

/**
 * Toast state holder for showing custom global toast messages across screens.
 */
class ToastState {
    var message by mutableStateOf<String?>(null)
        private set

    var isVisible by mutableStateOf(false)
        private set

    var timestamp by mutableLongStateOf(0L)
        private set

    fun show(msg: String) {
        message = msg
        timestamp = System.currentTimeMillis()
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
    }
}

val LocalToastState = compositionLocalOf { ToastState() }

/**
 * Global toast host overlay displayed at the top of the app window.
 */
@Composable
fun ToastHost(
    toastState: ToastState,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(toastState.timestamp, toastState.isVisible) {
        if (toastState.isVisible) {
            delay(2200)
            toastState.dismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 16.dp)
            .zIndex(999f),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = toastState.isVisible && toastState.message != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            BrewkeryToastPill(message = toastState.message.orEmpty())
        }
    }
}

/**
 * Artisan pill toast component matching the prototype specification.
 * Dark Espresso capsule with emerald green checkmark and crisp white text.
 */
@Composable
fun BrewkeryToastPill(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            ),
        shape = CircleShape,
        color = Espresso,
        border = BorderStroke(1.dp, WarmBorder.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emerald green circular checkmark icon badge
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Espresso,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.width(9.dp))

            // Toast message
            Text(
                text = message,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFAF7)
@Composable
private fun BrewkeryToastPillPreview() {
    BrewkeryTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            BrewkeryToastPill(message = "Added 1x Toasted Caramel Macchiato to Cart")
        }
    }
}
