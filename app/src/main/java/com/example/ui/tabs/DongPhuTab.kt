package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InventoryItem
import com.example.model.ItemCategory
import com.example.model.SpiritRoot
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun DongPhuTab(
    state: GameUiState,
    onBeQuan: (years: Int) -> Unit,
    onAttemptBreakthrough: () -> Unit,
    onConsumePill: (item: InventoryItem) -> Unit,
    onTanDoc: (years: Int) -> Unit,
    onConsultOldDemon: () -> Unit,
    onTriggerDarkEvent: () -> Unit = {}
) {
    val remainingLifespan = maxOf(0, state.maxLifespan - state.age)
    val pillItems = state.inventory.filter { it.category == ItemCategory.DAN_DUOC && it.count > 0 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("tab_dong_phu"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SECTION 1: BẾ QUAN TU LUYỆN (THỌ NGUYÊN & TỌA HÓA)
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
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Bế quan",
                                tint = ImmortalGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Động Phủ Bế Quan",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Thọ còn: $remainingLifespan năm",
                            color = if (remainingLifespan <= 10) FatalPoison else SpiritCyan,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tu hành không có năm tháng. Bế quan tiêu tốn Thọ Nguyên để nạp linh khí. Nếu thọ nguyên cạn kiệt mà chưa đột phá sẽ Tọa Hóa!",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: 1 năm, 3 năm, 5 năm, 10 năm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BeQuanButton(years = 1, enabled = remainingLifespan > 0, modifier = Modifier.weight(1f)) { onBeQuan(1) }
                        BeQuanButton(years = 3, enabled = remainingLifespan > 0, modifier = Modifier.weight(1f)) { onBeQuan(3) }
                        BeQuanButton(years = 5, enabled = remainingLifespan > 0, modifier = Modifier.weight(1f)) { onBeQuan(5) }
                        BeQuanButton(years = 10, enabled = remainingLifespan > 0, modifier = Modifier.weight(1f)) { onBeQuan(10) }
                    }
                }
            }
        }

        // SECTION: LỊCH LUYỆN SINH TỬ (KỲ DUYÊN HẮC ÁM - CẨU ĐẠO / TRANH ĐOẠT / ẨN NHẪN TÀ ĐẠO)
        item {
            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FatalPoison.copy(alpha = 0.8f)),
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
                                imageVector = Icons.Default.Dangerous,
                                contentDescription = "Kỳ duyên hắc ám",
                                tint = FatalPoison,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kỳ Duyên Hắc Ám (Lịch Luyện)",
                                color = FatalPoison,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "3 Lựa Chọn Sinh Tử",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Vào hiểm địa tầm bảo, đối mặt ma quái đoạt xá. 3 hướng lựa chọn: [Cẩu Đạo] an toàn, [Tranh Đoạt] liều mạng, hoặc [Ẩn Nhẫn / Tà Đạo] mưu mô phản sát.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onTriggerDarkEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("btn_trigger_dark_event"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FatalPoison),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FatalPoison
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Xuất sơn lịch luyện",
                            modifier = Modifier.size(16.dp),
                            tint = FatalPoison
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xuất Sơn Lịch Luyện (Gặp Kỳ Ngộ Sinh Tử)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // SECTION 2: ĐỘT PHÁ CẢNH GIỚI (LÔI KIẾP & TÂM MA)
        item {
            val isQiFull = state.qi >= state.maxQi
            val isMajor = state.subStage >= state.realm.maxSubStages

            Surface(
                color = DarkSlate,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isQiFull) GoldenSun else MysticBorder
                ),
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
                                imageVector = Icons.Default.Upgrade,
                                contentDescription = "Đột phá",
                                tint = if (isQiFull) GoldenSun else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMajor) "Đột Phá Đại Cảnh Giới (Lôi Kiếp!)" else "Xung Kích Bình Cảnh",
                                color = if (isQiFull) GoldenSun else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (state.pillToxicity > 60) {
                            Text(
                                text = "Đan độc > 60% phạt tỉ lệ!",
                                color = FatalPoison,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isMajor) {
                            "Đột phá đại cảnh giới phải chịu Lôi Kiếp và Tâm Ma Vấn Đạo! Sát Khí càng cao thì Lôi Kiếp càng thêm nhiều đợt sét tàn phá!"
                        } else {
                            "Tích lũy đầy linh khí để xung kích tầng tiếp theo. Tỉ lệ đột phá chịu ảnh hưởng bởi Ngộ Tính và độ thuần khiết kinh mạch."
                        },
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onAttemptBreakthrough,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_breakthrough"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMajor) LightningPurple else ImmortalGold,
                            contentColor = if (isMajor) TextPrimary else VoidBlack
                        ),
                        enabled = isQiFull
                    ) {
                        Icon(
                            imageVector = if (isMajor) Icons.Default.Bolt else Icons.Default.FlashOn,
                            contentDescription = "Đột phá",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!isQiFull) "Linh Khí Chưa Đủ (${state.qi}/${state.maxQi})"
                            else if (isMajor) "NGHỊCH THIÊN ĐỘ KIẾP"
                            else "XUNG KÍCH BÌNH CẢNH",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // SECTION 3: CẮN ĐAN DƯỢC & VẬN CÔNG TÁN ĐỘC
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
                                imageVector = Icons.Default.Medication,
                                contentDescription = "Đan Dược",
                                tint = DangerPoison,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Đan Dược & Đan Độc",
                                color = GoldenSun,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Độc: ${state.pillToxicity}% / 100%",
                            color = if (state.pillToxicity >= 80) FatalPoison else if (state.pillToxicity >= 60) DangerPoison else JadeGreen,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Uống đan dược giúp tăng vọt Linh Khí nhưng để lại Đan Độc. Vượt 60% giảm tỉ lệ đột phá; đạt 100% bạo thể chết tại chỗ! Tán Độc tiêu hao thọ nguyên.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pill List
                    if (pillItems.isNotEmpty()) {
                        pillItems.forEach { pill ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MysticSurface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${pill.name} (x${pill.count})",
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "+${pill.qiBonus} Khí • +${pill.toxicityBonus}% Độc",
                                        color = DangerPoison,
                                        fontSize = 10.5.sp
                                    )
                                }

                                Button(
                                    onClick = { onConsumePill(pill) },
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("btn_consume_pill_${pill.id}"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = JadeGreen,
                                        contentColor = VoidBlack
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) {
                                    Text("Uống", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "(Không có đan dược trong túi. Hãy ghé Phường Thị mua hoặc nhờ đệ tử hái)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tán Độc Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onTanDoc(2) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_tan_doc_2"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JadeGreen),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JadeGreen),
                            enabled = state.pillToxicity > 0 && remainingLifespan > 2
                        ) {
                            Text("Tán Độc 2 năm", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onTanDoc(5) },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_tan_doc_5"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JadeGreen),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JadeGreen),
                            enabled = state.pillToxicity > 0 && remainingLifespan > 5
                        ) {
                            Text("Tán Độc 5 năm", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // SECTION 4: KỲ DUYÊN & TÀN HỒN LÃO MA
        item {
            if (state.hasRemnantSoul) {
                Surface(
                    color = DarkSlate,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BloodRed.copy(alpha = 0.5f)),
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
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Lão Ma",
                                    tint = BloodRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Giới Chỉ: U Minh Lão Ma",
                                    color = GoldenSun,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Hồn Lực: ${state.remnantSoulPower}%",
                                color = if (state.remnantSoulPower >= 60) FatalPoison else SpiritCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tàn hồn đại năng thượng cổ trú ngụ trong chiếc nhẫn đen. Lão có thể truyền dạy ma công hoặc mẹo luyện đan, nhưng luôn chực chờ đoạt xá nếu Thần Thức của ngươi suy yếu!",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onConsultOldDemon,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_consult_demon"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BloodAura,
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Thỉnh giáo",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Thỉnh Giáo Lão Ma (Nguy Cơ Đoạt Xá)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BeQuanButton(
    years: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(42.dp)
            .testTag("btn_be_quan_$years"),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MysticSurfaceVariant,
            contentColor = GoldenSun
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, AncientAmber.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$years Năm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
