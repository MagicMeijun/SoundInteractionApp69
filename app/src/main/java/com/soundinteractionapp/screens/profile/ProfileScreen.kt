package com.soundinteractionapp.screens.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.soundinteractionapp.data.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit = {}, // ✅ 新增：刪除成功的回調
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    val userProfile by profileViewModel.userProfile.collectAsState()
    val isAnonymous by profileViewModel.isAnonymous.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.uploadProfileImage(it, context)
            Toast.makeText(context, "頭像上傳中...", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("個人資料", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF673AB7),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F5FF))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isAnonymous) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "您目前以訪客身分登入，無法修改個人資料",
                            fontSize = 14.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            Box(
                modifier = Modifier.size(128.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, Color(0xFF673AB7), CircleShape)
                        .clickable(enabled = !isAnonymous) {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (userProfile.photoUrl.isNotEmpty()) {
                        Base64Image(
                            base64String = userProfile.photoUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "預設頭像",
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF673AB7)
                        )
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color.White
                            )
                        }
                    }
                }

                if (!isAnonymous) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF673AB7))
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "編輯頭像",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = userProfile.displayName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF673AB7)
            )

            if (!isAnonymous) {
                Text(
                    text = userProfile.account,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (!isAnonymous) {
                        // ✅ 1. 帳號（不可編輯）
                        ProfileItem(
                            icon = Icons.Default.AccountCircle,
                            title = "帳號",
                            value = userProfile.account,
                            onClick = null
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // ✅ 2. 暱稱（可編輯）
                        ProfileItem(
                            icon = Icons.Default.Person,
                            title = "暱稱",
                            value = userProfile.displayName,
                            onClick = { showEditDialog = true }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // ✅ 3. 關於我（可編輯）
                        ProfileItem(
                            icon = Icons.Default.Info,
                            title = "關於我",
                            value = userProfile.bio.ifEmpty { "點擊設定關於我" },
                            onClick = { showAboutDialog = true }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // 4. 變更密碼
                        ProfileItem(
                            icon = Icons.Default.Lock,
                            title = "變更密碼",
                            value = "點擊修改",
                            onClick = { showPasswordDialog = true }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    // 5. 註冊日期
                    ProfileItem(
                        icon = Icons.Default.DateRange,
                        title = "註冊日期",
                        value = userProfile.createdAt.ifEmpty { "未知" },
                        onClick = null
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "勳章展示",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF673AB7),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (userProfile.badges.isEmpty()) {
                        Text("暫無勳章", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        Text("已獲得 ${userProfile.badges.size} 枚勳章", color = Color(0xFF673AB7))
                    }
                }
            }

            // ✅ 新增：刪除帳號按鈕（只有非訪客才顯示）
            if (!isAnonymous) {
                Spacer(Modifier.height(32.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE57373)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE57373)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = "刪除帳號",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("刪除帳號", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // 對話框
    if (showEditDialog) {
        EditNameDialog(
            currentName = userProfile.displayName,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                profileViewModel.updateDisplayName(newName)
                showEditDialog = false
                Toast.makeText(context, "暱稱已更新", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAboutDialog) {
        EditAboutDialog(
            currentBio = userProfile.bio,
            onDismiss = { showAboutDialog = false },
            onConfirm = { newBio ->
                profileViewModel.updateBio(newBio)
                showAboutDialog = false
                Toast.makeText(context, "已更新關於我", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword ->
                profileViewModel.changePassword(oldPassword, newPassword) { success, error ->
                    if (success) {
                        showPasswordDialog = false
                        Toast.makeText(context, "密碼已成功變更", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error ?: "變更失敗", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    // ✅ 新增：刪除帳號確認對話框
    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { password ->
                profileViewModel.deleteAccount(password) { success, error ->
                    if (success) {
                        showDeleteDialog = false
                        Toast.makeText(context, "帳號已刪除，即將返回登入畫面", Toast.LENGTH_SHORT).show()

                        // ✅ 延遲 500ms 讓用戶看到提示訊息後再導航
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            onAccountDeleted() // 調用導航回調
                        }, 500)
                    } else {
                        Toast.makeText(context, error ?: "刪除失敗", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
fun Base64Image(
    base64String: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(base64String) {
        try {
            val pureBase64 = base64String.substringAfter("base64,")
            val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "頭像",
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            Icons.Default.Person,
            contentDescription = "預設頭像",
            modifier = modifier.size(60.dp),
            tint = Color(0xFF673AB7)
        )
    }
}

@Composable
fun ProfileItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
        }
        if (onClick != null) {
            Icon(Icons.Default.ChevronRight, "編輯", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("編輯暱稱") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("暱稱") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) {
                Text("確定")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
fun EditAboutDialog(currentBio: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var bio by remember { mutableStateOf(currentBio) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("編輯關於我") },
        text = {
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("關於我(可留空)") },
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(bio.trim()) }) { Text("確定") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("變更密碼", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("目前密碼") },
                    visualTransformation = if (oldVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { oldVisible = !oldVisible }) {
                            Icon(if (oldVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("新密碼(至少6字元)") },
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newVisible = !newVisible }) {
                            Icon(if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("確認新密碼") },
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        oldPassword.isBlank() -> {
                            Toast.makeText(context, "請輸入目前密碼", Toast.LENGTH_SHORT).show()
                        }
                        newPassword.length < 6 -> {
                            Toast.makeText(context, "新密碼至少需要6個字元", Toast.LENGTH_SHORT).show()
                        }
                        newPassword != confirmPassword -> {
                            Toast.makeText(context, "兩次新密碼不一致", Toast.LENGTH_SHORT).show()
                        }
                        oldPassword == newPassword -> {
                            Toast.makeText(context, "新密碼不能與目前密碼相同", Toast.LENGTH_SHORT).show()
                        }
                        else -> onConfirm(oldPassword, newPassword)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("確定變更")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// ✅ 新增：刪除帳號對話框
@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "刪除帳號",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE57373),
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 警告訊息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "⚠️ 此操作無法復原",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            "以下資料將被永久刪除：",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Text("• 個人資料", fontSize = 11.sp, color = Color(0xFF666666))
                        Text("• 遊戲進度", fontSize = 11.sp, color = Color(0xFF666666))
                        Text("• 所有勳章", fontSize = 11.sp, color = Color(0xFF666666))
                    }
                }

                // 密碼輸入框
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("輸入密碼確認", fontSize = 14.sp) },
                    placeholder = { Text("請輸入您的密碼", fontSize = 13.sp) },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "隱藏密碼" else "顯示密碼",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE57373),
                        focusedLabelColor = Color(0xFFE57373)
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        password.isBlank() -> {
                            Toast.makeText(context, "請輸入密碼", Toast.LENGTH_SHORT).show()
                        }
                        password.length < 6 -> {
                            Toast.makeText(context, "密碼長度不正確", Toast.LENGTH_SHORT).show()
                        }
                        else -> onConfirm(password)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("確認刪除", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF9E9E9E))
            ) {
                Text("取消", fontSize = 14.sp, color = Color(0xFF666666))
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}