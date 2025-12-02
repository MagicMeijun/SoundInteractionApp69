package com.soundinteractionapp.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 自由探索模式中的單個聲音互動按鈕。
 */
@Composable
fun RowScope.SoundInteractionButton(
    // ▼▼▼ 關鍵修改：新增標準 modifier 參數 ▼▼▼
    modifier: Modifier = Modifier,
    // ▲▲▲ 關鍵修改：新增標準 modifier 參數 ▲▲▲
    soundName: String,
    icon: @Composable () -> Unit,
    isActive: Boolean,
    onClick: () -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isActive || isPressed) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "interactionScale"
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        // ▼▼▼ 關鍵修改：整合外部傳入的 modifier ▼▼▼
        modifier = Modifier
            .weight(1f) // RowScope 擴充屬性 (必須留著)
            .fillMaxHeight()
            .padding(8.dp)
            .then(modifier) // 套用外部傳入的 modifier
            .scale(scale.value),
        // ▲▲▲ 關鍵修改：整合外部傳入的 modifier ▲▲▲
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ⭐ icon 比較小
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.displayLarge.copy(
                        fontSize = 36.sp,
                        color = Color.White
                    )
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ⭐ 字體縮小
                Text(
                    text = soundName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}