package com.soundinteractionapp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

//////////////////////////////////////////////////////////
////////////////////////新增此資料檔////////////////////////
//////////////////////////////////////////////////////////

// 定義一個資料類，用於存儲各關卡的最高分數
data class ScoreEntry(
    val level1Score: Int = 0,
    val level2Score: Int = 0
    // 之後如果有 Level 3, Level 4 也可以加在這裡
)

class RankingRepository {
    // 使用 StateFlow 存儲當前的分數狀態，這樣 UI 就可以觀察它的變化
    private val _scores = MutableStateFlow(ScoreEntry())
    val scores: StateFlow<ScoreEntry> = _scores.asStateFlow()

    /**
     * 更新指定關卡的最高分數。
     * @param levelId 關卡 ID (1 或 2)
     * @param newScore 玩家獲得的新分數
     */
    fun updateHighScore(levelId: Int, newScore: Int) {
        _scores.value = when (levelId) {
            1 -> {
                // 只更新比當前分數高的成績
                if (newScore > _scores.value.level1Score) {
                    _scores.value.copy(level1Score = newScore)
                } else {
                    _scores.value
                }
            }
            2 -> {
                if (newScore > _scores.value.level2Score) {
                    _scores.value.copy(level2Score = newScore)
                } else {
                    _scores.value
                }
            }
            else -> _scores.value
        }
    }
}