package com.soundinteractionapp.screens.relax.ambiences

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.soundinteractionapp.R
import com.soundinteractionapp.SoundManager

@OptIn(UnstableApi::class)
@Composable
fun RainInteractionScreen(
    onNavigateBack: () -> Unit,
    soundManager: SoundManager
) {
    val context = LocalContext.current

    // 狀態：是否正在播放
    var isPlaying by remember { mutableStateOf(false) }

    // --- 1. 背景音效播放器 (rain_sound.mp3) ---
    val audioPlayer = remember {
        try {
            // 【修改點】使用雨的聲音檔案
            MediaPlayer.create(context, R.raw.rain_sound).apply {
                isLooping = true // 循環播放
                setVolume(0.6f, 0.6f) // 設定音量
            }
        } catch (e: Exception) {
            null
        }
    }

    // --- 2. 影片播放器 (ExoPlayer - 負責畫面) ---
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // 【修改點】使用雨的影片檔案
            val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.rain_video}")
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ONE // 影片循環
            volume = 0f // 影片設為靜音
            prepare()
        }
    }

    // --- 3. 生命週期管理 & 同步控制 ---
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()     // 釋放影片
            audioPlayer?.release()  // 釋放音樂
        }
    }

    // 當 isPlaying 改變時，同時控制「影片」和「音樂」
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
            audioPlayer?.start()
        } else {
            exoPlayer.pause()
            if (audioPlayer?.isPlaying == true) {
                audioPlayer.pause()
            }
        }
    }

    // --- 4. 畫面 UI ---
    Box(modifier = Modifier.fillMaxSize()) {

        // (A) 影片層 (無聲背景)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // (B) 透明互動層
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (isPlaying) {
                        // 播放中點擊不做任何事 (依照你的需求)
                    } else {
                        // 尚未播放時，點擊 -> 開始
                        isPlaying = true
                    }
                }
        ) {
            // 提示文字
            if (!isPlaying) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        // 【修改點】文字改為雨聲
                        text = "點擊畫面感受雨聲",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // (C) 返回按鈕 (保持一致樣式)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = onNavigateBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.height(50.dp)
            ) {
                Text("← 返回自由探索", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // (D) 暫停按鈕
        if (isPlaying) {
            Button(
                onClick = { isPlaying = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            ) {
                // 【修改點】文字改為暫停雨聲
                Text("暫停雨聲")
            }
        }
    }
}