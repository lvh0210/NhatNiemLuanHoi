package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PlayerEntity
import com.example.engine.EngineStateTransition
import com.example.engine.GameEngine
import com.example.engine.GameEngineCore
import com.example.model.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GameLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val year: Int,
    val tag: String,
    val message: String,
    val type: String // INFO, SUCCESS, WARNING, DANGER, TRIBULATION
)

data class TribulationState(
    val isActive: Boolean = false,
    val currentWave: Int = 1,
    val totalWaves: Int = 3,
    val waveDamage: Int = 100,
    val isWaitingTamMa: Boolean = false,
    val tamMaQuestion: String = "",
    val tamMaChoiceA: String = "",
    val tamMaChoiceB: String = "",
    val successAscend: Boolean = false
)

data class SoulClashState(
    val isActive: Boolean = false,
    val demonName: String = "U Minh Lão Ma",
    val playerWill: Int = 100,
    val demonWill: Int = 100,
    val narrative: String = ""
)

data class GameUiState(
    val name: String = "Tiêu Phàm",
    val generation: Int = 1,
    val age: Int = 16,
    val maxLifespan: Int = 100,
    val realm: CultivationRealm = CultivationRealm.LUYEN_KHI,
    val subStage: Int = 1,
    val qi: Long = 15L,
    val maxQi: Long = 100L,
    val qiState: QiState = QiState.BINH_ON,
    val pillToxicity: Int = 0, // 0 - 100%
    val satKhi: Int = 0,       // Nghiệp Lực / Sát Khí
    val hungDanh: Int = 0,     // Trục Hung Danh (Ma Tu) vs Ẩn Nhẫn (Cẩu Tu)
    val anNhanTri: Int = 10,   // Ẩn Nhẫn Trị / Cẩu Đạo
    val daoTam: Int = 80,      // Đạo Tâm (0-100)
    val spiritStones: Long = 180L,
    val spiritRoot: SpiritRoot = SpiritRoot.THIEN_LINH_CAN,
    val congDuc: Int = 150,
    val thanThuc: Int = 50,
    val canCot: Int = 50,
    val ngoTinh: Int = 50,
    val caveGrade: CaveGrade = CaveGrade.HA_PHAM,
    val tuLinhTranLevel: Int = 1,
    val sealedVault: SealedVault? = null,
    val hasVengefulGhost: Boolean = false,
    val sectContribution: Int = 60,
    val hasRemnantSoul: Boolean = true,
    val remnantSoulBond: Int = 40,
    val remnantSoulPower: Int = 35,
    val hasNguHanhCongPhap: Boolean = false,
    val beastEgg: BeastEggData = BeastEggData(),
    val flameFusion: FlameFusionState = FlameFusionState(),
    val activeTraits: List<DestinyTrait> = emptyList(),
    val inventory: List<InventoryItem> = emptyList(),
    val disciples: List<Disciple> = emptyList(),
    val logs: List<GameLogEntry> = emptyList(),
    val pastLives: List<PastLifeRecord> = emptyList(),
    val currentDivination: DivinationEvent? = null,
    val activeDarkEvent: DarkCultivationEvent? = null,
    val darkEventResultNarrative: String? = null,
    val tribulation: TribulationState = TribulationState(),
    val soulClash: SoulClashState = SoulClashState(),
    val showLuanHoiDialog: Boolean = false,
    val isDead: Boolean = false,
    val deathCause: String = "",
    val stateTransition: EngineStateTransition = if (isDead) EngineStateTransition.TOA_HOA else EngineStateTransition.ALIVE
) {
    val currentAge: Int get() = age
    val currentCultivationLevel: CultivationRealm get() = realm

    fun toGameEngine(): GameEngine = GameEngine(
        currentAge = age,
        maxLifespan = maxLifespan,
        currentCultivationLevel = realm,
        karma = satKhi,
        spiritRoots = spiritRoot,
        insight = ngoTinh,
        traits = activeTraits,
        subStage = subStage,
        stateTransition = stateTransition,
        isDead = isDead,
        deathCause = deathCause,
        currentQi = qi,
        maxQi = maxQi,
        congDuc = congDuc,
        generation = generation,
        pillToxicity = pillToxicity,
        anNhanTri = anNhanTri,
        daoTam = daoTam,
        thanThuc = thanThuc,
        canCot = canCot,
        spiritStones = spiritStones
    )
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.playerDao()

    // GameEngineCore quản lý game loop và StateFlow trạng thái trò chơi
    val gameEngine = GameEngineCore(
        onStateChanged = { newState ->
            saveStateToDb(newState)
        }
    )

    // StateFlow kết nối từ GameEngine cho Jetpack Compose UI
    val uiState: StateFlow<GameUiState> = gameEngine.gameState

    init {
        loadOrInitGame()
    }

    private fun loadOrInitGame() {
        viewModelScope.launch {
            val entity = dao.getPlayerStateSync()
            if (entity != null) {
                restoreFromEntity(entity)
            } else {
                initDefaultGame()
            }
        }
    }

    private fun initDefaultGame() {
        val root = SpiritRoot.entries.random()
        val baseLifespan = CultivationRealm.LUYEN_KHI.baseLifespan
        val traits = gameEngine.defaultTraits()

        val initialLogs = listOf(
            GameLogEntry(
                year = 16,
                tag = "Luân Hồi",
                message = "Ngươi chuyển thế tái sinh ở Kiếp Thứ 1, nắm giữ linh căn [${root.title}]. " +
                        "Mang theo ký ức luân hồi bắt đầu bước lên con đường nghịch thiên!",
                type = "SUCCESS"
            )
        )

        gameEngine.updateState(
            GameUiState(
                name = "Tiêu Phàm",
                generation = 1,
                age = 16,
                maxLifespan = baseLifespan,
                realm = CultivationRealm.LUYEN_KHI,
                subStage = 1,
                qi = 25L,
                maxQi = CultivationRealm.LUYEN_KHI.baseQiRequirement,
                qiState = QiState.BINH_ON,
                pillToxicity = 0,
                satKhi = 0,
                anNhanTri = 15,
                daoTam = 80,
                spiritStones = 200L,
                spiritRoot = root,
                congDuc = 150,
                thanThuc = 50,
                canCot = 50,
                ngoTinh = 50,
                hasRemnantSoul = true,
                remnantSoulBond = 40,
                remnantSoulPower = 30,
                activeTraits = traits,
                inventory = gameEngine.defaultInventory(),
                disciples = gameEngine.defaultDisciples(),
                logs = initialLogs,
                isDead = false,
                showLuanHoiDialog = true // Show Character Initialization on first launch!
            )
        )
    }

    private fun restoreFromEntity(entity: PlayerEntity) {
        val currentRealm = CultivationRealm.entries.getOrElse(entity.realmIndex) { CultivationRealm.LUYEN_KHI }
        val root = try { SpiritRoot.valueOf(entity.spiritRootName) } catch (e: Exception) { SpiritRoot.THIEN_LINH_CAN }
        val qiStateEnum = try { QiState.valueOf(entity.qiState) } catch (e: Exception) { QiState.BINH_ON }

        gameEngine.updateState(
            GameUiState(
                name = entity.name,
                generation = entity.generation,
                age = entity.age,
                maxLifespan = entity.maxLifespan,
                realm = currentRealm,
                subStage = entity.subStage,
                qi = entity.qi,
                maxQi = entity.maxQi,
                qiState = qiStateEnum,
                pillToxicity = entity.pillToxicity,
                satKhi = entity.satKhi,
                anNhanTri = entity.anNhanTri,
                daoTam = entity.daoTam,
                spiritStones = entity.spiritStones,
                spiritRoot = root,
                congDuc = entity.congDuc,
                thanThuc = entity.thanThuc,
                canCot = entity.canCot,
                ngoTinh = entity.ngoTinh,
                hasRemnantSoul = entity.hasRemnantSoul,
                remnantSoulBond = entity.remnantSoulBond,
                remnantSoulPower = entity.remnantSoulPower,
                hasNguHanhCongPhap = entity.hasNguHanhCongPhap,
                beastEgg = BeastEggData(
                    bloodNourishCount = entity.eggBloodCount,
                    isHatched = entity.eggHatched,
                    beastName = if (entity.eggBeastName.isNotEmpty()) entity.eggBeastName else null
                ),
                flameFusion = FlameFusionState(
                    currentTier = entity.flameTier,
                    bonusDmgPercent = entity.flameBonusDmg
                ),
                inventory = gameEngine.defaultInventory(),
                disciples = gameEngine.defaultDisciples(),
                logs = listOf(
                    GameLogEntry(
                        year = entity.age,
                        tag = "Tỉnh Giấc",
                        message = "Ngươi mở mắt từ động phủ, tiếp tục hành trình tu đạo tại kiếp thứ ${entity.generation}.",
                        type = "INFO"
                    )
                )
            )
        )
    }

    private fun saveStateToDb(s: GameUiState) {
        viewModelScope.launch {
            val entity = PlayerEntity(
                name = s.name,
                generation = s.generation,
                age = s.age,
                maxLifespan = s.maxLifespan,
                realmIndex = s.realm.ordinal,
                subStage = s.subStage,
                qi = s.qi,
                maxQi = s.maxQi,
                qiState = s.qiState.name,
                pillToxicity = s.pillToxicity,
                satKhi = s.satKhi,
                anNhanTri = s.anNhanTri,
                daoTam = s.daoTam,
                spiritStones = s.spiritStones,
                spiritRootName = s.spiritRoot.name,
                congDuc = s.congDuc,
                thanThuc = s.thanThuc,
                canCot = s.canCot,
                ngoTinh = s.ngoTinh,
                hasRemnantSoul = s.hasRemnantSoul,
                remnantSoulBond = s.remnantSoulBond,
                remnantSoulPower = s.remnantSoulPower,
                hasNguHanhCongPhap = s.hasNguHanhCongPhap,
                eggBloodCount = s.beastEgg.bloodNourishCount,
                eggHatched = s.beastEgg.isHatched,
                eggBeastName = s.beastEgg.beastName ?: "",
                flameTier = s.flameFusion.currentTier,
                flameBonusDmg = s.flameFusion.bonusDmgPercent,
                lastDivinationYear = s.age
            )
            dao.savePlayerState(entity)
        }
    }

    // =========================================================================
    // Game Loop Actions do GameEngine điều phối
    // =========================================================================

    /**
     * Bế quan: Tiến triển game loop, kiểm tra thọ nguyên và kích hoạt Tọa Hóa nếu hết thọ
     */
    fun beQuan(yearsRequested: Int) {
        gameEngine.advanceGameLoop(yearsRequested)
    }

    fun attemptBreakthrough() {
        gameEngine.attemptBreakthrough()
    }

    fun withstandTribulationWave(defensiveItemId: String?) {
        gameEngine.withstandTribulationWave(defensiveItemId)
    }

    fun answerTamMa(choiceIndex: Int) {
        gameEngine.answerTamMa(choiceIndex)
    }

    fun consumePill(item: InventoryItem) {
        gameEngine.consumePill(item)
    }

    fun tanDoc(yearsToSpend: Int) {
        gameEngine.tanDoc(yearsToSpend)
    }

    fun consultOldDemon() {
        gameEngine.consultOldDemon()
    }

    fun fightSoulClash(choice: String) {
        gameEngine.fightSoulClash(choice)
    }

    fun shakeDivinationCylinder() {
        gameEngine.shakeDivinationCylinder()
    }

    fun resolveDivination(choiceIndex: Int) {
        gameEngine.resolveDivination(choiceIndex)
    }

    fun gambleAncientStone(tierCost: Long = 100L) {
        gameEngine.gambleAncientStone(tierCost)
    }

    fun pushYourLuckFlame() {
        gameEngine.pushYourLuckFlame()
    }

    fun nourishBeastEgg() {
        gameEngine.nourishBeastEgg()
    }

    fun recruitDisciple() {
        gameEngine.recruitDisciple()
    }

    fun startNewLifeWithCustomization(
        name: String,
        spiritRoot: SpiritRoot,
        selectedTraits: List<DestinyTrait>,
        lifespan: Int,
        congDucRemaining: Int,
        satKhi: Int,
        anNhanTri: Int,
        daoTam: Int,
        thanThuc: Int,
        canCot: Int,
        ngoTinh: Int,
        startingQi: Long = 25L,
        maxQi: Long = 100L
    ) {
        gameEngine.startNewLifeWithCustomization(
            name = name,
            spiritRoot = spiritRoot,
            selectedTraits = selectedTraits,
            lifespan = lifespan,
            congDucRemaining = congDucRemaining,
            satKhi = satKhi,
            anNhanTri = anNhanTri,
            daoTam = daoTam,
            thanThuc = thanThuc,
            canCot = canCot,
            ngoTinh = ngoTinh,
            startingQi = startingQi,
            maxQi = maxQi
        )
    }

    fun openLuanHoiMirror() {
        gameEngine.openLuanHoiMirror()
    }

    fun closeLuanHoiMirror() {
        gameEngine.closeLuanHoiMirror()
    }

    fun getAvailableDestinyTraits(): List<DestinyTrait> {
        return gameEngine.getAllAvailableTraits()
    }

    fun triggerDarkEvent() {
        gameEngine.triggerDarkEvent()
    }

    fun resolveDarkEventChoice(choicePath: ChoicePath) {
        gameEngine.resolveDarkEventChoice(choicePath)
    }

    fun dismissDarkEvent() {
        gameEngine.dismissDarkEvent()
    }

    fun withstandTribulationWave(defensiveItemId: String?, sacrificeArtifact: Boolean = false) {
        gameEngine.withstandTribulationWave(defensiveItemId, sacrificeArtifact)
    }

    fun upgradeTuLinhTran() {
        gameEngine.upgradeTuLinhTran()
    }

    fun sealVaultInDeath(location: String, stones: Long, itemId: String?) {
        gameEngine.sealVaultInDeath(location, stones, itemId)
    }

    fun claimSealedVault() {
        gameEngine.claimSealedVault()
    }

    fun dispatchDisciple(discipleId: String, taskType: String) {
        gameEngine.dispatchDisciple(discipleId, taskType)
    }

    fun setSubstituteDisciple(discipleId: String) {
        gameEngine.setSubstituteDisciple(discipleId)
    }

    fun contributeToSect(stones: Long) {
        gameEngine.contributeToSect(stones)
    }

    fun exchangeSectItem(itemId: String) {
        gameEngine.exchangeSectItem(itemId)
    }
}
