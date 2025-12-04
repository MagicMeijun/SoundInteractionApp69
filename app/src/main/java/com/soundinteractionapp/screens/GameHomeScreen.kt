package com.soundinteractionapp.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset as GeometryOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.soundinteractionapp.R
import com.soundinteractionapp.data.AuthViewModel
import com.soundinteractionapp.data.ProfileViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// =====================================================
// 🎵 音效管理 SoundManager
// =====================================================
class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<Int, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap[R.raw.settings] = soundPool.load(context, R.raw.settings, 1)
        soundMap[R.raw.cancel] = soundPool.load(context, R.raw.cancel, 1)
        soundMap[R.raw.options2] = soundPool.load(context, R.raw.options2, 1)
    }

    fun play(soundResId: Int) {
        soundMap[soundResId]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}

// =====================================================
// 🏠 主畫面 GameHomeScreen
// =====================================================
@Composable
fun GameHomeScreen(
    onNavigateToFreePlay: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val isLoggingOut = remember { mutableStateOf(false) }

    val blackAlpha by animateFloatAsState(
        targetValue = if (isLoggingOut.value) 1f else 0f,
        animationSpec = tween(700),
        finishedListener = {
            if (isLoggingOut.value) {
                onLogout()
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }

    var currentIndex by remember { mutableStateOf(1) }

    val modes = listOf(
        ModeData("自由探索", "模式一", Icons.Default.Search, Color(0xFF8C7AE6), onNavigateToFreePlay),
        ModeData("放鬆時光", "模式二", Icons.Filled.FavoriteBorder, Color(0xFF4FC3F7), onNavigateToRelax),
        ModeData("音樂遊戲", "模式三", Icons.Filled.PlayArrow, Color(0xFFFF9800), onNavigateToGame)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
            TopInfoBar(
                soundManager = soundManager,
                onNavigateToProfile = onNavigateToProfile,
                onLogoutStart = {
                    soundManager.play(R.raw.cancel)
                    isLoggingOut.value = true
                },
                authViewModel = authViewModel
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 60.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f).padding(end = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "樂之聲 Logo",
                            modifier = Modifier.size(90.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "樂之聲",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                                ),
                                letterSpacing = (-1.5).sp,
                                shadow = Shadow(
                                    color = Color.White,
                                    offset = GeometryOffset(0f, 0f),
                                    blurRadius = 6f
                                )
                            ),
                            maxLines = 1,
                            softWrap = false
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "讓每位孩子都能聽見快樂的聲音\n專為心智障礙孩童設計的音樂互動 App",
                            fontSize = 12.sp,
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    SwipeableCardCarousel(
                        soundManager = soundManager,
                        modes = modes,
                        currentIndex = currentIndex,
                        onIndexChange = { currentIndex = it }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = blackAlpha }
                .background(Color.Black)
        )
    }
}

// =====================================================
// 🔝 上方資訊欄（顯示真實用戶名和頭像）
// =====================================================
@Composable
fun TopInfoBar(
    soundManager: SoundManager,
    onNavigateToProfile: () -> Unit,
    onLogoutStart: () -> Unit,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    val isAnonymous = authViewModel.isAnonymous()
    val userProfile by profileViewModel.userProfile.collectAsState()  // ✅ 監聽變化

    // ✅ 每次進入畫面都重新載入資料
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "樂之聲",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF673AB7),
                modifier = Modifier.weight(1f)
            )

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFE8EAF6))
                        .clickable {
                            soundManager.play(R.raw.settings)
                            showDropdownMenu = !showDropdownMenu
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // 顯示用戶頭像
                    if (!isAnonymous && userProfile.photoUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(userProfile.photoUrl),
                            contentDescription = "頭像",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            if (isAnonymous) "訪客" else "用戶",
                            tint = Color(0xFF673AB7),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    // ✅ 顯示最新的暱稱
                    Text(
                        text = if (isAnonymous) "訪客" else userProfile.displayName,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1
                    )

                    Icon(
                        Icons.Filled.ArrowDropDown,
                        "下拉",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false },
                    modifier = Modifier.width(180.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Person,
                                    "個人資料",
                                    tint = Color(0xFF673AB7),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text("個人資料", fontSize = 14.sp)
                            }
                        },
                        onClick = {
                            soundManager.play(R.raw.settings)
                            showDropdownMenu = false
                            onNavigateToProfile()
                        }
                    )

                    Divider(color = Color(0xFFE0E0E0))

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.ExitToApp,
                                    "登出",
                                    tint = Color(0xFFE57373),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text("登出", fontSize = 14.sp, color = Color(0xFFE57373))
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            onLogoutStart()
                        }
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Icon(
                Icons.Filled.Settings,
                "設定",
                tint = Color(0xFF673AB7),
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        soundManager.play(R.raw.settings)
                    }
            )
        }
    }
}

// =====================================================
// 📌 卡片輪播
// =====================================================
@Composable
fun SwipeableCardCarousel(
    soundManager: SoundManager,
    modes: List<ModeData>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        finishedListener = { isAnimating = false }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .pointerInput(currentIndex) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (!isAnimating) {
                            if (offsetX > 80 && currentIndex > 0) {
                                isAnimating = true
                                soundManager.play(R.raw.options2)
                                onIndexChange(currentIndex - 1)
                            } else if (offsetX < -80 && currentIndex < modes.size - 1) {
                                isAnimating = true
                                soundManager.play(R.raw.options2)
                                onIndexChange(currentIndex + 1)
                            }
                            offsetX = 0f
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (!isAnimating)
                            offsetX = (offsetX + dragAmount).coerceIn(-200f, 200f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        modes.forEachIndexed { index, mode ->
            val offset = index - currentIndex
            if (offset != 0) ModeCardSwiper(mode, offset, animatedOffset, false)
        }

        modes.getOrNull(currentIndex)?.let {
            ModeCardSwiper(it, 0, animatedOffset, true)
        }
    }
}

// =====================================================
// 🃏 卡片
// =====================================================
@Composable
fun ModeCardSwiper(mode: ModeData, offset: Int, dragOffset: Float, isCenter: Boolean) {
    val scale by animateFloatAsState(if (isCenter) 1f else 0.8f, tween(300))
    val translationX = offset * 180f + dragOffset
    val rotationY = (translationX / 25f).coerceIn(-20f, 20f)
    val alpha = if (offset.absoluteValue > 1) 0f else (1f - offset.absoluteValue * 0.5f)

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(240.dp)
            .graphicsLayer {
                this.translationX = translationX
                this.rotationY = rotationY
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(if (isCenter) 16.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(mode.color.copy(0.25f), mode.color.copy(0.08f))
                        )
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(mode.color.copy(0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(mode.icon, mode.title, tint = mode.color, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(mode.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(mode.subtitle, fontSize = 11.sp, color = mode.color, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))

            Button(
                onClick = { if (isCenter) mode.onClick() },
                enabled = isCenter,
                colors = ButtonDefaults.buttonColors(
                    containerColor = mode.color,
                    disabledContainerColor = mode.color.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(34.dp)
            ) {
                Text(
                    "進入遊戲",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCenter) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// =====================================================
data class ModeData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)