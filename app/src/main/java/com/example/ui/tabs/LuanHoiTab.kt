package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun LuanHoiTab(
    state: GameUiState,
    onOpenLuanHoiGacha: () -> Unit,
    onClaimVault: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("tab_luan_hoi"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SECTION 1: LUÂN HỒI KÍNH TỔNG QUAN
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
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Luân hồi",
                                tint = ImmortalGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bảo Kính Luân Hồi",
                                color = GoldenSun,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${state.congDuc} Công Đức Tích Lũy",
                            color = ImmortalGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Vật báu thiên cổ liên kết hồn phách qua muôn kiếp luân hồi. Điểm Công Đức dùng để nghịch thiên cải mệnh, gacha 3 Thiên Mệnh Từ Điều mỗi đầu kiếp sống.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenLuanHoiGacha,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_trigger_reincarnate_manual"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AncientAmber,
                            contentColor = VoidBlack
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Tái sinh",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHUYỂN THẾ ĐẦU THAI MỚI (KHỞI TẠO CHÂN LINH)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // SECTION: ĐỘNG PHỦ DI TRẠCH & HUYẾT THÙ TRUY KIẾP
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (state.hasVengefulGhost) BloodRed else AncientAmber),
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
                                imageVector = Icons.Default.LockReset,
                                contentDescription = "Di trạch",
                                tint = if (state.hasVengefulGhost) BloodRed else AncientAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Di Trạch & Huyết Thù Kiếp Trước",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val vault = state.sealedVault
                    if (vault != null) {
                        if (!vault.isFound) {
                            Text(
                                text = "Phát hiện vết tích Động Phủ Di Trạch tại [${vault.location}] từ kiếp thứ ${vault.generation}! Chứa ${vault.stones} Linh Thạch ${if (vault.itemName != null) "cùng di vật [${vault.itemName}]" else ""}.",
                                color = SpiritCyan,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onClaimVault,
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AncientAmber)
                            ) {
                                Text(
                                    text = "Diễn Toán Khai Mở Di Trạch (+${vault.stones} LT)",
                                    fontSize = 11.5.sp,
                                    color = VoidBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "Đã thu hồi trọn vẹn Di Trạch tại [${vault.location}] (+${vault.stones} LT)!",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Chưa có di trạch nào được niêm phong từ kiếp trước. (Khi tọa hóa có thể chọn niêm phong di trạch)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (state.hasVengefulGhost) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "HUYẾT THÙ TRUY KIẾP: Oan Hồn Bám Thân! Do nợ máu tiền kiếp quá nặng, hậu duệ cừu tộc đang ngấm ngầm lần theo khí tức để phục kích báo thù!",
                            color = BloodRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // SECTION 2: THIÊN MỆNH TỪ ĐIỀU KIẾP NÀY
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Thiên Mệnh Từ Điều Đang Kích Hoạt (Kiếp ${state.generation})",
                        color = GoldenSun,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.activeTraits.isNotEmpty()) {
                        state.activeTraits.forEach { trait ->
                            val tierColor = when (trait.tier) {
                                "SSR" -> ImmortalGold
                                "SR" -> LightningPurple
                                else -> SpiritCyan
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                color = MysticSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(tierColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = trait.tier, color = tierColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = trait.name, color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(text = "• Phúc: ${trait.boonDesc}", color = JadeGreen, fontSize = 10.5.sp)
                                    Text(text = "• Lời nguyền: ${trait.curseDesc}", color = FatalPoison, fontSize = 10.5.sp)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "(Không có từ điều nào được kích hoạt)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // SECTION 3: BẢNG VÀNG TIỀN KIẾP (PAST LIVES)
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Bảng Vàng Các Kiếp Luân Hồi",
                        color = GoldenSun,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.pastLives.isNotEmpty()) {
                        state.pastLives.reversed().forEach { life ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                color = MysticSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Kiếp thứ ${life.generation}: ${life.finalRealm}",
                                            color = GoldenSun,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Hưởng thọ: ${life.ageAtDeath} • ${life.causeOfDeath}",
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                    Text(
                                        text = "+${life.congDucEarned} CĐ",
                                        color = SpiritCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Đây là kiếp đầu tiên của ngươi. Chưa có ký ức tiền kiếp.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
