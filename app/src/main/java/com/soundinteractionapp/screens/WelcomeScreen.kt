package com.soundinteractionapp.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun WelcomeScreen(
    onNavigateToFreePlay: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    // 使用 rememberSaveable 保存登入狀態
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginClick = { /* TODO: 實作登入邏輯 */ },
            onRegisterClick = { /* TODO: 實作註冊邏輯 */ },
            onGuestLoginClick = { isLoggedIn = true }
        )
    } else {
        GameHomeScreen(
            onNavigateToFreePlay = onNavigateToFreePlay,
            onNavigateToRelax = onNavigateToRelax,
            onNavigateToGame = onNavigateToGame,
            onNavigateToProfile = onNavigateToProfile,
            onLogout = {
                isLoggedIn = false
                onLogout()
            }
        )
    }
}