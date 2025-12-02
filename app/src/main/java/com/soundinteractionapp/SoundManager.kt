package com.soundinteractionapp

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

class SoundManager(private val context: Context) {

    // --- 背景音樂 (BGM) 用的 MediaPlayer ---
    private var mediaPlayer: MediaPlayer? = null

    // --- 打擊音效 (SFX) 用的 SoundPool ---
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<String, Int>()

    init {
        // 1. 初始化 SoundPool (設定音效屬性)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME) // 設定用途為遊戲
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5) // 最多同時播放 5 個音效 (避免破音)
            .setAudioAttributes(audioAttributes)
            .build()

        // 2. 預先載入音效檔案 (請確保 res/raw 資料夾有這些檔案)
        // 如果你的檔名不同，請在這裡修改
        loadSound("perfect", R.raw.sfx_perfect)
        loadSound("good", R.raw.sfx_good)
        loadSound("miss", R.raw.sfx_miss)
    }

    /**
     * 載入音效到記憶體
     */
    private fun loadSound(key: String, resId: Int) {
        // priority 設為 1
        soundMap[key] = soundPool.load(context, resId, 1)
    }

    /**
     * 播放短音效 (這就是 Level1Screen 呼叫的函式)
     */
    fun playSFX(name: String) {
        val soundId = soundMap[name]
        if (soundId != null && soundId != 0) {
            // 參數: id, 左聲道, 右聲道, 優先權, 迴圈(0=不迴圈), 速率(1.0=正常)
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    /**
     * 播放背景音樂 (BGM)
     */
    fun playMusic(resId: Int) {
        stopMusic() // 先停止舊的
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true // 設定循環播放
            start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * 釋放所有資源 (當 App 關閉時呼叫)
     */
    fun release() {
        stopMusic()
        soundPool.release()
    }

    fun playSound(resId: Int) {
        // 建立一個臨時的 MediaPlayer 來播放
        val mp = MediaPlayer.create(context, resId)

        // 設定監聽器：當音效播放完畢後，立即釋放資源
        mp.setOnCompletionListener {
            it.release()
        }
        mp.start()
    }
}