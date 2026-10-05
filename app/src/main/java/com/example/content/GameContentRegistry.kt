package com.example.content

import com.example.model.*

/**
 * ==============================================================================
 * HỆ THỐNG ĐĂNG KÝ NỘI DUNG TU TIÊN (GAME CONTENT REGISTRY)
 * ==============================================================================
 * Kiến trúc module hóa dữ liệu (Data-driven Architecture):
 * Khi muốn thêm bất kỳ nội dung nào vào game:
 * 1. THÊM THÁNH THỂ / LINH CĂN:
 *    - Mở [SpiritRoot] trong GameModels.kt, thêm 1 enum mới điền:
 *      title, speedMultiplier, combatNote, possessionRisk, lifespanBonus,
 *      canCotBonus, ngoTinhBonus, thanThucBonus, satKhiBonus, daoTamBonus.
 *    - Giao diện Luân Hồi Kính và hệ thống tự động nhận diện và tính toán!
 *
 * 2. THÊM THIÊN MỆNH TỪ ĐIỀU:
 *    - Thêm 1 đối tượng [DestinyTrait] vào danh sách [GameContentRegistry.Traits.ALL].
 *    - Điền đầy đủ các mod: lifespanMod, canCotMod, ngoTinhMod, satKhiMod, pointCost...
 *    - Toàn bộ cơ chế Gacha, cân bằng điểm Thiên Đạo, và tính chỉ số tự động nhận diện!
 *
 * 3. THÊM QUẺ BÓI / DIỄN TOÁN THIÊN CƠ:
 *    - Thêm 1 đối tượng [DivinationEvent] vào [GameContentRegistry.Divinations.ALL].
 *
 * 4. THÊM KỲ DUYÊN / SỰ KIỆN LỊCH LUYỆN:
 *    - Thêm 1 đối tượng [CultivationEncounter] vào [GameContentRegistry.Encounters.ALL].
 *
 * 5. THÊM ĐAN DƯỢC / PHÁP BẢO:
 *    - Thêm 1 đối tượng [InventoryItem] vào [GameContentRegistry.Items.ALL].
 * ==============================================================================
 */

/**
 * Định nghĩa Kỳ Duyên / Sự Kiện Lịch Luyện Bất Ngờ khi bế quan hoặc du lịch
 */
data class CultivationEncounter(
    val id: String,
    val title: String,
    val description: String,
    val minAge: Int = 16,
    val minRealm: CultivationRealm = CultivationRealm.LUYEN_KHI,
    val qiReward: Long = 0L,
    val spiritStonesReward: Long = 0L,
    val karmaDelta: Int = 0,
    val daoTamDelta: Int = 0,
    val itemRewardId: String? = null,
    val encounterType: String = "INFO" // INFO, SUCCESS, DANGER, TRIBULATION
)

object GameContentRegistry {

    /**
     * KHO TỪ ĐIỀU THIÊN MỆNH (DESTINY TRAITS REGISTRY)
     * Thêm từ điều mới trực tiếp tại đây:
     */
    object Traits {
        private val _customTraits = mutableListOf<DestinyTrait>()

        val BASE_TRAITS: List<DestinyTrait> = listOf(
            // -------------------------------------------------------------
            // TIER 1: THẦN THOẠI (pointCost = +3, Yêu cầu ít nhất 1 Tai Họa)
            // -------------------------------------------------------------
            DestinyTrait(
                id = "t_hoang_co",
                name = "Hoang Cổ Thánh Thể",
                tier = "Thần Thoại",
                boonDesc = "Thể phách vô địch, cận chiến x2, miễn nhiễm 100% Đan Độc, Căn Cốt +40, Thọ Nguyên +25.",
                curseDesc = "Thiên Đạo chèn ép: Linh khí cần x3; Khởi đầu gánh chịu +40 điểm Sát Khí.",
                costCongDuc = 130,
                pointCost = 3,
                lifespanMod = 25,
                canCotMod = 40,
                satKhiMod = 40,
                qiMultiplierMod = 3L
            ),
            DestinyTrait(
                id = "t_trong_sinh",
                name = "Trọng Sinh Chi Hồn",
                tier = "Thần Thoại",
                boonDesc = "Ký ức tiền kiếp: Ngộ Tính +50, Thần Thức +30, dự đoán thiên cơ chuẩn xác.",
                curseDesc = "Nghiệp chướng tiền kiếp: +35 điểm Sát Khí oán hận, Đạo Tâm -10.",
                costCongDuc = 110,
                pointCost = 3,
                ngoTinhMod = 50,
                thanThucMod = 30,
                satKhiMod = 35,
                daoTamMod = -10
            ),
            DestinyTrait(
                id = "t_cuu_am",
                name = "Cửu Âm Tuyệt Mạch",
                tier = "Thần Thoại",
                boonDesc = "Kinh mạch hàn khí cực phẩm, nạp khí x2.5, Ngộ Tính +15, Thần Thức +10.",
                curseDesc = "Hàn độc đoạt mệnh: Thọ Nguyên giảm 25 năm, phải Trúc Cơ sớm để tục mệnh.",
                costCongDuc = 100,
                pointCost = 3,
                lifespanMod = -25,
                ngoTinhMod = 15,
                thanThucMod = 10
            ),
            DestinyTrait(
                id = "t_hon_don",
                name = "Hỗn Độn Đạo Thể",
                tier = "Thần Thoại",
                boonDesc = "Đạo thể nguyên sơ, tương thích vạn pháp ngũ hành, Ngộ Tính +40, Căn Cốt +30, Đạo Tâm +10.",
                curseDesc = "Thiên Đạo đố kỵ: Tiêu hao 120 Công Đức, lôi kiếp hung hiểm x1.5.",
                costCongDuc = 120,
                pointCost = 3,
                ngoTinhMod = 40,
                canCotMod = 30,
                daoTamMod = 10
            ),
            DestinyTrait(
                id = "t_kiem_tien_tai_the",
                name = "Kiếm Tiên Tái Thế",
                tier = "Thần Thoại",
                boonDesc = "Sinh ra ngậm kiếm phách: Ngộ Tính +45, Căn Cốt +25, sát thương kiếm thuật x2.5.",
                curseDesc = "Kiếm sát cô độc: Khởi đầu +30 Sát Khí, dễ thu hút kiếm tu khiêu chiến.",
                costCongDuc = 115,
                pointCost = 3,
                ngoTinhMod = 45,
                canCotMod = 25,
                satKhiMod = 30
            ),

            // -------------------------------------------------------------
            // TIER 2: TAI HỌA (pointCost = -3, Cân bằng điểm số mệnh)
            // -------------------------------------------------------------
            DestinyTrait(
                id = "t_gia_toc_diet",
                name = "Gia Tộc Diệt Môn",
                tier = "Tai Họa",
                boonDesc = "Nung nấu ý chí báo thù: Động lực sinh tồn giúp tăng 20 điểm Ẩn Nhẫn Trị.",
                curseDesc = "Huyết hải thâm thù: +45 điểm Sát Khí/Nghiệp Lực, Đạo Tâm -15.",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                anNhanTriMod = 20,
                satKhiMod = 45,
                daoTamMod = -15
            ),
            DestinyTrait(
                id = "t_doan_menh",
                name = "Đoản Mệnh Thiên Kiếp",
                tier = "Tai Họa",
                boonDesc = "Phá kén cấp bách: Tốc độ hấp thu linh khí trong 30 năm đầu tăng 20%.",
                curseDesc = "Mệnh bạc như tờ: Thọ Nguyên cực hạn giảm vĩnh viễn 30 năm.",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                lifespanMod = -30
            ),
            DestinyTrait(
                id = "t_phe_mach",
                name = "Phế Mạch Trầm Kha",
                tier = "Tai Họa",
                boonDesc = "Gian nan rèn tâm: Nhờ chịu khổ cực từ nhỏ mà Đạo Tâm tăng +10.",
                curseDesc = "Kinh mạch tắc nghẽn: Căn Cốt -25 điểm, Lôi Kiếp đợt đầu sát thương tăng.",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                canCotMod = -25,
                daoTamMod = 10
            ),
            DestinyTrait(
                id = "t_tam_ma_am_anh",
                name = "Tâm Ma Ám Ảnh",
                tier = "Tai Họa",
                boonDesc = "Ngộ đạo cực đoan: Tăng 10 điểm Ngộ Tính khi nghiên cứu cấm thuật.",
                curseDesc = "Vực sâu tâm ma: Đạo Tâm giảm 25 điểm, sát khí +15.",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                ngoTinhMod = 10,
                daoTamMod = -25,
                satKhiMod = 15
            ),
            DestinyTrait(
                id = "t_thien_sat_co_tinh",
                name = "Thiên Sát Cô Tinh",
                tier = "Tai Họa",
                boonDesc = "Sát phạt lãnh khốc: Sát thương bạo kích tăng 30%.",
                curseDesc = "Khắc thân khắc hữu: Sát Khí +40, Đạo Tâm -10, đệ tử khó trung thành.",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                satKhiMod = 40,
                daoTamMod = -10
            ),

            // -------------------------------------------------------------
            // TIER 3: HIẾM CÓ & THƯỜNG (pointCost = +1 hoặc 0)
            // -------------------------------------------------------------
            DestinyTrait(
                id = "t_duoc_than",
                name = "Dược Thần Chi Khu",
                tier = "Hiếm Có",
                boonDesc = "Uống đan dược nhận x1.8 Linh Khí, Thần Thức +20, tán độc nhanh.",
                curseDesc = "Thể chất suy nhược: Căn Cốt -15 điểm.",
                costCongDuc = 75,
                pointCost = 1,
                thanThucMod = 20,
                canCotMod = -15
            ),
            DestinyTrait(
                id = "t_cau_dao",
                name = "Cẩu Đạo Tiên Mầm",
                tier = "Hiếm Có",
                boonDesc = "Ẩn Nhẫn Trị tăng x2, Lôi Kiếp giảm 30% sát thương, Đạo Tâm +15.",
                curseDesc = "Tâm tính quá cẩn thận: Giảm khả năng nhặt bảo vật SSR từ quẻ Đại Hung.",
                costCongDuc = 65,
                pointCost = 1,
                anNhanTriMod = 20,
                daoTamMod = 15
            ),
            DestinyTrait(
                id = "t_kiem_thai",
                name = "Vô Thượng Kiếm Thai",
                tier = "Hiếm Có",
                boonDesc = "Sát thương kiếm đạo +100%, Ngộ Tính +15, Căn Cốt +10.",
                curseDesc = "Kiếm ý cương liệt: Nếu đan độc cao bế quan dễ nghịch chuyển chân khí.",
                costCongDuc = 85,
                pointCost = 1,
                ngoTinhMod = 15,
                canCotMod = 10
            ),
            DestinyTrait(
                id = "t_khi_van",
                name = "Khí Vận Chi Tử",
                tier = "Thường",
                boonDesc = "Phúc duyên dồi dào: Cược thạch phường thị tỉ lệ trúng tăng 50%, Đạo Tâm +5.",
                curseDesc = "Dễ bị đồng đạo ghen ghét ám toán.",
                costCongDuc = 45,
                pointCost = 0,
                daoTamMod = 5
            ),
            DestinyTrait(
                id = "t_pha_toai_cot",
                name = "Phá Toái Tiên Cốt",
                tier = "Thường",
                boonDesc = "Từng có tiên căn: Ngộ Tính khởi đầu +10.",
                curseDesc = "Đã bị phế: Căn Cốt -10.",
                costCongDuc = 30,
                pointCost = 0,
                ngoTinhMod = 10,
                canCotMod = -10
            )
        )

        /**
         * Danh sách tất cả từ điều đang hoạt động trong game
         */
        val ALL: List<DestinyTrait>
            get() = BASE_TRAITS + _customTraits

        /**
         * Hàm thêm từ điều mới dạng code runtime
         */
        fun registerTrait(trait: DestinyTrait) {
            if (_customTraits.none { it.id == trait.id }) {
                _customTraits.add(trait)
            }
        }
    }

    /**
     * KHO QUẺ BÓI DIỄN TOÁN THIÊN CƠ (DIVINATIONS REGISTRY)
     * Thêm sự kiện quẻ bói mới trực tiếp tại đây:
     */
    object Divinations {
        val ALL: List<DivinationEvent> = listOf(
            DivinationEvent(
                type = HexagramType.THUONG_THUONG_CAT,
                title = "Thượng Cổ Động Phủ Hiện Thế",
                narrative = "Mây tía vạn dặm cuồn cuộn phía Đông, một tòa di phủ tiên nhân vừa lộ ra tại Đoạn Hồn Cốc.",
                safeChoiceLabel = "Cẩu đạo quan sát, nhặt tàn diệp",
                safeChoiceEffect = "+80 Linh Thạch, an nhàn vô sự",
                riskyChoiceLabel = "Liều mạng tranh đoạt trung tâm",
                riskyChoiceEffect = "50% nhặt Vạn Niên Linh Dược, 50% gánh chịu sát thủ vây công"
            ),
            DivinationEvent(
                type = HexagramType.THUONG_CAT,
                title = "Thiên Hàng Dị Thú",
                narrative = "Một đạo linh quang giáng xuống rừng sau động phủ, ẩn hiện tiếng thú gầm thanh thúy.",
                safeChoiceLabel = "Đem linh quả thuần hóa",
                safeChoiceEffect = "+30 Thần Thức, dị thú gia tăng độ thân mật",
                riskyChoiceLabel = "Cưỡng ép ký huyết khế",
                riskyChoiceEffect = "Nếu căn cốt > 60 thành công nhận dị thú hộ sơn; ngược lại bị cắn trả"
            ),
            DivinationEvent(
                type = HexagramType.BINH_HOA,
                title = "Phường Thị Đấu Giá Hội",
                narrative = "Vạn Bảo Các tổ chức đại hội bán đấu giá trăm năm một lần, đông đảo tu sĩ tụ họp.",
                safeChoiceLabel = "Bán linh thảo kiếm chênh lệch",
                safeChoiceEffect = "+120 Linh Thạch",
                riskyChoiceLabel = "Cược thạch cổ mạt bảo",
                riskyChoiceEffect = "Tiêu 100 Linh Thạch, cơ hội nhận Cổ Bảo vô giá"
            ),
            DivinationEvent(
                type = HexagramType.HUNG,
                title = "Ma Tu Đột Kích Động Phủ",
                narrative = "Một gã Kim Đan tà tu bị trọng thương chạy trốn ngang qua trận pháp phòng ngự của ngươi.",
                safeChoiceLabel = "Đóng kín đại trận, cẩu đạo ẩn giấu",
                safeChoiceEffect = "Mất 10 Linh Khí gia cố đại trận, bình an",
                riskyChoiceLabel = "Xuất kích diệt ma đoạt bảo",
                riskyChoiceEffect = "Thắng nhận 200 Linh Thạch; Bại bị trúng Huyết Thi Độc"
            ),
            DivinationEvent(
                type = HexagramType.DAI_HUNG,
                title = "Thiên Đạo Trừu Liệt Kiếp Nạn",
                narrative = "Khí tức tử khí bốc lên, số mệnh ngươi chạm phải họa diệt đỉnh do sát khí quá nặng.",
                safeChoiceLabel = "Tụng niệm Hoàng Đình Kinh, tịnh hóa",
                safeChoiceEffect = "-30 Sát Khí, tiêu hao 50 Công Đức",
                riskyChoiceLabel = "Nghịch thiên đối kháng sát trận",
                riskyChoiceEffect = "Vượt qua Đạo Tâm +25; Thất bại tổn thọ 15 năm"
            )
        )
    }

    /**
     * KHO KỲ DUYÊN & SỰ KIỆN NỘI DUNG BẾ QUAN (ENCOUNTERS REGISTRY)
     * Thêm sự kiện lịch luyện bất ngờ mới tại đây:
     */
    object Encounters {
        val ALL: List<CultivationEncounter> = listOf(
            CultivationEncounter(
                id = "enc_ruong_dong",
                title = "Động Thiên Nhặt Bảo",
                description = "Trong lúc bế quan đả tọa, vách đá nứt ra làm lộ một chiếc cổ giới tử...",
                qiReward = 150L,
                spiritStonesReward = 100L,
                encounterType = "SUCCESS"
            ),
            CultivationEncounter(
                id = "enc_chan_khi_nghich",
                title = "Chân Khí Nghịch Lưu",
                description = "Uống đan dược quá độ, kinh mạch chấn động...",
                daoTamDelta = -5,
                karmaDelta = 5,
                encounterType = "WARNING"
            ),
            CultivationEncounter(
                id = "enc_de_tu_hieu_thao",
                title = "Đệ Tử Dâng Tặng Linh Thảo",
                description = "Đệ tử lịch luyện bên ngoài hái được Chu Quả 500 năm hiến tặng sư tôn...",
                qiReward = 300L,
                spiritStonesReward = 50L,
                encounterType = "SUCCESS"
            )
        )
    }

    /**
     * KHO ĐAN DƯỢC & PHÁP BẢO CHUẨN HÓA (ITEMS REGISTRY)
     */
    object Items {
        val ALL: List<InventoryItem> = listOf(
            InventoryItem(
                id = "item_truc_co_dan",
                name = "Trúc Cơ Đan",
                category = ItemCategory.DAN_DUOC,
                description = "Đan dược phá cảnh Luyện Khí -> Trúc Cơ, tăng 35% tỉ lệ thành công, thêm 300 Linh Khí, sinh 15% Đan Độc.",
                count = 1,
                qiBonus = 300L,
                toxicityBonus = 15,
                sellPrice = 150L
            ),
            InventoryItem(
                id = "item_tay_tuy_dan",
                name = "Tẩy Tủy Đan",
                category = ItemCategory.DAN_DUOC,
                description = "Tẩy kinh phạt tủy, bài tiết 25% Đan Độc tích tụ trong kinh mạch.",
                count = 1,
                toxicityBonus = -25,
                sellPrice = 80L
            ),
            InventoryItem(
                id = "item_ngu_nguyen_dan",
                name = "Ngũ Nguyên Hóa Khí Đan",
                category = ItemCategory.DAN_DUOC,
                description = "Nạp nhanh 120 Linh Khí, sinh 8% Đan Độc.",
                count = 1,
                qiBonus = 120L,
                toxicityBonus = 8,
                sellPrice = 35L
            ),
            InventoryItem(
                id = "item_cuu_chuyen_hoan_hon",
                name = "Cửu Chuyển Hoàn Hồn Đan",
                category = ItemCategory.DAN_DUOC,
                description = "Nghịch chuyển âm dương, giảm 50 Sát Khí, tăng 30 năm thọ nguyên cực hạn.",
                count = 1,
                sellPrice = 500L
            )
        )
    }
}
