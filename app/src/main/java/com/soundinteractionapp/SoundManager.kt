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
        // 1. 初始化 SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // 2. 預先載入音效 (請確保 res/raw 資料夾有這些檔案)
        // 如果您的檔名不同，請在這裡修改
        // 注意：這裡假設您有 sfx_perfect, sfx_good, sfx_miss 這些檔案
        // 如果沒有，請註解掉以免報錯
        try {
            loadSound("perfect", R.raw.sfx_perfect)
            loadSound("good", R.raw.sfx_good)
            loadSound("miss", R.raw.sfx_miss)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSound(key: String, resId: Int) {
        soundMap[key] = soundPool.load(context, resId, 1)
    }

    /**
     * 播放預先載入的短音效 (使用 SoundPool)
     * 用於遊戲判定 (Perfect, Good, Miss)
     */
    fun playSFX(name: String) {
        val soundId = soundMap[name]
        if (soundId != null && soundId != 0) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    /**
     * 播放背景音樂 (BGM)
     * 專用於遊戲關卡，設定為不循環
     */
    fun playMusic(resId: Int) {
        stopMusic() // 先停止舊的
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = false // 節奏遊戲通常不迴圈
            start()

            setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
        }
    }

    fun stopMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun release() {
        stopMusic()
        soundPool.release()
    }

    // ✅ 這就是您缺少的函式：直接用 ID 播放聲音
    fun playSound(resId: Int) {
        try {
            val mp = MediaPlayer.create(context, resId)
            mp?.setOnCompletionListener {
                it.release()
            }
            mp?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}