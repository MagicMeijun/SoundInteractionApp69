package com.soundinteractionapp.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.soundinteractionapp.R
import com.soundinteractionapp.data.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit = {},
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
    var showAvatarPicker by remember { mutableStateOf(false) }

    // 預設頭像列表
    val defaultAvatars = remember {
        listOf(
            R.drawable.avatar_01,
            R.drawable.avatar_02,
            R.drawable.avatar_03,
            R.drawable.avatar_04,
            R.drawable.avatar_05,
            R.drawable.avatar_06,
            R.drawable.avatar_07,
            R.drawable.avatar_08,
            R.drawable.avatar_09,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12,
            R.drawable.avatar_13,
            R.drawable.avatar_14,
            R.drawable.avatar_15,
            R.drawable.avatar_16,
            R.drawable.avatar_17,
            R.drawable.avatar_18,
            R.drawable.avatar_19,
            R.drawable.avatar_20,
            R.drawable.avatar_21,
            R.drawable.avatar_22,
            R.drawable.avatar_23,
            R.drawable.avatar_24
        )
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

            // 頭像顯示區域
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
                            showAvatarPicker = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 顯示頭像
                    if (isAnonymous) {
                        // ✅ 訪客使用 user.png
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "訪客頭像",
                            modifier = Modifier.size(60.dp)
                        )
                    } else if (userProfile.photoUrl.isNotEmpty()) {
                        val avatarResId = userProfile.photoUrl.toIntOrNull()
                        if (avatarResId != null && defaultAvatars.contains(avatarResId)) {
                            Image(
                                painter = painterResource(id = avatarResId),
                                contentDescription = "頭像",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "預設頭像",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "預設頭像",
                            modifier = Modifier.size(60.dp)
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
                        ProfileItem(
                            icon = Icons.Default.AccountCircle,
                            title = "帳號",
                            value = userProfile.account,
                            onClick = null
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        ProfileItem(
                            icon = Icons.Default.Person,
                            title = "暱稱",
                            value = userProfile.displayName,
                            onClick = { showEditDialog = true }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        ProfileItem(
                            icon = Icons.Default.Info,
                            title = "關於我",
                            value = userProfile.bio.ifEmpty { "點擊設定關於我" },
                            onClick = { showAboutDialog = true }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        ProfileItem(
                            icon = Icons.Default.Lock,
                            title = "變更密碼",
                            value = "點擊修改",
                            onClick = { showPasswordDialog = true }
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }

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

    // 頭像選擇器對話框
    if (showAvatarPicker) {
        AvatarPickerDialog(
            avatars = defaultAvatars,
            currentAvatarResId = userProfile.photoUrl.toIntOrNull(),
            onDismiss = { showAvatarPicker = false },
            onSelect = { selectedResId ->
                profileViewModel.updateAvatar(selectedResId.toString())
                showAvatarPicker = false
                Toast.makeText(context, "頭像已更新", Toast.LENGTH_SHORT).show()
            }
        )
    }

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

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { password ->
                profileViewModel.deleteAccount(password) { success, error ->
                    if (success) {
                        showDeleteDialog = false
                        Toast.makeText(context, "帳號已刪除，即將返回登入畫面", Toast.LENGTH_SHORT).show()

                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            onAccountDeleted()
                        }, 500)
                    } else {
                        Toast.makeText(context, error ?: "刪除失敗", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

// 頭像選擇器對話框（完美圓形）
@Composable
fun AvatarPickerDialog(
    avatars: List<Int>,
    currentAvatarResId: Int?,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "選擇頭像",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF673AB7)
                )

                Spacer(Modifier.height(20.dp))

                // 網格顯示頭像（4 列）
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(avatars) { avatarResId ->
                        val isSelected = currentAvatarResId == avatarResId

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color(0xFF673AB7).copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF673AB7) else Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelect(avatarResId) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = "頭像選項",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // 選中狀態的勾勾（完整圓形）
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(Color(0xFF673AB7))
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "已選擇",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("取消", fontSize = 14.sp)
                }
            }
        }
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