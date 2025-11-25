package com.soundinteractionapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soundinteractionapp.screens.*
import com.soundinteractionapp.screens.profile.ProfileScreen
import com.soundinteractionapp.components.FreePlayScreenContent
import com.soundinteractionapp.screens.freeplay.interactions.*
import com.soundinteractionapp.screens.game.*
import com.soundinteractionapp.ui.theme.SoundInteractionAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        soundManager = SoundManager(this)

        setContent {
            SoundInteractionAppTheme {
                val navController = rememberNavController()

                // 直接從 Welcome 開始（裡面已經有登入頁 + 主畫面切換）
                val startDestination = Screen.Welcome.route

                DisposableEffect(Unit) {
                    onDispose { soundManager.release() }
                }

                NavHost(navController = navController, startDestination = startDestination) {

                    // 主畫面 + 登入頁（已經合併在 WelcomeScreen 裡面了）
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onNavigateToFreePlay = { navController.navigate(Screen.FreePlay.route) },
                            onNavigateToRelax = { navController.navigate(Screen.Relax.route) },
                            onNavigateToGame = { navController.navigate(Screen.Game.route) },
                            onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                            onLogout = {
                                // 不需要特別跳轉，WelcomeScreen 內部會自動切回登入頁
                            }
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    composable(Screen.FreePlay.route) {
                        FreePlayScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager,
                            onNavigateToCatInteraction = { navController.navigate(Screen.CatInteraction.route) },
                            onNavigateToPianoInteraction = { navController.navigate(Screen.PianoInteraction.route) },
                            onNavigateToDogInteraction = { navController.navigate(Screen.DogInteraction.route) },
                            onNavigateToBirdInteraction = { navController.navigate(Screen.BirdInteraction.route) },
                            onNavigateToDrumInteraction = { navController.navigate(Screen.DrumInteraction.route) },
                            onNavigateToBellInteraction = { navController.navigate(Screen.BellInteraction.route) },
                            onNavigateToOceanInteraction = { navController.navigate(Screen.OceanInteraction.route) },
                            onNavigateToRainInteraction = { navController.navigate(Screen.RainInteraction.route) },
                            onNavigateToWindInteraction = { navController.navigate(Screen.WindInteraction.route) }
                        )
                    }

                    composable(Screen.Relax.route) {
                        OceanInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager)
                    }

                    composable(Screen.Game.route) {
                        GameModeScreenContent(onNavigateBack = { navController.popBackStack() }, onNavigateToLevel = { navController.navigate(it) })
                    }

                    // 所有互動關卡
                    composable(Screen.CatInteraction.route) { CatInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.PianoInteraction.route) { PianoInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.DogInteraction.route) { DogInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.BirdInteraction.route) { BirdInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.DrumInteraction.route) { DrumInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.BellInteraction.route) { BellInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.OceanInteraction.route) { OceanInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.RainInteraction.route) { RainInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }
                    composable(Screen.WindInteraction.route) { WindInteractionScreen(onNavigateBack = { navController.popBackStack() }, soundManager = soundManager) }

                    // 遊戲關卡
                    composable(Screen.GameLevel1.route) { Level1FollowBeatScreen { navController.popBackStack() } }
                    composable(Screen.GameLevel2.route) { Level2FindAnimalScreen { navController.popBackStack() } }
                    composable(Screen.GameLevel3.route) { Level3PitchScreen { navController.popBackStack() } }
                    composable(Screen.GameLevel4.route) { Level4CompositionScreen { navController.popBackStack() } }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundManager.isInitialized) soundManager.release()
    }
}