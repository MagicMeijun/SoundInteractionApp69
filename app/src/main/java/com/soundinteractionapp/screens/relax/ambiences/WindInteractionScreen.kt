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
fun WindInteractionScreen(
    onNavigateBack: () -> Unit,
    soundManager: SoundManager
) {
    val context = LocalContext.current

    // 狀態：是否正在播放
    var isPlaying by remember { mutableStateOf(false) }

    // 1. 背景音效 (wind_sound.mp3)
    val audioPlayer = remember {
        try {
            MediaPlayer.create(context, R.raw.wind_sound).apply {
                isLooping = true
                setVolume(0.6f, 0.6f)
            }
        } catch (e: Exception) {
            null
        }
    }

    // 2. 影片播放器 (wind_video.mp4) - 靜音
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.wind_video}")
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f // 靜音
            prepare()
        }
    }

    // 生命週期管理
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            audioPlayer?.release()
        }
    }

    // 同步控制
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

    // UI
    Box(modifier = Modifier.fillMaxSize()) {

        // 影片層
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

        // 互動層
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!isPlaying) {
                        isPlaying = true
                    }
                }
        ) {
            if (!isPlaying) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "點擊畫面感受微風",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // 返回按鈕
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

        // 暫停按鈕
        if (isPlaying) {
            Button(
                onClick = { isPlaying = false },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            ) {
                Text("暫停風聲")
            }
        }
    }
}