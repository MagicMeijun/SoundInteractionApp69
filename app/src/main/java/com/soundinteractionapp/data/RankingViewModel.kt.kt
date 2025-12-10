package com.soundinteractionapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// ⚠️ 注意：為了讓所有 Composable 共享同一個實例，
// 我們需要確保這個 ViewModel 在 App 級別被提供，
// 或者使用 Hilt/Koin 等依賴注入框架。
// 這裡我們假設您在 MainActivity 中將它作為參數傳遞。

class RankingViewModel(private val repository: RankingRepository) : ViewModel() {

    // 將 Repository 的分數狀態暴露給 UI
    val scores: StateFlow<ScoreEntry> = repository.scores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScoreEntry()
        )

    /**
     * 遊戲結束時，呼叫此函數來嘗試更新最高分數
     */
    fun onGameFinished(levelId: Int, finalScore: Int) {
        repository.updateHighScore(levelId, finalScore)
    }
}