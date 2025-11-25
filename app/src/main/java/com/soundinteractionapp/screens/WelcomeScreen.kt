package com.soundinteractionapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.soundinteractionapp.data.AuthManager
import kotlinx.coroutines.launch

/**
 * 歡迎畫面 - 包含登入/註冊功能
 */
@Composable
fun WelcomeScreen(
    onNavigateToFreePlay: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    // 初始化 AuthManager
    val authManager = remember { AuthManager() }
    val scope = rememberCoroutineScope()

    // 狀態管理
    var currentUser by remember { mutableStateOf(authManager.currentUser) }
    var userName by remember { mutableStateOf("使用者") }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 當使用者狀態改變時,嘗試獲取名字
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (user.isAnonymous) {
                userName = "訪客"
            } else {
                val name = authManager.getUserName(user.uid)
                if (name != null) userName = name
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 右上角使用者資訊區
            UserInfoSection(
                currentUser = currentUser,
                userName = userName,
                onLoginClick = { showLoginDialog = true },
                onRegisterClick = { showRegisterDialog = true },
                onLogoutClick = {
                    authManager.signOut()
                    currentUser = null
                    userName = "使用者"
                },
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // 主要內容區
            MainContentSection(
                onNavigateToFreePlay = onNavigateToFreePlay,
                onNavigateToGame = onNavigateToGame,
                onNavigateToRelax = onNavigateToRelax
            )

            // 錯誤訊息顯示
            errorMessage?.let { message ->
                ErrorSnackbar(
                    message = message,
                    onDismiss = { errorMessage = null },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // 登入對話框
            if (showLoginDialog) {
                LoginDialog(
                    onDismiss = { showLoginDialog = false },
                    onLogin = { email, password ->
                        scope.launch {
                            val result = authManager.signIn(email, password)
                            if (result.isSuccess) {
                                currentUser = result.getOrNull()
                                showLoginDialog = false
                                errorMessage = null
                            } else {
                                errorMessage = "登入失敗: ${result.exceptionOrNull()?.message}"
                            }
                        }
                    }
                )
            }

            // 註冊對話框
            if (showRegisterDialog) {
                RegisterDialog(
                    onDismiss = { showRegisterDialog = false },
                    onRegister = { name, email, password ->
                        scope.launch {
                            val result = authManager.signUp(email, password, name)
                            if (result.isSuccess) {
                                currentUser = result.getOrNull()
                                userName = name
                                showRegisterDialog = false
                                errorMessage = null
                            } else {
                                errorMessage = "註冊失敗: ${result.exceptionOrNull()?.message}"
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * 使用者資訊區塊
 */
@Composable
private fun UserInfoSection(
    currentUser: com.google.firebase.auth.FirebaseUser?,
    userName: String,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentUser == null) {
            // 未登入狀態
            TextButton(onClick = onLoginClick) {
                Text("登入", fontSize = 18.sp)
            }
            Text("|", modifier = Modifier.padding(horizontal = 4.dp))
            TextButton(onClick = onRegisterClick) {
                Text("註冊", fontSize = 18.sp)
            }
        } else {
            // 已登入狀態
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "使用者頭像",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "登出",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable(onClick = onLogoutClick)
                )
            }
        }
    }
}

/**
 * 主要內容區塊
 */
@Composable
private fun MainContentSection(
    onNavigateToFreePlay: () -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToRelax: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左側:大標題區
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SoundJoy",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "音樂互動訓練 App",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 右側:模式按鈕區
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AppModeButton(
                text = "自由探索模式",
                description = "提供安全的聲音探索環境",
                onClick = onNavigateToFreePlay,
                color = MaterialTheme.colorScheme.tertiary
            )

            AppModeButton(
                text = "遊戲訓練模式",
                description = "透過遊戲提升專注力與認知",
                onClick = onNavigateToGame,
                color = MaterialTheme.colorScheme.primary
            )

            AppModeButton(
                text = "放鬆模式",
                description = "情緒調節與安撫焦慮",
                onClick = onNavigateToRelax,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * 應用模式按鈕
 */
@Composable
private fun AppModeButton(
    text: String,
    description: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 錯誤訊息 Snackbar
 */
@Composable
private fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(16.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text("關閉")
            }
        }
    ) {
        Text(message)
    }
}

/**
 * 登入對話框
 */
@Composable
private fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("登入帳號", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密碼") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = { onLogin(email, password) },
                        modifier = Modifier.weight(1f),
                        enabled = email.isNotBlank() && password.isNotBlank()
                    ) {
                        Text("登入")
                    }
                }
            }
        }
    }
}

/**
 * 註冊對話框
 */
@Composable
private fun RegisterDialog(
    onDismiss: () -> Unit,
    onRegister: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("註冊新帳號", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("您的名字") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密碼 (至少6位)") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = { onRegister(name, email, password) },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() &&
                                email.isNotBlank() &&
                                password.length >= 6
                    ) {
                        Text("註冊")
                    }
                }
            }
        }
    }
}