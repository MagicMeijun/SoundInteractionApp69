package com.soundinteractionapp

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalTextStyle
import kotlinx.coroutines.delay

// 確保 R 類別可以被識別
import com.soundinteractionapp.R

// =======================================================
// 自由探索模式 (Free Play)
// =======================================================

/**
 * 自由探索模式 (Free Play) 的 UI 介面內容。 (FreePlayScreenContent)
 */
@Composable
fun FreePlayScreenContent(onNavigateBack: () -> Unit, soundManager: SoundManager, onNavigateToCatInteraction: () -> Unit) {

    // 狀態管理：追蹤當前啟動視覺效果的按鈕 ID
    var activeEffectButtonId by remember { mutableStateOf<Int?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 頂部控制列
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按鈕
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("← 返回模式選擇", style = MaterialTheme.typography.bodyLarge)
                }

                // 震動開關按鈕 (已移除，改為 Spacer 保持佈局對稱)
                Spacer(modifier = Modifier.width(150.dp))
            }

            // 中間：9 個聲音互動按鈕 (3x3 Grid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 佔據剩餘空間
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // 模擬 3x3 網格
                repeat(3) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), // 每個 Row 平均分配高度
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { colIndex ->
                            val buttonId = rowIndex * 3 + colIndex

                            // 獲取聲音數據（包含名稱、圖案、資源ID）
                            val soundData = getSoundInteractionData(buttonId)

                            // 聲音按鈕
                            SoundInteractionButton(
                                soundName = soundData.name,
                                icon = soundData.icon,
                                isActive = activeEffectButtonId == buttonId,
                                onClick = {
                                    if (buttonId == 0) {
                                        // 這是唯一的實作按鈕：導航到貓咪互動畫面
                                        onNavigateToCatInteraction()
                                    } else {
                                        // 這是佔位按鈕的點擊邏輯
                                        // 2. 觸發視覺回饋 (設置狀態)
                                        activeEffectButtonId = buttonId
                                        // 3. 觸覺回饋 (震動) 邏輯已移除
                                    }
                                }
                            )

                            // 視覺效果重置：在效果結束後重置 activeEffectButtonId
                            LaunchedEffect(activeEffectButtonId) {
                                if (activeEffectButtonId != null) {
                                    kotlinx.coroutines.delay(200) // 效果持續 200ms
                                    activeEffectButtonId = null
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 自由探索模式中的單個聲音互動按鈕 (高對比度、大尺寸)。
 */
@Composable
fun RowScope.SoundInteractionButton(soundName: String, icon: @Composable () -> Unit, isActive: Boolean, onClick: () -> Unit) {
    // 按鈕按下時的縮放動畫
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 視覺回饋：脈衝效果 (Pulse Effect)
    val scale = animateFloatAsState(
        targetValue = if (isActive || isPressed) 1.05f else 1.0f, // 點擊或激活時放大 5%
        animationSpec = tween(durationMillis = 150),
        label = "interactionScale"
    )

    Card(
        onClick = onClick,
        // 傳遞 InteractionSource 才能追蹤按壓狀態
        interactionSource = interactionSource,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(8.dp)
            .scale(scale.value), // 應用縮放動畫
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 視覺回饋：圖示
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp, color = Color.White)) {
                    icon()
                }
                Spacer(modifier = Modifier.height(8.dp))
                // 文字標籤
                Text(
                    text = soundName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =======================================================
// 數據結構與輔助函數
// =======================================================

/** 聲音互動數據模型 */
data class SoundData(val name: String, val resId: Int, val icon: @Composable () -> Unit)

/** 根據 ID 獲取 Free Play 模式的聲音數據 */
@Composable
fun getSoundInteractionData(id: Int): SoundData {
    // 只有 ID 0 (貓咪) 有實作功能和資源引用，其他按鈕都是佔位符
    return when (id) {
        0 -> SoundData("貓咪", R.raw.cat_meow, { Text("🐾") }) // 唯一實作的按鈕
        else -> SoundData("開發中", 0, { Text("🛠️") }) // 其他 8 個按鈕使用佔位圖標
    }
}

