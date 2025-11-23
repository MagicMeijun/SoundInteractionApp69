package com.soundinteractionapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soundinteractionapp.ui.theme.SoundInteractionAppTheme
import com.soundinteractionapp.components.FreePlayScreenContent

// 導入所有畫面 Composable
import com.soundinteractionapp.screens.WelcomeScreenContent

// 導入互動畫面
import com.soundinteractionapp.screens.freeplay.interactions.CatInteractionScreen
import com.soundinteractionapp.screens.freeplay.interactions.PianoInteractionScreen
import com.soundinteractionapp.screens.freeplay.interactions.DogInteractionScreen
import com.soundinteractionapp.screens.freeplay.interactions.BirdInteractionScreen
import com.soundinteractionapp.screens.freeplay.interactions.DrumInteractionScreen
import com.soundinteractionapp.screens.freeplay.interactions.BellInteractionScreen
import com.soundinteractionapp.screens.freeplay.interactions.OceanInteractionScreen // 海浪
import com.soundinteractionapp.screens.freeplay.interactions.RainInteractionScreen  // 雨聲
import com.soundinteractionapp.screens.freeplay.interactions.WindInteractionScreen

// 導入遊戲相關畫面
import com.soundinteractionapp.screens.game.GameModeScreenContent
import com.soundinteractionapp.screens.game.Level1FollowBeatScreen
import com.soundinteractionapp.screens.game.Level2FindAnimalScreen
import com.soundinteractionapp.screens.game.Level3PitchScreen
import com.soundinteractionapp.screens.game.Level4CompositionScreen

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 設定全螢幕
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // 初始化音效管理器
        soundManager = SoundManager(this)

        setContent {
            SoundInteractionAppTheme {

                val navController = rememberNavController()

                // 確保 App 關閉時釋放 SoundManager 資源
                DisposableEffect(Unit) {
                    onDispose {
                        soundManager.release()
                    }
                }

                NavHost(navController = navController, startDestination = Screen.Welcome.route) {

                    // ==========================================
                    // 1. 主要選單與導航
                    // ==========================================

                    // 歡迎畫面
                    composable(Screen.Welcome.route) {
                        WelcomeScreenContent(
                            onNavigateToFreePlay = { navController.navigate(Screen.FreePlay.route) },
                            onNavigateToRelax = { navController.navigate(Screen.Relax.route) },
                            onNavigateToGame = { navController.navigate(Screen.Game.route) }
                        )
                    }

                    // 自由探索模式 (九宮格選單)
                    composable(Screen.FreePlay.route) {
                        FreePlayScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager,
                            // 傳遞各個按鈕的導航動作
                            onNavigateToCatInteraction = { navController.navigate(Screen.CatInteraction.route) },
                            onNavigateToPianoInteraction = { navController.navigate(Screen.PianoInteraction.route) },
                            onNavigateToDogInteraction = { navController.navigate(Screen.DogInteraction.route) },
                            onNavigateToBirdInteraction = { navController.navigate(Screen.BirdInteraction.route) },
                            onNavigateToDrumInteraction = { navController.navigate(Screen.DrumInteraction.route) },
                            onNavigateToBellInteraction = { navController.navigate(Screen.BellInteraction.route) },

                            // [海浪]
                            onNavigateToOceanInteraction = { navController.navigate(Screen.OceanInteraction.route) },

                            // [雨聲] 新增的參數
                            onNavigateToRainInteraction = { navController.navigate(Screen.RainInteraction.route) },
                            onNavigateToWindInteraction = { navController.navigate(Screen.WindInteraction.route) }                        )
                    }

                    // 放鬆模式 (預設進入海浪，也可改為獨立選單)
                    composable(Screen.Relax.route) {
                        OceanInteractionScreen(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager
                        )
                    }

                    // 遊戲模式選單
                    composable(Screen.Game.route) {
                        GameModeScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLevel = { route -> navController.navigate(route) }
                        )
                    }

                    // ==========================================
                    // 2. 互動子畫面 (Interactions)
                    // ==========================================

                    composable(Screen.CatInteraction.route) {
                        CatInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }
                    composable(Screen.PianoInteraction.route) {
                        PianoInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }
                    composable(Screen.DogInteraction.route) {
                        DogInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }
                    composable(Screen.BirdInteraction.route) {
                        BirdInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }
                    composable(Screen.DrumInteraction.route) {
                        DrumInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }
                    composable(Screen.BellInteraction.route) {
                        BellInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }

                    // [海浪畫面]
                    composable(Screen.OceanInteraction.route) {
                        OceanInteractionScreen(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager
                        )
                    }

                    // [雨聲畫面]
                    composable(Screen.RainInteraction.route) {
                        RainInteractionScreen(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager
                        )
                    }
                    composable(Screen.WindInteraction.route) {
                        WindInteractionScreen(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager
                        )
                    }
                    // ==========================================
                    // 3. 遊戲關卡 (Game Levels)
                    // ==========================================

                    composable(Screen.GameLevel1.route) { Level1FollowBeatScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable(Screen.GameLevel2.route) { Level2FindAnimalScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable(Screen.GameLevel3.route) { Level3PitchScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable(Screen.GameLevel4.route) { Level4CompositionScreen(onNavigateBack = { navController.popBackStack() }) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 確保 Activity 銷毀時釋放資源
        if (::soundManager.isInitialized) {
            soundManager.release()
        }
    }
}