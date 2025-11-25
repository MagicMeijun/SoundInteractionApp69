package com.soundinteractionapp.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue

@Composable
fun WelcomeScreen(
    onNavigateToFreePlay: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        // ─────────────── 登入頁整體縮到 70%（四周超大留白）───────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F5FF)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = 0.7f     // 想調整大小改這邊
                    scaleY = 0.7f
                }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SoundJoy",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.displayLarge.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6),
                                    Color(0xFFEC4899)
                                )
                            )
                        ),
                    )

                    Text(
                        text = "專為心智障礙孩童設計的音樂互動 App",
                        fontSize = 15.sp,
                        color = Color(0xFF777777),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 80.dp)
                    )

                    // 登入
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                    ) {
                        Text(
                            "登入帳號",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 註冊
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                    ) {
                        Text(
                            "註冊帳號",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 訪客登入
                    OutlinedButton(
                        onClick = { isLoggedIn = true },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF673AB7), Color(0xFF9C27B0))
                            )
                        ),
                    ) {
                        Text(
                            "訪客登入",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF673AB7)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    } else {
        // ─────────────── 主畫面（三大滑動卡片）保持原比例不縮小 ───────────────
        var currentIndex by remember { mutableStateOf(1) }

        val modes = listOf(
            ModeData("自由探索", "模式一", Icons.Default.Search, Color(0xFF8C7AE6), onNavigateToFreePlay),
            ModeData("放鬆時光", "模式二", Icons.Filled.FavoriteBorder, Color(0xFF4FC3F7), onNavigateToRelax),
            ModeData("音樂遊戲", "模式三", Icons.Filled.PlayArrow, Color(0xFFFF9800), onNavigateToGame)
        )

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
            TopInfoBar(onNavigateToProfile = onNavigateToProfile, onLogout = {
                isLoggedIn = false
                onLogout()
            })

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 60.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).padding(end = 40.dp), contentAlignment = Alignment.CenterStart) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "SoundJoy",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.displayLarge.copy(
                                brush = Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
                                letterSpacing = (-1.5).sp
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "讓每位孩子都能聽見快樂的聲音\n專為心智障礙孩童設計的音樂互動 App",
                            fontSize = 13.sp,
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    SwipeableCardCarousel(
                        modes = modes,
                        currentIndex = currentIndex,
                        onIndexChange = { currentIndex = it }
                    )
                }
            }
        }
    }
}

// ────────────────── 以下全部保持不變，完美無缺 ──────────────────

@Composable
fun TopInfoBar(onNavigateToProfile: () -> Unit, onLogout: () -> Unit) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SoundJoy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF673AB7), modifier = Modifier.weight(1f))
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFE8EAF6))
                        .clickable { showDropdownMenu = !showDropdownMenu }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Person, "訪客", tint = Color(0xFF673AB7), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("訪客", fontSize = 14.sp, color = Color.Black)
                    Icon(Icons.Filled.ArrowDropDown, "下拉", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(expanded = showDropdownMenu, onDismissRequest = { showDropdownMenu = false }, modifier = Modifier.width(180.dp)) {
                    DropdownMenuItem(text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, "個人資料", tint = Color(0xFF673AB7), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("個人資料", fontSize = 14.sp)
                        }
                    }, onClick = { showDropdownMenu = false; onNavigateToProfile() })
                    Divider(color = Color(0xFFE0E0E0))
                    DropdownMenuItem(text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ExitToApp, "登出", tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("登出", fontSize = 14.sp, color = Color(0xFFE57373))
                        }
                    }, onClick = { showDropdownMenu = false; onLogout() })
                }
            }
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Filled.Settings, "設定", tint = Color(0xFF673AB7), modifier = Modifier.size(28.dp).clickable { })
        }
    }
}

@Composable
fun SwipeableCardCarousel(modes: List<ModeData>, currentIndex: Int, onIndexChange: (Int) -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        finishedListener = { isAnimating = false }
    )

    Box(
        modifier = Modifier.fillMaxWidth().height(260.dp).pointerInput(currentIndex) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (!isAnimating) {
                        if (offsetX > 80 && currentIndex > 0) {
                            isAnimating = true
                            onIndexChange(currentIndex - 1)
                        } else if (offsetX < -80 && currentIndex < modes.size - 1) {
                            isAnimating = true
                            onIndexChange(currentIndex + 1)
                        }
                        offsetX = 0f
                    }
                },
                onHorizontalDrag = { _, dragAmount ->
                    if (!isAnimating) offsetX = (offsetX + dragAmount).coerceIn(-200f, 200f)
                }
            )
        },
        contentAlignment = Alignment.Center
    ) {
        modes.forEachIndexed { index, mode ->
            val offset = index - currentIndex
            if (offset != 0) ModeCardSwiper(mode = mode, offset = offset, dragOffset = animatedOffset, isCenter = false)
        }
        modes.getOrNull(currentIndex)?.let {
            ModeCardSwiper(mode = it, offset = 0, dragOffset = animatedOffset, isCenter = true)
        }
    }
}

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
        Column(modifier = Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(70.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(mode.color.copy(0.25f), mode.color.copy(0.08f)))).padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(mode.color.copy(0.12f)), contentAlignment = Alignment.Center) {
                    Icon(mode.icon, mode.title, tint = mode.color, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(mode.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(mode.subtitle, fontSize = 11.sp, color = mode.color, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.weight(1f))
            Button(onClick = mode.onClick, colors = ButtonDefaults.buttonColors(mode.color), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(34.dp)) {
                Text("進入遊戲", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

data class ModeData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)