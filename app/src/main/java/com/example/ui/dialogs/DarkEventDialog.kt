package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ChoicePath
import com.example.model.DarkCultivationEvent
import com.example.model.DarkEventChoice
import com.example.ui.theme.*

/**
 * Hộp thoại Sự Kiện Tu Tiên Hắc Ám (Dark Cultivation Event)
 * Bám sát nguyên tác tiên hiệp: Cẩu Đạo, Tranh Đoạt, Ẩn Nhẫn / Tà Đạo
 */
@Composable
fun DarkEventDialog(
    event: DarkCultivationEvent,
    onChoose: (ChoicePath) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("dark_event_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = DarkSlate,
            border = BorderStroke(1.5.dp, FatalPoison)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Dangerous,
                                contentDescription = "Sinh Tử Kiếp",
                                tint = FatalPoison,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KỲ DUYÊN HẮC ÁM",
                                color = FatalPoison,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = event.title,
                            color = GoldenSun,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MysticSurfaceVariant)
                            .border(1.dp, MysticBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = event.minRealm.realmName,
                            color = SpiritCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cốt truyện sự kiện
                Surface(
                    color = MysticSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MysticBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.narrative,
                        color = TextPrimary,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LỰA CHỌN CON ĐƯỜNG NGHỊCH THIÊN (3 HƯỚNG SINH TỬ):",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3 Lựa chọn
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Lựa chọn 1: CẨU ĐẠO
                    item {
                        ChoiceCard(
                            choice = event.cauDao,
                            icon = Icons.Default.Shield,
                            themeColor = SpiritCyan,
                            tag = "choice_btn_cau_dao",
                            onClick = { onChoose(ChoicePath.CAU_DAO) }
                        )
                    }

                    // Lựa chọn 2: TRANH ĐOẠT
                    item {
                        ChoiceCard(
                            choice = event.tranhDoat,
                            icon = Icons.Default.FlashOn,
                            themeColor = AncientAmber,
                            tag = "choice_btn_tranh_doat",
                            onClick = { onChoose(ChoicePath.TRANH_DOAT) }
                        )
                    }

                    // Lựa chọn 3: ẨN NHẪN / TÀ ĐẠO
                    item {
                        ChoiceCard(
                            choice = event.anNhanTaDao,
                            icon = Icons.Default.VisibilityOff,
                            themeColor = FatalPoison,
                            tag = "choice_btn_an_nhan_ta_dao",
                            onClick = { onChoose(ChoicePath.AN_NHAN_TA_DAO) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceCard(
    choice: DarkEventChoice,
    icon: ImageVector,
    themeColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        color = MysticSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.8f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = choice.label,
                    tint = themeColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = choice.label,
                    color = themeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = choice.actionText,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Hậu quả chỉ số
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val eff = choice.effects
                if (eff.lifespan != 0) {
                    StatPill(
                        label = "Thọ",
                        value = if (eff.lifespan > 0) "+${eff.lifespan}n" else "${eff.lifespan}n",
                        isPositive = eff.lifespan > 0
                    )
                }
                if (eff.qi != 0L) {
                    StatPill(
                        label = "Khí",
                        value = if (eff.qi > 0) "+${eff.qi}" else "${eff.qi}",
                        isPositive = eff.qi > 0
                    )
                }
                if (eff.pillToxin != 0) {
                    StatPill(
                        label = "Độc",
                        value = if (eff.pillToxin > 0) "+${eff.pillToxin}%" else "${eff.pillToxin}%",
                        isPositive = eff.pillToxin < 0
                    )
                }
                if (eff.karma != 0) {
                    StatPill(
                        label = "Sát",
                        value = if (eff.karma > 0) "+${eff.karma}" else "${eff.karma}",
                        isPositive = eff.karma < 0
                    )
                }
                if (eff.divineSense != 0) {
                    StatPill(
                        label = "Thức",
                        value = if (eff.divineSense > 0) "+${eff.divineSense}" else "${eff.divineSense}",
                        isPositive = eff.divineSense > 0
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, isPositive: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DarkSlate)
            .border(
                0.8.dp,
                if (isPositive) SpiritCyan.copy(alpha = 0.5f) else FatalPoison.copy(alpha = 0.5f),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label: $value",
            color = if (isPositive) SpiritCyan else FatalPoison,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
