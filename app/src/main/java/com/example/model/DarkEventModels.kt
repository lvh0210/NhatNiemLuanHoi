package com.example.model

/**
 * Tác động chỉ số sinh tử của tu sĩ sau mỗi quyết định:
 * - lifespan: Biến động Thọ Nguyên (+/- năm)
 * - qi: Lượng Linh Khí hấp thu hoặc tiêu hao
 * - pillToxin: Mức độ đan độc / độc tố kinh mạch tích tụ (+/- %)
 * - karma: Điểm Nghiệp Lực / Sát Khí dẫn khởi Thiên Kiếp trừng phạt
 * - divineSense: Độ cường đại của Thần Thức / Thức Hải
 */
data class EventStatsEffect(
    val lifespan: Int = 0,
    val qi: Long = 0L,
    val pillToxin: Int = 0,
    val karma: Int = 0,
    val divineSense: Int = 0
)

/**
 * 3 Con đường sinh tồn trong thế giới tu tiên hắc ám
 */
enum class ChoicePath {
    CAU_DAO,        // [Cẩu Đạo]: Cẩn trọng, ẩn nhẫn, giữ mạng là trên hết, bỏ qua hư danh
    TRANH_DOAT,     // [Tranh Đoạt]: Liều mạng đoạt bảo, dốc cạn lá bài tẩy (High Risk - High Reward)
    AN_NHAN_TA_DAO  // [Ẩn Nhẫn / Tà Đạo]: Lấy ác trị ác, phản sát, hiến tế, ma đạo mưu mô
}

/**
 * Lựa chọn chi tiết kèm hậu quả sinh tử
 */
data class DarkEventChoice(
    val path: ChoicePath,
    val label: String,
    val actionText: String,
    val outcomeNarrative: String,
    val effects: EventStatsEffect
)

/**
 * Cấu trúc Sự Kiện Tu Tiên Hắc Ám chuẩn hóa (Dark Cultivation Event)
 */
data class DarkCultivationEvent(
    val id: String,
    val title: String,
    val minRealm: CultivationRealm = CultivationRealm.LUYEN_KHI,
    val narrative: String,
    val cauDao: DarkEventChoice,
    val tranhDoat: DarkEventChoice,
    val anNhanTaDao: DarkEventChoice
) {
    val choices: List<DarkEventChoice>
        get() = listOf(cauDao, tranhDoat, anNhanTaDao)
}
