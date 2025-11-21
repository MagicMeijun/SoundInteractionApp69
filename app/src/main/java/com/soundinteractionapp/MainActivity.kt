package com.soundinteractionapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.soundinteractionapp.ui.theme.SoundInteractionAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// 導入所有跨檔案 Composable
import com.soundinteractionapp.WelcomeScreenContent
import com.soundinteractionapp.FreePlayScreenContent
import com.soundinteractionapp.GameModeScreenContent
import com.soundinteractionapp.CatInteractionScreen



class MainActivity : ComponentActivity() {

    // 延遲初始化 SoundManager，確保在 onDestroy 時可以釋放資源
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 啟用全螢幕模式
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // 初始化 SoundManager
        // 由於 SoundManager 需要 Context，我們在 onCreate 這裡初始化它
        soundManager = SoundManager(this)

        setContent {
            SoundInteractionAppTheme {

                val navController = rememberNavController()

                // 確保 SoundManager 在 Composable 作用域結束時釋放資源
                DisposableEffect(Unit) {
                    onDispose {
                        soundManager.release()
                    }
                }

                NavHost(navController = navController, startDestination = Screen.Welcome.route) {

                    // 1. 歡迎畫面
                    composable(Screen.Welcome.route) {
                        WelcomeScreenContent(
                            onNavigateToFreePlay = {
                                navController.navigate(Screen.FreePlay.route)
                            },
                            onNavigateToRelax = {
                                navController.navigate(Screen.Relax.route)
                            },
                            onNavigateToGame = {
                                navController.navigate(Screen.Game.route)
                            }
                        )
                    }

                    // 2. 自由探索模式畫面 (傳遞 SoundManager 和 CatInteraction 導航)
                    composable(Screen.FreePlay.route) {
                        FreePlayScreenContent(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            soundManager = soundManager,
                            onNavigateToCatInteraction = { // 傳遞導航到貓咪畫面
                                navController.navigate(Screen.CatInteraction.route)
                            }
                        )
                    }

                    // 3. 放鬆模式畫面 (使用 Placeholder)
                    composable(Screen.Relax.route) {
                        GameLevelPlaceholderScreen(
                            title = "放鬆模式",
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // 4. 遊戲訓練模式畫面
                    composable(Screen.Game.route) {
                        GameModeScreenContent(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLevel = { route ->
                                navController.navigate(route)
                            }
                        )
                    }

                    // 5. 貓咪互動畫面 (新增)
                    composable(Screen.CatInteraction.route) {
                        CatInteractionScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            soundManager = soundManager
                        )
                    }

                    // 6. 四個關卡畫面 (使用 Level Composable)
                    composable(Screen.GameLevel1.route) {
                        Level1FollowBeatScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(Screen.GameLevel2.route) {
                        Level2FindAnimalScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(Screen.GameLevel3.route) {
                        Level3PitchScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(Screen.GameLevel4.route) {
                        Level4CompositionScreen(onNavigateBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    // 額外確保 Activity 被銷毀時 SoundManager 釋放資源 (系統級別保障)
    override fun onDestroy() {
        super.onDestroy()
        if (::soundManager.isInitialized) {
            soundManager.release()
        }
    }
}


