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
import com.example.model.ItemCategory
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun PhuongThiTab(
    state: GameUiState,
    onGambleStone: (cost: Long) -> Unit,
    onPushFlame: () -> Unit
) {
    val flame = state.flameFusion

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("tab_phuong_thi"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SECTION 1: CƯỢC THẠCH PHƯỜNG THỊ (CƠ CHẾ 8)
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
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Cược thạch",
                                tint = ImmortalGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Đoạt Bảo Khai Khoáng (Cược Thạch)",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${state.spiritStones} Linh Thạch",
                            color = ImmortalGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tại chợ đen phường thị, mua khối Cổ Thạch Thượng Cổ không thể nhìn thấu bên trong:\n• 60%: Đất đá vụn (Mất trắng)\n• 35%: Bảo vật / Đan dược cổ\n• 5%: Thái Cổ Thi Trùng cắn đứt kinh mạch!",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onGambleStone(100L) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_gamble_stone_100"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MysticSurfaceVariant,
                                contentColor = GoldenSun
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AncientAmber.copy(alpha = 0.5f)),
                            enabled = state.spiritStones >= 100L
                        ) {
                            Text("Cược Khối Thường (100 LT)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onGambleStone(300L) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_gamble_stone_300"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MysticSurfaceVariant,
                                contentColor = ImmortalGold
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImmortalGold),
                            enabled = state.spiritStones >= 300L
                        ) {
                            Text("Cược Vực Thạch (300 LT)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SECTION 2: DUNG HỢP DỊ HỎA (PUSH YOUR LUCK - CƠ CHẾ 11)
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
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Dị hỏa",
                                tint = DangerPoison,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dung Hợp Dị Hỏa (${flame.flameName})",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Tầng ${flame.currentTier} / 3 (+${flame.bonusDmgPercent}% Dmg)",
                            color = SpiritCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Càng lên tầng cao uy lực càng khủng khiếp, nhưng một khi thất bại sẽ bị phản phệ: thiêu rụi kinh mạch, tụt 2 tiểu cảnh giới và tiêu hao 10 năm thọ nguyên!\n• Tầng 1 (90% thành công: +15% dmg)\n• Tầng 2 (60% thành công: +45% dmg)\n• Tầng 3 (30% thành công: Thần thông độc nhất)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPushFlame,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_push_flame"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (flame.currentTier >= 2) FatalPoison else AncientAmber,
                            contentColor = VoidBlack
                        ),
                        enabled = flame.currentTier < 3
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Dung hợp",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (flame.currentTier >= 3) "ĐÃ ĐẠT CỰC HẠN TẦNG 3" else "DUNG HỢP LÊN TẦNG ${flame.currentTier + 1} (LIỀU MẠNG)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // SECTION 3: TÚI TRỮ VẬT (KHO BẢO VẬT)
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Túi trữ vật",
                            tint = SpiritCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Túi Trữ Vật (${state.inventory.sumOf { it.count }} vật phẩm)",
                            color = GoldenSun,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.inventory.isNotEmpty()) {
                        state.inventory.forEach { item ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                color = MysticSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.name} (x${item.count})",
                                            color = TextPrimary,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = item.description,
                                            color = TextSecondary,
                                            fontSize = 10.5.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MysticSurfaceVariant)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = when (item.category) {
                                                ItemCategory.DAN_DUOC -> "Đan Dược"
                                                ItemCategory.PHU_LUC -> "Phù Lục"
                                                ItemCategory.PHAP_BAO -> "Pháp Bảo"
                                                ItemCategory.KHOANG_THACH -> "Khoáng Thạch"
                                                ItemCategory.DI_HOA -> "Dị Hỏa"
                                                ItemCategory.LINH_THAO -> "Linh Thảo"
                                            },
                                            color = SpiritCyan,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Túi trữ vật trống không.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
