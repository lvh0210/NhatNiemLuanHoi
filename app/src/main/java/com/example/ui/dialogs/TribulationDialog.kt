package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.InventoryItem
import com.example.model.ItemCategory
import com.example.ui.theme.*
import com.example.viewmodel.TribulationState

@Composable
fun TribulationDialog(
    tribulation: TribulationState,
    inventory: List<InventoryItem>,
    onWithstandWave: (defensiveItemId: String?, sacrificeArtifact: Boolean) -> Unit,
    onAnswerTamMa: (choiceIndex: Int) -> Unit
) {
    if (!tribulation.isActive) return

    val defensiveItems = inventory.filter {
        (it.category == ItemCategory.PHU_LUC || it.category == ItemCategory.PHAP_BAO) && (it.defValue > 0 || it.isSacrificable) && it.count > 0
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_tribulation"),
            shape = RoundedCornerShape(16.dp),
            color = VoidBlack,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LightningPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(LightningPurple.copy(alpha = 0.2f))
                        .border(1.5.dp, LightningPurple, RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Lôi Kiếp",
                        tint = LightningPurple,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!tribulation.isWaitingTamMa) {
                    // PHASE 1: LÔI KIẾP (Lightning Waves)
                    Text(
                        text = "CỬU THIÊN LÔI KIẾP",
                        color = LightningPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Đợt sét thứ ${tribulation.currentWave} / ${tribulation.totalWaves}",
                        color = GoldenSun,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val waveDmg = tribulation.waveDamage + (tribulation.currentWave * 20)
                    Text(
                        text = "Sấm sét màu tím xé toạc bầu trời! Uy lực đợt này là $waveDmg sát thương chuẩn. Tiêu hao pháp bảo hoặc dùng nhục thân ngạnh kháng!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Defensive choices
                    Text(
                        text = "Chọn Pháp Bảo / Phù Lục Hộ Thân:",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (defensiveItems.isNotEmpty()) {
                        defensiveItems.forEach { item ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                color = MysticSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "${item.name} (Còn ${item.count})",
                                        color = GoldenSun,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Nút 1: Tế xuất pháp bảo gánh 100% lôi kiếp (Vỡ nát hoàn toàn)
                                        Button(
                                            onClick = { onWithstandWave(item.id, true) },
                                            modifier = Modifier.weight(1f).height(36.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "TẾ XUẤT (Chặn 100% - Vỡ)",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Nút 2: Kích hoạt đỡ thông thường
                                        OutlinedButton(
                                            onClick = { onWithstandWave(item.id, false) },
                                            modifier = Modifier.weight(1f).height(36.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SpiritCyan),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, SpiritCyan),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "Đỡ Thường (-${item.defValue} dmg)",
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "(Không còn phù lục hay pháp bảo hộ thể trong túi)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Direct impact button
                    Button(
                        onClick = { onWithstandWave(null, false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_withstand_body"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FatalPoison,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text(
                            text = "Lấy Thân Chịu Đòn (Trừ máu / Thọ nguyên)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {
                    // PHASE 2: TÂM MA VẤN ĐẠO (Moral Dilemma)
                    Text(
                        text = "TÂM MA VẤN ĐẠO",
                        color = BloodRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tribulation.tamMaQuestion,
                        color = TextPrimary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Choice A
                    OutlinedButton(
                        onClick = { onAnswerTamMa(0) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("btn_tam_ma_a"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenSun),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GoldenSun
                        )
                    ) {
                        Text(
                            text = tribulation.tamMaChoiceA,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Choice B
                    OutlinedButton(
                        onClick = { onAnswerTamMa(1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("btn_tam_ma_b"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BloodRed),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BloodRed
                        )
                    ) {
                        Text(
                            text = tribulation.tamMaChoiceB,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
