package com.example.ui.dialogs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DestinyTrait
import com.example.model.InitialStats
import com.example.model.SpiritRoot
import com.example.ui.theme.*
import com.example.viewmodel.CharacterCreationViewModel

@Composable
fun CharacterCreationDialog(
    initialCongDuc: Int,
    generation: Int,
    availableTraitsPool: List<DestinyTrait>,
    onConfirmCharacter: (
        name: String,
        spiritRoot: SpiritRoot,
        selectedTraits: List<DestinyTrait>,
        calculatedStats: InitialStats
    ) -> Unit,
    onDismiss: () -> Unit = {},
    creationViewModel: CharacterCreationViewModel = viewModel()
) {
    LaunchedEffect(generation, initialCongDuc, availableTraitsPool) {
        creationViewModel.setupSession(generation, initialCongDuc, availableTraitsPool)
    }

    val state by creationViewModel.uiState.collectAsStateWithLifecycle()
    var showTraitPickerForSlot by remember { mutableStateOf<Int?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(VoidBlack)
                .testTag("dialog_character_creation"),
            color = VoidBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Luân Hồi Kính: Khởi Tạo Chân Linh",
                            color = GoldenSun,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Kiếp Thứ ${state.generation} • Nghịch Thiên Cải Mệnh",
                            color = SpiritCyan,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MysticSurfaceVariant)
                            .border(1.dp, AncientAmber, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${state.initialCongDuc} Công Đức",
                            color = GoldenSun,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SECTION 1: Đạo Hiệu / Danh Xưng
                    item {
                        Surface(
                            color = DarkSlate,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "1. Đạo Hiệu / Danh Xưng Kiếp Này",
                                    color = GoldenSun,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = state.name,
                                        onValueChange = { creationViewModel.setName(it) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_player_name"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldenSun,
                                            unfocusedBorderColor = MysticBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { creationViewModel.randomizeName() },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(MysticSurfaceVariant, RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Casino,
                                            contentDescription = "Ngẫu nhiên tên",
                                            tint = ImmortalGold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 2: Chọn Thuộc Tính Linh Căn (Roots)
                    item {
                        Surface(
                            color = DarkSlate,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "2. Thiên Mệnh Linh Căn (Bất Đối Xứng)",
                                        color = GoldenSun,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ảnh hưởng căn bản tu vi",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                SpiritRoot.entries.forEach { root ->
                                    val isSelected = state.selectedRoot == root
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { creationViewModel.selectRoot(root) }
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) ImmortalGold else MysticBorder,
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        color = if (isSelected) MysticSurfaceVariant else DarkSlate
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { creationViewModel.selectRoot(root) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = ImmortalGold,
                                                    unselectedColor = TextMuted
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = root.title,
                                                        color = if (isSelected) GoldenSun else TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "• ${root.speedDesc}",
                                                        color = SpiritCyan,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = root.combatNote,
                                                    color = TextSecondary,
                                                    fontSize = 11.sp,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 3: Gacha 3 Thiên Mệnh Từ Điều (Trade-off & Validation)
                    item {
                        Surface(
                            color = DarkSlate,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "3. Gacha 3 Thiên Mệnh Từ Điều",
                                            color = GoldenSun,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Quy tắc: 'Thần Thoại' BẮT BUỘC gánh thêm 'Tai Họa'!",
                                            color = DangerPoison,
                                            fontSize = 10.5.sp
                                        )
                                    }

                                    Button(
                                        onClick = { creationViewModel.rollGacha() },
                                        modifier = Modifier
                                            .height(36.dp)
                                            .testTag("btn_gacha_reroll"),
                                        contentPadding = PaddingValues(horizontal = 10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AncientAmber,
                                            contentColor = VoidBlack
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Lắc",
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Lắc Kính", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Rule Status / Warning Banner
                                if (!state.isSelectionValid && state.validationErrorMessage != null) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        color = FatalPoison.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, FatalPoison)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = "Cảnh báo",
                                                    tint = FatalPoison,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Quy Tắc Cân Bằng Thiên Đạo Chưa Thỏa Mãn!",
                                                    color = FatalPoison,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = state.validationErrorMessage ?: "",
                                                color = TextPrimary,
                                                fontSize = 10.5.sp,
                                                lineHeight = 15.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedButton(
                                                onClick = { creationViewModel.autoBalanceCalamity() },
                                                modifier = Modifier.height(30.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FatalPoison),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, FatalPoison)
                                            ) {
                                                Text("Tự Động Cân Bằng (Chọn 1 Tai Họa)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                } else if (state.hasHighTierTrait && state.hasTaiHoaTrait) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        color = JadeGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, JadeGreen)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Hợp lệ",
                                                tint = JadeGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Số Mệnh Đã Cân Bằng: Thần Thoại song hành cùng Tai Họa!",
                                                color = JadeGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // 3 Gacha Cards
                                GachaSlotCard(
                                    slotIndex = 0,
                                    trait = state.traitSlot0,
                                    isLocked = state.lockSlot0,
                                    onToggleLock = { creationViewModel.toggleLockSlot(0) },
                                    onChangeTrait = { showTraitPickerForSlot = 0 }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                GachaSlotCard(
                                    slotIndex = 1,
                                    trait = state.traitSlot1,
                                    isLocked = state.lockSlot1,
                                    onToggleLock = { creationViewModel.toggleLockSlot(1) },
                                    onChangeTrait = { showTraitPickerForSlot = 1 }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                GachaSlotCard(
                                    slotIndex = 2,
                                    trait = state.traitSlot2,
                                    isLocked = state.lockSlot2,
                                    onToggleLock = { creationViewModel.toggleLockSlot(2) },
                                    onChangeTrait = { showTraitPickerForSlot = 2 }
                                )
                            }
                        }
                    }

                    // SECTION 4: Bảng Tổng Kết Chỉ Số Khởi Đầu Thiết Lập
                    item {
                        Surface(
                            color = MysticSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenSun.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "4. Chỉ Số Khởi Đầu Chân Linh",
                                        color = GoldenSun,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Căn cứ theo Linh Căn & Từ Điều",
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                val stats = state.calculatedStats

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    StatItemView(
                                        label = "Thọ Nguyên (Longevity)",
                                        value = "${stats.lifespan} tuổi",
                                        color = if (stats.lifespan < 100) FatalPoison else if (stats.lifespan > 100) JadeGreen else SpiritCyan,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatItemView(
                                        label = "Sát Khí / Nghiệp Lực (Karma)",
                                        value = "${stats.satKhi} điểm",
                                        color = if (stats.satKhi > 20) BloodRed else TextSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    StatItemView(
                                        label = "Ngộ Tính (Insight)",
                                        value = "${stats.ngoTinh} điểm",
                                        color = SpiritCyan,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatItemView(
                                        label = "Tu Vi & Cơ Sở (Cultivation Base)",
                                        value = "Luyện Khí 1 • ${stats.startingQi}/${stats.maxQi} Khí",
                                        color = GoldenSun,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    StatItemView(
                                        label = "Căn Cốt (Physique)",
                                        value = "${stats.canCot} điểm",
                                        color = JadeGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatItemView(
                                        label = "Thần Thức (Divine Sense)",
                                        value = "${stats.thanThuc} điểm",
                                        color = NetherFlameCold,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    StatItemView(
                                        label = "Đạo Tâm (Dao Heart)",
                                        value = "${stats.daoTam}%",
                                        color = GoldenSun,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatItemView(
                                        label = "Ẩn Nhẫn Trị (Cẩu Đạo)",
                                        value = "${stats.anNhanTri} điểm",
                                        color = SpiritCyan,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action: Đầu Thai Nhập Thế
                Button(
                    onClick = {
                        if (state.isSelectionValid) {
                            onConfirmCharacter(
                                state.name.ifBlank { "Tiêu Phàm" },
                                state.selectedRoot,
                                state.selectedTraits,
                                state.calculatedStats
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_confirm_reincarnation"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isSelectionValid) ImmortalGold else MysticBorder,
                        contentColor = if (state.isSelectionValid) VoidBlack else TextMuted
                    ),
                    enabled = state.isSelectionValid
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = "Đầu thai",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isSelectionValid) "ĐẦU THAI NHẬP THẾ" else "CHƯA THỎA MÃN QUY TẮC SỐ MỆNH",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    // Modal Sheet to swap a trait for a specific slot
    if (showTraitPickerForSlot != null) {
        val slotIndex = showTraitPickerForSlot!!
        TraitSelectionSheet(
            traitsPool = state.availableTraitsPool,
            onSelectTrait = { selected ->
                creationViewModel.selectTraitForSlot(slotIndex, selected)
                showTraitPickerForSlot = null
            },
            onDismiss = { showTraitPickerForSlot = null }
        )
    }
}

@Composable
private fun GachaSlotCard(
    slotIndex: Int,
    trait: DestinyTrait?,
    isLocked: Boolean,
    onToggleLock: () -> Unit,
    onChangeTrait: () -> Unit
) {
    if (trait == null) return

    val tierColor = when {
        trait.isHighTier -> ImmortalGold
        trait.isTaiHoa -> BloodRed
        trait.tier == "Hiếm Có" || trait.tier == "SR" -> LightningPurple
        else -> SpiritCyan
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (isLocked) GoldenSun else tierColor.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            ),
        color = MysticSurface
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tierColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = trait.tier,
                            color = tierColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trait.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Change trait button
                    TextButton(
                        onClick = onChangeTrait,
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Đổi", fontSize = 10.sp, color = SpiritCyan)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Lock toggle button
                    OutlinedButton(
                        onClick = onToggleLock,
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isLocked) GoldenSun else TextMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isLocked) GoldenSun else MysticBorder
                        )
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Khóa từ điều",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isLocked) "Khóa" else "Mở",
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Phúc (Boon)
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "Phúc: ",
                    color = JadeGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = trait.boonDesc,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Họa / Giá Đắt (Curse / Trade-off)
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "Họa: ",
                    color = FatalPoison,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = trait.curseDesc,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TraitSelectionSheet(
    traitsPool: List<DestinyTrait>,
    onSelectTrait: (DestinyTrait) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            color = DarkSlate,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenSun)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chọn Thiên Mệnh Từ Điều",
                        color = GoldenSun,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = TextMuted)
                    }
                }

                Text(
                    text = "Gợi ý: Nếu đã chọn Thần Thoại (+3 điểm), hãy chọn Từ Điều Tai Họa (-3 điểm) để cân bằng!",
                    color = DangerPoison,
                    fontSize = 10.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(traitsPool.size) { index ->
                        val item = traitsPool[index]
                        val tierColor = when {
                            item.isHighTier -> ImmortalGold
                            item.isTaiHoa -> BloodRed
                            item.tier == "Hiếm Có" || item.tier == "SR" -> LightningPurple
                            else -> SpiritCyan
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSelectTrait(item) }
                                .border(1.dp, tierColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            color = MysticSurface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(tierColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(text = item.tier, color = tierColor, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = item.name, color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = if (item.pointCost > 0) "+${item.pointCost} Đ" else "${item.pointCost} Đ",
                                        color = if (item.isTaiHoa) FatalPoison else GoldenSun,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "• Phúc: ${item.boonDesc}", color = JadeGreen, fontSize = 10.5.sp)
                                Text(text = "• Họa: ${item.curseDesc}", color = FatalPoison, fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItemView(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, color = TextMuted, fontSize = 10.5.sp)
        Text(text = value, color = color, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
    }
}
