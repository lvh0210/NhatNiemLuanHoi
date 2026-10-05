package com.example

import com.example.engine.EngineStateTransition
import com.example.engine.GameEngine
import com.example.model.CultivationRealm
import com.example.model.DestinyTrait
import com.example.model.SpiritRoot
import com.example.viewmodel.CharacterCreationViewModel
import com.example.viewmodel.GameEngineViewModel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    private val creationVm = CharacterCreationViewModel()

    @Test
    fun testGameEngineDataClass_FieldsAndAccessors() {
        val testTraits = listOf(
            DestinyTrait(
                id = "t_hoang_co",
                name = "Hoang Cổ Thánh Thể",
                tier = "Thần Thoại",
                boonDesc = "Thể phách vô địch",
                curseDesc = "Linh khí x3"
            )
        )
        val engine = GameEngine(
            currentAge = 25,
            maxLifespan = 100,
            currentCultivationLevel = CultivationRealm.LUYEN_KHI,
            karma = 30,
            spiritRoots = SpiritRoot.THIEN_LINH_CAN,
            insight = 70,
            traits = testTraits,
            subStage = 3,
            stateTransition = EngineStateTransition.ALIVE
        )

        assertEquals(25, engine.currentAge)
        assertEquals(100, engine.maxLifespan)
        assertEquals(CultivationRealm.LUYEN_KHI, engine.currentCultivationLevel)
        assertEquals(30, engine.karma)
        assertEquals(SpiritRoot.THIEN_LINH_CAN, engine.spiritRoots)
        assertEquals(70, engine.insight)
        assertEquals(testTraits, engine.traits)
        assertEquals(EngineStateTransition.ALIVE, engine.stateTransition)
        assertEquals(75, engine.remainingLifespan)
        assertEquals("Luyện Khí Kỳ Tầng 3", engine.cultivationLevelName)
    }

    @Test
    fun testGameEngineViewModel_AdvanceYear_IncrementsAgeAndTriggersToaHoa() {
        val vm = GameEngineViewModel()
        val initial = com.example.viewmodel.PlayerState(
            currentAge = 98,
            maxLifespan = 100,
            currentCultivationLevel = CultivationRealm.LUYEN_KHI,
            isDead = false
        )
        vm.updatePlayerState(initial)

        // Advance 1 year -> age becomes 99
        vm.advanceYear()
        assertEquals(99, vm.playerState.value.currentAge)
        assertFalse(vm.playerState.value.isDead)
        assertEquals(EngineStateTransition.BE_QUAN, vm.playerState.value.stateTransition)

        // Advance 1 more year -> age reaches 100 (maxLifespan), triggers Toa Hoa
        vm.advanceYear()
        assertEquals(100, vm.playerState.value.currentAge)
        assertTrue(vm.playerState.value.isDead)
        assertEquals(EngineStateTransition.TOA_HOA, vm.playerState.value.stateTransition)
        assertTrue(vm.playerState.value.congDuc > 150)
    }

    @Test
    fun testGameEngineViewModel_ToaHoaTriggerWhenCurrentAgeReachesMaxLifespan() {
        val vm = GameEngineViewModel()
        val initial = GameEngine(
            currentAge = 95,
            maxLifespan = 100,
            currentCultivationLevel = CultivationRealm.LUYEN_KHI,
            subStage = 5,
            congDuc = 150,
            isDead = false,
            stateTransition = EngineStateTransition.ALIVE
        )
        vm.updateEngineState(initial)

        // Advance 10 years when only 5 years remaining
        vm.advanceGameLoop(10)

        val finalState = vm.engineState.value
        // Must auto-cap at maxLifespan and trigger Toa Hoa event
        assertEquals(100, finalState.currentAge)
        assertTrue(finalState.isDead)
        assertEquals(EngineStateTransition.TOA_HOA, finalState.stateTransition)
        assertTrue("Cong Duc must increase on Toa Hoa", finalState.congDuc > 150)
        assertTrue(finalState.deathCause.contains("Tọa Hóa"))
    }

    @Test
    fun testGameEngineViewModel_BreakthroughIncreasesLifespan() {
        val vm = GameEngineViewModel()
        vm.updateEngineState(
            GameEngine(
                currentAge = 80,
                maxLifespan = 100,
                currentCultivationLevel = CultivationRealm.LUYEN_KHI,
                subStage = 9,
                currentQi = 100L,
                maxQi = 100L,
                stateTransition = EngineStateTransition.ALIVE
            )
        )

        // Advance to next major realm (Trúc Cơ)
        vm.completeMajorBreakthrough()

        val ascendedState = vm.engineState.value
        assertEquals(CultivationRealm.TRUC_CO, ascendedState.currentCultivationLevel)
        assertEquals(1, ascendedState.subStage)
        // Truc Co adds 150 years to maxLifespan: 100 + 150 = 250
        assertEquals(250, ascendedState.maxLifespan)
        assertEquals(EngineStateTransition.BREAKTHROUGH, ascendedState.stateTransition)
        assertFalse(ascendedState.isDead)
    }

    @Test
    fun testComputeInitialStats_HoangCoThanhTheTradeOff() {
        val traits = listOf(
            DestinyTrait(
                id = "t_hoang_co",
                name = "Hoang Cổ Thánh Thể",
                tier = "SSR",
                boonDesc = "Thể phách vô địch, cận chiến x2",
                curseDesc = "Linh khí cần x3, +40 Sát Khí"
            )
        )
        // SpiritRoot.THIEN_LINH_CAN with Hoang Cổ trait:
        val statsWithThienRoot = creationVm.computeCharacterStats(SpiritRoot.THIEN_LINH_CAN, traits, 150)
        assertEquals(125, statsWithThienRoot.lifespan)
        assertEquals(40, statsWithThienRoot.satKhi)
        assertEquals(300L, statsWithThienRoot.maxQi)
        assertEquals(25L, statsWithThienRoot.startingQi)

        // When both root and trait are Hoang Cổ Thánh Thể:
        val statsWithHoangCoRoot = creationVm.computeCharacterStats(SpiritRoot.HOANG_CO_THANH_THE, traits, 150)
        assertEquals(150, statsWithHoangCoRoot.lifespan)
        assertEquals(60, statsWithHoangCoRoot.satKhi)
        assertEquals(300L, statsWithHoangCoRoot.maxQi)
        assertTrue(statsWithHoangCoRoot.canCot >= 120)
    }

    @Test
    fun testComputeInitialStats_CuuAmTuyetMachLongevityPenalty() {
        val traits = listOf(
            DestinyTrait(
                id = "t_cuu_am",
                name = "Cửu Âm Tuyệt Mạch",
                tier = "SSR",
                boonDesc = "Nạp khí x2.5",
                curseDesc = "Thọ nguyên -25 năm"
            )
        )
        val stats = creationVm.computeCharacterStats(SpiritRoot.THIEN_LINH_CAN, traits, 100)

        // Base 100 - 25 = 75 years longevity!
        assertEquals(75, stats.lifespan)
        // Insight from Thien Linh Can (50 + 20) + Cuu Am (15) = 85
        assertEquals(85, stats.ngoTinh)
    }

    @Test
    fun testGameEngineCore_ToaHoaTriggerWhenLifespanLimitReached() {
        val engine = com.example.engine.GameEngineCore()
        val initialState = engine.currentState().copy(
            age = 95,
            maxLifespan = 100,
            isDead = false
        )
        engine.updateState(initialState)

        // Advance 10 years when remaining lifespan is only 5
        engine.advanceGameLoop(10)

        val finalState = engine.currentState()
        // Must automatically stop at 100 and trigger Toa Hoa
        assertEquals(100, finalState.age)
        assertTrue(finalState.isDead)
        assertTrue(finalState.congDuc > initialState.congDuc)
        assertTrue(finalState.showLuanHoiDialog)
    }

    @Test
    fun testCharacterCreationViewModel_ValidationRule_ForcesTaiHoaWhenThanThoaiChosen() {
        val vm = com.example.viewmodel.CharacterCreationViewModel()
        val pool = vm.getCanonicalTraitsPool()

        val thanThoaiTrait = pool.first { it.tier == "Thần Thoại" }
        val normalTrait1 = pool.first { it.tier == "Thường" || it.tier == "Hiếm Có" }
        val normalTrait2 = pool.filter { (it.tier == "Thường" || it.tier == "Hiếm Có") && it.id != normalTrait1.id }.first()
        val taiHoaTrait = pool.first { it.isTaiHoa }

        // Step 1: Select a Thần Thoại trait and 2 normal traits (NO Tai Họa)
        vm.selectTraitForSlot(0, thanThoaiTrait)
        vm.selectTraitForSlot(1, normalTrait1)
        vm.selectTraitForSlot(2, normalTrait2)

        val invalidState = vm.uiState.value
        assertTrue(invalidState.hasHighTierTrait)
        assertFalse(invalidState.hasTaiHoaTrait)
        // MUST BE INVALID: rule requires Tai Họa to balance Thần Thoại!
        assertFalse(invalidState.isSelectionValid)
        assertNotNull(invalidState.validationErrorMessage)
        assertTrue(invalidState.validationErrorMessage!!.contains("Thần Thoại"))

        // Step 2: Swap slot 2 with a Tai Họa trait to balance points
        vm.selectTraitForSlot(2, taiHoaTrait)

        val validState = vm.uiState.value
        assertTrue(validState.hasHighTierTrait)
        assertTrue(validState.hasTaiHoaTrait)
        // MUST BE VALID NOW
        assertTrue(validState.isSelectionValid)
        assertNull(validState.validationErrorMessage)

        // Step 3: Verify stats are accurately initialized
        val stats = validState.calculatedStats
        assertTrue(stats.lifespan > 0)
        assertTrue(stats.satKhi >= 0)
        assertTrue(stats.ngoTinh > 0)
        assertTrue(stats.canCot > 0)
    }

    @Test
    fun testDeclarativeContentSystem_AddNewTraitSimplyByWritingStructure() {
        val vm = CharacterCreationViewModel()

        // Giả lập người dùng muốn thêm 1 Từ Điều mới chỉ bằng cách viết đúng cấu trúc
        val customTrait = DestinyTrait(
            id = "t_kiem_quy_tuyet_the",
            name = "Kiếm Quỷ Tuyệt Thế",
            tier = "Thần Thoại",
            boonDesc = "Kiếm ý nghịch thiên",
            curseDesc = "Ma khí quấn thân",
            costCongDuc = 150,
            pointCost = 3,
            lifespanMod = 15,
            canCotMod = 35,
            ngoTinhMod = 40,
            satKhiMod = 55
        )

        // Đăng ký vào registry
        com.example.content.GameContentRegistry.Traits.registerTrait(customTrait)

        // 1. Game tự động nhận diện từ điều trong kho
        val pool = vm.getCanonicalTraitsPool()
        assertTrue("Game phải tự nhận từ điều mới", pool.any { it.id == "t_kiem_quy_tuyet_the" })

        // 2. Game tự động tính toán chỉ số theo đúng cấu trúc đã khai báo mà không cần sửa code ViewModel
        val stats = vm.computeCharacterStats(
            root = SpiritRoot.THIEN_LINH_CAN,
            traits = listOf(customTrait),
            initialCongDuc = 150
        )
        // Thọ nguyên: 100 (base) + 15 (trait) = 115
        assertEquals(115, stats.lifespan)
        // Căn cốt: 50 (base) + 35 (trait) = 85
        assertEquals(85, stats.canCot)
        // Ngộ tính: 50 (base) + 20 (Thien Linh Can) + 40 (trait) = 110
        assertEquals(110, stats.ngoTinh)
        // Sát khí: 0 (base) + 55 (trait) = 55
        assertEquals(55, stats.satKhi)
    }

    @Test
    fun testDeclarativeContentSystem_DivinePhysique_HonDonDaoThe() {
        val vm = CharacterCreationViewModel()

        // Kiểm tra Thánh Thể mới 'Hỗn Độn Đạo Thể' được nhận diện và tính toán tự động
        val stats = vm.computeCharacterStats(
            root = SpiritRoot.HON_DON_DAO_THE,
            traits = emptyList(),
            initialCongDuc = 150
        )
        // Base 100 + 40 (bonus Hỗn Độn Đạo Thể) = 140
        assertEquals(140, stats.lifespan)
        // Base 50 + 35 = 85 Căn Cốt
        assertEquals(85, stats.canCot)
        // Base 50 + 35 = 85 Ngộ Tính
        assertEquals(85, stats.ngoTinh)
        // Qi x2 multiplier: 100 * 2 = 200L
        assertEquals(200L, stats.maxQi)
    }
}
