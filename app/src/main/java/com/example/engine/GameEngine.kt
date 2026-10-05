package com.example.engine

import com.example.model.CultivationRealm
import com.example.model.DestinyTrait
import com.example.model.SpiritRoot

/**
 * Các trạng thái chuyển đổi trong vòng lặp game tu tiên (State Transitions)
 */
enum class EngineStateTransition {
    ALIVE,          // Trạng thái bình thường còn sống
    BE_QUAN,        // Đang bế quan tu luyện tiêu tốn năm tháng
    BREAKTHROUGH,   // Đang nỗ lực đột phá cảnh giới
    TRIBULATION,    // Đang độ lôi kiếp & vấn đạo tâm ma
    TOA_HOA,        // Thọ nguyên cạn kiệt (Tọa Hóa - Chết già)
    REINCARNATION   // Khai mở Luân Hồi Kính chuyển thế kiếp sau
}

/**
 * GameEngine data class:
 * Định nghĩa đầy đủ các thuộc tính cốt lõi của nhân vật người chơi trong vòng lặp tu tiên:
 * - currentAge: Tuổi tác hiện tại của tu sĩ
 * - maxLifespan: Thọ nguyên cực hạn theo cảnh giới và căn cốt
 * - currentCultivationLevel: Cảnh giới tu vi hiện tại (Cultivation Level)
 * - karma: Điểm Nghiệp lực / Sát khí tích tụ từ tiền kiếp và sát phạt
 * - spiritRoots: Phẩm chất linh căn khởi đầu (Thiên Linh Căn, Ngũ Linh Căn,...)
 * - insight: Điểm Ngộ tính quyết định tốc độ lĩnh ngộ công pháp và đột phá
 * - traits: Danh sách các Thiên Mệnh Từ Điều đang kích hoạt (Destiny Traits)
 */
data class GameEngine(
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
    // Thuộc tính phụ trợ tương thích ngược
    val satKhi: Int get() = karma
    val spiritRoot: SpiritRoot get() = spiritRoots
    val ngoTinh: Int get() = insight
    val activeTraits: List<DestinyTrait> get() = traits

    val remainingLifespan: Int
        get() = maxOf(0, maxLifespan - currentAge)

    val cultivationLevelName: String
        get() = currentCultivationLevel.getDisplayName(subStage)
}
