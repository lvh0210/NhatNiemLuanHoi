package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Tiêu Phàm",
    val generation: Int = 1,
    val age: Int = 16,
    val maxLifespan: Int = 100,
    val realmIndex: Int = 0,
    val subStage: Int = 1,
    val qi: Long = 10L,
    val maxQi: Long = 100L,
    val qiState: String = "BINH_ON",
    val pillToxicity: Int = 0, // 0 - 100%
    val satKhi: Int = 0,       // Nghiệp lực / Sát khí
    val anNhanTri: Int = 10,   // Ẩn nhẫn trị / Cẩu đạo
    val daoTam: Int = 75,      // Đạo tâm (0-100)
    val spiritStones: Long = 150L,
    val spiritRootName: String = "THIEN_LINH_CAN",
    val congDuc: Int = 200,
    val thanThuc: Int = 50,
    val canCot: Int = 50,
    val ngoTinh: Int = 50,
    val hasRemnantSoul: Boolean = true,
    val remnantSoulBond: Int = 40,
    val remnantSoulPower: Int = 30,
    val hasNguHanhCongPhap: Boolean = false,
    val eggBloodCount: Int = 0,
    val eggHatched: Boolean = false,
    val eggBeastName: String = "",
    val flameTier: Int = 0,
    val flameBonusDmg: Int = 0,
    val activeTraitsJson: String = "",
    val inventoryJson: String = "",
    val disciplesJson: String = "",
    val logsJson: String = "",
    val pastLivesJson: String = "",
    val lastDivinationYear: Int = 15
)
