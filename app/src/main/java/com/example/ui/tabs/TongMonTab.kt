package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiscipleArchetype
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun TongMonTab(
    state: GameUiState,
    onNourishEgg: () -> Unit,
    onRecruitDisciple: () -> Unit,
    onDispatchDisciple: (discipleId: String, taskType: String) -> Unit = { _, _ -> },
    onSetSubstitute: (discipleId: String) -> Unit = {},
    onContributeSect: (stones: Long) -> Unit = {},
    onExchangeSectItem: (itemId: String) -> Unit = {}
) {
    val egg = state.beastEgg

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("tab_tong_mon"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SECTION 1: ẤP TRỨNG DỊ THÚ (TINH HUYẾT GACHA - CƠ CHẾ 9)
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Egg,
                                contentDescription = "Trứng thú",
                                tint = AncientAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ấp Trứng Dị Thú Bằng Tinh Huyết",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (egg.isHatched) "Đã Nở" else "${egg.bloodNourishCount}/5 Giọt Máu",
                            color = if (egg.isHatched) JadeGreen else FatalPoison,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Nhỏ Tinh Huyết nuôi dưỡng Hỗn Độn Thú Noãn qua từng năm. Thuộc tính tinh huyết và nhân phẩm sẽ quyết định giống loài:\n• 70%: Hỏa Nha thông thường\n• 25%: Tam Túc Kim Ô (SSR Biến Dị)\n• 5%: Hung Thú Thao Thiết nuốt sạch kho linh dược rồi tẩu thoát!",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!egg.isHatched) {
                        LinearProgressIndicator(
                            progress = { egg.bloodNourishCount / 5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = BloodRed,
                            trackColor = MysticSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onNourishEgg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("btn_nourish_egg"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BloodAura,
                                contentColor = TextPrimary
                            ),
                            enabled = state.qi >= 30
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Tinh huyết",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.qi < 30) "Linh Khí Không Đủ (<30)" else "Nhỏ Tinh Huyết Nuôi Trứng (Tiêu hao 30 Khí)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Hatched status
                        Surface(
                            color = MysticSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenSun.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = "Thú cưng",
                                    tint = ImmortalGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = egg.beastName ?: "Dị Thú",
                                        color = GoldenSun,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Phẩm cấp: ${egg.beastTier} • Kỹ năng: ${egg.beastSkill ?: "Trợ chiến"}",
                                        color = SpiritCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 2: ĐĂNG TIÊN BẢNG (GACHA TUYỂN ĐỆ TỬ ĐỘC LẠ - CƠ CHẾ 10)
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = "Đệ tử",
                                tint = SpiritCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Đăng Tiên Bảng (Tuyển Đệ Tử)",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${state.disciples.size} Đệ Tử",
                            color = SpiritCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Mở sơn môn chiêu nạp đồ đệ mang các archetype kinh điển:\n• [Khí Vận Chi Tử]: Linh căn phế nhưng vấp té nhặt được đồ xịn dâng sư phụ\n• [Phản Cốt Tử]: Thiên tài tu nhanh nhưng dễ ám sát sư phụ phản tông\n• [Chuyển Thế Lão Quái]: Tính tình quái đản, thỉnh thoảng chỉ điểm công pháp",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onRecruitDisciple,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_recruit_disciple"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MysticSurfaceVariant,
                            contentColor = GoldenSun
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AncientAmber),
                        enabled = state.spiritStones >= 100
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Chiêu mộ",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.spiritStones < 100) "Thiếu Linh Thạch (Cần 100 LT)" else "Khai Sơn Chiêu Mộ Đệ Tử (100 LT)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Disciples Roster
                    if (state.disciples.isNotEmpty()) {
                        state.disciples.forEach { disc ->
                            val archColor = when (disc.archetype) {
                                DiscipleArchetype.KHI_VAN_CHI_TU -> ImmortalGold
                                DiscipleArchetype.PHAN_COT_TU -> BloodRed
                                DiscipleArchetype.CHUYEN_THE_LAO_QUAI -> LightningPurple
                                DiscipleArchetype.TRUNG_THANH_DE_TU -> JadeGreen
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                color = MysticSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, archColor.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = disc.name,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(archColor.copy(alpha = 0.2f))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = disc.archetype.title,
                                                    color = archColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (disc.isSubstitute) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(BloodRed)
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "THẾ THÂN",
                                                        color = TextPrimary,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                            }
                                            Text(
                                                text = "Trung thành: ${disc.loyalty}%",
                                                color = if (disc.loyalty < 40) FatalPoison else SpiritCyan,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = disc.specialTalent,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Action buttons for disciple
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onDispatchDisciple(disc.id, "Hái Linh Thảo & Đào Mỏ") },
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, SpiritCyan)
                                        ) {
                                            Text(text = "Sai Phái Lịch Luyện", fontSize = 10.5.sp, color = SpiritCyan)
                                        }

                                        OutlinedButton(
                                            onClick = { onSetSubstitute(disc.id) },
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (disc.isSubstitute) BloodRed else AncientAmber)
                                        ) {
                                            Text(
                                                text = if (disc.isSubstitute) "Hủy Thế Thân" else "Chỉ Định Thế Thân",
                                                fontSize = 10.5.sp,
                                                color = if (disc.isSubstitute) BloodRed else GoldenSun
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 3: TÔNG MÔN CỐNG HIẾN & ĐAN CÁC (TÔNG MÔN KHẢO HẠCH)
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AncientAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Tông môn",
                                tint = GoldenSun,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tông Môn Cống Hiến & Đan Các",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${state.sectContribution} Cống Hiến",
                            color = SpiritCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Đóng góp tài nguyên cho sơn môn để đổi lấy các đan dược đột phá độc quyền và bảo vật trấn tông mà chợ đen không thể mua được.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onContributeSect(100L) },
                        modifier = Modifier.fillMaxWidth().height(38.dp),
                        enabled = state.spiritStones >= 100,
                        colors = ButtonDefaults.buttonColors(containerColor = AncientAmber)
                    ) {
                        Text(
                            text = if (state.spiritStones < 100) "Thiếu Linh Thạch (Cần 100 LT)" else "Dâng Nạp 100 LT (+100 Cống Hiến)",
                            fontSize = 11.5.sp,
                            color = VoidBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "VẬT PHẨM ĐỘC QUYỀN ĐAN CÁC:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Item 1
                    SectStoreRow(
                        name = "Trúc Cơ Đan (Cực Phẩm)",
                        desc = "Đan Các luyện chế, 100% tinh thuần, 0% Đan Độc",
                        cost = 250,
                        currentContribution = state.sectContribution,
                        onBuy = { onExchangeSectItem("item_truc_co_dan_tong_mon") }
                    )

                    // Item 2
                    SectStoreRow(
                        name = "Ngưng Kim Đan",
                        desc = "Cực phẩm đan dược giúp ngưng kết Kim Đan, tăng 500 Linh Khí",
                        cost = 600,
                        currentContribution = state.sectContribution,
                        onBuy = { onExchangeSectItem("item_ngung_kim_dan") }
                    )

                    // Item 3
                    SectStoreRow(
                        name = "Hộ Tông Bí Phù",
                        desc = "Phù lục trấn phái, tế xuất chặn 100% một đợt lôi kiếp",
                        cost = 200,
                        currentContribution = state.sectContribution,
                        onBuy = { onExchangeSectItem("item_ho_tong_phu") }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectStoreRow(
    name: String,
    desc: String,
    cost: Int,
    currentContribution: Int,
    onBuy: () -> Unit
) {
    Surface(
        color = MysticSurface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onBuy,
                enabled = currentContribution >= cost,
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (currentContribution >= cost) SpiritCyan else MysticBorder)
            ) {
                Text(text = "$cost Đ", fontSize = 10.5.sp, color = if (currentContribution >= cost) SpiritCyan else TextMuted)
            }
        }
    }
}
