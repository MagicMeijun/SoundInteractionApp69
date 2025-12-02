package com.soundinteractionapp

import android.os.Bundle
import android.view.View
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
import com.soundinteractionapp.screens.relax.RelaxScreenContent
import com.soundinteractionapp.screens.relax.ambiences.OceanInteractionScreen
import com.soundinteractionapp.screens.relax.ambiences.RainInteractionScreen
import com.soundinteractionapp.screens.relax.ambiences.WindInteractionScreen
import com.soundinteractionapp.ui.theme.SoundInteractionAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 隱藏系統 UI
        hideSystemUI()

        // 監聽系統 UI 變化，自動隱藏
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

        soundManager = SoundManager(this)

        setContent {
            SoundInteractionAppTheme {
                val navController = rememberNavController()

                // 從 Splash 畫面開始
                val startDestination = Screen.Splash.route

                DisposableEffect(Unit) {
                    onDispose { soundManager.release() }
                }

                NavHost(navController = navController, startDestination = startDestination) {

                    // Splash 畫面（淡入淡出動畫）
                    composable(Screen.Splash.route) {
                        SplashScreen(navController = navController)
                    }

                    // Welcome 畫面
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onNavigateToFreePlay = { navController.navigate(Screen.FreePlay.route) },
                            onNavigateToRelax = { navController.navigate(Screen.Relax.route) },
                            onNavigateToGame = { navController.navigate(Screen.Game.route) },
                            onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                            onLogout = { }
                        )
                    }

                    // 個人資料頁
                    composable(Screen.Profile.route) {
                        ProfileScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // 自由遊玩頁
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

                    // 放鬆模式
                    composable(Screen.Relax.route) {
                        RelaxScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager,
                            onNavigateToOceanInteraction = { navController.navigate(Screen.OceanInteraction.route) },
                            onNavigateToRainInteraction = { navController.navigate(Screen.RainInteraction.route) },
                            onNavigateToWindInteraction = { navController.navigate(Screen.WindInteraction.route) }
                        )
                    }

                    // 遊戲模式
                    composable(Screen.Game.route) {
                        GameModeScreenContent(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLevel = { navController.navigate(it) }
                        )
                    }

                    // 所有互動關卡
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
                    composable(Screen.OceanInteraction.route) {
                        OceanInteractionScreen(
                            onNavigateBack = { navController.popBackStack() },
                            soundManager = soundManager
                        )
                    }
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

                    // 遊戲關卡
                    composable(Screen.GameLevel1.route) { Level1FollowBeatScreen { navController.popBackStack() } }
                    composable(Screen.GameLevel2.route) { Level2FindAnimalScreen { navController.popBackStack() } }
                    composable(Screen.GameLevel3.route) { Level3PitchScreen { navController.popBackStack() } }
                    composable(Screen.GameLevel4.route) { Level4CompositionScreen { navController.popBackStack() } }
                }
            }
        }
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