package com.soundinteractionapp.screens.game.levels

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundinteractionapp.R
import com.soundinteractionapp.SoundManager
import com.soundinteractionapp.utils.GameInputManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.compose.runtime.withFrameMillis
import kotlin.math.abs

// ----------------------------------------------------------------------
// 1. 資料結構與狀態
// ----------------------------------------------------------------------
data class Note(val id: Long, val targetTime: Long, var isHit: Boolean = false)
enum class GameState { COUNTDOWN, PLAYING, FINISHED }

// ----------------------------------------------------------------------
// 2. 完整譜面 (3分37秒) - 保持不變
// ----------------------------------------------------------------------
val CANON_CHART = listOf(
    Note(1, 2449), Note(2, 4444), Note(3, 6667), Note(4, 8844), Note(5, 11020),
    Note(6, 13333), Note(7, 15556), Note(8, 17732), Note(9, 19955), Note(10, 22268),
    Note(11, 24580), Note(12, 26848), Note(13, 29070), Note(14, 31202), Note(15, 33424),
    Note(16, 35556), Note(17, 37914), Note(18, 40136), Note(19, 42222), Note(20, 44444),
    Note(21, 46440), Note(22, 48662), Note(23, 50748), Note(24, 52971), Note(25, 55193),
    Note(26, 56100), Note(27, 57143), Note(28, 58095), Note(29, 59138), Note(30, 60181),
    Note(31, 61088), Note(32, 62177), Note(33, 63084), Note(34, 64127), Note(35, 65079),
    Note(36, 66077), Note(37, 66893), Note(38, 67891), Note(39, 68889), Note(40, 69841),
    Note(41, 70748), Note(42, 71202), Note(43, 71701), Note(44, 72200), Note(45, 72608),
    Note(46, 73016), Note(47, 73515), Note(48, 73923), Note(49, 74422), Note(50, 74830),
    Note(51, 75238), Note(52, 75692), Note(53, 76145), Note(54, 76553), Note(55, 77007),
    Note(56, 77460), Note(57, 77914), Note(58, 78367), Note(59, 78776), Note(60, 79229),
    Note(61, 79683), Note(62, 80136), Note(63, 80590), Note(64, 80998), Note(65, 81451),
    Note(66, 81905), Note(67, 82358), Note(68, 82812), Note(69, 83311), Note(70, 83719),
    Note(71, 84127), Note(72, 84580), Note(73, 85034), Note(74, 85488), Note(75, 85896),
    Note(76, 86349), Note(77, 86803), Note(78, 87211), Note(79, 87664), Note(80, 88073),
    Note(81, 88526), Note(82, 88934), Note(83, 89342), Note(84, 89796), Note(85, 90249),
    Note(86, 90703), Note(87, 91111), Note(88, 91565), Note(89, 92018), Note(90, 92472),
    Note(91, 92880), Note(92, 93243), Note(93, 93696), Note(94, 94104), Note(95, 94558),
    Note(96, 94966), Note(97, 95465), Note(98, 95873), Note(99, 96327), Note(100, 96735),
    Note(101, 97234), Note(102, 97732), Note(103, 98231), Note(104, 99320), Note(105, 103900),
    Note(106, 104263), Note(107, 104580), Note(108, 104989), Note(109, 105442), Note(110, 105714),
    Note(111, 105941), Note(112, 106168), Note(113, 106395), Note(114, 106621), Note(115, 106848),
    Note(116, 107075), Note(117, 107256), Note(118, 107619), Note(119, 107846), Note(120, 108118),
    Note(121, 108481), Note(122, 108753), Note(123, 108980), Note(124, 109206), Note(125, 109433),
    Note(126, 109660), Note(127, 109887), Note(128, 110113), Note(129, 110295), Note(130, 110522),
    Note(131, 110748), Note(132, 111111), Note(133, 111338), Note(134, 111565), Note(135, 111973),
    Note(136, 112154), Note(137, 112426), Note(138, 112608), Note(139, 112834), Note(140, 113016),
    Note(141, 113243), Note(142, 113424), Note(143, 113651), Note(144, 113878), Note(145, 114104),
    Note(146, 114467), Note(147, 114694), Note(148, 114921), Note(149, 115329), Note(150, 115556),
    Note(151, 115782), Note(152, 116009), Note(153, 116190), Note(154, 116417), Note(155, 116599),
    Note(156, 116825), Note(157, 117052), Note(158, 117279), Note(159, 117506), Note(160, 117868),
    Note(161, 118050), Note(162, 118322), Note(163, 118730), Note(164, 118957), Note(165, 119229),
    Note(166, 119410), Note(167, 119637), Note(168, 119819), Note(169, 120091), Note(170, 120272),
    Note(171, 120499), Note(172, 120726), Note(173, 120907), Note(174, 121315), Note(175, 121497),
    Note(176, 121723), Note(177, 122132), Note(178, 122358), Note(179, 122585), Note(180, 122812),
    Note(181, 123039), Note(182, 123265), Note(183, 123447), Note(184, 123673), Note(185, 123900),
    Note(186, 124082), Note(187, 124308), Note(188, 124671), Note(189, 124853), Note(190, 125125),
    Note(191, 125488), Note(192, 125714), Note(193, 125941), Note(194, 126168), Note(195, 126349),
    Note(196, 126576), Note(197, 126757), Note(198, 126984), Note(199, 127166), Note(200, 127392),
    Note(201, 127619), Note(202, 127982), Note(203, 128209), Note(204, 128435), Note(205, 128844),
    Note(206, 129070), Note(207, 129297), Note(208, 129524), Note(209, 129751), Note(210, 129977),
    Note(211, 130159), Note(212, 130385), Note(213, 130612), Note(214, 130794), Note(215, 131020),
    Note(216, 131429), Note(217, 131655), Note(218, 131927), Note(219, 132336), Note(220, 132562),
    Note(221, 132789), Note(222, 133016), Note(223, 133243), Note(224, 133469), Note(225, 133651),
    Note(226, 133878), Note(227, 134104), Note(228, 134286), Note(229, 134558), Note(230, 134921),
    Note(231, 135147), Note(232, 135374), Note(233, 135828), Note(234, 136054), Note(235, 136281),
    Note(236, 136508), Note(237, 136780), Note(238, 136961), Note(239, 137234), Note(240, 137460),
    Note(241, 137687), Note(242, 137868), Note(243, 138095), Note(244, 138503), Note(245, 138730),
    Note(246, 138957), Note(247, 139365), Note(248, 139592), Note(249, 139819), Note(250, 140045),
    Note(251, 140272), Note(252, 140499), Note(253, 140771), Note(254, 140998), Note(255, 141224),
    Note(256, 141451), Note(257, 141633), Note(258, 142086), Note(259, 142268), Note(260, 142540),
    Note(261, 142902), Note(262, 143129), Note(263, 143356), Note(264, 143628), Note(265, 143855),
    Note(266, 144082), Note(267, 144308), Note(268, 144580), Note(269, 144807), Note(270, 145125),
    Note(271, 145351), Note(272, 146122), Note(273, 146349), Note(274, 146757), Note(275, 146984),
    Note(276, 147256), Note(277, 147664), Note(278, 148027), Note(279, 148435), Note(280, 148662),
    Note(281, 148934), Note(282, 149388), Note(283, 149615), Note(284, 149841), Note(285, 150249),
    Note(286, 150658), Note(287, 151111), Note(288, 151338), Note(289, 151610), Note(290, 152018),
    Note(291, 152472), Note(292, 152880), Note(293, 153107), Note(294, 153288), Note(295, 153741),
    Note(296, 154150), Note(297, 154603), Note(298, 154785), Note(299, 155011), Note(300, 155465),
    Note(301, 155918), Note(302, 156372), Note(303, 156553), Note(304, 156780), Note(305, 157234),
    Note(306, 157642), Note(307, 158095), Note(308, 158322), Note(309, 158549), Note(310, 158957),
    Note(311, 159410), Note(312, 161134), Note(313, 161950), Note(314, 162313), Note(315, 162721),
    Note(316, 163039), Note(317, 163401), Note(318, 163810), Note(319, 164218), Note(320, 164626),
    Note(321, 164989), Note(322, 165351), Note(323, 165714), Note(324, 166122), Note(325, 166485),
    Note(326, 166848), Note(327, 167664), Note(328, 168390), Note(329, 168753), Note(330, 169161),
    Note(331, 169569), Note(332, 169977), Note(333, 170385), Note(334, 170748), Note(335, 171066),
    Note(336, 171429), Note(337, 171791), Note(338, 172154), Note(339, 172562), Note(340, 172925),
    Note(341, 173696), Note(342, 174376), Note(343, 174739), Note(344, 175147), Note(345, 175465),
    Note(346, 175873), Note(347, 176236), Note(348, 176689), Note(349, 177370), Note(350, 177687),
    Note(351, 178095), Note(352, 178458), Note(353, 178866), Note(354, 179683), Note(355, 180045),
    Note(356, 180499), Note(357, 180862), Note(358, 181179), Note(359, 181587), Note(360, 181950),
    Note(361, 182358), Note(362, 182721), Note(363, 183084), Note(364, 183492), Note(365, 183855),
    Note(366, 184263), Note(367, 184626), Note(368, 185034), Note(369, 185442), Note(370, 185805),
    Note(371, 186168), Note(372, 186576), Note(373, 186939), Note(374, 187256), Note(375, 187664),
    Note(376, 188390), Note(377, 188798), Note(378, 189161), Note(379, 190023), Note(380, 190385),
    Note(381, 190748), Note(382, 191610), Note(383, 191927), Note(384, 192336), Note(385, 193197),
    Note(386, 193560), Note(387, 193968), Note(388, 194785), Note(389, 195147), Note(390, 195556),
    Note(391, 196508), Note(392, 196871), Note(393, 197370), Note(394, 199864), Note(395, 201678),
    Note(396, 203764), Note(397, 205941), Note(398, 208209), Note(399, 210340), Note(400, 212608),
    Note(401, 214785), Note(402, 217007)
)

// ----------------------------------------------------------------------
// 3. 遊戲主畫面
// ----------------------------------------------------------------------

@Composable
fun Level1FollowBeatScreen(
    onNavigateBack: () -> Unit,
    soundManager: SoundManager
) {
    // --- 參數設定 ---
    val noteSpeed = 0.4f
    val judgeLineX = 250f
    val trackHeight = 160f

    // --- 狀態管理 ---
    var gameState by remember { mutableStateOf(GameState.COUNTDOWN) }
    var score by remember { mutableStateOf(0) }
    var combo by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var countdownValue by remember { mutableStateOf(3) }

    // ✨ 軌道邊框顏色
    var trackBorderColor by remember { mutableStateOf(Color.White.copy(alpha = 0.5f)) }

    // ✨ 打擊特效顏色
    var effectColor by remember { mutableStateOf(Color.White) }

    // ✨ 定義淡粉色 (Pale Pink)
    val palePink = Color(0xFFF48FB1)

    // 譜面
    val totalNotes = remember {
        mutableStateListOf<Note>().apply {
            addAll(CANON_CHART.map { it.copy(isHit = false) })
        }
    }

    var startTime by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }

    // 動畫變數
    val hitEffectScale = remember { Animatable(1f) }

    // --- 隨機語錄清單 ---
    val perfectPhrases = remember { listOf("太棒了!", "太厲害了吧", "是個高手") }
    val goodPhrases = remember { listOf("差一點呀", "很接近了!") }
    val missPhrases = remember { listOf("好可惜呀", "加把勁", "還是菜鳥呢") }

    // --- 軌道顏色自動還原 ---
    LaunchedEffect(trackBorderColor) {
        if (trackBorderColor != Color.White.copy(alpha = 0.5f)) {
            delay(200)
            trackBorderColor = Color.White.copy(alpha = 0.5f)
        }
    }

    // --- 遊戲流程 ---
    LaunchedEffect(Unit) {
        feedbackText = "Ready..."
        delay(500)
        while (countdownValue > 0) {
            delay(1000)
            countdownValue--
        }
        feedbackText = "GO!"
        gameState = GameState.PLAYING
        soundManager.playMusic(R.raw.canon)
        startTime = System.currentTimeMillis()

        while (gameState == GameState.PLAYING && isActive) {
            currentTime = System.currentTimeMillis() - startTime

            // Miss 檢查
            totalNotes.forEach { note ->
                if (!note.isHit && (currentTime - note.targetTime > 200)) {
                    note.isHit = true
                    combo = 0

                    // ✨ 隨機 Miss 語錄 (紅色)
                    feedbackText = missPhrases.random()
                    effectColor = Color.Red
                    trackBorderColor = Color.Red
                }
            }
            withFrameMillis { }
        }
    }

    // --- 輸入監聽 ---
    LaunchedEffect(Unit) {
        GameInputManager.keyEvents.collectLatest {
            if (gameState != GameState.PLAYING) return@collectLatest

            val targetNote = totalNotes.firstOrNull { note ->
                !note.isHit && abs(note.targetTime - currentTime) < 150
            }

            if (targetNote != null) {
                val diff = abs(targetNote.targetTime - currentTime)
                targetNote.isHit = true

                if (diff < 60) {
                    // --- PERFECT (淡粉色) ---
                    score += 100
                    combo++

                    feedbackText = perfectPhrases.random()
                    effectColor = palePink
                    trackBorderColor = palePink

                    // 動畫: 大爆發
                    launch {
                        hitEffectScale.snapTo(1.5f)
                        hitEffectScale.animateTo(1f, animationSpec = tween(300))
                    }

                } else {
                    // --- GOOD (青色) ---
                    score += 50
                    combo++

                    feedbackText = goodPhrases.random()
                    effectColor = Color.Cyan
                    trackBorderColor = Color.Cyan

                    // 動畫: 小彈跳
                    launch {
                        hitEffectScale.snapTo(1.2f)
                        hitEffectScale.animateTo(1f, animationSpec = tween(150))
                    }
                }
            }
        }
    }

    // --- 畫面繪製 ---
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF222222)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // (A) 倒數畫面
            if (gameState == GameState.COUNTDOWN) {
                Text(
                    text = if (countdownValue > 0) "$countdownValue" else "GO!",
                    fontSize = 120.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // (B) 遊戲畫面
            if (gameState == GameState.PLAYING) {
                // ✨ 1. 分數與 Combo (移至頂部)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("分數: $score", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text("完美連續: $combo", style = MaterialTheme.typography.displayMedium, color = Color.White)
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerY = size.height / 2

                    // 1. 動態軌道
                    drawRect(
                        color = Color.White.copy(alpha = 0.2f),
                        topLeft = Offset(0f, centerY - trackHeight / 2),
                        size = Size(size.width, trackHeight)
                    )
                    drawLine(
                        color = trackBorderColor,
                        start = Offset(0f, centerY - trackHeight / 2),
                        end = Offset(size.width, centerY - trackHeight / 2),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = trackBorderColor,
                        start = Offset(0f, centerY + trackHeight / 2),
                        end = Offset(size.width, centerY + trackHeight / 2),
                        strokeWidth = 4f
                    )

                    // 2. 判定圈
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 60f,
                        center = Offset(judgeLineX, centerY),
                        style = Stroke(width = 4f)
                    )

                    // 3. 打擊特效
                    if (hitEffectScale.value > 1.0f) {
                        drawCircle(
                            color = effectColor,
                            radius = 60f * hitEffectScale.value,
                            center = Offset(judgeLineX, centerY),
                            style = Stroke(width = 8f)
                        )
                    }

                    // 4. 音符
                    totalNotes.forEach { note ->
                        if (!note.isHit) {
                            val noteX = judgeLineX + (note.targetTime - currentTime) * noteSpeed
                            if (noteX > -100 && noteX < size.width + 200) {
                                drawCircle(
                                    color = Color(0xFFFF5252),
                                    radius = 40f,
                                    center = Offset(noteX, centerY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 40f,
                                    center = Offset(noteX, centerY),
                                    style = Stroke(width = 4f)
                                )
                            }
                        }
                    }
                }

                // ✨ 2. 隨機語錄 (移至軌道下方)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 220.dp) // 位於軌道下方
                ) {
                    Text(
                        text = feedbackText,
                        style = MaterialTheme.typography.headlineLarge,
                        color = effectColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // 退出按鈕
            Button(
                onClick = {
                    soundManager.stopMusic()
                    onNavigateBack()
                },
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Text("退出")
            }
        }
    }
}