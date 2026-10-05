package com.example.engine

import com.example.model.*
import com.example.viewmodel.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * GameEngineCore: Quản lý vòng lặp chính của trò chơi tu tiên (Game Loop)
 * - Quản lý StateFlow phản ứng trạng thái người chơi
 * - Theo dõi tuổi hiện tại (current age), tổng thọ nguyên (total lifespan) dựa theo cảnh giới tu vi (cultivation level)
 * - Tự động kích hoạt sự kiện 'Tọa Hóa' (chết già) khi tuổi thọ đạt giới hạn cực hạn
 * - Chuyển đổi thành tựu kiếp này thành Công Đức cho kiếp sau
 */
class GameEngineCore(
    private val onStateChanged: (GameUiState) -> Unit = {}
) {
    private val _gameState = MutableStateFlow(GameUiState())
    val gameState: StateFlow<GameUiState> = _gameState.asStateFlow()

    fun currentState(): GameUiState = _gameState.value

    fun updateState(newState: GameUiState) {
        _gameState.value = newState
        onStateChanged(newState)
    }

    /**
     * Tính toán tổng thọ nguyên tối đa dựa theo cảnh giới tu vi và thể chất
     */
    fun calculateTotalLifespan(
        realm: CultivationRealm,
        canCot: Int = 50,
        extraBonus: Int = 0
    ): Int {
        val base = realm.baseLifespan
        val physiqueBonus = (canCot - 50) / 5
        return (base + physiqueBonus + extraBonus).coerceAtLeast(30)
    }

    /**
     * Vòng lặp thời gian chính (Game Loop Progression)
     * Bế quan tiêu tốn thời gian (số năm), nạp linh khí, kiểm tra thọ nguyên và kích hoạt Tọa Hóa nếu hết thọ
     */
    fun advanceGameLoop(yearsRequested: Int) {
        val s = _gameState.value
        if (s.isDead) return

        val remainingLifespan = maxOf(0, s.maxLifespan - s.age)
        if (remainingLifespan <= 0) {
            triggerToaHoa()
            return
        }

        // Tự động ngắt khi chạm hạn mức thọ nguyên
        val actualYears = minOf(yearsRequested, remainingLifespan)
        val newAge = s.age + actualYears

        // 1. Chi phí duy trì Tụ Linh Trận (Linh Thạch tiêu hao mỗi năm)
        val yearlyCost = s.caveGrade.baseCostPerYear * s.tuLinhTranLevel
        val totalStoneCost = actualYears * yearlyCost
        val hasEnoughStones = s.spiritStones >= totalStoneCost
        val actualStonesDeducted = if (hasEnoughStones) totalStoneCost else s.spiritStones
        val remainingStones = s.spiritStones - actualStonesDeducted

        // 2. Tính toán linh khí tích lũy qua các năm
        val rootMultiplier = s.spiritRoot.speedMultiplier
        val qiStateMult = s.qiState.absorptionMultiplier
        val baseYearlyQi = 15L

        // Nếu thiếu linh thạch: Tụ Linh Trận sụp đổ, tốc độ nạp linh khí giảm 90% (tu luyện phàm trần)
        val arrayMultiplier = if (hasEnoughStones) {
            s.caveGrade.speedMultiplier * (1f + (s.tuLinhTranLevel - 1) * 0.25f)
        } else {
            0.1f // Phạt 90%
        }

        // Đan độc làm suy giảm khả năng hấp thụ linh khí
        val toxicityPenalty = (s.pillToxicity / 100f) * 0.5f
        val effectiveRate = maxOf(0.05f, (rootMultiplier * qiStateMult * arrayMultiplier) - toxicityPenalty)
        val gainedQi = (baseYearlyQi * actualYears * effectiveRate).toLong()

        val updatedQi = minOf(s.maxQi, s.qi + gainedQi)

        if (!hasEnoughStones) {
            addLog(
                year = newAge,
                tag = "Tụ Linh Trận",
                message = "Linh Thạch cạn kiệt (-$actualStonesDeducted LT), Tụ Linh Trận sụp đổ! Tốc độ nạp linh khí giảm 90% (tu luyện phàm trần). Mau xuất sơn kiếm Linh Thạch!",
                type = "WARNING"
            )
        } else {
            addLog(
                year = newAge,
                tag = "Bế Quan",
                message = "Ngươi bế quan $actualYears năm (Tiêu hao $actualStonesDeducted LT duy trì ${s.caveGrade.label} Cấp ${s.tuLinhTranLevel}). Tăng $gainedQi Linh Khí. (Tuổi: $newAge/${s.maxLifespan})",
                type = "INFO"
            )
        }

        // 3. Trục Đối Kháng: Ẩn Nhẫn vs Hung Danh (Tập kích / Chính phái truy sát / Huyết thù)
        var updatedLifespan = s.maxLifespan
        var updatedSatKhi = s.satKhi
        if (s.hasVengefulGhost && Random.nextInt(100) < 25) {
            addLog(
                year = newAge,
                tag = "Huyết Thù",
                message = "OAN HỒN BÁM THÂN! Hậu duệ cừu tộc kiếp trước tìm đến sơn môn tập kích! Ngươi huyết chiến đẩy lùi thích khách nhưng tổn thọ 2 năm, sát khí +15.",
                type = "DANGER"
            )
            updatedLifespan -= 2
            updatedSatKhi += 15
        } else if (s.hungDanh >= 60 && Random.nextInt(100) < 30) {
            addLog(
                year = newAge,
                tag = "Chính Đạo Truy Sát",
                message = "HUNG DANH MA ĐẠO QUÁ CAO! Trưởng lão danh môn chính phái hạ sơn bao vây động phủ! Ngươi liều mình đào tẩu, kinh mạch chấn thương tổn thọ 3 năm.",
                type = "DANGER"
            )
            updatedLifespan -= 3
        }

        // Kinh mạch tự bài tiết một phần nhỏ đan độc theo thời gian nếu bế quan đủ lâu
        var updatedToxicity = s.pillToxicity
        if (actualYears >= 3 && updatedToxicity > 0) {
            val naturalPurge = minOf(updatedToxicity, actualYears * 2)
            updatedToxicity -= naturalPurge
            addLog(newAge, "Hóa Độc", "Kinh mạch tự vận chuyển, bài tiết $naturalPurge% Đan Độc ra ngoài.", "INFO")
        }

        // Đệ tử lịch luyện ngẫu nhiên
        val discipleWindfall = maybeTriggerDiscipleEvent(newAge)

        val updatedState = _gameState.value.copy(
            age = newAge,
            qi = updatedQi,
            maxLifespan = updatedLifespan,
            spiritStones = remainingStones,
            satKhi = updatedSatKhi,
            pillToxicity = updatedToxicity,
            qiState = reevaluateQiState(updatedToxicity)
        )
        updateState(updatedState)

        if (discipleWindfall != null) {
            addLog(newAge, "Đệ Tử", discipleWindfall, "SUCCESS")
        }

        // KIỂM TRA THỌ NGUYÊN: Kích hoạt TỌA HÓA khi chạm giới hạn thọ nguyên
        if (newAge >= s.maxLifespan) {
            triggerToaHoa()
        }
    }

    /**
     * Kích hoạt sự kiện Tọa Hóa (Chết già do hết thọ nguyên)
     * Quy đổi điểm thành tựu tích lũy kiếp này thành Công Đức cho kiếp sau
     */
    fun triggerToaHoa() {
        val s = _gameState.value
        val earnedCongDuc = 50 + (s.realm.ordinal * 100) + (s.subStage * 15) + (s.age / 2)
        val totalCongDuc = s.congDuc + earnedCongDuc

        addLog(
            year = s.maxLifespan,
            tag = "Tọa Hóa",
            message = "Năm ${s.maxLifespan} tuổi, thọ nguyên cạn kiệt, ngươi tại động phủ hóa đạo tọa hóa! Khai mở Luân Hồi Kính, nhận $earnedCongDuc điểm Công Đức cho kiếp sau.",
            type = "TRIBULATION"
        )

        val record = PastLifeRecord(
            generation = s.generation,
            finalRealm = s.realm.getDisplayName(s.subStage),
            ageAtDeath = "${s.maxLifespan}/${s.maxLifespan}",
            causeOfDeath = "Thọ nguyên cạn kiệt (Tọa Hóa)",
            congDucEarned = earnedCongDuc
        )

        updateState(
            s.copy(
                isDead = true,
                deathCause = "Thọ nguyên cạn kiệt tại tuổi ${s.maxLifespan}",
                congDuc = totalCongDuc,
                pastLives = s.pastLives + record,
                showLuanHoiDialog = true
            )
        )
    }

    /**
     * Đột phá cảnh giới (Tiểu cảnh giới hoặc Đại cảnh giới)
     * Khi đột phá Đại Cảnh Giới thành công sẽ gia tăng tổng Thọ Nguyên cực lớn
     */
    fun attemptBreakthrough() {
        val s = _gameState.value
        if (s.isDead) return

        if (s.qi < s.maxQi) {
            addLog(s.age, "Đột Phá", "Linh khí chưa viên mãn (${s.qi}/${s.maxQi}), cưỡng ép đột phá chỉ chuốc lấy bại vong!", "WARNING")
            return
        }

        val isMajorBreakthrough = s.subStage >= s.realm.maxSubStages
        if (isMajorBreakthrough) {
            triggerMajorTribulation()
        } else {
            performMinorBreakthrough()
        }
    }

    private fun performMinorBreakthrough() {
        val s = _gameState.value
        var rate = s.realm.breakthroughBaseRate + (s.ngoTinh * 0.002f)
        if (s.spiritRoot == SpiritRoot.THIEN_LINH_CAN) rate += 0.20f

        if (s.pillToxicity > 60) {
            val penalty = ((s.pillToxicity - 50) / 100f) * 0.7f
            rate -= penalty
        }

        rate = rate.coerceIn(0.10f, 0.95f)
        val roll = Random.nextFloat()

        if (roll <= rate) {
            val newSub = s.subStage + 1
            val newMaxQi = (s.maxQi * 1.35f).toLong()
            addLog(
                s.age,
                "Đột Phá",
                "Đạo tâm kiên định, linh khí xông phá kinh mạch! Chúc mừng thăng lên ${s.realm.getDisplayName(newSub)}! (Tỉ lệ: ${(rate * 100).toInt()}%)",
                "SUCCESS"
            )
            updateState(
                s.copy(
                    subStage = newSub,
                    qi = 0L,
                    maxQi = newMaxQi,
                    daoTam = minOf(100, s.daoTam + 2)
                )
            )
        } else {
            val lostQi = (s.maxQi * 0.4f).toLong()
            val qiRemaining = maxOf(0L, s.qi - lostQi)
            val recoilDamage = if (s.pillToxicity > 60) 2 else 0
            val newLifespan = s.maxLifespan - recoilDamage

            addLog(
                s.age,
                "Bình Cảnh",
                "Đột phá thất bại! Bình cảnh quá kiên cố, chân khí phản phệ mất $lostQi Linh Khí." +
                        (if (recoilDamage > 0) " Đan độc bùng phát làm tổn hao $recoilDamage năm thọ nguyên!" else ""),
                "DANGER"
            )
            updateState(
                s.copy(
                    qi = qiRemaining,
                    maxLifespan = newLifespan,
                    daoTam = maxOf(20, s.daoTam - 5),
                    qiState = QiState.NGHICH_LUU
                )
            )
            if (newLifespan <= s.age) {
                triggerToaHoa()
            }
        }
    }

    private fun triggerMajorTribulation() {
        val s = _gameState.value
        val extraWaves = (s.satKhi / 30).coerceIn(0, 4)
        val totalWaves = 3 + extraWaves
        val baseDamage = when (s.realm) {
            CultivationRealm.LUYEN_KHI -> 80
            CultivationRealm.TRUC_CO -> 200
            CultivationRealm.KIM_DAN -> 450
            CultivationRealm.NGUYEN_ANH -> 1000
            CultivationRealm.HOA_THAN -> 2500
        }

        addLog(
            s.age,
            "Đại Kiếp",
            "Mây đen ngút trời, Cửu Thiên Lôi Kiếp ngưng tụ! " +
                    (if (extraWaves > 0) "Do ngươi tích lũy nhiều Sát Khí/Nghiệp Lực, Lôi Kiếp tăng thêm $extraWaves đợt sét!" else "Tam Cửu Lôi Kiếp giáng thế!"),
            "TRIBULATION"
        )

        updateState(
            s.copy(
                tribulation = TribulationState(
                    isActive = true,
                    currentWave = 1,
                    totalWaves = totalWaves,
                    waveDamage = baseDamage,
                    isWaitingTamMa = false
                )
            )
        )
    }

    fun withstandTribulationWave(defensiveItemId: String?, sacrificeArtifact: Boolean = false) {
        val s = _gameState.value
        val trib = s.tribulation
        if (!trib.isActive) return

        var waveDmg = trib.waveDamage + (trib.currentWave * 20)
        if (s.spiritRoot == SpiritRoot.BIEN_DI_LOI_CAN) {
            waveDmg = (waveDmg * 0.6f).toInt()
        }

        var blockedDmg = 0
        val updatedInventory = s.inventory.toMutableList()
        var logDefenseNote = ""

        if (defensiveItemId != null) {
            val itemIdx = updatedInventory.indexOfFirst { it.id == defensiveItemId && it.count > 0 }
            if (itemIdx >= 0) {
                val item = updatedInventory[itemIdx]
                if (sacrificeArtifact) {
                    // Tế xuất pháp bảo: gánh 100% sát thương đợt sét, bảo vật nát vụn
                    blockedDmg = waveDmg
                    if (item.count == 1) {
                        updatedInventory.removeAt(itemIdx)
                    } else {
                        updatedInventory[itemIdx] = item.copy(count = item.count - 1)
                    }
                    logDefenseNote = " TẾ XUẤT PHÁP BẢO! Ngươi tế xuất [${item.name}] hộ thể, gánh trọn vẹn 100% lôi kiếp đợt này rồi nát vụn thành tro bụi!"
                } else {
                    blockedDmg = item.defValue
                    if (item.count == 1) {
                        updatedInventory.removeAt(itemIdx)
                    } else {
                        updatedInventory[itemIdx] = item.copy(count = item.count - 1)
                    }
                    logDefenseNote = " Ngươi kích hoạt [${item.name}] đỡ được $blockedDmg sát thương!"
                }
            }
        }

        var damageTaken = maxOf(0, waveDmg - blockedDmg)
        var updatedLifespan = s.maxLifespan
        var updatedCanCot = s.canCot
        var updatedDisciples = s.disciples
        var updatedSatKhi = s.satKhi
        var updatedDaoTam = s.daoTam

        if (damageTaken > 0) {
            val lifespanLoss = (damageTaken / 50).coerceAtLeast(1)
            updatedLifespan -= lifespanLoss
            updatedCanCot = maxOf(10, updatedCanCot - 2)
            logDefenseNote += " Lôi điện đánh trúng thân thể! Trọng thương, tổn thất $lifespanLoss năm thọ nguyên!"

            // Kiểm tra Đệ Tử Thế Thân nếu nhận đòn chí mạng
            val subIdx = s.disciples.indexOfFirst { it.isSubstitute }
            if (updatedLifespan <= s.age && subIdx >= 0) {
                val subDisc = s.disciples[subIdx]
                val mutDisciples = s.disciples.toMutableList()
                mutDisciples.removeAt(subIdx)
                updatedDisciples = mutDisciples
                updatedLifespan = s.age + 5 // Bảo toàn mạng sống
                updatedSatKhi += 35
                updatedDaoTam = maxOf(10, updatedDaoTam - 15)
                logDefenseNote += " [HÌNH NHÂN THẾ THÂN] Trong giây phút nguy cấp, đệ tử thế thân [${subDisc.name}] lao ra đỡ thay đòn hủy diệt! Đệ tử tan biến, ngươi giữ được mạng nhỏ (Sát khí +35, Đạo tâm -15)."
            }
        }

        addLog(
            s.age,
            "Lôi Kiếp",
            "Đợt sét thứ ${trib.currentWave}/${trib.totalWaves} (Sát thương: $waveDmg).$logDefenseNote",
            if (damageTaken > 0) "DANGER" else "INFO"
        )

        if (updatedLifespan <= s.age) {
            updateState(
                s.copy(
                    maxLifespan = updatedLifespan,
                    inventory = updatedInventory,
                    disciples = updatedDisciples,
                    tribulation = TribulationState(isActive = false)
                )
            )
            triggerDeathByLightning()
            return
        }

        if (trib.currentWave < trib.totalWaves) {
            updateState(
                s.copy(
                    maxLifespan = updatedLifespan,
                    canCot = updatedCanCot,
                    inventory = updatedInventory,
                    disciples = updatedDisciples,
                    satKhi = updatedSatKhi,
                    daoTam = updatedDaoTam,
                    tribulation = trib.copy(currentWave = trib.currentWave + 1)
                )
            )
        } else {
            prepareTamMaOrdeal(updatedLifespan, updatedCanCot, updatedInventory)
        }
    }

    private fun prepareTamMaOrdeal(lifespan: Int, canCot: Int, inventory: List<InventoryItem>) {
        val s = _gameState.value
        val (q, choiceA, choiceB) = when {
            s.hungDanh >= 50 || s.satKhi > 40 -> {
                Triple(
                    "TÂM MA MA ĐẠO: Hiện ra oán hồn những kẻ ngươi từng sát hại cướp bảo: 'Ngươi vì tu tiên mà sát phạt vô số, hôm nay nợ máu phải trả bằng máu!'",
                    "[Cắn rứt lương tâm]: Đạo tâm dao động, hối hận vì máu tanh quá khứ.",
                    "[Nghịch Thiên Trảm Ma]: 'Tu tiên vốn là kẻ mạnh nuốt kẻ yếu! Lòng ta sắt đá không hối hận!' Vung kiếm chém tan oán hồn."
                )
            }
            s.anNhanTri >= 40 -> {
                Triple(
                    "TÂM MA CẨU ĐẠO: Ảo ảnh cười nhạo: 'Cả đời ngươi cẩu thả nhẫn nhịn, cúi đầu trước kẻ mạnh, trốn chui trốn lủi như chuột bọ trong hang, có tư cách gì đắc đạo trường sinh?'",
                    "[Mặc cảm hèn nhát]: Thừa nhận tâm tính yếu đuối, hoài nghi bản thân.",
                    "[Kiên Định Cẩu Đạo]: 'Sống sót mới là chân lý tối thượng! Người chết không có tư cách đắc đạo!' Đạo tâm sáng rõ."
                )
            }
            else -> {
                Triple(
                    "Tâm ma biến hóa thành hình bóng người thân, sư môn hoặc chấp niệm kiếp trước: 'Tu tiên tịch mịch ngàn năm, quay đầu lại chỉ còn cát bụi. Ngươi tu để làm gì?'",
                    "[Chính Đạo Kiên Định]: 'Ta tu tiên để bảo hộ thân nhân, nghịch thiên cải mệnh, bất hối!'",
                    "[Mê muội chấp niệm]: 'Có lẽ ta đã sai... tu tiên quả thực cô độc...'"
                )
            }
        }

        updateState(
            s.copy(
                maxLifespan = lifespan,
                canCot = canCot,
                inventory = inventory,
                tribulation = s.tribulation.copy(
                    isWaitingTamMa = true,
                    tamMaQuestion = q,
                    tamMaChoiceA = choiceA,
                    tamMaChoiceB = choiceB
                )
            )
        )
    }

    fun answerTamMa(choiceIndex: Int) {
        val s = _gameState.value
        val trib = s.tribulation
        if (!trib.isActive || !trib.isWaitingTamMa) return

        val isMaDao = s.satKhi > 40
        val isSuccess = if (isMaDao) (choiceIndex == 1) else (choiceIndex == 0)

        if (isSuccess) {
            val nextRealmOrdinal = s.realm.ordinal + 1
            if (nextRealmOrdinal < CultivationRealm.entries.size) {
                val newRealm = CultivationRealm.entries[nextRealmOrdinal]
                val lifespanBonus = when (newRealm) {
                    CultivationRealm.TRUC_CO -> 150
                    CultivationRealm.KIM_DAN -> 250
                    CultivationRealm.NGUYEN_ANH -> 700
                    CultivationRealm.HOA_THAN -> 1800
                    else -> 50
                }
                val newMaxLifespan = s.maxLifespan + lifespanBonus
                val newMaxQi = newRealm.baseQiRequirement

                addLog(
                    s.age,
                    "Đại Thành",
                    "Chém đứt Tâm Ma, Lôi Kiếp tan biến! Kim quang vạn trượng giáng lâm thân thể! " +
                            "Chúc mừng ngươi đã phá vỡ gông cùm, đăng lâm [${newRealm.realmName}]! Tăng thêm $lifespanBonus năm thọ nguyên!",
                    "SUCCESS"
                )

                updateState(
                    s.copy(
                        realm = newRealm,
                        subStage = 1,
                        qi = 0L,
                        maxQi = newMaxQi,
                        maxLifespan = newMaxLifespan,
                        daoTam = minOf(100, s.daoTam + 15),
                        thanThuc = s.thanThuc + 25,
                        canCot = s.canCot + 20,
                        tribulation = TribulationState(isActive = false)
                    )
                )
            }
        } else {
            val lostSubStage = maxOf(1, s.subStage - 2)
            addLog(
                s.age,
                "Tâm Ma",
                "Đạo tâm dao động, bị Tâm Ma cắn xé! Đan điền rạn nứt, tụt về ${s.realm.getDisplayName(lostSubStage)}, trọng thương tổn thọ!",
                "DANGER"
            )
            updateState(
                s.copy(
                    subStage = lostSubStage,
                    qi = 0L,
                    daoTam = maxOf(10, s.daoTam - 25),
                    maxLifespan = maxOf(s.age + 5, s.maxLifespan - 15),
                    qiState = QiState.NGHICH_LUU,
                    tribulation = TribulationState(isActive = false)
                )
            )
        }
    }

    private fun triggerDeathByLightning() {
        val s = _gameState.value
        val earned = 80 + (s.realm.ordinal * 120) + (s.subStage * 10)
        addLog(
            s.age,
            "Hồn Phi Phách Tán",
            "Năm ${s.age} tuổi, lôi kiếp hung tàn đánh nát thể xác và đan điền, hồn phi phách tán! Luân Hồi Kính bảo lưu chân linh, quy đổi $earned Công Đức.",
            "TRIBULATION"
        )
        val record = PastLifeRecord(
            generation = s.generation,
            finalRealm = s.realm.getDisplayName(s.subStage),
            ageAtDeath = "${s.age}/${s.maxLifespan}",
            causeOfDeath = "Hồn phi phách tán dưới Cửu Thiên Lôi Kiếp",
            congDucEarned = earned
        )
        updateState(
            s.copy(
                isDead = true,
                deathCause = "Lôi kiếp giáng lâm, thân thể hóa thành tro bụi",
                congDuc = s.congDuc + earned,
                pastLives = s.pastLives + record,
                showLuanHoiDialog = true
            )
        )
    }

    fun consumePill(item: InventoryItem) {
        val s = _gameState.value
        if (s.isDead) return
        if (item.category != ItemCategory.DAN_DUOC || item.count <= 0) return

        val isImmune = s.spiritRoot == SpiritRoot.HOANG_CO_THANH_THE
        val gainedToxicity = if (isImmune) 0 else item.toxicityBonus
        val newToxicity = minOf(100, s.pillToxicity + gainedToxicity)
        val newQi = minOf(s.maxQi, s.qi + item.qiBonus)

        val updatedInventory = s.inventory.map {
            if (it.id == item.id) it.copy(count = it.count - 1) else it
        }.filter { it.count > 0 }

        addLog(
            s.age,
            "Cắn Đan",
            "Ngươi nuốt một viên [${item.name}]. Linh khí dâng trào +${item.qiBonus}." +
                    if (isImmune) " [Hoang Cổ Thánh Thể] miễn nhiễm đan độc!" else " Tích tụ thêm $gainedToxicity% Đan Độc (Hiện tại: $newToxicity%).",
            if (newToxicity >= 60) "WARNING" else "SUCCESS"
        )

        if (newToxicity >= 100) {
            updateState(
                s.copy(
                    qi = newQi,
                    pillToxicity = newToxicity,
                    inventory = updatedInventory
                )
            )
            triggerBaoTheDeath()
            return
        }

        updateState(
            s.copy(
                qi = newQi,
                pillToxicity = newToxicity,
                qiState = reevaluateQiState(newToxicity),
                inventory = updatedInventory
            )
        )
    }

    private fun triggerBaoTheDeath() {
        val s = _gameState.value
        val earned = 40 + (s.realm.ordinal * 60)
        addLog(
            s.age,
            "Bạo Thể",
            "Đan độc tích tụ 100%! Đan điền nứt toác, độc hỏa bộc phát, bạo thể chết tại chỗ! Luân Hồi Kính kích hoạt, bảo lưu $earned Công Đức.",
            "TRIBULATION"
        )
        val record = PastLifeRecord(
            generation = s.generation,
            finalRealm = s.realm.getDisplayName(s.subStage),
            ageAtDeath = "${s.age}/${s.maxLifespan}",
            causeOfDeath = "Đan độc tích tụ 100%, bạo thể mà chết",
            congDucEarned = earned
        )
        updateState(
            s.copy(
                isDead = true,
                deathCause = "Đan độc vượt ngưỡng 100%, kinh mạch nổ tung",
                congDuc = s.congDuc + earned,
                pastLives = s.pastLives + record,
                showLuanHoiDialog = true
            )
        )
    }

    fun tanDoc(yearsToSpend: Int) {
        val s = _gameState.value
        if (s.isDead) return
        if (s.pillToxicity <= 0) {
            addLog(s.age, "Tán Độc", "Kinh mạch thanh tịnh, không có đan độc cần bài xuất.", "INFO")
            return
        }

        val remainingLifespan = s.maxLifespan - s.age
        if (remainingLifespan <= yearsToSpend) {
            addLog(s.age, "Tán Độc", "Thọ nguyên còn lại không đủ để vận công giải độc dài hạn!", "WARNING")
            return
        }

        val newAge = s.age + yearsToSpend
        val purgeAmount = (yearsToSpend * 16).coerceAtMost(s.pillToxicity)
        val updatedToxicity = s.pillToxicity - purgeAmount

        addLog(
            newAge,
            "Tán Độc",
            "Ngươi bế quan vận chuyển tâm pháp, tiêu hao $yearsToSpend năm thọ nguyên trục xuất $purgeAmount% Đan Độc ra khỏi kinh mạch. (Đan độc còn: $updatedToxicity%)",
            "INFO"
        )

        updateState(
            s.copy(
                age = newAge,
                pillToxicity = updatedToxicity,
                qiState = reevaluateQiState(updatedToxicity)
            )
        )

        if (newAge >= s.maxLifespan) {
            triggerToaHoa()
        }
    }

    private fun reevaluateQiState(toxicity: Int): QiState {
        return when {
            toxicity >= 80 -> QiState.BAO_LOAN
            toxicity >= 50 -> QiState.TAP_NHIEM
            toxicity >= 25 -> QiState.TAP_NHIEM
            else -> QiState.BINH_ON
        }
    }

    fun consultOldDemon() {
        val s = _gameState.value
        if (!s.hasRemnantSoul) return

        val roll = Random.nextInt(100)
        if (roll < 45) {
            val bonusQi = 80L
            val addedSatKhi = 8
            addLog(
                s.age,
                "Lão Ma",
                "Lão ma cười the thé: 'Hảo đồ nhi, vi sư truyền cho ngươi khẩu quyết Thôn Thiên Ma Quyết!'. Nhận +$bonusQi Linh Khí, nhưng Sát Khí tăng thêm $addedSatKhi điểm.",
                "SUCCESS"
            )
            updateState(
                s.copy(
                    qi = minOf(s.maxQi, s.qi + bonusQi),
                    satKhi = s.satKhi + addedSatKhi,
                    remnantSoulPower = minOf(100, s.remnantSoulPower + 10)
                )
            )
        } else if (roll < 80) {
            val bonusNgoTinh = 3
            addLog(
                s.age,
                "Lão Ma",
                "Lão ma điểm hóa bí quyết luyện đan thượng cổ: Ngộ Tính vĩnh viễn tăng thêm $bonusNgoTinh điểm!",
                "INFO"
            )
            updateState(
                s.copy(
                    ngoTinh = s.ngoTinh + bonusNgoTinh,
                    remnantSoulBond = minOf(100, s.remnantSoulBond + 5)
                )
            )
        } else {
            if (s.thanThuc < 60 || s.remnantSoulPower >= 70) {
                triggerSoulClash()
            } else {
                addLog(s.age, "Lão Ma", "Lão ma nhìn chằm chằm thể xác của ngươi với ánh mắt thèm thuồng, nhưng e ngại Thần Thức của ngươi nên đành thu liễm.", "WARNING")
            }
        }
    }

    private fun triggerSoulClash() {
        val s = _gameState.value
        addLog(
            s.age,
            "Đoạt Xá",
            "Lão ma cười lớn: 'Ha ha ha! Nhục thân Thiên Linh Căn hoàn mỹ như vậy, lão phu mượn tạm!'. Tàn hồn lao vào thức hải phát động Đoạt Xá!",
            "DANGER"
        )
        updateState(
            s.copy(
                soulClash = SoulClashState(
                    isActive = true,
                    demonName = "U Minh Lão Ma",
                    playerWill = s.thanThuc * 2,
                    demonWill = 120,
                    narrative = "Lão ma đang gặm nhấm linh hồn ngươi trong Thức Hải! Cần dùng Thần Thức và Đạo Tâm để trấn áp!"
                )
            )
        )
    }

    fun fightSoulClash(choice: String) {
        val s = _gameState.value
        val clash = s.soulClash
        if (!clash.isActive) return

        val playerRoll = Random.nextInt(20, 50) + (s.daoTam / 5)
        val demonRoll = Random.nextInt(15, 45)

        val updatedDemonWill = maxOf(0, clash.demonWill - playerRoll)
        val updatedPlayerWill = maxOf(0, clash.playerWill - demonRoll)

        if (updatedDemonWill == 0) {
            addLog(
                s.age,
                "Trấn Áp",
                "Ngươi gầm lên một tiếng, dùng Đạo Tâm kiên định chém nát tàn hồn Lão Ma! Thôn phệ tàn hồn, Thần Thức vĩnh viễn tăng thêm 45 điểm!",
                "SUCCESS"
            )
            updateState(
                s.copy(
                    hasRemnantSoul = false,
                    thanThuc = s.thanThuc + 45,
                    soulClash = SoulClashState(isActive = false)
                )
            )
        } else if (updatedPlayerWill == 0) {
            addLog(
                s.age,
                "Đoạt Xá",
                "Thức hải bị phá vỡ! Lão ma chiếm đoạt nhục thân của ngươi, linh hồn ngươi bị trục xuất vào luân hồi vĩnh cửu...",
                "TRIBULATION"
            )
            val earned = 50 + (s.realm.ordinal * 80)
            val record = PastLifeRecord(
                generation = s.generation,
                finalRealm = s.realm.getDisplayName(s.subStage),
                ageAtDeath = "${s.age}/${s.maxLifespan}",
                causeOfDeath = "Bị Lão Ma đoạt xá chiếm thân xác",
                congDucEarned = earned
            )
            updateState(
                s.copy(
                    isDead = true,
                    deathCause = "Bị U Minh Lão Ma đoạt xá thành công",
                    congDuc = s.congDuc + earned,
                    pastLives = s.pastLives + record,
                    soulClash = SoulClashState(isActive = false),
                    showLuanHoiDialog = true
                )
            )
        } else {
            updateState(
                s.copy(
                    soulClash = clash.copy(
                        playerWill = updatedPlayerWill,
                        demonWill = updatedDemonWill,
                        narrative = "Ngươi chém tổn thương hồn phách Lão Ma ($playerRoll sát thương)! Lão ma cắn trả gây $demonRoll tổn thương thức hải!"
                    )
                )
            )
        }
    }

    fun shakeDivinationCylinder() {
        val s = _gameState.value
        if (s.isDead) return

        val pool = com.example.content.GameContentRegistry.Divinations.ALL
        val rolledType = HexagramType.entries.random()
        val matchingEvents = pool.filter { it.type == rolledType }
        val event = if (matchingEvents.isNotEmpty()) {
            matchingEvents.random()
        } else {
            pool.random()
        }

        addLog(s.age, "Thiên Cơ", "Ngươi lắc ống quẻ: Bốc trúng quẻ [${rolledType.label}]! ${event.title}", "INFO")
        updateState(s.copy(currentDivination = event))
    }

    fun resolveDivination(choiceIndex: Int) {
        val s = _gameState.value
        val ev = s.currentDivination ?: return

        when (ev.type) {
            HexagramType.THUONG_THUONG_CAT -> {
                if (choiceIndex == 0) {
                    updateState(
                        s.copy(
                            qi = minOf(s.maxQi, s.qi + 60),
                            anNhanTri = s.anNhanTri + 10,
                            currentDivination = null
                        )
                    )
                    addLog(s.age, "Thiên Cơ", "Ngươi tĩnh tâm hấp thu tiên khí từ xa, tu vi tiến triển an hòa.", "INFO")
                } else {
                    updateState(
                        s.copy(
                            qi = minOf(s.maxQi, s.qi + 250),
                            spiritStones = s.spiritStones + 100,
                            satKhi = s.satKhi + 10,
                            currentDivination = null
                        )
                    )
                    addLog(s.age, "Thiên Cơ", "Ngươi đoạt được Tiên Tuyền di tích! Linh khí cuồn cuộn dâng trào +250!", "SUCCESS")
                }
            }
            HexagramType.DAI_HUNG -> {
                if (choiceIndex == 0) {
                    updateState(
                        s.copy(
                            anNhanTri = s.anNhanTri + 35,
                            currentDivination = null
                        )
                    )
                    addLog(s.age, "Cẩu Đạo", "Bên ngoài núi sập đất nứt, ngươi trong hầm ngầm an ổn như bàn thạch. Đạo tâm kiên cố!", "SUCCESS")
                } else {
                    if (s.thanThuc >= 55) {
                        val newStones = s.spiritStones + 500
                        val bonusPills = InventoryItem("item_rare_pill", "Cực Phẩm Kim Nguyên Đan", ItemCategory.DAN_DUOC, "Đan dược đại năng Kim Đan, tăng 400 Linh Khí", 2, qiBonus = 400L, toxicityBonus = 15)
                        addLog(
                            s.age,
                            "Nghịch Thiên",
                            "Thần Thức nhạy bén giúp ngươi né sạch chưởng phong! Hai đại năng Kim Đan đồng quy vu tận, ngươi vét sạch 2 túi trữ vật: +500 Linh Thạch & 2 Cực Phẩm Đan Dược!",
                            "SUCCESS"
                        )
                        updateState(
                            s.copy(
                                spiritStones = newStones,
                                inventory = s.inventory + bonusPills,
                                satKhi = s.satKhi + 25,
                                currentDivination = null
                            )
                        )
                    } else {
                        val lostLifespan = 15
                        val updatedLifespan = s.maxLifespan - lostLifespan
                        addLog(
                            s.age,
                            "Họa Diệt Thân",
                            "Thần Thức không đủ để tránh né, dư chấn của Kim Đan đại năng đập trúng người! Trọng thương hộc máu, mất $lostLifespan năm thọ nguyên!",
                            "DANGER"
                        )
                        updateState(
                            s.copy(
                                maxLifespan = updatedLifespan,
                                canCot = maxOf(10, s.canCot - 10),
                                currentDivination = null
                            )
                        )
                        if (updatedLifespan <= s.age) {
                            triggerToaHoa()
                        }
                    }
                }
            }
            HexagramType.HUNG -> {
                if (choiceIndex == 0) {
                    updateState(s.copy(anNhanTri = s.anNhanTri + 20, currentDivination = null))
                    addLog(s.age, "Ẩn Nhẫn", "Ngươi cấm chế động phủ không ra, tà tu săn lùng vô quả đành bỏ đi.", "INFO")
                } else {
                    updateState(
                        s.copy(
                            spiritStones = s.spiritStones + 150,
                            satKhi = s.satKhi + 30,
                            currentDivination = null
                        )
                    )
                    addLog(s.age, "Phản Sát", "Ngươi chém chết tà tu đoạt được 150 Linh Thạch! Khí tức nhuốm thêm 30 Sát Khí nghiệp lực.", "WARNING")
                }
            }
            else -> {
                updateState(
                    s.copy(
                        spiritStones = s.spiritStones + 30,
                        currentDivination = null
                    )
                )
                addLog(s.age, "Thiên Cơ", "Giải quyết thuận lợi sự vụ của quẻ bói.", "INFO")
            }
        }
    }

    /**
     * Kích hoạt Sự Kiện Tu Tiên Hắc Ám (3 hướng lựa chọn: Cẩu Đạo, Tranh Đoạt, Ẩn Nhẫn / Tà Đạo)
     */
    fun triggerDarkEvent(event: DarkCultivationEvent? = null) {
        val s = _gameState.value
        if (s.isDead) return
        val pool = com.example.content.GameContentRegistry.DarkEvents.ALL
        val chosen = event ?: pool.random()
        updateState(s.copy(activeDarkEvent = chosen, darkEventResultNarrative = null))
        addLog(s.age, "Kỳ Duyên", "Ngươi tao ngộ sinh tử kiếp: ${chosen.title}", "WARNING")
    }

    /**
     * Giải quyết lựa chọn trong Sự Kiện Tu Tiên Hắc Ám
     * Hậu quả tính toán trực tiếp vào: lifespan, qi, pillToxin, karma, divineSense
     */
    fun resolveDarkEventChoice(choicePath: ChoicePath) {
        val s = _gameState.value
        val event = s.activeDarkEvent ?: return
        val choice = when (choicePath) {
            ChoicePath.CAU_DAO -> event.cauDao
            ChoicePath.TRANH_DOAT -> event.tranhDoat
            ChoicePath.AN_NHAN_TA_DAO -> event.anNhanTaDao
        }

        val eff = choice.effects
        val newLifespan = (s.maxLifespan + eff.lifespan).coerceAtLeast(30)
        val newQi = (s.qi + eff.qi).coerceIn(0L, s.maxQi)
        val newPillToxin = (s.pillToxicity + eff.pillToxin).coerceIn(0, 100)
        val newSatKhi = (s.satKhi + eff.karma).coerceAtLeast(0)
        val newThanThuc = (s.thanThuc + eff.divineSense).coerceAtLeast(10)

        // Trục Đối Kháng: Ẩn Nhẫn vs Hung Danh
        val (newAnNhan, newHungDanh) = when (choicePath) {
            ChoicePath.CAU_DAO -> Pair(s.anNhanTri + 15, maxOf(0, s.hungDanh - 5))
            ChoicePath.TRANH_DOAT -> Pair(maxOf(0, s.anNhanTri - 10), s.hungDanh + 20)
            ChoicePath.AN_NHAN_TA_DAO -> Pair(maxOf(0, s.anNhanTri - 15), s.hungDanh + 35)
        }

        val statSummary = buildString {
            if (eff.lifespan != 0) append("Thọ ${if (eff.lifespan > 0) "+${eff.lifespan}" else "${eff.lifespan}"}n ")
            if (eff.qi != 0L) append("Khí ${if (eff.qi > 0) "+${eff.qi}" else "${eff.qi}"} ")
            if (eff.pillToxin != 0) append("Độc ${if (eff.pillToxin > 0) "+${eff.pillToxin}%" else "${eff.pillToxin}%"} ")
            if (eff.karma != 0) append("Sát ${if (eff.karma > 0) "+${eff.karma}" else "${eff.karma}"} ")
            if (eff.divineSense != 0) append("Thức ${if (eff.divineSense > 0) "+${eff.divineSense}" else "${eff.divineSense}"} ")
            if (choicePath == ChoicePath.CAU_DAO) append("Ẩn Nhẫn +15") else append("Hung Danh +${if (choicePath == ChoicePath.TRANH_DOAT) 20 else 35}")
        }.trim()

        addLog(
            s.age,
            choice.path.name,
            "[${choice.label}]: ${choice.outcomeNarrative} [$statSummary]",
            if (eff.karma >= 40 || eff.lifespan < 0) "DANGER" else "SUCCESS"
        )

        updateState(
            s.copy(
                maxLifespan = newLifespan,
                qi = newQi,
                pillToxicity = newPillToxin,
                satKhi = newSatKhi,
                thanThuc = newThanThuc,
                anNhanTri = newAnNhan,
                hungDanh = newHungDanh,
                darkEventResultNarrative = "${choice.outcomeNarrative}\n[$statSummary]",
                activeDarkEvent = null
            )
        )

        if (s.age >= newLifespan) {
            triggerToaHoa()
        }
    }

    fun dismissDarkEvent() {
        updateState(_gameState.value.copy(activeDarkEvent = null, darkEventResultNarrative = null))
    }

    /**
     * Nâng cấp Tụ Linh Trận Động Phủ
     */
    fun upgradeTuLinhTran() {
        val s = _gameState.value
        val upgradeCost = s.tuLinhTranLevel * 200L
        if (s.spiritStones < upgradeCost) {
            addLog(s.age, "Động Phủ", "Không đủ Linh Thạch để nâng cấp Tụ Linh Trận (Cần $upgradeCost LT)!", "WARNING")
            return
        }
        val nextLevel = s.tuLinhTranLevel + 1
        val nextGrade = when (nextLevel) {
            1 -> CaveGrade.HA_PHAM
            2 -> CaveGrade.TRUNG_PHAM
            3 -> CaveGrade.THUONG_PHAM
            else -> CaveGrade.CUC_PHAM
        }
        addLog(s.age, "Tụ Linh Trận", "Tiêu hao $upgradeCost Linh Thạch nâng cấp Tụ Linh Trận lên Cấp $nextLevel (${nextGrade.label})! Tốc độ hấp thu linh khí tăng vọt.", "SUCCESS")
        updateState(s.copy(spiritStones = s.spiritStones - upgradeCost, tuLinhTranLevel = nextLevel, caveGrade = nextGrade))
    }

    /**
     * Niêm Phong Động Phủ Di Trạch trước khi chuyển thế (Cross-run Persistence)
     */
    fun sealVaultInDeath(location: String = "Thiên Trúc Cổ Động", stonesToSeal: Long = 0L, itemId: String? = null) {
        val s = _gameState.value
        val actualStones = minOf(stonesToSeal, s.spiritStones)
        val item = s.inventory.firstOrNull { it.id == itemId }
        val vault = SealedVault(
            stones = actualStones,
            itemName = item?.name,
            location = location,
            generation = s.generation,
            isFound = false
        )
        // Nếu hung danh cao: Kiếp sau dính Huyết Thù Oan Hồn Bám Thân!
        val willHaveGhost = s.hungDanh >= 50 || s.satKhi >= 50
        updateState(
            s.copy(
                sealedVault = vault,
                spiritStones = s.spiritStones - actualStones,
                hasVengefulGhost = willHaveGhost
            )
        )
        addLog(s.age, "Di Trạch", "Đã niêm phong $actualStones Linh Thạch ${if (item != null) "cùng bảo vật [${item.name}]" else ""} tại $location cho hậu kiếp!" +
                (if (willHaveGhost) " [CẢNH BÁO] Do sát khí kiếp này quá nặng, kiếp sau bị Oan Hồn Bám Thân, cừu tộc truy sát!" else ""), "INFO")
    }

    /**
     * Khai mở Động Phủ Di Trạch từ kiếp trước
     */
    fun claimSealedVault() {
        val s = _gameState.value
        val vault = s.sealedVault
        if (vault == null || vault.isFound) {
            addLog(s.age, "Di Trạch", "Không tìm thấy động phủ di trạch nào của tiền kiếp!", "WARNING")
            return
        }
        updateState(
            s.copy(
                spiritStones = s.spiritStones + vault.stones,
                sealedVault = vault.copy(isFound = true)
            )
        )
        addLog(s.age, "Di Trạch", "Khai mở thành công Động Phủ Di Trạch tại ${vault.location}! Thu hồi ${vault.stones} Linh Thạch ${if (vault.itemName != null) "và di vật [${vault.itemName}]" else ""} từ kiếp thứ ${vault.generation}!", "SUCCESS")
    }

    /**
     * Sai phái đệ tử đi lịch luyện (Idle Gathering)
     */
    fun dispatchDisciple(discipleId: String, taskType: String) {
        val s = _gameState.value
        val discIdx = s.disciples.indexOfFirst { it.id == discipleId }
        if (discIdx < 0) return
        val disc = s.disciples[discIdx]
        val updatedDisciples = s.disciples.toMutableList()

        val roll = Random.nextInt(100)
        when {
            roll < 65 -> {
                val stonesFound = Random.nextLong(60, 180)
                addLog(s.age, "Tông Môn", "Đệ tử [${disc.name}] phụng mệnh đi $taskType, an toàn trở về dâng lên $stonesFound Linh Thạch!", "SUCCESS")
                updatedDisciples[discIdx] = disc.copy(loyalty = minOf(100, disc.loyalty + 5))
                updateState(s.copy(disciples = updatedDisciples, spiritStones = s.spiritStones + stonesFound, sectContribution = s.sectContribution + 20))
            }
            roll < 85 -> {
                val stonesFound = Random.nextLong(200, 450)
                addLog(s.age, "Kỳ Ngộ", "Đại Hỷ! Đệ tử [${disc.name}] tại hiểm địa đốn ngộ đột phá tu vi, thu hoạch $stonesFound Linh Thạch và cống nạp tông môn!", "SUCCESS")
                updatedDisciples[discIdx] = disc.copy(realm = "Trúc Cơ Sơ Kỳ", loyalty = minOf(100, disc.loyalty + 15))
                updateState(s.copy(disciples = updatedDisciples, spiritStones = s.spiritStones + stonesFound, sectContribution = s.sectContribution + 50))
            }
            else -> {
                updatedDisciples.removeAt(discIdx)
                addLog(s.age, "Tang Sự", "TIN DỮ! Đệ tử [${disc.name}] đi $taskType tao ngộ yêu ma xé xác, chỉ kịp gửi về một phong Huyết Thư tuyệt mệnh...", "DANGER")
                updateState(s.copy(disciples = updatedDisciples))
            }
        }
    }

    /**
     * Chỉ định đệ tử làm Hình Nhân Thế Thân (Tà Đạo)
     */
    fun setSubstituteDisciple(discipleId: String) {
        val s = _gameState.value
        val updatedDisciples = s.disciples.map {
            if (it.id == discipleId) it.copy(isSubstitute = !it.isSubstitute) else it.copy(isSubstitute = false)
        }
        val target = updatedDisciples.firstOrNull { it.id == discipleId }
        val isSub = target?.isSubstitute == true
        addLog(s.age, "Tà Thuật", if (isSub) "Đã khắc 'U Hồn Huyết Ấn' lên người đệ tử [${target?.name}], biến thành Hình Nhân Thế Thân đỡ đòn chí mạng!" else "Hủy bỏ huyết ấn thế thân của đệ tử [${target?.name}].", if (isSub) "WARNING" else "INFO")
        updateState(s.copy(disciples = updatedDisciples))
    }

    /**
     * Tông Môn Cống Hiến
     */
    fun contributeToSect(stones: Long = 100L) {
        val s = _gameState.value
        if (s.spiritStones < stones) {
            addLog(s.age, "Tông Môn", "Linh Thạch không đủ để cống hiến!", "WARNING")
            return
        }
        val gainedPoints = stones.toInt()
        addLog(s.age, "Cống Hiến", "Dâng nạp $stones Linh Thạch vào kho Tông Môn, nhận $gainedPoints Điểm Cống Hiến.", "SUCCESS")
        updateState(s.copy(spiritStones = s.spiritStones - stones, sectContribution = s.sectContribution + gainedPoints))
    }

    /**
     * Đổi đan dược độc quyền từ Đan Các Tông Môn
     */
    fun exchangeSectItem(itemId: String) {
        val s = _gameState.value
        val cost = when (itemId) {
            "item_truc_co_dan_tong_mon" -> 250
            "item_ngung_kim_dan" -> 600
            "item_ho_tong_phu" -> 200
            else -> 100
        }
        if (s.sectContribution < cost) {
            addLog(s.age, "Tông Môn", "Điểm cống hiến không đủ (Cần $cost điểm)!", "WARNING")
            return
        }
        val newItem = when (itemId) {
            "item_truc_co_dan_tong_mon" -> InventoryItem("item_truc_co_dan_tong_mon", "Trúc Cơ Đan (Cực Phẩm)", ItemCategory.DAN_DUOC, "Đan Các luyện chế, 100% thuần khiết, 0% Đan Độc!", count = 1, qiBonus = 400L, toxicityBonus = 0)
            "item_ngung_kim_dan" -> InventoryItem("item_ngung_kim_dan", "Ngưng Kim Đan", ItemCategory.DAN_DUOC, "Cực phẩm đan dược giúp ngưng kết Kim Đan, tăng 500 Linh Khí.", count = 1, qiBonus = 500L, toxicityBonus = 5)
            "item_ho_tong_phu" -> InventoryItem("item_ho_tong_phu", "Hộ Tông Bí Phù", ItemCategory.PHU_LUC, "Phù lục trấn tông, có thể tế xuất chặn 100% lôi kiếp đợt sét!", count = 1, defValue = 9999, isSacrificable = true)
            else -> InventoryItem("item_linh_thao", "Huyết Tinh Thảo", ItemCategory.LINH_THAO, "Linh thảo quý", count = 1)
        }
        val updatedInv = s.inventory.toMutableList()
        val existIdx = updatedInv.indexOfFirst { it.id == newItem.id }
        if (existIdx >= 0) {
            updatedInv[existIdx] = updatedInv[existIdx].copy(count = updatedInv[existIdx].count + 1)
        } else {
            updatedInv.add(newItem)
        }
        addLog(s.age, "Tông Môn", "Dùng $cost Cống Hiến đổi được [${newItem.name}] từ Đan Các!", "SUCCESS")
        updateState(s.copy(sectContribution = s.sectContribution - cost, inventory = updatedInv))
    }

    fun gambleAncientStone(tierCost: Long = 100L) {
        val s = _gameState.value
        if (s.isDead) return
        if (s.spiritStones < tierCost) {
            addLog(s.age, "Phường Thị", "Không đủ Linh Thạch để cược thạch! Cần ít nhất $tierCost Linh Thạch.", "WARNING")
            return
        }

        val roll = Random.nextInt(100)
        val remainingStones = s.spiritStones - tierCost

        when {
            roll < 60 -> {
                addLog(s.age, "Khai Khoáng", "Bỏ ra $tierCost Linh Thạch mở Cổ Thạch Thượng Cổ... Bên trong chỉ toàn đất đá vụn! Lỗ trắng tiền.", "DANGER")
                updateState(s.copy(spiritStones = remainingStones))
            }
            roll < 95 -> {
                val reward = if (Random.nextBoolean()) {
                    InventoryItem("item_rare_ore", "Vạn Năm Huyền Thiết", ItemCategory.PHAP_BAO, "Khoáng thạch cực phẩm rèn đúc phi kiếm", 1, defValue = 180, sellPrice = 300L)
                } else {
                    InventoryItem("item_ancient_pill", "Cổ Linh Đan Thượng Hạng", ItemCategory.DAN_DUOC, "Đan dược thời cổ đại, tăng 220 Linh Khí, chỉ 5% đan độc", 1, qiBonus = 220L, toxicityBonus = 5, sellPrice = 250L)
                }
                addLog(s.age, "Khai Khoáng", "Hào quang lấp lánh! Cổ Thạch vỡ ra tìm được [${reward.name}]! Lãi to!", "SUCCESS")
                updateState(
                    s.copy(
                        spiritStones = remainingStones,
                        inventory = s.inventory + reward
                    )
                )
            }
            else -> {
                val permCombatPenalty = 5
                val updatedCanCot = maxOf(10, s.canCot - permCombatPenalty)
                addLog(
                    s.age,
                    "Tai Họa",
                    "Khối Cổ Thạch nổ tung! Một con [Thái Cổ Thi Trùng] bay ra cắn đứt kinh mạch cánh tay ngươi! Căn Cốt vĩnh viễn giảm $permCombatPenalty điểm!",
                    "DANGER"
                )
                updateState(
                    s.copy(
                        spiritStones = remainingStones,
                        canCot = updatedCanCot
                    )
                )
            }
        }
    }

    fun pushYourLuckFlame() {
        val s = _gameState.value
        if (s.isDead) return
        val currentTier = s.flameFusion.currentTier
        if (currentTier >= 3) {
            addLog(s.age, "Dị Hỏa", "Ngươi đã dung hợp Dị Hỏa đến Cực Hạn Tầng 3! Không thể dung hợp thêm.", "SUCCESS")
            return
        }

        val targetTier = currentTier + 1
        val (successChance, bonusDmg) = when (targetTier) {
            1 -> Pair(90, 15)
            2 -> Pair(60, 45)
            3 -> Pair(30, 100)
            else -> Pair(0, 0)
        }

        val roll = Random.nextInt(100)
        if (roll < successChance) {
            addLog(
                s.age,
                "Dị Hỏa",
                "Dung hợp [${s.flameFusion.flameName}] Tầng $targetTier thành công ($successChance% tỉ lệ)! Sát thương công pháp tăng +$bonusDmg%!",
                "SUCCESS"
            )
            updateState(
                s.copy(
                    flameFusion = s.flameFusion.copy(
                        currentTier = targetTier,
                        bonusDmgPercent = bonusDmg
                    )
                )
            )
        } else {
            val recoilYears = 10
            val lostSubStage = maxOf(1, s.subStage - 2)
            addLog(
                s.age,
                "Phản Phệ",
                "Dung hợp Dị Hỏa Tầng $targetTier thất bại thảm hại! Ngọn lửa lạnh thiêu rụi kinh mạch, ngươi tụt xuống ${s.realm.getDisplayName(lostSubStage)}, bế quan dưỡng thương tiêu hao $recoilYears năm thọ nguyên!",
                "DANGER"
            )
            val updatedAge = s.age + recoilYears
            updateState(
                s.copy(
                    age = updatedAge,
                    subStage = lostSubStage,
                    flameFusion = FlameFusionState(currentTier = 0, isFailed = true, bonusDmgPercent = 0),
                    qiState = QiState.HU_HAO
                )
            )
            if (updatedAge >= s.maxLifespan) {
                triggerToaHoa()
            }
        }
    }

    fun nourishBeastEgg() {
        val s = _gameState.value
        if (s.isDead) return
        if (s.beastEgg.isHatched) {
            addLog(s.age, "Dị Thú", "Trứng thú đã nở thành [${s.beastEgg.beastName}], không cần nhỏ thêm tinh huyết!", "INFO")
            return
        }

        if (s.qi < 30) {
            addLog(s.age, "Dị Thú", "Khí huyết suy nhược (Linh Khí < 30), không thể nhỏ Tinh Huyết!", "WARNING")
            return
        }

        val newCount = s.beastEgg.bloodNourishCount + 1
        val updatedQi = s.qi - 30

        if (newCount < 5) {
            addLog(
                s.age,
                "Nuôi Trứng",
                "Ngươi cắn rách đầu ngón tay nhỏ một giọt Tinh Huyết vào Hỗn Độn Thú Noãn ($newCount/5). Vỏ trứng phát sáng dịu nhẹ!",
                "INFO"
            )
            updateState(
                s.copy(
                    qi = updatedQi,
                    beastEgg = s.beastEgg.copy(bloodNourishCount = newCount)
                )
            )
        } else {
            val roll = Random.nextInt(100)
            when {
                roll < 70 -> {
                    val beast = "Hỏa Nha (Quạ Lửa)"
                    addLog(
                        s.age,
                        "Trứng Nở",
                        "Vỏ trứng vỡ vụn! Một chú [Hỏa Nha] lông đỏ rực chui ra, kêu líu lo vây quanh ngươi. Nó sẽ giúp ngươi tuần tra thám thính linh dược!",
                        "SUCCESS"
                    )
                    updateState(
                        s.copy(
                            qi = updatedQi,
                            beastEgg = BeastEggData(bloodNourishCount = 5, isHatched = true, beastName = beast, beastTier = "Thường", beastSkill = "+25 Linh Thạch mỗi lần bế quan")
                        )
                    )
                }
                roll < 95 -> {
                    val beast = "Tam Túc Kim Ô (SSR Biến Dị)"
                    addLog(
                        s.age,
                        "Biến Dị SSR",
                        "Thiên địa biến sắc, Thái Dương Chân Hỏa bốc lên cuồn cuộn! Biến dị nở ra thần thú [Tam Túc Kim Ô]! Khả năng: Giảm 35% sát thương Lôi Kiếp và tăng x2 sát thương chiến đấu!",
                        "SUCCESS"
                    )
                    updateState(
                        s.copy(
                            qi = updatedQi,
                            beastEgg = BeastEggData(bloodNourishCount = 5, isHatched = true, beastName = beast, beastTier = "SSR Thần Thú", beastSkill = "Hộ Thể Thái Dương Chân Hỏa"),
                            canCot = s.canCot + 30
                        )
                    )
                }
                else -> {
                    val stolenCount = s.inventory.filter { it.category == ItemCategory.DAN_DUOC }.sumOf { it.count }
                    val remainingInv = s.inventory.filter { it.category != ItemCategory.DAN_DUOC }
                    addLog(
                        s.age,
                        "Phản Phệ",
                        "Hung thú Thao Thiết từ vỏ trứng lao ra đói khát, nuốt chửng sạch $stolenCount viên đan dược trong túi trữ vật của ngươi rồi vỗ cánh bay mất!",
                        "DANGER"
                    )
                    updateState(
                        s.copy(
                            qi = updatedQi,
                            inventory = remainingInv,
                            beastEgg = BeastEggData(bloodNourishCount = 5, isHatched = true, beastName = "Thao Thiết (Đã Đào Tẩu)", beastTier = "Hung Thú", beastSkill = "Không có")
                        )
                    )
                }
            }
        }
    }

    fun recruitDisciple() {
        val s = _gameState.value
        if (s.isDead) return
        if (s.spiritStones < 100) {
            addLog(s.age, "Đăng Tiên Bảng", "Cần ít nhất 100 Linh Thạch để mở sơn môn chiêu mộ đệ tử!", "WARNING")
            return
        }

        val archetypeRoll = Random.nextInt(100)
        val newDisciple = when {
            archetypeRoll < 35 -> Disciple(
                id = "disc_${System.currentTimeMillis()}",
                name = listOf("Tiêu Viêm", "Diệp Phàm", "Hàn Lập", "Thạch Hạo").random(),
                archetype = DiscipleArchetype.KHI_VAN_CHI_TU,
                realm = "Luyện Khí Tầng 1",
                loyalty = 90,
                specialTalent = "Khí vận kinh thiên: Hay ngã xuống vực sâu nhặt được linh thảo vạn năm cúng dường sư phụ."
            )
            archetypeRoll < 65 -> Disciple(
                id = "disc_${System.currentTimeMillis()}",
                name = listOf("Uông Cơ", "Bạch Nhãn Lang", "Cố Vô Thường").random(),
                archetype = DiscipleArchetype.PHAN_COT_TU,
                realm = "Luyện Khí Tầng 6",
                loyalty = 30,
                specialTalent = "Thiên tài nghịch tập nhưng mang phản cốt, tu vi tiến triển cực nhanh, có thể mưu phản nếu sư phụ suy yếu."
            )
            archetypeRoll < 85 -> Disciple(
                id = "disc_${System.currentTimeMillis()}",
                name = listOf("Cổ Trần Chân Nhân", "Vạn Kiếm Lão Tử").random(),
                archetype = DiscipleArchetype.CHUYEN_THE_LAO_QUAI,
                realm = "Trúc Cơ Tầng 1",
                loyalty = 75,
                specialTalent = "Chuyển thế đại năng, tính tình cổ quái, thỉnh thoảng chỉ điểm công pháp cao thâm."
            )
            else -> Disciple(
                id = "disc_${System.currentTimeMillis()}",
                name = "Trần Trung Thành",
                archetype = DiscipleArchetype.TRUNG_THANH_DE_TU,
                realm = "Luyện Khí Tầng 3",
                loyalty = 100,
                specialTalent = "Tâm tính chất phác, một lòng trung trinh bảo hộ sơn môn."
            )
        }

        addLog(
            s.age,
            "Chiêu Mộ",
            "Đăng Tiên Bảng khai mở! Thu nạp đệ tử [${newDisciple.name}] - Hình mẫu [${newDisciple.archetype.title}].",
            "SUCCESS"
        )

        updateState(
            s.copy(
                spiritStones = s.spiritStones - 100,
                disciples = s.disciples + newDisciple
            )
        )
    }

    private fun maybeTriggerDiscipleEvent(currentAge: Int): String? {
        val s = _gameState.value
        if (s.disciples.isEmpty()) return null

        val randomDisciple = s.disciples.random()
        return when (randomDisciple.archetype) {
            DiscipleArchetype.KHI_VAN_CHI_TU -> {
                val bonusHerb = 120L
                updateState(_gameState.value.copy(spiritStones = _gameState.value.spiritStones + bonusHerb))
                "Đệ tử [${randomDisciple.name}] rớt xuống vực sâu nhặt được một gốc Cửu Diệp Chi Lan vạn năm, cung kính đem dâng lên sư phụ (+120 Linh Thạch)!"
            }
            DiscipleArchetype.PHAN_COT_TU -> {
                if (randomDisciple.loyalty < 40 && Random.nextBoolean()) {
                    "Đệ tử mang phản cốt [${randomDisciple.name}] lén lút nhìn trộm bí kíp của ngươi, ánh mắt chứa đầy sát cơ..."
                } else {
                    null
                }
            }
            DiscipleArchetype.CHUYEN_THE_LAO_QUAI -> {
                updateState(_gameState.value.copy(ngoTinh = _gameState.value.ngoTinh + 1))
                "Đệ tử chuyển thế [${randomDisciple.name}] mỉm cười uống rượu, bâng quơ chỉ điểm một câu giúp Ngộ Tính của ngươi +1!"
            }
            DiscipleArchetype.TRUNG_THANH_DE_TU -> {
                updateState(_gameState.value.copy(spiritStones = _gameState.value.spiritStones + 15))
                "Đệ tử [${randomDisciple.name}] cặm cụi chăm sóc dược viên, thu hoạch thảo dược cống nộp tông môn (+15 Linh Thạch)."
            }
        }
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
        val s = _gameState.value
        val newGeneration = s.generation + (if (s.isDead) 1 else 0)

        val initialLogs = listOf(
            GameLogEntry(
                year = 16,
                tag = "Luân Hồi",
                message = "Ngươi [$name] đầu thai chuyển thế ở Kiếp Thứ $newGeneration, thức tỉnh [${spiritRoot.title}]. " +
                        "Dưới sự gia hộ của 3 Thiên Mệnh Từ Điều, đạo cơ tái sinh vững chắc!",
                type = "SUCCESS"
            )
        )

        val newState = GameUiState(
            name = name,
            generation = newGeneration,
            age = 16,
            maxLifespan = lifespan,
            realm = CultivationRealm.LUYEN_KHI,
            subStage = 1,
            qi = startingQi,
            maxQi = maxQi,
            qiState = QiState.BINH_ON,
            pillToxicity = 0,
            satKhi = satKhi,
            anNhanTri = anNhanTri,
            daoTam = daoTam,
            spiritStones = 200L,
            spiritRoot = spiritRoot,
            congDuc = congDucRemaining,
            thanThuc = thanThuc,
            canCot = canCot,
            ngoTinh = ngoTinh,
            hasRemnantSoul = true,
            remnantSoulBond = 40,
            remnantSoulPower = 30,
            activeTraits = selectedTraits,
            inventory = defaultInventory(),
            disciples = defaultDisciples(),
            logs = initialLogs,
            pastLives = s.pastLives,
            isDead = false,
            deathCause = "",
            showLuanHoiDialog = false
        )
        updateState(newState)
    }

    fun defaultInventory(): List<InventoryItem> {
        return listOf(
            InventoryItem("item_1", "Tụ Linh Đan (Hạ Phẩm)", ItemCategory.DAN_DUOC, "Tăng 50 Linh Khí, tích lũy 18% Đan Độc", 3, qiBonus = 50L, toxicityBonus = 18),
            InventoryItem("item_2", "Tụ Linh Đan (Trung Phẩm)", ItemCategory.DAN_DUOC, "Tăng 120 Linh Khí, tích lũy 25% Đan Độc", 1, qiBonus = 120L, toxicityBonus = 25),
            InventoryItem("item_3", "Kim Cương Phù", ItemCategory.PHU_LUC, "Hộ thể kim quang, chống đỡ 150 sát thương lôi kiếp", 2, defValue = 150),
            InventoryItem("item_4", "Thanh Phong Phi Kiếm", ItemCategory.PHAP_BAO, "Bản mệnh phi kiếm, có thể thay người đỡ 1 đợt lôi kiếp (vỡ nát sau đó)", 1, defValue = 200),
            InventoryItem("item_5", "Hỗn Độn Thú Noãn", ItemCategory.KHOANG_THACH, "Trứng thú thượng cổ bí ẩn, cần nhỏ Tinh Huyết nuôi dưỡng", 1)
        )
    }

    fun defaultDisciples(): List<Disciple> {
        return listOf(
            Disciple(
                id = "disc_1",
                name = "Lâm Phong",
                archetype = DiscipleArchetype.KHI_VAN_CHI_TU,
                realm = "Luyện Khí Tầng 2",
                loyalty = 95,
                specialTalent = "Bị hôn ước ruồng bỏ, rớt vực nhặt thần dược"
            )
        )
    }

    fun defaultTraits(): List<DestinyTrait> {
        return listOf(
            DestinyTrait(
                id = "t_trong_sinh",
                name = "Trọng Sinh Chi Hồn",
                tier = "SSR",
                boonDesc = "Ký ức tiền kiếp: Ngộ Tính +50, Thần Thức +25, nhìn thấu quẻ bói Đại Hung, né đòn chí mạng.",
                curseDesc = "Nghiệp chướng tiền kiếp: Khởi đầu mang theo +35 điểm Sát Khí/Nghiệp Lực, Đạo Tâm -10.",
                costCongDuc = 100
            ),
            DestinyTrait(
                id = "t_cau_dao",
                name = "Cẩu Đạo Tiên Mầm",
                tier = "SR",
                boonDesc = "Ẩn Nhẫn Trị tăng x2, Lôi Kiếp giảm 30% sát thương, luôn chạy trốn thành công.",
                curseDesc = "Tính cách quá cẩn trọng: Không thể nhặt bảo vật SSR từ quẻ Đại Hung.",
                costCongDuc = 60
            )
        )
    }

    /**
     * Toàn bộ kho Thiên Mệnh Từ Điều gacha từ Luân Hồi Kính tuân theo quy tắc:
     * 'Thể chất mạnh đi kèm giá đắt hoặc lời nguyền trời phạt'
     */
    fun getAllAvailableTraits(): List<DestinyTrait> {
        return com.example.content.GameContentRegistry.Traits.ALL
    }

    /**
     * Lắc gacha 3 từ điều từ Luân Hồi Kính
     * Hỗ trợ khóa (lock) từ điều mong muốn và roll lại các slot còn lại
     */
    fun rollGachaTraits(lockedTraits: List<DestinyTrait?>): List<DestinyTrait> {
        val pool = getAllAvailableTraits()
        val result = mutableListOf<DestinyTrait>()
        val usedIds = lockedTraits.filterNotNull().map { it.id }.toMutableSet()

        for (i in 0 until 3) {
            val locked = lockedTraits.getOrNull(i)
            if (locked != null) {
                result.add(locked)
            } else {
                val candidate = pool.filterNot { usedIds.contains(it.id) }.randomOrNull() ?: pool.random()
                usedIds.add(candidate.id)
                result.add(candidate)
            }
        }
        return result
    }

    fun openLuanHoiMirror() {
        updateState(_gameState.value.copy(showLuanHoiDialog = true))
    }

    fun closeLuanHoiMirror() {
        updateState(_gameState.value.copy(showLuanHoiDialog = false))
    }

    fun addLog(year: Int, tag: String, message: String, type: String = "INFO") {
        val entry = GameLogEntry(year = year, tag = tag, message = message, type = type)
        val updated = (listOf(entry) + _gameState.value.logs).take(60)
        updateState(_gameState.value.copy(logs = updated))
    }
}
