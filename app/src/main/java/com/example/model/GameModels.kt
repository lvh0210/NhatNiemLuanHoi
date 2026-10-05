package com.example.model

/**
 * Các cảnh giới tu tiên kinh điển
 */
enum class CultivationRealm(
    val realmName: String,
    val maxSubStages: Int,
    val baseLifespan: Int,
    val breakthroughBaseRate: Float,
    val baseQiRequirement: Long
) {
    LUYEN_KHI("Luyện Khí Kỳ", 9, 100, 0.75f, 100L),
    TRUC_CO("Trúc Cơ Kỳ", 9, 250, 0.60f, 600L),
    KIM_DAN("Kim Đan Kỳ", 9, 500, 0.45f, 3000L),
    NGUYEN_ANH("Nguyên Anh Kỳ", 9, 1200, 0.35f, 15000L),
    HOA_THAN("Hóa Thần Kỳ", 9, 3000, 0.25f, 80000L);

    fun getDisplayName(subStage: Int): String {
        return "$realmName Tầng $subStage"
    }
}

/**
 * Trạng thái kinh mạch và khí tức của người chơi
 */
enum class QiState(val label: String, val description: String, val absorptionMultiplier: Float) {
    BINH_ON("Bình Ổn", "Khí tức điều hòa, kinh mạch thông suốt, hấp thu linh khí 100%", 1.0f),
    TAP_NHIEM("Tạp Nhiễm", "Đan độc tích tụ làm đục kinh mạch, hấp thu linh khí 70%", 0.7f),
    NGHICH_LUU("Nghịch Lưu", "Chân khí hỗn loạn, linh khí hấp thu chỉ 40%, nguy cơ tổn thọ", 0.4f),
    BAO_LOAN("Bạo Loạn", "Linh khí xung kích đan điền, đan độc quá cao, cấm kỵ đột phá", 0.2f),
    HU_HAO("Hư Hao", "Kinh mạch suy kiệt sau lôi kiếp, cần tĩnh dưỡng", 0.5f)
}

/**
 * Phẩm chất Linh Căn / Thánh Thể (Constitutions & Divine Physiques)
 * Cấu trúc chuẩn hóa: Khi muốn thêm Thánh Thể mới, chỉ cần thêm 1 dòng enum với các chỉ số tương ứng.
 */
enum class SpiritRoot(
    val title: String,
    val speedDesc: String,
    val speedMultiplier: Float,
    val combatNote: String,
    val possessionRisk: Float,
    val lifespanBonus: Int = 0,
    val canCotBonus: Int = 0,
    val ngoTinhBonus: Int = 0,
    val thanThucBonus: Int = 0,
    val anNhanTriBonus: Int = 0,
    val satKhiBonus: Int = 0,
    val daoTamBonus: Int = 0,
    val qiRequirementMultiplier: Long = 1L
) {
    THIEN_LINH_CAN(
        title = "Thiên Linh Căn",
        speedDesc = "Tốc độ tu luyện x3, đột phá dễ dàng",
        speedMultiplier = 3.0f,
        combatNote = "Đơn hệ tinh thuần, sát thương bùng nổ",
        possessionRisk = 0.35f,
        ngoTinhBonus = 20,
        thanThucBonus = 10
    ),
    NGU_LINH_CAN(
        title = "Ngũ Linh Căn (Tạp)",
        speedDesc = "Nạp khí cực chậm (x0.5)",
        speedMultiplier = 0.5f,
        combatNote = "Khi có 'Ngũ Hành Hỗn Nguyên Quyết': Sức mạnh ngũ hành vượt cấp x2.5!",
        possessionRisk = 0.05f,
        canCotBonus = 15,
        anNhanTriBonus = 25
    ),
    SONG_LINH_CAN(
        title = "Song Linh Căn",
        speedDesc = "Tốc độ nạp khí khá nhanh (x1.6)",
        speedMultiplier = 1.6f,
        combatNote = "Tương sinh tương khắc ổn định",
        possessionRisk = 0.15f,
        ngoTinhBonus = 10,
        anNhanTriBonus = 10
    ),
    BIEN_DI_LOI_CAN(
        title = "Biến Dị Lôi Căn",
        speedDesc = "Tốc độ nạp khí x2.2",
        speedMultiplier = 2.2f,
        combatNote = "Kháng 40% sát thương Lôi Kiếp tự nhiên",
        possessionRisk = 0.25f,
        canCotBonus = 15,
        thanThucBonus = 10
    ),
    HOANG_CO_THANH_THE(
        title = "Hoang Cổ Thánh Thể",
        speedDesc = "Nạp khí x1.0, cần gấp 3 linh khí",
        speedMultiplier = 1.0f,
        combatNote = "Miễn nhiễm 100% Đan Độc, thể phách vô địch, cận chiến x2",
        possessionRisk = 0.40f,
        lifespanBonus = 25,
        canCotBonus = 40,
        satKhiBonus = 20,
        qiRequirementMultiplier = 3L
    ),
    HON_DON_DAO_THE(
        title = "Hỗn Độn Đạo Thể",
        speedDesc = "Nạp khí x3.5, bao hàm vạn tượng",
        speedMultiplier = 3.5f,
        combatNote = "Vạn pháp bất xâm, tương thích ngũ hành công pháp",
        possessionRisk = 0.45f,
        lifespanBonus = 40,
        canCotBonus = 35,
        ngoTinhBonus = 35,
        daoTamBonus = 15,
        qiRequirementMultiplier = 2L
    ),
    TIEN_MA_DONG_THE(
        title = "Tiên Ma Đồng Thể",
        speedDesc = "Nạp khí x2.5, chính tà song tu",
        speedMultiplier = 2.5f,
        combatNote = "Sát khí càng cao công lực càng cuồng bạo x2",
        possessionRisk = 0.50f,
        canCotBonus = 30,
        ngoTinhBonus = 25,
        satKhiBonus = 40,
        daoTamBonus = -10
    ),
    VO_CAU_DAO_THE(
        title = "Vô Cấu Đạo Thể",
        speedDesc = "Nạp khí x2.8, đạo thể thanh tịnh",
        speedMultiplier = 2.8f,
        combatNote = "Kinh mạch không tì vết, giảm 50% nguy cơ đan độc",
        possessionRisk = 0.20f,
        lifespanBonus = 30,
        canCotBonus = 20,
        ngoTinhBonus = 30,
        daoTamBonus = 25
    )
}

/**
 * Thiên Mệnh Từ Điều gacha từ Luân Hồi Kính (Phúc Họa Tương Phụ)
 * Cấu trúc chuẩn hóa: Khi muốn thêm Từ Điều mới, chỉ cần điền đúng các trường chỉ số (Mod)
 * Game sẽ tự động tính toán, hiển thị và kiểm tra luật cân bằng điểm mà không cần viết code xử lý riêng.
 */
data class DestinyTrait(
    val id: String,
    val name: String,
    val tier: String, // "Thần Thoại", "Hiếm Có", "Thường", "Tai Họa" / "SSR", "SR", "R"
    val boonDesc: String,
    val curseDesc: String,
    val costCongDuc: Int = 100,
    val pointCost: Int = 0,
    val isCalamity: Boolean = false,
    // Cấu trúc chỉ số cộng/trừ trực tiếp (Declarative Stat Modifiers)
    val lifespanMod: Int = 0,
    val canCotMod: Int = 0,
    val ngoTinhMod: Int = 0,
    val thanThucMod: Int = 0,
    val satKhiMod: Int = 0,
    val daoTamMod: Int = 0,
    val anNhanTriMod: Int = 0,
    val qiMultiplierMod: Long = 1L
) {
    val isHighTier: Boolean
        get() = tier.contains("Thần Thoại", ignoreCase = true) || tier.equals("SSR", ignoreCase = true) || pointCost >= 3

    val isTaiHoa: Boolean
        get() = isCalamity || tier.contains("Tai Họa", ignoreCase = true) || pointCost < 0
}

/**
 * Quẻ bói Diễn Toán Thiên Cơ hàng năm
 */
enum class HexagramType(val label: String, val colorHex: String) {
    THUONG_THUONG_CAT("Thượng Thượng Cát", "#10B981"),
    THUONG_CAT("Thượng Cát", "#06B6D4"),
    BINH_HOA("Bình Hòa", "#FBBF24"),
    HUNG("Hung", "#F97316"),
    DAI_HUNG("Đại Hung", "#EF4444")
}

data class DivinationEvent(
    val type: HexagramType,
    val title: String,
    val narrative: String,
    val safeChoiceLabel: String,
    val safeChoiceEffect: String,
    val riskyChoiceLabel: String,
    val riskyChoiceEffect: String
)

/**
 * Đệ tử tông môn từ Đăng Tiên Bảng
 */
enum class DiscipleArchetype(val title: String, val description: String) {
    KHI_VAN_CHI_TU("Khí Vận Chi Tử", "Linh căn thấp nhưng rớt vực là nhặt được linh thảo thần binh dâng sư phụ."),
    PHAN_COT_TU("Phản Cốt Tử", "Thiên tài tu luyện thần tốc, nhưng tâm cơ thâm hiểm, cần uy áp để không phản tông."),
    CHUYEN_THE_LAO_QUAI("Chuyển Thế Lão Quái", "Đại năng kiếp trước chuyển sinh, tính tình quái gở nhưng ban truyền bí pháp."),
    TRUNG_THANH_DE_TU("Trung Thành Đệ Tử", "Tư chất bình phàm nhưng một lòng hộ vệ tông môn, chăm lo dược viên.")
}

data class Disciple(
    val id: String,
    val name: String,
    val archetype: DiscipleArchetype,
    val realm: String,
    var loyalty: Int,
    val specialTalent: String
)

/**
 * Trứng Dị Thú nuôi bằng Tinh Huyết
 */
data class BeastEggData(
    var bloodNourishCount: Int = 0, // 0 - 5
    var isHatched: Boolean = false,
    var beastName: String? = null,
    var beastTier: String? = null,
    var beastSkill: String? = null
)

/**
 * Vật phẩm trong túi trữ vật
 */
enum class ItemCategory {
    DAN_DUOC, PHU_LUC, PHAP_BAO, KHOANG_THACH, DI_HOA, LINH_THAO
}

data class InventoryItem(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val description: String,
    var count: Int,
    val qiBonus: Long = 0L,
    val toxicityBonus: Int = 0,
    val lifespanCost: Int = 0,
    val defValue: Int = 0,
    val sellPrice: Long = 10L
)

/**
 * Dị Hỏa Dung Hợp (Push Your Luck)
 */
data class FlameFusionState(
    val flameName: String = "U Minh Lãnh Hỏa",
    var currentTier: Int = 0, // 0, 1, 2, 3
    var isFailed: Boolean = false,
    var bonusDmgPercent: Int = 0
)

/**
 * Lịch sử các đời luân hồi
 */
data class PastLifeRecord(
    val generation: Int,
    val finalRealm: String,
    val ageAtDeath: String,
    val causeOfDeath: String,
    val congDucEarned: Int
)

/**
 * Chỉ số ban đầu của nhân vật khi khởi tạo
 */
data class InitialStats(
    val lifespan: Int,
    val congDuc: Int,
    val satKhi: Int,
    val anNhanTri: Int,
    val daoTam: Int,
    val thanThuc: Int,
    val canCot: Int,
    val ngoTinh: Int,
    val startingQi: Long = 25L,
    val maxQi: Long = 100L
)

fun getRandomName(): String {
    val surnames = listOf("Tiêu", "Hàn", "Lâm", "Diệp", "Bạch", "Cố", "Trần", "Mặc", "Vân", "Sở", "Phương", "Lý")
    val names = listOf("Phàm", "Lập", "Động", "Viêm", "Tiểu Thuần", "Vô Ngôn", "Thiên Hành", "Cư Nhân", "Triệt", "Bất Hối", "Trường Sinh", "Thanh Phong")
    return "${surnames.random()} ${names.random()}"
}
