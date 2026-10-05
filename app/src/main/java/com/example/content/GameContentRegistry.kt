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
            // =============================================================
            // 1. THẦN THOẠI (pointCost = +3, Chi phí Công Đức dương: 135 - 140)
            // =============================================================
            DestinyTrait(
                id = "t_hoang_co",
                name = "Hoang Cổ Thánh Thể",
                tier = "Thần Thoại",
                boonDesc = "Nhục thân chí tôn vô địch: Căn Cốt +45, Thọ Nguyên +30, cận chiến x2.5 sát thương, vĩnh viễn miễn nhiễm 100% Đan Độc.",
                curseDesc = "Thiên Đạo giáng tội chèn ép: Đoạn tuyệt thiên địa linh khí! Đột phá cần lượng Linh Khí x3, Lôi Kiếp uy lực x2; Khởi đầu gánh chịu +40 Sát Khí bị kẻ thù truy sát.",
                costCongDuc = 140,
                pointCost = 3,
                lifespanMod = 25,
                canCotMod = 40,
                satKhiMod = 40,
                qiMultiplierMod = 3L
            ),
            DestinyTrait(
                id = "t_thai_so_hon_don",
                name = "Thái Sơ Hỗn Độn Khí",
                tier = "Thần Thoại",
                boonDesc = "Thai nghén luồng Tiên Khí nguyên sơ: Ngộ Tính +50, Thần Thức +35, Đạo Tâm +20, đồng hóa vạn pháp ngũ hành.",
                curseDesc = "Đạo khí nghịch thiên thu hút Thiên Ma thức hải: Mỗi lần đột phá giáng hạ Cửu U Tâm Ma Kiếp hung hiểm x2; Khởi đầu +25 Sát Khí oán niệm.",
                costCongDuc = 135,
                pointCost = 3,
                ngoTinhMod = 50,
                thanThucMod = 35,
                daoTamMod = 20,
                satKhiMod = 25
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

            // =============================================================
            // 2. SỬ THI (pointCost = +2, Chi phí Công Đức dương: 90 - 95)
            // =============================================================
            DestinyTrait(
                id = "t_tan_hon_lao_gia_gia",
                name = "Tàn Hồn Lão Gia Gia",
                tier = "Sử Thi",
                boonDesc = "Giới chỉ phong ấn tàn hồn Tiên Tôn/Lão Ma: Thường xuyên điểm hóa bí thuật, Thần Thức +30, Ngộ Tính +25, miễn tử đòn trí mạng 1 lần.",
                curseDesc = "Ngọa hổ tàng long, lão ma ngày đêm rình rập đoạt xá: Đạo Tâm -15, Sát Khí +30; Nếu Đan Độc vượt quá 50% sẽ lập tức bị lão ma cưỡng ép khởi động Đoạt Xá!",
                costCongDuc = 95,
                pointCost = 2,
                thanThucMod = 30,
                ngoTinhMod = 25,
                daoTamMod = -15,
                satKhiMod = 30
            ),
            DestinyTrait(
                id = "t_hung_thu_huyet_mach",
                name = "Thái Cổ Hung Thú Huyết Mạch",
                tier = "Sử Thi",
                boonDesc = "Kế thừa huyết thống Thao Thiết/Chân Long: Căn Cốt +35, Thọ Nguyên +20, nạp đan dược hấp thu x2 Linh Khí, bạo phát cuồng bạo khi nguy kịch.",
                curseDesc = "Hung tính khó thuần: Mỗi 10 năm phát tác Cuồng Huyết Nhập Não một lần (bắt buộc phải sát sinh hoặc tự cắn xé kinh mạch tổn thọ 5 năm), Ẩn Nhẫn Trị -20, Sát Khí +35.",
                costCongDuc = 90,
                pointCost = 2,
                canCotMod = 35,
                lifespanMod = 20,
                satKhiMod = 35,
                anNhanTriMod = -20
            ),

            // =============================================================
            // 3. HIẾM (pointCost = +1, Chi phí Công Đức dương: 65 - 70)
            // =============================================================
            DestinyTrait(
                id = "t_di_chung_kiem_cot",
                name = "Dị Chủng Kiếm Cốt",
                tier = "Hiếm",
                boonDesc = "Xương tủy tiên thiên kiếm thai: Sát thương kiếm thuật +80%, Ngộ Tính +20, Căn Cốt +15, lĩnh ngộ kiếm đạo cực nhanh.",
                curseDesc = "Kiếm sát bén nhọn rạch xé kinh mạch mỗi khi vận công: Khí tức lộ liễu khiến nguy cơ bị kẻ thù phục kích tăng 25%, Sát Khí +15.",
                costCongDuc = 70,
                pointCost = 1,
                ngoTinhMod = 20,
                canCotMod = 15,
                satKhiMod = 15
            ),
            DestinyTrait(
                id = "t_chan_duong_hoa_mach",
                name = "Chân Dương Hỏa Mạch",
                tier = "Hiếm",
                boonDesc = "Chân hỏa rực cháy kinh lạc: Kháng 70% Hỏa Độc, luyện đan tỉ lệ cực phẩm tăng 40%, Căn Cốt +15, Linh Khí ban đầu +100.",
                curseDesc = "Hỏa độc thiêu đốt tâm can: Tánh khí nóng nảy bức bách, Đạo Tâm -10; Nếu không tìm được linh dược âm hàn trước 50 tuổi sẽ tự thiêu đốt ngũ tạng (Thọ Nguyên -10).",
                costCongDuc = 65,
                pointCost = 1,
                canCotMod = 15,
                daoTamMod = -10,
                lifespanMod = -10
            ),

            // =============================================================
            // 4. PHÀM PHẨM / NGUYỄN RỦA (pointCost = -3, Hoàn trả điểm số mệnh âm)
            // =============================================================
            DestinyTrait(
                id = "t_gia_toc_diet",
                name = "Gia Tộc Bị Diệt (Huyết Oán)",
                tier = "Tai Họa",
                boonDesc = "Biến số đốn ngộ: Kích phát ý chí sinh tồn tột cùng, Ẩn Nhẫn Trị +35; Khi HP dưới 20% tỉ lệ đốn ngộ đột phá sinh tử tăng 30%.",
                curseDesc = "Huyết hải thâm thù: Toàn gia bị tru diệt, trên trán khắc Huyết Oán Nguyền, gánh chịu +50 Sát Khí/Nghiệp Lực, Đạo Tâm -20, định kỳ bị ma tu truy sát!",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                anNhanTriMod = 35,
                satKhiMod = 50,
                daoTamMod = -20
            ),
            DestinyTrait(
                id = "t_doan_menh",
                name = "Cửu Âm Đoản Mệnh (Tuyệt Mạch)",
                tier = "Tai Họa",
                boonDesc = "Biến số đốn ngộ: Thời gian cạn kiệt ép buộc nghịch mệnh; Tốc độ hấp thu linh khí trong 40 năm đầu x1.5, Ngộ Tính tuyệt cảnh +25.",
                curseDesc = "Mệnh bạc như tờ: Thọ Nguyên cực hạn bị chém đứt vĩnh viễn 35 năm (chỉ sống tối đa 65 tuổi ở Luyện Khí Kỳ), Căn Cốt -20 điểm.",
                costCongDuc = 0,
                pointCost = -3,
                isCalamity = true,
                lifespanMod = -35,
                canCotMod = -20,
                ngoTinhMod = 25
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

    /**
     * KHO SỰ KIỆN TU TIÊN HẮC ÁM (DARK CULTIVATION EVENTS)
     * Thiết kế theo 3 con đường sinh tồn:
     * - [Cẩu Đạo]: An toàn, chịu thiệt thòi tài vật nhưng giữ mạng.
     * - [Tranh Đoạt]: Liều mạng cướp tạo hóa, sinh tử nhất tuyến (High risk - High reward).
     * - [Ẩn Nhẫn / Tà Đạo]: Dùng mưu kế, đánh lén, hiến tế, ma đạo mưu mô.
     */
    object DarkEvents {
        val ALL: List<DarkCultivationEvent> = listOf(
            DarkCultivationEvent(
                id = "evt_doat_xa_thi_lac_coc",
                title = "Tàn Hồn Đoạt Xá Tại Thi Lạc Cốc",
                narrative = "Bên trong hang đá âm u ngập tràn mùi thối rữa của tử thi, một bộ bạch cốt bỗng bốc lên u hỏa xanh biếc. Tàn hồn một lão quái Trúc Cơ kỳ chậm rãi bay ra, âm lãnh khóa chặt lấy mi tâm ngươi: 'Tiểu bối nhục thân không tệ, dâng hiến thể xác cho lão phu, lão phu ban cho ngươi đại đạo tiền trình!'",
                cauDao = DarkEventChoice(
                    path = ChoicePath.CAU_DAO,
                    label = "[Cẩu Đạo] Dập đầu tha mạng, ném Túi Trữ Vật rồi độn thổ đào tẩu",
                    actionText = "Thu liễm khí tức, vứt bỏ toàn bộ tài vật vắt chân lên cổ chạy trối chết!",
                    outcomeNarrative = "Ngươi mất sạch tài vật tích góp, kinh mạch rạn nứt tổn thọ 2 năm, nhưng bảo toàn được tính mạng nguyên vẹn trước nanh vuốt lão quái.",
                    effects = EventStatsEffect(lifespan = -2, qi = -20, pillToxin = 0, karma = -5, divineSense = 10)
                ),
                tranhDoat = DarkEventChoice(
                    path = ChoicePath.TRANH_DOAT,
                    label = "[Tranh Đoạt] Dốc cạn đan điền, kích phát toàn bộ Phù Lục liều chết một trận",
                    actionText = "Gầm lên giận dữ, rút Linh Kiếm kích phát Tam Muội Chân Hỏa quyết tử chiến!",
                    outcomeNarrative = "Ngươi bị ma khí đánh nát kinh mạch thổ huyết liên tục, nhưng may mắn kích sát được tàn hồn, đoạt được Trúc Cơ Tàn Đan cùng ký ức công pháp thượng cổ!",
                    effects = EventStatsEffect(lifespan = -5, qi = 350, pillToxin = 15, karma = 30, divineSense = 25)
                ),
                anNhanTaDao = DarkEventChoice(
                    path = ChoicePath.AN_NHAN_TA_DAO,
                    label = "[Ẩn Nhẫn / Tà Đạo] Giả vờ mở thức hải, ngậm sẵn Độc Đan dẫn dụ lão ma nuốt chửng",
                    actionText = "Quỳ gối xưng thần, ngậm Hủ Cốt Tán chờ lão ma xông vào thức hải liền dẫn bạo tương tàn!",
                    outcomeNarrative = "Lão quái trúng kế bị độc khí ăn mòn thảm thiết. Ngươi cắn răng nuốt chửng tàn hồn ngược lại, thần thức bạo tăng, nhưng sát khí và đan độc nhập cốt tủy.",
                    effects = EventStatsEffect(lifespan = -3, qi = 180, pillToxin = 35, karma = 45, divineSense = 50)
                )
            ),
            DarkCultivationEvent(
                id = "evt_tranh_doat_huyet_tinh_chi",
                title = "Huyết Đầm Tranh Đoạt Huyết Tinh Chi",
                narrative = "Giữa đầm lầy tanh tưởi, một gốc Huyết Tinh Chi ngàn năm đỏ rực như máu đang tỏa hương thơm ngát. Xác chết chục tu sĩ vương vãi, một con Nhị Giai Xích Lân Mãng cuộn tròn say ngủ.",
                cauDao = DarkEventChoice(
                    path = ChoicePath.CAU_DAO,
                    label = "[Cẩu Đạo] Ẩn nấp trong bùn lầy nửa tháng, chỉ nhặt rác túi trữ vật rồi lặng lẽ rút",
                    actionText = "Nuốt Bế Khí Đan, vùi mình sâu trong hố bùn kiên nhẫn chờ thời cơ an toàn.",
                    outcomeNarrative = "Chờ mãng xà bỏ đi, ngươi rón rén gom vét 3 chiếc túi trữ vật vô chủ rồi lùi bước an toàn, tâm cảnh cẩn trọng vững như bàn thạch.",
                    effects = EventStatsEffect(lifespan = 0, qi = 40, pillToxin = -5, karma = 0, divineSense = 15)
                ),
                tranhDoat = DarkEventChoice(
                    path = ChoicePath.TRANH_DOAT,
                    label = "[Tranh Đoạt] Phục kích bạo khởi, chém đầu yêu mãng đoạt Huyết Tinh Chi ngàn năm",
                    actionText = "Bày trận pháp, dẫn bạo 10 lá Hỏa Cầu Phù thiêu cháy hốc mắt cự thú, lao vào cận chiến!",
                    outcomeNarrative = "Ngươi chém đứt đầu mãng xà, ngực gãy 3 dẻ sườn. Nuốt tươi Huyết Tinh Chi, linh khí cuồn cuộn phá tan bình cảnh, thọ nguyên tăng vọt!",
                    effects = EventStatsEffect(lifespan = 15, qi = 600, pillToxin = 20, karma = 40, divineSense = 10)
                ),
                anNhanTaDao = DarkEventChoice(
                    path = ChoicePath.AN_NHAN_TA_DAO,
                    label = "[Ẩn Nhẫn / Tà Đạo] Đánh mê đồng môn ném vào miệng mãng xà, nhân lúc nó ăn mồi ra tay",
                    actionText = "Mặt không đổi sắc hạ độc đồng môn làm mồi nhử, bản thân lẻn hái trộm linh dược rồi phóng hỏa thiêu rụi dấu vết.",
                    outcomeNarrative = "Đồng môn thê thảm trong bụng rắn. Ngươi hái trọn linh dược trốn thoát êm thấm. Lòng dạ tàn nhẫn như sói dữ, không tốn chút sức lực đoạt đại tạo hóa!",
                    effects = EventStatsEffect(lifespan = 20, qi = 500, pillToxin = 0, karma = 65, divineSense = 20)
                )
            ),
            DarkCultivationEvent(
                id = "evt_tong_mon_huyet_te",
                title = "Tông Môn Huyết Tế Ma Trận",
                narrative = "Tông môn bị Vạn Ma Tông vây hãm. Chưởng Môn mắt đỏ ngầu triệu tập đệ tử: 'Đại trận sắp vỡ, bản tọa cần tinh huyết của chư vị hiến tế Hộ Tông Ma Đỉnh!'",
                cauDao = DarkEventChoice(
                    path = ChoicePath.CAU_DAO,
                    label = "[Cẩu Đạo] Đã đào sẵn đường hầm bí mật từ 5 năm trước, lập tức cải trang đào tẩu",
                    actionText = "Kích hoạt trận bàn tàng hình đã giấu từ lâu, không ngoảnh đầu nhìn lại sư môn.",
                    outcomeNarrative = "Ngươi bỏ trốn thành công. Sư môn sau đó bị đồ sát sạch sẽ. Ngươi sống sót biến thành tán tu phiêu bạt, thấu hiểu quy tắc sinh tồn máu lạnh.",
                    effects = EventStatsEffect(lifespan = 0, qi = -50, pillToxin = 0, karma = 10, divineSense = 20)
                ),
                tranhDoat = DarkEventChoice(
                    path = ChoicePath.TRANH_DOAT,
                    label = "[Tranh Đoạt] Cùng đồng môn xông lên tuyến đầu huyết chiến cản phá ma tu",
                    actionText = "Rút bổn mạng pháp bảo, dẫn đầu chiến đội đệ tử xông thẳng vào vạn ma sát trận quyết tử!",
                    outcomeNarrative = "Ngươi chém giết 5 ma tu cùng cấp, trọng thương suýt chết nhưng được thái thượng trưởng lão truyền công ban thưởng Huyết Linh Đan phá cảnh!",
                    effects = EventStatsEffect(lifespan = -10, qi = 900, pillToxin = 25, karma = 50, divineSense = 35)
                ),
                anNhanTaDao = DarkEventChoice(
                    path = ChoicePath.AN_NHAN_TA_DAO,
                    label = "[Ẩn Nhẫn / Tà Đạo] Đánh lén Chưởng Môn từ sau lưng, dâng đầu lâu Chưởng Môn hàng Ma Tông",
                    actionText = "Phóng Độc Hồn Châm đâm thủng đan điền Chưởng Môn, chặt đầu dâng nạp mở toang trận môn nghênh đón ma đầu.",
                    outcomeNarrative = "Ma Tông Ma Đầu cười vang tán thưởng lòng dạ độc ác, phong ngươi làm Đường Chủ Ma Môn, ban thưởng Ma Đạo Tâm Pháp. Đạo tâm vỡ vụn biến thành Ma Đầu chân chính!",
                    effects = EventStatsEffect(lifespan = 30, qi = 1200, pillToxin = 40, karma = 90, divineSense = 40)
                )
            ),
            DarkCultivationEvent(
                id = "evt_song_tu_lo_dinh",
                title = "Bẫy Ngọt Ngào: Mỹ Nhân Song Tu Hay Độc Lô Đỉnh?",
                narrative = "Một vị Dao Trì Tiên Tử tuyệt sắc chủ động ngỏ lời mời ngươi cùng bế quan song tu tại Đào Hoa Động. Thần thức nhạy bén phát hiện trong hương phấn có pha lẫn 'Toái Hồn Tán' biến ngươi thành hình nhân Lô Đỉnh hút cạn tu vi.",
                cauDao = DarkEventChoice(
                    path = ChoicePath.CAU_DAO,
                    label = "[Cẩu Đạo] Giả vờ đau bụng thổ tả vì tẩu hỏa nhập ma, suốt đêm dọn nhà đào tẩu ngàn dặm",
                    actionText = "Miệng phun bọt mép giả điên giả dại xin lỗi tiên tử rồi dọn sạch động phủ đào tẩu.",
                    outcomeNarrative = "Nữ tu tức giận nhưng không thể làm gì. Ngươi bảo toàn được nguyên dương tinh thuần, tuy mất mặt nhưng giữ trọn cái mạng nhỏ sống lâu.",
                    effects = EventStatsEffect(lifespan = 0, qi = 0, pillToxin = 0, karma = -5, divineSense = 15)
                ),
                tranhDoat = DarkEventChoice(
                    path = ChoicePath.TRANH_DOAT,
                    label = "[Tranh Đoạt] Tương kế tựu kế, vào phòng liền kích hoạt Kiếm Trận chém chết độc phụ",
                    actionText = "Vừa bước vào phòng hoa liền phóng ra 36 thanh Thanh Trúc Kiếm bao vây kết liễu nữ tu!",
                    outcomeNarrative = "Nữ tu bị kiếm trận băm thành từng mảnh. Ngươi đoạt được Túi Trữ Vật chứa đầy cực phẩm đan dược và linh thạch, nhưng bị Dao Trì phát lệnh truy nã toàn cảnh.",
                    effects = EventStatsEffect(lifespan = -5, qi = 1200, pillToxin = 10, karma = 50, divineSense = 40)
                ),
                anNhanTaDao = DarkEventChoice(
                    path = ChoicePath.AN_NHAN_TA_DAO,
                    label = "[Ẩn Nhẫn / Tà Đạo] Uống trước Nghịch Huyết Đan, lúc song tu đảo ngược trận pháp biến nàng thành Lô Đỉnh",
                    actionText = "Hùa theo cuộc vui, chờ thời khắc âm dương giao hòa then chốt liền cưỡng ép hút sạch linh âm nguyên khí của nàng!",
                    outcomeNarrative = "Nữ tu dung nhan héo rũ, tu vi Kim Đan bị ngươi hút cạn biến thành phàm nhân già nua. Tu vi ngươi bạo trướng chạm ngưỡng Nguyên Anh Kỳ, nhưng sát khí dày đặc như mây đen!",
                    effects = EventStatsEffect(lifespan = 50, qi = 3000, pillToxin = 20, karma = 85, divineSense = 70)
                )
            )
        )
    }
}
