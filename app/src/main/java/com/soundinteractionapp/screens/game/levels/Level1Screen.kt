package com.soundinteractionapp.screens.game.levels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soundinteractionapp.SoundManager // 確保引用正確
import com.soundinteractionapp.utils.GameInputManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

// 定義音符資料結構
data class Note(
    val id: Long,
    val targetTime: Long, // 預計被擊中的時間 (毫秒)
    var isHit: Boolean = false // 是否已被擊中
)

@Composable
fun Level1FollowBeatScreen(
    onNavigateBack: () -> Unit,
    soundManager: SoundManager // 接收 SoundManager 以播放音效
) {

    // --- 1. 遊戲參數設定 ---
    val noteSpeed = 0.5f // 音符飛行速度 (像素/毫秒)
    val judgeLineX = 200f // 判定線 X 座標

    // --- 2. 遊戲狀態 ---
    var score by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("Ready?") }

    // 產生假譜面 (每 800ms 一個音符，共 50 個)
    val totalNotes = remember {
        mutableStateListOf<Note>().apply {
            for (i in 1..50) {
                add(Note(id = i.toLong(), targetTime = 2000L + (i * 800L)))
            }
        }
    }

    // 遊戲計時器
    var startTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }

    // --- 3. 啟動遊戲迴圈 (Game Loop) ---
    LaunchedEffect(Unit) {
        delay(1000) // 倒數緩衝
        startTime = System.currentTimeMillis()
        isPlaying = true

        while (isPlaying) {
            currentTime = System.currentTimeMillis() - startTime

            // 檢查 Miss (音符跑過頭還沒打)
            totalNotes.forEach { note ->
                // 邏輯：未擊中 且 超過判定線 200ms
                if (!note.isHit && (currentTime - note.targetTime > 200)) {
                    note.isHit = true
                    combo = 0
                    feedbackText = "Miss"

                    // [音效] 播放 Miss 聲音
                    soundManager.playSFX("miss")
                }
            }

            // 每一幀刷新 (約 60FPS)
            withFrameMillis { }
        }
    }

    // --- 4. 接收按鍵訊號 (Input Listener) ---
    LaunchedEffect(Unit) {
        // 監聽來自 MainActivity 的訊號
        GameInputManager.keyEvents.collectLatest {

            // 找出最接近判定時間的那個「未擊中」音符 (誤差範圍 150ms 內)
            val targetNote = totalNotes.firstOrNull { note ->
                !note.isHit && abs(note.targetTime - currentTime) < 150
            }

            if (targetNote != null) {
                val diff = abs(targetNote.targetTime - currentTime)
                targetNote.isHit = true // 標記為已擊中

                // 判定標準
                if (diff < 60) {
                    score += 100
                    combo++
                    feedbackText = "PERFECT!"

                    // [音效] 播放 Perfect 聲音
                    soundManager.playSFX("perfect")
                } else {
                    score += 50
                    combo++
                    feedbackText = "Good"

                    // [音效] 播放 Good 聲音
                    soundManager.playSFX("good")
                }
            } else {
                // 空揮 (沒有打到音符)
                // 可選擇是否播放一個空的打擊聲，例如 "don"
            }
        }
    }

    // --- 5. 畫面繪製 (UI) ---
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF222222) // 深色背景
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 資訊顯示 (分數、Combo)
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Score: $score", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Combo: $combo", style = MaterialTheme.typography.displayMedium, color = Color.Yellow)
                Text(feedbackText, style = MaterialTheme.typography.headlineLarge, color = Color.Cyan)
            }

            // 遊戲主畫面 Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2

                // 畫軌道
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, centerY),
                    end = Offset(size.width, centerY),
                    strokeWidth = 4f
                )

                // 畫判定圈
                drawCircle(
                    color = Color.White,
                    radius = 60f,
                    center = Offset(judgeLineX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                )

                // 畫音符
                totalNotes.forEach { note ->
                    if (!note.isHit) {
                        // 計算位置
                        val noteX = judgeLineX + (note.targetTime - currentTime) * noteSpeed

                        // 只畫螢幕內的音符
                        if (noteX > -100 && noteX < size.width + 100) {
                            drawCircle(
                                color = Color(0xFFFF5252),
                                radius = 40f,
                                center = Offset(noteX, centerY)
                            )
                            // 音符白框
                            drawCircle(
                                color = Color.White,
                                radius = 40f,
                                center = Offset(noteX, centerY),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                            )
                        }
                    }
                }
            }

            // 返回按鈕
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text("退出")
            }
        }
    }
}