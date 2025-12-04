package com.soundinteractionapp

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soundinteractionapp.ui.theme.SoundInteractionAppTheme
import com.soundinteractionapp.utils.GameInputManager

// --- Screens Imports ---
import com.soundinteractionapp.screens.SplashScreen
import com.soundinteractionapp.screens.WelcomeScreen
import com.soundinteractionapp.screens.profile.ProfileScreen

// FreePlay Mode Imports
import com.soundinteractionapp.components.FreePlayScreenContent
import com.soundinteractionapp.screens.freeplay.interactions.*

// Relax Mode Imports
import com.soundinteractionapp.screens.relax.RelaxScreenContent
import com.soundinteractionapp.screens.relax.ambiences.OceanInteractionScreen
import com.soundinteractionapp.screens.relax.ambiences.RainInteractionScreen
import com.soundinteractionapp.screens.relax.ambiences.WindInteractionScreen

// Game Mode Imports
import com.soundinteractionapp.screens.game.GameModeScreenContent
import com.soundinteractionapp.screens.game.levels.Level1FollowBeatScreen
import com.soundinteractionapp.screens.game.levels.Level2FindAnimalScreen
import com.soundinteractionapp.screens.game.levels.Level3PitchScreen
import com.soundinteractionapp.screens.game.levels.Level4CompositionScreen

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 設定全螢幕模式
        hideSystemUI()
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

        // 初始化 SoundManager
        soundManager = SoundManager(this)

        setContent {
            SoundInteractionAppTheme {
                val navController = rememberNavController()

                // 確保離開 App 時釋放音訊資源
                DisposableEffect(Unit) {
                    onDispose { soundManager.release() }
                }

                NavHost(navController = navController, startDestination = Screen.Splash.route) {

                    // --- 啟動與登入流程 ---
                    composable(Screen.Splash.route) {
                        SplashScreen(navController = navController)
                    }

                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onNavigateToFreePlay = { navController.navigate(Screen.FreePlay.route) },
                            onNavigateToRelax = { navController.navigate(Screen.Relax.route) },
                            onNavigateToGame = { navController.navigate(Screen.Game.route) },
                            onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                            onLogout = {
                                navController.navigate(Screen.Splash.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onAccountDeleted = {
                                navController.navigate(Screen.Splash.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // --- 自由探索模式 ---
                    composable(Screen.FreePlay.route) {
                        FreePlayScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager,
                            onNavigateToCatInteraction = { navController.navigate(Screen.CatInteraction.route) },
                            onNavigateToPianoInteraction = { navController.navigate(Screen.PianoInteraction.route) },
                            onNavigateToDogInteraction = { navController.navigate(Screen.DogInteraction.route) },
                            onNavigateToBirdInteraction = { navController.navigate(Screen.BirdInteraction.route) },
                            onNavigateToDrumInteraction = { navController.navigate(Screen.DrumInteraction.route) },
                            onNavigateToBellInteraction = { navController.navigate(Screen.BellInteraction.route) }
                        )
                    }

                    // --- 放鬆模式 ---
                    composable(Screen.Relax.route) {
                        RelaxScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager,
                            onNavigateToOceanInteraction = { navController.navigate(Screen.OceanInteraction.route) },
                            onNavigateToRainInteraction = { navController.navigate(Screen.RainInteraction.route) },
                            onNavigateToWindInteraction = { navController.navigate(Screen.WindInteraction.route) }
                        )
                    }

                    // --- 遊戲訓練模式 ---
                    composable(Screen.Game.route) {
                        GameModeScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLevel = { route -> navController.navigate(route) }
                        )
                    }

                    // Level 1: 太鼓節奏 (已更新)
                    composable(Screen.GameLevel1.route) {
                        Level1FollowBeatScreen(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager
                        )
                    }

                    // 其他關卡保持不變
                    composable(Screen.GameLevel2.route) {
                        Level2FindAnimalScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(Screen.GameLevel3.route) {
                        Level3PitchScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(Screen.GameLevel4.route) {
                        Level4CompositionScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // --- 個別互動畫面 ---
                    composable(Screen.CatInteraction.route) { CatInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.PianoInteraction.route) { PianoInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.DogInteraction.route) { DogInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.BirdInteraction.route) { BirdInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.DrumInteraction.route) { DrumInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.BellInteraction.route) { BellInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.OceanInteraction.route) { OceanInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.RainInteraction.route) { RainInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.WindInteraction.route) { WindInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                }
            }
        }
    }

    // --- 實體按鍵監聽 ---
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.repeatCount != 0) {
            return super.onKeyDown(keyCode, event)
        }

        // 攔截音量鍵和其他媒體鍵，轉為遊戲打擊訊號
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_CAMERA,
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                GameInputManager.triggerBeat()
                return true // 阻止系統處理音量變化
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun hideSystemUI() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundManager.isInitialized) soundManager.release()
    }
}