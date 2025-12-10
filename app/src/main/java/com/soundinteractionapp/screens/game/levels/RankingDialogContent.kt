package com.soundinteractionapp.screens.game.levels

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue

////////////////////////新增////////////////////////
import androidx.compose.runtime.collectAsState
import com.soundinteractionapp.data.RankingViewModel

////////////////////////新增////////////////////////

//////////////////////////////////////////////////////////
////////////////////////新增此資料檔////////////////////////
//////////////////////////////////////////////////////////

/**
 * 排名系統畫面 (Ranking Dialog)。
 * 作為彈出視窗的內容，顯示在遊戲選單之上。
 */
@Composable
fun RankingDialogContent(onClose: () -> Unit, rankingViewModel: RankingViewModel) { // 【新增】接收 ViewModel

    // 觀察分數狀態
    val scores by rankingViewModel.scores.collectAsState()

    Card(
        // ... (Card 佈局不變)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ... (Top Bar 佈局不變)

            // 排名列表顯示區
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 【顯示分數】
                Text(
                    "關卡分數紀錄",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ScoreRow(levelName = "關卡 1: 跟著按", score = scores.level1Score)
                Divider()
                ScoreRow(levelName = "關卡 2: 找出動物", score = scores.level2Score)
                Divider()

                // TODO: 在此處實作 LazyColumn 顯示更詳細的排名
            }
        }
    }
}

// 輔助 Composable 顯示單行分數
@Composable
fun ScoreRow(levelName: String, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(levelName, style = MaterialTheme.typography.bodyLarge)
        Text(
            "${score} 分",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}