package com.soundinteractionapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soundinteractionapp.R
import com.soundinteractionapp.SoundManager
import com.soundinteractionapp.data.SoundData
import kotlinx.coroutines.delay

@Composable
fun FreePlayScreenContent(
    onNavigateBack: () -> Unit,
    soundManager: SoundManager,
    // 只保留動物和樂器的導航參數
    onNavigateToCatInteraction: () -> Unit,
    onNavigateToPianoInteraction: () -> Unit,
    onNavigateToDogInteraction: () -> Unit,
    onNavigateToBirdInteraction: () -> Unit,
    onNavigateToDrumInteraction: () -> Unit,
    onNavigateToBellInteraction: () -> Unit

) {
    var activeEffectButtonId by remember { mutableStateOf<Int?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 頂部控制列
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("← 返回模式選擇", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.width(150.dp))
            }

            // 中間：6 個聲音互動按鈕 (改為 2 排)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // ✅ 修改：這裡改為 repeat(2)，只顯示前兩排
                repeat(2) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { colIndex ->
                            val buttonId = rowIndex * 3 + colIndex
                            val soundData = getSoundInteractionData(buttonId)

                            SoundInteractionButton(
                                // ✅ 這裡非常重要：加上 modifier 讓按鈕填滿格子
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                soundName = soundData.name,
                                icon = soundData.icon,
                                isActive = activeEffectButtonId == buttonId,
                                onClick = {
                                    when (buttonId) {
                                        0 -> onNavigateToCatInteraction()     // 貓咪
                                        1 -> onNavigateToDogInteraction()     // 狗狗
                                        2 -> onNavigateToBirdInteraction()    // 鳥兒

                                        3 -> onNavigateToPianoInteraction()   // 鋼琴
                                        4 -> onNavigateToDrumInteraction()    // 爵士鼓
                                        5 -> onNavigateToBellInteraction()    // 鈴鐺

                                        // ❌ 已移除：6, 7, 8 的跳轉邏輯

                                        else -> {
                                            activeEffectButtonId = buttonId
                                            soundManager.playSound(soundData.resId)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // 視覺效果重置
            LaunchedEffect(activeEffectButtonId) {
                if (activeEffectButtonId != null) {
                    delay(200)
                    activeEffectButtonId = null
                }
            }
        }
    }
}

@Composable
fun getSoundInteractionData(id: Int): SoundData {
    return when (id) {
        // 第一排：動物
        0 -> SoundData("貓咪", R.raw.cat_meow, { Text("🐾") })
        1 -> SoundData("狗狗", R.raw.dog_barking, { Text("🐕") })
        2 -> SoundData("鳥兒", R.raw.bird_sound, { Text("🐦") })

        // 第二排：樂器
        3 -> SoundData("鋼琴", R.raw.piano_c1, { Text("🎹") })
        4 -> SoundData("爵士鼓", R.raw.drum_cymbal_closed, { Text("🥁") })
        5 -> SoundData("鈴鐺", R.raw.desk_bell, { Text("🔔") })

        // ❌ 已移除：第三排自然聲音 (移至 RelaxScreen)

        else -> SoundData("未知", R.raw.cat_meow, { Text("⛔") })
    }
}