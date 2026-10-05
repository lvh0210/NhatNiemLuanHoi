package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.CultivationRealm
import com.example.model.DestinyTrait
import com.example.model.InitialStats
import com.example.model.QiState
import com.example.model.SpiritRoot
import com.example.model.getRandomName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State cho quá trình Khởi tạo nhân vật (Character Initialization)
 */
data class CharacterCreationUiState(
    val name: String = "Tiêu Phàm",
    val generation: Int = 1,
    val initialCongDuc: Int = 150,
    val selectedRoot: SpiritRoot = SpiritRoot.THIEN_LINH_CAN,
    val traitSlot0: DestinyTrait? = null,
    val traitSlot1: DestinyTrait? = null,
    val traitSlot2: DestinyTrait? = null,
    val lockSlot0: Boolean = false,
    val lockSlot1: Boolean = false,
    val lockSlot2: Boolean = false,
    val availableTraitsPool: List<DestinyTrait> = emptyList(),
    val destinyPointsBalance: Int = 0,
    val hasHighTierTrait: Boolean = false,
    val hasTaiHoaTrait: Boolean = false,
    val isSelectionValid: Boolean = true,
    val validationErrorMessage: String? = null,
    val calculatedStats: InitialStats = InitialStats(
        lifespan = 100,
        congDuc = 150,
        satKhi = 0,
        anNhanTri = 10,
        daoTam = 80,
        thanThuc = 50,
        canCot = 50,
        ngoTinh = 50,
        startingQi = 25L,
        maxQi = 100L
    )
) {
    val selectedTraits: List<DestinyTrait>
        get() = listOfNotNull(traitSlot0, traitSlot1, traitSlot2)
}

/**
 * CharacterCreationViewModel:
 * Quản lý toàn bộ quy trình khởi tạo nhân vật:
 * 1. Chọn thuộc tính Linh Căn (Roots)
 * 2. Gacha/chọn 3 Thiên Mệnh Từ Điều từ Luân Hồi Kính
 * 3. Áp dụng quy tắc kiểm tra (Selection Validation Rule):
 *    - Nếu chọn Từ Điều phẩm cấp cao (như 'Thần Thoại' / 'SSR'), BẮT BUỘC người chơi phải chọn thêm
 *      ít nhất 1 Từ Điều 'Tai Họa' (curse) để cân bằng điểm số mệnh (destiny points).
 * 4. Tính toán chuẩn xác các chỉ số cuối cùng (Longevity, Karma, Insight, Roots, Cultivation Base).
 */
class CharacterCreationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterCreationUiState())
    val uiState: StateFlow<CharacterCreationUiState> = _uiState.asStateFlow()

    init {
        initializePoolAndDefaults()
    }

    private fun initializePoolAndDefaults() {
        val pool = getCanonicalTraitsPool()
        val randomName = getRandomName()

        // Lấy 3 từ điều khởi đầu hợp lệ
        val t0 = pool.first { it.id == "t_trong_sinh" }     // Thần Thoại (+3)
        val t1 = pool.first { it.id == "t_gia_toc_diet" }   // Tai Họa (-3) để cân bằng Thần Thoại!
        val t2 = pool.first { it.id == "t_cau_dao" }        // Hiếm Có (+1)

        _uiState.value = CharacterCreationUiState(
            name = randomName,
            generation = 1,
            initialCongDuc = 150,
            selectedRoot = SpiritRoot.THIEN_LINH_CAN,
            traitSlot0 = t0,
            traitSlot1 = t1,
            traitSlot2 = t2,
            availableTraitsPool = pool
        )
        recalculateAndValidate()
    }

    fun setupSession(generation: Int, congDuc: Int, pool: List<DestinyTrait>) {
        val traitPool = if (pool.isNotEmpty()) pool else getCanonicalTraitsPool()
        _uiState.update { current ->
            current.copy(
                generation = generation,
                initialCongDuc = congDuc,
                availableTraitsPool = traitPool
            )
        }
        recalculateAndValidate()
    }

    fun setName(newName: String) {
        _uiState.update { it.copy(name = newName) }
        recalculateAndValidate()
    }

    fun randomizeName() {
        _uiState.update { it.copy(name = getRandomName()) }
    }

    fun selectRoot(root: SpiritRoot) {
        _uiState.update { it.copy(selectedRoot = root) }
        recalculateAndValidate()
    }

    fun toggleLockSlot(slotIndex: Int) {
        _uiState.update { current ->
            when (slotIndex) {
                0 -> current.copy(lockSlot0 = !current.lockSlot0)
                1 -> current.copy(lockSlot1 = !current.lockSlot1)
                2 -> current.copy(lockSlot2 = !current.lockSlot2)
                else -> current
            }
        }
    }

    fun selectTraitForSlot(slotIndex: Int, trait: DestinyTrait) {
        _uiState.update { current ->
            when (slotIndex) {
                0 -> current.copy(traitSlot0 = trait)
                1 -> current.copy(traitSlot1 = trait)
                2 -> current.copy(traitSlot2 = trait)
                else -> current
            }
        }
        recalculateAndValidate()
    }

    /**
     * Gacha rút 3 Thiên Mệnh Từ Điều từ Luân Hồi Kính
     * Tôn trọng các slot đã được người chơi khóa (Lock)
     */
    fun rollGacha() {
        val s = _uiState.value
        val pool = s.availableTraitsPool.ifEmpty { getCanonicalTraitsPool() }

        val usedIds = mutableSetOf<String>()
        if (s.lockSlot0 && s.traitSlot0 != null) usedIds.add(s.traitSlot0.id)
        if (s.lockSlot1 && s.traitSlot1 != null) usedIds.add(s.traitSlot1.id)
        if (s.lockSlot2 && s.traitSlot2 != null) usedIds.add(s.traitSlot2.id)

        var newT0 = s.traitSlot0
        var newT1 = s.traitSlot1
        var newT2 = s.traitSlot2

        if (!s.lockSlot0) {
            val candidate = pool.filterNot { usedIds.contains(it.id) }.randomOrNull() ?: pool.random()
            usedIds.add(candidate.id)
            newT0 = candidate
        }
        if (!s.lockSlot1) {
            val candidate = pool.filterNot { usedIds.contains(it.id) }.randomOrNull() ?: pool.random()
            usedIds.add(candidate.id)
            newT1 = candidate
        }
        if (!s.lockSlot2) {
            val candidate = pool.filterNot { usedIds.contains(it.id) }.randomOrNull() ?: pool.random()
            usedIds.add(candidate.id)
            newT2 = candidate
        }

        _uiState.update {
            it.copy(
                traitSlot0 = newT0,
                traitSlot1 = newT1,
                traitSlot2 = newT2
            )
        }
        recalculateAndValidate()
    }

    /**
     * Tự động cân bằng số mệnh bằng cách gán 1 từ điều Tai Họa ngẫu nhiên vào slot chưa khóa
     */
    fun autoBalanceCalamity() {
        val s = _uiState.value
        val calamityTraits = s.availableTraitsPool.filter { it.isTaiHoa }
        if (calamityTraits.isEmpty()) return

        val chosenCalamity = calamityTraits.random()

        _uiState.update { current ->
            when {
                !current.lockSlot2 -> current.copy(traitSlot2 = chosenCalamity)
                !current.lockSlot1 -> current.copy(traitSlot1 = chosenCalamity)
                !current.lockSlot0 -> current.copy(traitSlot0 = chosenCalamity)
                else -> current.copy(traitSlot2 = chosenCalamity, lockSlot2 = false)
            }
        }
        recalculateAndValidate()
    }

    /**
     * KIỂM TRA QUY TẮC CHỌN (Selection Validation Rule) & TÍNH TOÁN CHỈ SỐ:
     * - Nếu có Từ Điều Thần Thoại (hoặc SSR/pointCost >= 3): BẮT BUỘC phải có ít nhất 1 Từ Điều Tai Họa
     * - Cân bằng điểm số mệnh (destiny points balance)
     * - Tính toán Longevity, Karma, Insight, Roots, Cultivation Base
     */
    private fun recalculateAndValidate() {
        val s = _uiState.value
        val traits = s.selectedTraits

        val hasHighTier = traits.any { it.isHighTier }
        val hasTaiHoa = traits.any { it.isTaiHoa }
        val pointsBalance = traits.sumOf { it.pointCost }

        // Validation Rule Checking
        val (isValid, errorMessage) = when {
            traits.size < 3 -> {
                Pair(false, "Cần chọn đủ 3 Thiên Mệnh Từ Điều trước khi nhập thế!")
            }
            hasHighTier && !hasTaiHoa -> {
                Pair(
                    false,
                    "QUY TẮC THIÊN ĐẠO: Khi chọn Thiên Mệnh [Thần Thoại], ngươi BẮT BUỘC phải chọn thêm ít nhất 1 Từ Điều [Tai Họa] để nghịch thiên cân bằng số mệnh!"
                )
            }
            hasHighTier && pointsBalance > 1 -> {
                Pair(
                    false,
                    "QUY TẮC CÂN BẰNG ĐIỂM: Tổng điểm số mệnh đang quá cao (+$pointsBalance). Hãy chọn thêm Từ Điều Tai Họa để cân bằng!"
                )
            }
            else -> {
                Pair(true, null)
            }
        }

        // Tính toán các chỉ số ban đầu chính xác
        val stats = computeCharacterStats(
            root = s.selectedRoot,
            traits = traits,
            initialCongDuc = s.initialCongDuc
        )

        _uiState.update { current ->
            current.copy(
                destinyPointsBalance = pointsBalance,
                hasHighTierTrait = hasHighTier,
                hasTaiHoaTrait = hasTaiHoa,
                isSelectionValid = isValid,
                validationErrorMessage = errorMessage,
                calculatedStats = stats
            )
        }
    }

    /**
     * Tính toán chuẩn xác các chỉ số:
     * - Thọ Nguyên (Longevity)
     * - Sát Khí / Nghiệp Lực (Karma)
     * - Ngộ Tính (Insight)
     * - Linh Căn & Thánh Thể (Roots & Divine Physiques)
     * - Tu Vi & Linh Khí (Cultivation Base)
     *
     * Áp dụng kiến trúc Declarative Data: Đọc trực tiếp các bonus từ SpiritRoot và
     * các Mod từ DestinyTrait mà không cần viết lệnh if/else cứng.
     */
    fun computeCharacterStats(
        root: SpiritRoot,
        traits: List<DestinyTrait>,
        initialCongDuc: Int
    ): InitialStats {
        var lifespan = 100 + root.lifespanBonus
        var satKhi = root.satKhiBonus
        var anNhanTri = 10 + root.anNhanTriBonus
        var daoTam = 80 + root.daoTamBonus
        var thanThuc = 50 + root.thanThucBonus
        var canCot = 50 + root.canCotBonus
        var ngoTinh = 50 + root.ngoTinhBonus
        var qiMultiplier = root.qiRequirementMultiplier

        // Tự động áp dụng từ cấu trúc dữ liệu của từng Từ Điều
        traits.forEach { rawTrait ->
            // Nếu từ điều chưa điền sẵn mod, tự động tra cứu từ GameContentRegistry
            val trait = if (rawTrait.lifespanMod == 0 && rawTrait.canCotMod == 0 && rawTrait.ngoTinhMod == 0 && rawTrait.satKhiMod == 0 && rawTrait.qiMultiplierMod == 1L) {
                com.example.content.GameContentRegistry.Traits.ALL.find { 
                    it.id == rawTrait.id || it.name.contains(rawTrait.name, ignoreCase = true) || rawTrait.name.contains(it.name, ignoreCase = true)
                } ?: rawTrait
            } else {
                rawTrait
            }

            lifespan += trait.lifespanMod
            canCot += trait.canCotMod
            ngoTinh += trait.ngoTinhMod
            thanThuc += trait.thanThucMod
            satKhi += trait.satKhiMod
            daoTam += trait.daoTamMod
            anNhanTri += trait.anNhanTriMod
            if (trait.qiMultiplierMod > 1L) {
                qiMultiplier = maxOf(qiMultiplier, trait.qiMultiplierMod)
            }
        }

        // Ràng buộc giới hạn an toàn của tu tiên
        lifespan = lifespan.coerceAtLeast(40)
        daoTam = daoTam.coerceIn(10, 100)
        canCot = canCot.coerceAtLeast(10)
        ngoTinh = ngoTinh.coerceAtLeast(10)
        satKhi = satKhi.coerceAtLeast(0)

        val maxQi = 100L * qiMultiplier

        return InitialStats(
            lifespan = lifespan,
            congDuc = initialCongDuc,
            satKhi = satKhi,
            anNhanTri = anNhanTri,
            daoTam = daoTam,
            thanThuc = thanThuc,
            canCot = canCot,
            ngoTinh = ngoTinh,
            startingQi = 25L,
            maxQi = maxQi
        )
    }

    /**
     * Kho từ điều chuẩn hóa - nạp trực tiếp từ GameContentRegistry
     */
    fun getCanonicalTraitsPool(): List<DestinyTrait> {
        return com.example.content.GameContentRegistry.Traits.ALL
    }
}
