package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HourglassDisabled
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
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun ToaHoaDialog(
    state: GameUiState,
    onOpenLuanHoiMirror: () -> Unit
) {
    val earnedCongDuc = 50 + (state.realm.ordinal * 100) + (state.subStage * 15) + (state.age / 2)

    Dialog(
        onDismissRequest = {}, // Cannot dismiss without reincarnating
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_toa_hoa"),
            shape = RoundedCornerShape(16.dp),
            color = VoidBlack,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AncientAmber)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(FatalPoison.copy(alpha = 0.2f))
                        .border(1.5.dp, FatalPoison, RoundedCornerShape(27.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassDisabled,
                        contentDescription = "Tọa Hóa",
                        tint = FatalPoison,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "THỌ NGUYÊN CẠN KIỆT",
                    color = FatalPoison,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Năm ${state.age} tuổi, thọ nguyên cực hạn đã tới mà chưa thể đột phá phá kén. Ngươi tại động phủ mỉm cười hóa đạo, thể xác hòa quyện cùng thiên địa...",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown of this life's achievements
                Surface(
                    color = DarkSlate,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Kết Toán Thành Tựu Kiếp ${state.generation}",
                            color = GoldenSun,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        AchievementRow("Cảnh giới tối hậu", state.realm.getDisplayName(state.subStage), ImmortalGold)
                        AchievementRow("Hưởng thọ", "${state.age} tuổi", SpiritCyan)
                        AchievementRow("Linh Căn", state.spiritRoot.title, TextPrimary)
                        AchievementRow("Sát Khí / Nghiệp Lực", "${state.satKhi} điểm", if (state.satKhi > 30) BloodRed else TextSecondary)
                        AchievementRow("Đệ tử đào tạo", "${state.disciples.size} người", JadeGreen)

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MysticBorder
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Công Đức Quy Đổi Kiếp Này:",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+$earnedCongDuc Điểm",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tổng Công Đức Luân Hồi Kính:",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${state.congDuc} Điểm",
                                color = SpiritCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Button to open Reincarnation Mirror
                Button(
                    onClick = onOpenLuanHoiMirror,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_open_luan_hoi_after_death"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmortalGold,
                        contentColor = VoidBlack
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Luân Hồi Kính",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KHAI MỞ LUÂN HỒI KÍNH",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = valueColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
