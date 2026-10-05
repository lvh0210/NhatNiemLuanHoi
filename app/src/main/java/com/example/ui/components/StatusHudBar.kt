package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QiState
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState

@Composable
fun StatusHudBar(
    state: GameUiState,
    modifier: Modifier = Modifier,
    onOpenLuanHoi: () -> Unit
) {
    val remainingLifespan = maxOf(0, state.maxLifespan - state.age)
    val lifespanProgress = (remainingLifespan.toFloat() / state.maxLifespan.toFloat()).coerceIn(0f, 1f)
    val qiProgress = if (state.maxQi > 0) (state.qi.toFloat() / state.maxQi.toFloat()).coerceIn(0f, 1f) else 0f
    val toxicityProgress = (state.pillToxicity / 100f).coerceIn(0f, 1f)

    val toxicityColor by animateColorAsState(
        targetValue = when {
            state.pillToxicity >= 80 -> FatalPoison
            state.pillToxicity >= 60 -> DangerPoison
            state.pillToxicity >= 25 -> PoisonGreen
            else -> JadeGreen
        },
        label = "toxicityColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("status_hud_bar"),
        color = MysticSurface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Row 1: Player Name, Generation badge, Realm badge & Công Đức
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Brush.horizontalGradient(listOf(AncientAmber, ImmortalGold)))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Kiếp ${state.generation}",
                            color = VoidBlack,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.name,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${state.spiritRoot.title}",
                        color = SpiritCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Luân Hồi Kính button / Công Đức indicator
                FilledTonalButton(
                    onClick = onOpenLuanHoi,
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("btn_open_luan_hoi"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MysticSurfaceVariant,
                        contentColor = GoldenSun
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Công Đức",
                        modifier = Modifier.size(13.dp),
                        tint = ImmortalGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.congDuc} Công Đức",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Cultivation Realm & Remaining Lifespan (Thọ Nguyên)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Realm Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MysticSurfaceVariant)
                            .border(1.dp, GoldenSun.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = state.realm.getDisplayName(state.subStage),
                            color = GoldenSun,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Qi State Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (state.qiState) {
                                    QiState.BINH_ON -> JadeGreen.copy(alpha = 0.2f)
                                    QiState.TAP_NHIEM -> DangerPoison.copy(alpha = 0.2f)
                                    QiState.NGHICH_LUU -> BloodRed.copy(alpha = 0.2f)
                                    QiState.BAO_LOAN -> FatalPoison.copy(alpha = 0.25f)
                                    QiState.HU_HAO -> TextMuted.copy(alpha = 0.3f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Khí: ${state.qiState.label}",
                            color = when (state.qiState) {
                                QiState.BINH_ON -> JadeGreen
                                QiState.TAP_NHIEM -> DangerPoison
                                QiState.NGHICH_LUU, QiState.BAO_LOAN -> FatalPoison
                                QiState.HU_HAO -> TextSecondary
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Lifespan Badge: Tuổi thọ & Thọ nguyên còn lại
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = "Thọ Nguyên",
                            modifier = Modifier.size(13.dp),
                            tint = if (remainingLifespan <= 10) FatalPoison else SpiritCyan
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Thọ: ${state.age}/${state.maxLifespan} tuổi",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Còn $remainingLifespan năm ${if (remainingLifespan <= 10) "(Sắp Tọa Hóa!)" else ""}",
                        color = if (remainingLifespan <= 10) FatalPoison else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (remainingLifespan <= 10) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gauge 1: Linh Khí (Qi Progress Bar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Linh Khí",
                    color = SpiritCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${state.qi} / ${state.maxQi}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { qiProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SpiritCyan,
                trackColor = MysticSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Gauge 2: Đan Độc (Pill Toxicity Bar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Đan Độc",
                        color = toxicityColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (state.pillToxicity >= 60) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.pillToxicity >= 100) "(BẠO THỂ!)" else "(Nguy hiểm >60%)",
                            color = FatalPoison,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "${state.pillToxicity}% / 100%",
                    color = toxicityColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { toxicityProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = toxicityColor,
                trackColor = MysticSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom stats row: Sát Khí/Nghiệp Lực, Ẩn Nhẫn Trị, Đạo Tâm, Linh Thạch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sát Khí / Nghiệp Lực
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(BloodRed)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sát Khí: ${state.satKhi}",
                        color = if (state.satKhi > 40) BloodRed else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (state.satKhi > 40) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Ẩn Nhẫn Trị
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SpiritCyan)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ẩn Nhẫn: ${state.anNhanTri}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Đạo Tâm
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(GoldenSun)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Đạo Tâm: ${state.daoTam}%",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Linh Thạch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Linh Thạch",
                        modifier = Modifier.size(13.dp),
                        tint = ImmortalGold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${state.spiritStones}",
                        color = ImmortalGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
