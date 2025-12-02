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
    onNavigateToCatInteraction: () -> Unit,
    onNavigateToPianoInteraction: () -> Unit,
    onNavigateToDogInteraction: () -> Unit,
    onNavigateToBirdInteraction: () -> Unit,
    onNavigateToDrumInteraction: () -> Unit,
    onNavigateToBellInteraction: () -> Unit,
    onNavigateToOceanInteraction: () -> Unit,
    onNavigateToRainInteraction: () -> Unit,
    onNavigateToWindInteraction: () -> Unit
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

            // 中間：6 個聲音互動按鈕 (2行 x 3列)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 48.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
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

        else -> SoundData("未知", R.raw.cat_meow, { Text("⛔") })
    }
}