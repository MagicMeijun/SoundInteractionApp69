package com.soundinteractionapp.screens.relax

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soundinteractionapp.R
import com.soundinteractionapp.SoundManager
import com.soundinteractionapp.components.SoundInteractionButton
import com.soundinteractionapp.data.SoundData
import kotlinx.coroutines.delay

@Composable
fun RelaxScreenContent(
    onNavigateBack: () -> Unit,
    soundManager: SoundManager,
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

            // 中間：3 個環境音互動按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 雨聲
                val rainData = SoundData("雨聲", R.raw.rain_sound) { Text("🌧️") }
                SoundInteractionButton(
                    soundName = rainData.name,
                    icon = rainData.icon,
                    isActive = activeEffectButtonId == 0,
                    onClick = { onNavigateToRainInteraction() }
                )

                // 海浪
                val oceanData = SoundData("海浪", R.raw.wave_sound) { Text("🌊") }
                SoundInteractionButton(
                    soundName = oceanData.name,
                    icon = oceanData.icon,
                    isActive = activeEffectButtonId == 1,
                    onClick = { onNavigateToOceanInteraction() }
                )

                // 微風
                val windData = SoundData("微風", R.raw.wind_sound) { Text("🍃") }
                SoundInteractionButton(
                    soundName = windData.name,
                    icon = windData.icon,
                    isActive = activeEffectButtonId == 2,
                    onClick = { onNavigateToWindInteraction() }
                )
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