package com.soundinteractionapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 這是一個單例物件 (Singleton)，用來連接 MainActivity (實體按鍵) 與 Compose (遊戲畫面)。
 */
object GameInputManager {
    // 使用 SharedFlow 來廣播按鍵事件，extraBufferCapacity = 1 確保沒人聽時不會卡住
    private val _keyEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val keyEvents = _keyEvents.asSharedFlow()

    // 當 MainActivity 收到按鍵時，呼叫此方法發送訊號
    fun triggerBeat() {
        _keyEvents.tryEmit(Unit)
    }
}

