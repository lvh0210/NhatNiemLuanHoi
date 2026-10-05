package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.engine.EngineStateTransition
import com.example.engine.GameEngine
import com.example.model.CultivationRealm
import com.example.model.DestinyTrait
import com.example.model.SpiritRoot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * PlayerState data class:
 * Định nghĩa trạng thái và các thuộc tính cốt lõi của người chơi trong vòng lặp tu tiên:
 * - currentAge: Tuổi tác hiện tại của tu sĩ
 * - maxLifespan: Thọ nguyên cực hạn theo cảnh giới và căn cốt
 * - currentCultivationLevel: Cảnh giới tu vi hiện tại (Cultivation Level)
 * - karma: Sát khí / Nghiệp lực
 * - spiritRoots: Phẩm chất linh căn
 * - insight: Ngộ tính
 * - traits: Danh sách các Thiên Mệnh Từ Điều
 */
data class PlayerState(
    val currentAge: Int = 16,
    val maxLifespan: Int = 100,
    val currentCultivationLevel: CultivationRealm = CultivationRealm.LUYEN_KHI,
    val karma: Int = 0,
    val spiritRoots: SpiritRoot = SpiritRoot.THIEN_LINH_CAN,
    val insight: Int = 50,
    val traits: List<DestinyTrait> = emptyList(),
    val subStage: Int = 1,
    val stateTransition: EngineStateTransition = EngineStateTransition.ALIVE,
    val isDead: Boolean = false,
    val deathCause: String = "",
    val currentQi: Long = 15L,
    val maxQi: Long = 100L,
    val congDuc: Int = 150,
    val generation: Int = 1,
    val pillToxicity: Int = 0,
    val anNhanTri: Int = 10,
    val daoTam: Int = 80,
    val thanThuc: Int = 50,
    val canCot: Int = 50,
    val spiritStones: Long = 180L,
    val lastEventLog: String = ""
) {
    val remainingLifespan: Int
        get() = maxOf(0, maxLifespan - currentAge)

    val cultivationLevelName: String
        get() = currentCultivationLevel.getDisplayName(subStage)

    val satKhi: Int get() = karma
    val spiritRoot: SpiritRoot get() = spiritRoots
    val ngoTinh: Int get() = insight
    val activeTraits: List<DestinyTrait> get() = traits

    fun toGameEngine(): GameEngine = GameEngine(
        currentAge = currentAge,
        maxLifespan = maxLifespan,
        currentCultivationLevel = currentCultivationLevel,
        karma = karma,
        spiritRoots = spiritRoots,
        insight = insight,
        traits = traits,
        subStage = subStage,
        stateTransition = stateTransition,
        isDead = isDead,
        deathCause = deathCause,
        currentQi = currentQi,
        maxQi = maxQi,
        congDuc = congDuc,
        generation = generation,
        pillToxicity = pillToxicity,
        anNhanTri = anNhanTri,
        daoTam = daoTam,
        thanThuc = thanThuc,
        canCot = canCot,
        spiritStones = spiritStones,
        lastEventLog = lastEventLog
    )
}

fun GameEngine.toPlayerState(): PlayerState = PlayerState(
    currentAge = currentAge,
    maxLifespan = maxLifespan,
    currentCultivationLevel = currentCultivationLevel,
    karma = karma,
    spiritRoots = spiritRoots,
    insight = insight,
    traits = traits,
    subStage = subStage,
    stateTransition = stateTransition,
    isDead = isDead,
    deathCause = deathCause,
    currentQi = currentQi,
    maxQi = maxQi,
    congDuc = congDuc,
    generation = generation,
    pillToxicity = pillToxicity,
    anNhanTri = anNhanTri,
    daoTam = daoTam,
    thanThuc = thanThuc,
    canCot = canCot,
    spiritStones = spiritStones,
    lastEventLog = lastEventLog
)

/**
 * GameEngineViewModel:
 * Quản lý vòng lặp chính của người chơi (Player Loop) bằng MutableStateFlow<PlayerState>:
 * - Quản lý 'playerState' StateFlow
 * - Cung cấp hàm 'advanceYear()' tăng 1 năm tuổi và kiểm tra 'if (currentAge >= maxLifespan) triggerToaHoa()'
 * - Cung cấp cơ chế Tọa Hóa 'triggerToaHoa()' chuyển hóa thành tựu thành Công Đức cho kiếp sau
 */
open class GameEngineViewModel : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // Thuộc tính tương thích cho GameEngine StateFlow
    val engineState: StateFlow<PlayerState> get() = playerState

    val currentAge: Int get() = _playerState.value.currentAge
    val maxLifespan: Int get() = _playerState.value.maxLifespan
    val currentCultivationLevel: CultivationRealm get() = _playerState.value.currentCultivationLevel
    val stateTransition: EngineStateTransition get() = _playerState.value.stateTransition

    fun updatePlayerState(newState: PlayerState) {
        _playerState.value = newState
        onPlayerStateUpdated(newState)
    }

    fun updateEngineState(newState: PlayerState) {
        updatePlayerState(newState)
    }

    fun updateEngineState(newState: GameEngine) {
        updatePlayerState(newState.toPlayerState())
    }

    protected open fun onPlayerStateUpdated(newState: PlayerState) {}

    /**
     * Vòng lặp tu luyện chính của người chơi: Tăng 1 năm tuổi (advanceYear)
     * Tăng 'currentAge' và kiểm tra 'if (currentAge >= maxLifespan) triggerToaHoa()'
     */
    fun advanceYear() {
        val current = _playerState.value
        if (current.isDead) return

        val newAge = current.currentAge + 1

        // Hấp thu linh khí qua 1 năm
        val rootMultiplier = current.spiritRoots.speedMultiplier
        val toxicityPenalty = (current.pillToxicity / 100f) * 0.5f
        val effectiveRate = maxOf(0.1f, rootMultiplier - toxicityPenalty)
        val gainedQi = (15L * effectiveRate).toLong()
        val newQi = minOf(current.maxQi, current.currentQi + gainedQi)

        _playerState.value = current.copy(
            currentAge = newAge,
            currentQi = newQi,
            stateTransition = if (newAge >= current.maxLifespan) EngineStateTransition.TOA_HOA else EngineStateTransition.BE_QUAN,
            lastEventLog = "Thời gian trôi qua 1 năm. Hấp thu $gainedQi Linh Khí. Tuổi: $newAge/${current.maxLifespan}."
        )

        // KIỂM TRA THỌ NGUYÊN: Kích hoạt Tọa Hóa khi chạm hạn mức
        if (newAge >= current.maxLifespan) {
            triggerToaHoa()
        }
    }

    /**
     * Kích hoạt sự kiện 'Tọa Hóa' (chết già khi currentAge >= maxLifespan):
     * - Cập nhật stateTransition = EngineStateTransition.TOA_HOA
     * - Đánh dấu isDead = true
     * - Quy đổi điểm thành tựu kiếp này thành Công Đức cho kiếp sau
     */
    fun triggerToaHoa() {
        val s = _playerState.value
        val earnedCongDuc = 50 + (s.currentCultivationLevel.ordinal * 100) + (s.subStage * 15) + (s.currentAge / 2)
        val newCongDuc = s.congDuc + earnedCongDuc

        val deathMsg = "Năm ${s.maxLifespan} tuổi, thọ nguyên cạn kiệt, ngươi tại động phủ hóa đạo tọa hóa! Khai mở Luân Hồi Kính, nhận $earnedCongDuc điểm Công Đức cho kiếp sau."
        val updated = s.copy(
            currentAge = s.maxLifespan,
            isDead = true,
            deathCause = "Thọ nguyên cạn kiệt (Tọa Hóa) tại tuổi ${s.maxLifespan}",
            congDuc = newCongDuc,
            stateTransition = EngineStateTransition.TOA_HOA,
            lastEventLog = deathMsg
        )
        updatePlayerState(updated)
        onToaHoa(earnedCongDuc, deathMsg)
    }

    protected open fun onToaHoa(earnedCongDuc: Int, message: String) {}

    /**
     * Tiến triển vòng lặp game bế quan nhiều năm
     */
    fun advanceGameLoop(yearsRequested: Int) {
        val s = _playerState.value
        if (s.isDead) return

        val remainingLifespan = maxOf(0, s.maxLifespan - s.currentAge)
        if (remainingLifespan <= 0) {
            triggerToaHoa()
            return
        }

        val actualYears = minOf(yearsRequested, remainingLifespan)
        for (i in 1..actualYears) {
            if (_playerState.value.isDead) break
            advanceYear()
        }
    }

    /**
     * Đột phá cảnh giới (Breakthrough)
     */
    fun attemptBreakthrough() {
        val s = _playerState.value
        if (s.isDead) return

        if (s.currentQi < s.maxQi) {
            updatePlayerState(
                s.copy(
                    lastEventLog = "Linh khí chưa viên mãn (${s.currentQi}/${s.maxQi}), chưa thể đột phá!"
                )
            )
            return
        }

        val isMajor = s.subStage >= s.currentCultivationLevel.maxSubStages
        if (isMajor) {
            updatePlayerState(
                s.copy(
                    stateTransition = EngineStateTransition.TRIBULATION,
                    lastEventLog = "Đại bình cảnh! Cửu Thiên Lôi Kiếp sắp giáng thế!"
                )
            )
        } else {
            val rate = s.currentCultivationLevel.breakthroughBaseRate + (s.insight * 0.002f)
            val roll = Random.nextFloat()
            if (roll <= rate) {
                val newSub = s.subStage + 1
                val newMaxQi = (s.maxQi * 1.35f).toLong()
                updatePlayerState(
                    s.copy(
                        subStage = newSub,
                        currentQi = 0L,
                        maxQi = newMaxQi,
                        daoTam = minOf(100, s.daoTam + 2),
                        stateTransition = EngineStateTransition.BREAKTHROUGH,
                        lastEventLog = "Đột phá thành công ${s.currentCultivationLevel.getDisplayName(newSub)}!"
                    )
                )
            } else {
                val lostQi = (s.maxQi * 0.35f).toLong()
                updatePlayerState(
                    s.copy(
                        currentQi = maxOf(0L, s.currentQi - lostQi),
                        stateTransition = EngineStateTransition.ALIVE,
                        lastEventLog = "Đột phá thất bại! Chân khí phản phệ mất $lostQi Linh Khí."
                    )
                )
            }
        }
    }

    /**
     * Hoàn thành Lôi Kiếp và đột phá Đại Cảnh Giới:
     * Tăng cảnh giới và gia tăng Thọ Nguyên tối đa (maxLifespan)
     */
    fun completeMajorBreakthrough() {
        val s = _playerState.value
        val nextOrdinal = s.currentCultivationLevel.ordinal + 1
        if (nextOrdinal < CultivationRealm.entries.size) {
            val nextRealm = CultivationRealm.entries[nextOrdinal]
            val lifespanBonus = when (nextRealm) {
                CultivationRealm.TRUC_CO -> 150
                CultivationRealm.KIM_DAN -> 250
                CultivationRealm.NGUYEN_ANH -> 700
                CultivationRealm.HOA_THAN -> 1800
                else -> 50
            }
            val newMaxLifespan = s.maxLifespan + lifespanBonus
            val newMaxQi = nextRealm.baseQiRequirement

            updatePlayerState(
                s.copy(
                    currentCultivationLevel = nextRealm,
                    subStage = 1,
                    currentQi = 0L,
                    maxQi = newMaxQi,
                    maxLifespan = newMaxLifespan,
                    daoTam = minOf(100, s.daoTam + 15),
                    thanThuc = s.thanThuc + 25,
                    canCot = s.canCot + 20,
                    stateTransition = EngineStateTransition.BREAKTHROUGH,
                    lastEventLog = "Đăng lâm cảnh giới [${nextRealm.realmName}]! Tăng thêm $lifespanBonus năm thọ nguyên!"
                )
            )
        }
    }

    /**
     * Tái sinh kiếp sau từ Luân Hồi Kính
     */
    fun reincarnate(
        name: String = "Tiêu Phàm",
        spiritRoot: SpiritRoot = SpiritRoot.THIEN_LINH_CAN,
        lifespan: Int = 100,
        initialCongDuc: Int = _playerState.value.congDuc
    ) {
        val current = _playerState.value
        val updated = current.copy(
            currentAge = 16,
            maxLifespan = lifespan,
            currentCultivationLevel = CultivationRealm.LUYEN_KHI,
            subStage = 1,
            currentQi = 25L,
            maxQi = CultivationRealm.LUYEN_KHI.baseQiRequirement,
            generation = current.generation + 1,
            spiritRoots = spiritRoot,
            congDuc = initialCongDuc,
            isDead = false,
            deathCause = "",
            pillToxicity = 0,
            stateTransition = EngineStateTransition.REINCARNATION,
            lastEventLog = "Khai mở Luân Hồi Kính, chuyển thế tái sinh ở Kiếp Thứ ${current.generation + 1}!"
        )
        updatePlayerState(updated)
    }
}
