package com.soundinteractionapp.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 個人資料畫面
 * 顯示使用者的基本資料和設定選項
 *
 * TODO: 之後需要實作的功能
 * 1. 從資料庫或 AuthManager 讀取真實的使用者資料
 * 2. 實作編輯個人資料功能（姓名、大頭貼、email等）
 * 3. 實作遊戲紀錄統計（遊玩次數、完成關卡等）
 * 4. 實作偏好設定（音量、難度、主題等）
 * 5. 實作頭像上傳或選擇功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit  // 返回上一頁
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "個人資料",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color(0xFF673AB7)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 大頭貼區域
            // TODO: 實作點擊更換頭像功能
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAF6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "使用者頭像",
                    tint = Color(0xFF673AB7),
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 使用者名稱
            // TODO: 從 AuthManager 或資料庫讀取真實姓名
            Text(
                text = "訪客",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Email 或帳號資訊
            // TODO: 顯示真實的 email 或帳號
            Text(
                text = "guest@soundjoy.com",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 個人資料卡片區域
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // 基本資料區塊
                    Text(
                        text = "基本資料",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF673AB7)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 姓名欄位
                    // TODO: 實作編輯功能，可以修改姓名
                    ProfileInfoItem(
                        icon = Icons.Filled.Person,
                        label = "姓名",
                        value = "訪客"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFE0E0E0)
                    )

                    // Email 欄位
                    // TODO: 實作編輯功能，可以修改 Email
                    ProfileInfoItem(
                        icon = Icons.Filled.Email,
                        label = "Email",
                        value = "guest@soundjoy.com"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFE0E0E0)
                    )

                    // 帳號類型
                    // TODO: 根據真實帳號類型顯示（訪客/一般會員/VIP等）
                    ProfileInfoItem(
                        icon = Icons.Filled.AccountCircle,
                        label = "帳號類型",
                        value = "訪客帳號"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 遊戲統計卡片
            // TODO: 從資料庫讀取真實的遊戲統計數據
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "遊戲統計",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF673AB7)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 遊玩次數
                        // TODO: 計算真實的遊玩次數
                        StatisticItem(
                            label = "遊玩次數",
                            value = "0"
                        )

                        // 完成關卡
                        // TODO: 計算真實的完成關卡數
                        StatisticItem(
                            label = "完成關卡",
                            value = "0"
                        )

                        // 累計時間
                        // TODO: 計算真實的累計遊玩時間
                        StatisticItem(
                            label = "累計時間",
                            value = "0 分鐘"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 編輯按鈕
            // TODO: 實作編輯個人資料的功能
            Button(
                onClick = {
                    // TODO: 導航到編輯個人資料頁面
                    // 或彈出編輯對話框
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF673AB7)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "編輯",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "編輯個人資料",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * 個人資料資訊項目
 * 顯示一個圖標、標籤和值的組合
 */
@Composable
fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF673AB7),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

/**
 * 統計項目
 * 顯示統計數據的標籤和數值
 */
@Composable
fun StatisticItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF673AB7)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}