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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HexagramType
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun ThienCoTab(
    state: GameUiState,
    onShakeCylinder: () -> Unit,
    onResolveDivination: (choiceIndex: Int) -> Unit
) {
    val div = state.currentDivination

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("tab_thien_co"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SECTION 1: DIỄN TOÁN THIÊN CƠ (BỐC QUẺ MỖI NĂM)
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
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Thiên cơ",
                                tint = ImmortalGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Diễn Toán Thiên Cơ",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Năm ${state.age} tuổi",
                            color = SpiritCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Đầu mỗi năm, lắc ống quẻ để dự đoán vận trình: Thượng Thượng Cát, Bình Hòa, Hung, Đại Hung. Quẻ Đại Hung mang họa sát thân nhưng ẩn chứa cơ duyên nghịch thiên!",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (div == null) {
                        Button(
                            onClick = onShakeCylinder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_shake_divination"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ImmortalGold,
                                contentColor = VoidBlack
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Lắc quẻ",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LẮC ỐNG QUẺ THIÊN CƠ NĂM NAY",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Display active divination result card
                        val hexColor = when (div.type) {
                            HexagramType.THUONG_THUONG_CAT -> JadeGreen
                            HexagramType.THUONG_CAT -> SpiritCyan
                            HexagramType.BINH_HOA -> GoldenSun
                            HexagramType.HUNG -> DangerPoison
                            HexagramType.DAI_HUNG -> FatalPoison
                        }

                        Surface(
                            color = MysticSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, hexColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = div.title,
                                        color = hexColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(hexColor.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = div.type.label,
                                            color = hexColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = div.narrative,
                                    color = TextPrimary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Safe Choice Button (Cẩu Đạo)
                                OutlinedButton(
                                    onClick = { onResolveDivination(0) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .testTag("btn_divination_choice_safe"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SpiritCyan),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SpiritCyan),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = div.safeChoiceLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "(${div.safeChoiceEffect})", fontSize = 9.5.sp, color = TextMuted)
                                    }
                                }

                                // Risky Choice Button (Mạo Hiểm)
                                OutlinedButton(
                                    onClick = { onResolveDivination(1) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .testTag("btn_divination_choice_risky"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = hexColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, hexColor),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = div.riskyChoiceLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "(${div.riskyChoiceEffect})", fontSize = 9.5.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 2: NHÂN QUẢ, SÁT KHÍ & CẨU ĐẠO (TRỌNG TÂM CƠ CHẾ 12)
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
                                imageVector = Icons.Default.Balance,
                                contentDescription = "Nhân quả",
                                tint = AncientAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nhân Quả, Sát Khí & Cẩu Đạo",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Mọi hành vi giết người cướp của đều tích lũy Sát Khí/Nghiệp Lực; ngược lại, nhẫn nhịn ẩn tu tích lũy Ẩn Nhẫn Trị.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row comparison
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Sát Khí Card
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = MysticSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BloodRed.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BloodRed))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Sát Khí: ${state.satKhi}", color = BloodRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Tăng sát thương chí mạng\n• Lôi Kiếp hung bạo x2\n• Thu hút trưởng lão trả thù",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // Ẩn Nhẫn Card
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = MysticSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SpiritCyan.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SpiritCyan))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Ẩn Nhẫn: ${state.anNhanTri}", color = SpiritCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Giảm sát thương Lôi Kiếp\n• Tránh né tà tu phục kích\n• Thiếu thốn tài nguyên bảo vật",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
