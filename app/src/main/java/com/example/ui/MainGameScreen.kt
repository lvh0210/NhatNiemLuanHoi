package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.InitialStats
import com.example.ui.components.LogChronicleView
import com.example.ui.components.StatusHudBar
import com.example.ui.dialogs.*
import com.example.ui.tabs.*
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

enum class GameTab(val label: String, val icon: ImageVector) {
    DONG_PHU("Động Phủ", Icons.Default.SelfImprovement),
    THIEN_CO("Thiên Cơ", Icons.Default.Casino),
    PHUONG_THI("Phường Thị", Icons.Default.Storefront),
    TONG_MON("Tông Môn", Icons.Default.Groups),
    LUAN_HOI("Luân Hồi", Icons.Default.AutoAwesome)
}

@Composable
fun MainGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(GameTab.DONG_PHU) }

    // Handle back button on sub-tabs
    BackHandler(enabled = selectedTab != GameTab.DONG_PHU) {
        selectedTab = GameTab.DONG_PHU
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_game_screen"),
        containerColor = VoidBlack,
        topBar = {
            Column {
                StatusHudBar(
                    state = state,
                    onOpenLuanHoi = { viewModel.openLuanHoiMirror() }
                )
                LogChronicleView(
                    logs = state.logs,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("game_navigation_bar"),
                containerColor = MysticSurface,
                tonalElevation = 8.dp
            ) {
                GameTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) ImmortalGold else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (isSelected) GoldenSun else TextSecondary,
                                fontSize = 10.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ImmortalGold,
                            unselectedIconColor = TextSecondary,
                            selectedTextColor = GoldenSun,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = MysticSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(VoidBlack)
        ) {
            when (selectedTab) {
                GameTab.DONG_PHU -> DongPhuTab(
                    state = state,
                    onBeQuan = { years -> viewModel.beQuan(years) },
                    onAttemptBreakthrough = { viewModel.attemptBreakthrough() },
                    onConsumePill = { item -> viewModel.consumePill(item) },
                    onTanDoc = { years -> viewModel.tanDoc(years) },
                    onConsultOldDemon = { viewModel.consultOldDemon() },
                    onTriggerDarkEvent = { viewModel.triggerDarkEvent() },
                    onUpgradeTuLinhTran = { viewModel.upgradeTuLinhTran() }
                )
                GameTab.THIEN_CO -> ThienCoTab(
                    state = state,
                    onShakeCylinder = { viewModel.shakeDivinationCylinder() },
                    onResolveDivination = { choiceIdx -> viewModel.resolveDivination(choiceIdx) }
                )
                GameTab.PHUONG_THI -> PhuongThiTab(
                    state = state,
                    onGambleStone = { cost -> viewModel.gambleAncientStone(cost) },
                    onPushFlame = { viewModel.pushYourLuckFlame() }
                )
                GameTab.TONG_MON -> TongMonTab(
                    state = state,
                    onNourishEgg = { viewModel.nourishBeastEgg() },
                    onRecruitDisciple = { viewModel.recruitDisciple() },
                    onDispatchDisciple = { discId, task -> viewModel.dispatchDisciple(discId, task) },
                    onSetSubstitute = { discId -> viewModel.setSubstituteDisciple(discId) },
                    onContributeSect = { stones -> viewModel.contributeToSect(stones) },
                    onExchangeSectItem = { itemId -> viewModel.exchangeSectItem(itemId) }
                )
                GameTab.LUAN_HOI -> LuanHoiTab(
                    state = state,
                    onOpenLuanHoiGacha = { viewModel.openLuanHoiMirror() },
                    onClaimVault = { viewModel.claimSealedVault() }
                )
            }
        }
    }

    // DIALOG 1: TỌA HÓA (CHẾT GIÀ DO HẾT THỌ NGUYÊN)
    if (state.isDead && !state.showLuanHoiDialog) {
        ToaHoaDialog(
            state = state,
            onOpenLuanHoiMirror = { viewModel.openLuanHoiMirror() },
            onSealVault = { loc, stones, item -> viewModel.sealVaultInDeath(loc, stones, item) }
        )
    }

    // DIALOG 2: LUÂN HỒI KÍNH (KHỞI TẠO NHÂN VẬT & GACHA 3 TỪ ĐIỀU)
    if (state.showLuanHoiDialog) {
        CharacterCreationDialog(
            initialCongDuc = state.congDuc,
            generation = state.generation + (if (state.isDead) 1 else 0),
            availableTraitsPool = viewModel.getAvailableDestinyTraits(),
            onConfirmCharacter = { name, spiritRoot, selectedTraits, stats ->
                viewModel.startNewLifeWithCustomization(
                    name = name,
                    spiritRoot = spiritRoot,
                    selectedTraits = selectedTraits,
                    lifespan = stats.lifespan,
                    congDucRemaining = stats.congDuc,
                    satKhi = stats.satKhi,
                    anNhanTri = stats.anNhanTri,
                    daoTam = stats.daoTam,
                    thanThuc = stats.thanThuc,
                    canCot = stats.canCot,
                    ngoTinh = stats.ngoTinh,
                    startingQi = stats.startingQi,
                    maxQi = stats.maxQi
                )
            },
            onDismiss = {
                if (!state.isDead) {
                    viewModel.closeLuanHoiMirror()
                }
            }
        )
    }

    // DIALOG 3: LÔI KIẾP & TÂM MA
    if (state.tribulation.isActive) {
        TribulationDialog(
            tribulation = state.tribulation,
            inventory = state.inventory,
            onWithstandWave = { itemId, sacrifice -> viewModel.withstandTribulationWave(itemId, sacrifice) },
            onAnswerTamMa = { choiceIdx -> viewModel.answerTamMa(choiceIdx) }
        )
    }

    // DIALOG 4: LÃO MA ĐOẠT XÁ (SOUL CLASH)
    if (state.soulClash.isActive) {
        SoulClashDialog(
            clash = state.soulClash,
            onFightSoul = { choice -> viewModel.fightSoulClash(choice) }
        )
    }

    // DIALOG 5: SỰ KIỆN TU TIÊN HẮC ÁM (3 HƯỚNG: CẨU ĐẠO, TRANH ĐOẠT, ẨN NHẪN / TÀ ĐẠO)
    state.activeDarkEvent?.let { event ->
        com.example.ui.dialogs.DarkEventDialog(
            event = event,
            onChoose = { path -> viewModel.resolveDarkEventChoice(path) },
            onDismiss = { viewModel.dismissDarkEvent() }
        )
    }
}
