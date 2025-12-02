package com.soundinteractionapp.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.soundinteractionapp.Screen

/**
 * 遊戲訓練模式 (Game Mode) 的 UI 介面內容。
 * 負責顯示 2x2 的關卡選擇按鈕。
 */
@Composable
fun GameModeScreenContent(onNavigateBack: () -> Unit, onNavigateToLevel: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 頂部：返回按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("← 返回模式選擇", style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    "遊戲訓練模式 - 選擇關卡",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // 佔位，保持對齊
                Spacer(modifier = Modifier.width(100.dp).height(50.dp))
            }

            // 中間：關卡選擇區 (2x2 Grid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // 模擬 2x2 關卡網格
                repeat(2) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), // 每個 Row 平均分配高度
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(2) { colIndex ->
                            val levelIndex = rowIndex * 2 + colIndex

                            // 決定關卡路由
                            val levelRoute = when (levelIndex) {
                                0 -> Screen.GameLevel1.route
                                1 -> Screen.GameLevel2.route
                                2 -> Screen.GameLevel3.route
                                else -> Screen.GameLevel4.route
                            }

                            GameLevelButton(
                                levelNumber = levelIndex + 1,
                                levelTitle = when (levelIndex) {
                                    0 -> "跟著拍拍手"
                                    1 -> "找出小動物"
                                    2 -> "音階高低"
                                    else -> "創作小樂曲"
                                },
                                onClick = { onNavigateToLevel(levelRoute) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 遊戲模式中的單個關卡按鈕 (2x2 網格)。
 */
@Composable
fun RowScope.GameLevelButton(levelNumber: Int, levelTitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .weight(1f) // 每個按鈕平均分配寬度
            .fillMaxHeight()
            .padding(16.dp), // 內部間隔
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "關卡 ${levelNumber}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = levelTitle,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}