package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CasinoGameType
import com.example.ui.theme.BurnishedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldJade
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.MutedRose
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PaleGold
import com.example.ui.theme.RubySurface
import com.example.ui.theme.VelvetMaroonDark
import com.example.ui.theme.VelvetMaroonMid
import com.example.ui.viewmodel.ActiveModal
import com.example.ui.viewmodel.CasinoViewModel
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameTableDispatcher(
    game: CasinoGameType,
    viewModel: CasinoViewModel
) {
    BackHandler {
        viewModel.openGame(null)
    }

    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedChip by viewModel.selectedChip.collectAsStateWithLifecycle()
    val toastBanner by viewModel.toastBanner.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF471025), VelvetMaroonMid, VelvetMaroonDark),
                    radius = 1300f
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Universal Game Table Header Bar
            GameTableHeaderBar(
                game = game,
                mainBalance = profile.mainBalance,
                bonusBalance = profile.bonusBalance,
                onBackToLobby = { viewModel.openGame(null) },
                onOpenShop = {
                    viewModel.openGame(null)
                    viewModel.openModal(ActiveModal.PAY_SHOP)
                }
            )

            // Active Game Table Surface
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                when (game) {
                    CasinoGameType.ZOO_ROULETTE -> ZooRouletteTable(viewModel, selectedChip)
                    CasinoGameType.SEVEN_UP_DOWN -> SevenUpDownTable(viewModel, selectedChip)
                    CasinoGameType.RUMMY -> RummyTeenPattiTable(viewModel, selectedChip)
                    CasinoGameType.ROULETTE -> EuropeanRouletteTable(viewModel, selectedChip)
                    CasinoGameType.CAR_ROULETTE -> CarRouletteTable(viewModel, selectedChip)
                    CasinoGameType.MINES -> MinesVaultTable(viewModel, selectedChip)
                    CasinoGameType.DOMINO -> DominoRoyalTable(viewModel, selectedChip)
                    CasinoGameType.FRUIT_LINE -> FruitLineSlotTable(viewModel, selectedChip)
                    CasinoGameType.BINGO_777 -> Bingo777Table(viewModel, selectedChip)
                    CasinoGameType.RATTLING_GEMS -> DragonTigerAndGemsTable(viewModel, selectedChip)
                }
            }

            // Universal Bottom Chip Rack
            ChipSelectorRack(
                selectedChip = selectedChip,
                onSelectChip = viewModel::selectChip
            )
        }

        if (toastBanner != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(VelvetMaroonDark.copy(alpha = 0.95f))
                    .border(1.5.dp, ImperialGold, RoundedCornerShape(50))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text(
                    text = toastBanner!!,
                    color = ImperialGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GameTableHeaderBar(
    game: CasinoGameType,
    mainBalance: Double,
    bonusBalance: Double,
    onBackToLobby: () -> Unit,
    onOpenShop: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF280613))
            .border(1.dp, BurnishedGold.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(RubySurface)
                .border(1.dp, ImperialGold, RoundedCornerShape(50))
                .clickable(onClick = onBackToLobby)
                .padding(horizontal = 12.dp, vertical = 5.dp)
                .testTag("back_to_lobby_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Lobby",
                tint = ImperialGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Lobby", color = CreamWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = game.title.uppercase(),
                color = ImperialGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = game.subtitle,
                color = MutedRose,
                fontSize = 10.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(VelvetMaroonDark)
                .border(1.dp, ImperialGold, RoundedCornerShape(50))
                .clickable(onClick = onOpenShop)
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Rs ${mainBalance.toInt()}",
                color = ImperialGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "(+Rs ${bonusBalance.toInt()})",
                color = EmeraldJade,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add Cash",
                tint = ImperialGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ChipSelectorRack(
    selectedChip: Double,
    onSelectChip: (Double) -> Unit
) {
    val chips = listOf(10.0, 50.0, 100.0, 500.0, 1000.0, 5000.0)
    val chipColors = listOf(
        Color(0xFF10B981),
        Color(0xFF0284C7),
        Color(0xFF9333EA),
        Color(0xFFE11D48),
        Color(0xFFD97706),
        Color(0xFFFFD700)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF230511))
            .border(1.dp, BurnishedGold.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("SELECT CHIP:", color = PaleGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        chips.forEachIndexed { idx, chip ->
            val selected = selectedChip == chip
            Box(
                modifier = Modifier
                    .size(if (selected) 44.dp else 38.dp)
                    .clip(CircleShape)
                    .background(chipColors[idx])
                    .border(
                        width = if (selected) 3.dp else 1.5.dp,
                        color = if (selected) CreamWhite else ImperialGold,
                        shape = CircleShape
                    )
                    .clickable { onSelectChip(chip) }
                    .testTag("chip_${chip.toInt()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (chip >= 1000) "${(chip / 1000).toInt()}K" else "${chip.toInt()}",
                    color = if (idx == 5) VelvetMaroonDark else CreamWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// ============================================================================
// GAME 1: ZOO ROULETTE (8 Animals + Shark 24x + Bird/Beast 2x)
// ============================================================================
private data class ZooZone(val name: String, val emoji: String, val multiplier: Int, val group: String)

@Composable
private fun ZooRouletteTable(viewModel: CasinoViewModel, selectedChip: Double) {
    val zones = remember {
        listOf(
            ZooZone("Lion", "🦁", 12, "Beast"),
            ZooZone("Eagle", "🦅", 12, "Bird"),
            ZooZone("Panda", "🐼", 8, "Beast"),
            ZooZone("Peacock", "🦚", 8, "Bird"),
            ZooZone("Monkey", "🐒", 8, "Beast"),
            ZooZone("Pigeon", "🕊️", 8, "Bird"),
            ZooZone("Rabbit", "🐰", 6, "Beast"),
            ZooZone("Swallow", "🐦", 6, "Bird"),
            ZooZone("Shark", "🦈", 24, "Shark"),
            ZooZone("All Beasts", "🐾", 2, "BeastSide"),
            ZooZone("All Birds", "🪽", 2, "BirdSide")
        )
    }
    val bets = remember { mutableStateMapOf<String, Double>() }
    val history = remember { mutableStateListOf("🦁 Lion", "🦚 Peacock", "🐰 Rabbit", "🦈 Shark", "🦅 Eagle") }
    var activeHighlight by remember { mutableIntStateOf(0) }
    var isSpinning by remember { mutableStateOf(false) }
    var resultBanner by remember { mutableStateOf("Place chips on Animals or Birds/Beasts & Spin!") }
    val scope = rememberCoroutineScope()

    val wheelAnimals = zones.take(9) // 8 animals + Shark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Recent History + Result Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(resultBanner, color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                history.take(5).forEach { h ->
                    Text(
                        text = h,
                        color = PaleGold,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RubySurface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Animated Animal Wheel Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            wheelAnimals.forEachIndexed { index, animal ->
                val highlighted = index == activeHighlight
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (highlighted) ImperialGold else VelvetMaroonDark
                    ),
                    border = BorderStroke(if (highlighted) 2.5.dp else 1.dp, if (highlighted) CreamWhite else BurnishedGold)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(animal.emoji, fontSize = 18.sp)
                        Text(
                            animal.name,
                            fontSize = 9.sp,
                            color = if (highlighted) VelvetMaroonDark else CreamWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Betting Zones Grid
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            zones.chunked(4).forEach { rowZones ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowZones.forEach { zone ->
                        val betAmt = bets[zone.name] ?: 0.0
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isSpinning) {
                                    val totalCurrent = bets.values.sum()
                                    if (viewModel.canAffordBet(totalCurrent + selectedChip)) {
                                        bets[zone.name] = betAmt + selectedChip
                                    } else {
                                        viewModel.showToast("Not enough balance for this chip!")
                                    }
                                }
                                .testTag("zoo_bet_${zone.name}"),
                            colors = CardDefaults.cardColors(containerColor = RubySurface),
                            border = BorderStroke(
                                if (betAmt > 0) 2.dp else 1.dp,
                                if (betAmt > 0) EmeraldJade else ImperialGold
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("${zone.emoji} ${zone.name}", color = CreamWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${zone.multiplier}X", color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                Text(
                                    text = if (betAmt > 0) "Bet: Rs ${betAmt.toInt()}" else "Tap to Bet",
                                    color = if (betAmt > 0) EmeraldJade else MutedRose,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Spin & Clear Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { bets.clear() },
                enabled = !isSpinning && bets.isNotEmpty(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
            ) {
                Text("Clear Bets", color = CreamWhite)
            }
            Button(
                onClick = {
                    val totalBet = bets.values.sum()
                    if (totalBet <= 0) {
                        viewModel.showToast("Place a chip on any animal first!")
                        return@Button
                    }
                    isSpinning = true
                    scope.launch {
                        val winningIdx = Random.nextInt(wheelAnimals.size)
                        val steps = 18 + winningIdx
                        for (s in 0..steps) {
                            activeHighlight = s % wheelAnimals.size
                            delay(65L)
                        }
                        val winner = wheelAnimals[activeHighlight]
                        val directWin = (bets[winner.name] ?: 0.0) * winner.multiplier
                        val sideWin = when (winner.group) {
                            "Beast" -> (bets["All Beasts"] ?: 0.0) * 2.0
                            "Bird" -> (bets["All Birds"] ?: 0.0) * 2.0
                            else -> 0.0
                        }
                        val totalWin = directWin + sideWin
                        val summary = "${winner.emoji} ${winner.name} (${winner.multiplier}x)"
                        history.add(0, "${winner.emoji} ${winner.name}")
                        resultBanner = "Winner: $summary • Payout: Rs ${totalWin.toInt()}"
                        viewModel.settleGameRound(CasinoGameType.ZOO_ROULETTE, totalBet, totalWin, summary)
                        bets.clear()
                        isSpinning = false
                    }
                },
                enabled = !isSpinning,
                modifier = Modifier
                    .weight(2f)
                    .testTag("zoo_spin_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
            ) {
                Text(
                    text = if (isSpinning) "SPINNING ZOO WHEEL..." else "SPIN ZOO ROULETTE (Bet: Rs ${bets.values.sum().toInt()})",
                    color = VelvetMaroonDark,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// ============================================================================
// GAME 2: 7 UP DOWN (2-6 Down 2x | 7 Lucky 5x | 8-12 Up 2x)
// ============================================================================
@Composable
private fun SevenUpDownTable(viewModel: CasinoViewModel, selectedChip: Double) {
    var downBet by remember { mutableStateOf(0.0) }
    var sevenBet by remember { mutableStateOf(0.0) }
    var upBet by remember { mutableStateOf(0.0) }
    var die1 by remember { mutableIntStateOf(3) }
    var die2 by remember { mutableIntStateOf(4) }
    var rolling by remember { mutableStateOf(false) }
    val historySums = remember { mutableStateListOf(8, 5, 7, 11, 4, 9, 6) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dice Shaker & History Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(VelvetMaroonDark)
                    .border(2.dp, ImperialGold, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                DiceFaceBox(die1)
                DiceFaceBox(die2)
                Column {
                    Text("TOTAL SUM", color = MutedRose, fontSize = 10.sp)
                    Text(
                        text = "${die1 + die2}",
                        color = ImperialGold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                historySums.take(7).forEach { sum ->
                    val badgeColor = when {
                        sum < 7 -> Color(0xFF0284C7)
                        sum == 7 -> ImperialGold
                        else -> EmeraldJade
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$sum",
                            color = if (sum == 7) VelvetMaroonDark else CreamWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3 Big Felt Betting Zones: 2-6 DOWN (2x), 7 LUCKY (5x), 8-12 UP (2x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SevenUpZoneCard(
                title = "2 - 6 DOWN",
                odds = "WIN 2X",
                betAmount = downBet,
                bgColor = Color(0xFF0369A1),
                onClick = {
                    val total = downBet + sevenBet + upBet
                    if (!rolling && viewModel.canAffordBet(total + selectedChip)) {
                        downBet += selectedChip
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("seven_down_zone")
            )
            SevenUpZoneCard(
                title = "LUCKY 7",
                odds = "JACKPOT 5X",
                betAmount = sevenBet,
                bgColor = Color(0xFFB45309),
                onClick = {
                    val total = downBet + sevenBet + upBet
                    if (!rolling && viewModel.canAffordBet(total + selectedChip)) {
                        sevenBet += selectedChip
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("seven_lucky_zone")
            )
            SevenUpZoneCard(
                title = "8 - 12 UP",
                odds = "WIN 2X",
                betAmount = upBet,
                bgColor = EmeraldDark,
                onClick = {
                    val total = downBet + sevenBet + upBet
                    if (!rolling && viewModel.canAffordBet(total + selectedChip)) {
                        upBet += selectedChip
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("seven_up_zone")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    downBet = 0.0
                    sevenBet = 0.0
                    upBet = 0.0
                },
                enabled = !rolling,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
            ) {
                Text("Clear", color = CreamWhite)
            }
            Button(
                onClick = {
                    val totalBet = downBet + sevenBet + upBet
                    if (totalBet <= 0) {
                        viewModel.showToast("Tap 2-6 Down, 7, or 8-12 Up to place a bet!")
                        return@Button
                    }
                    rolling = true
                    scope.launch {
                        repeat(10) {
                            die1 = Random.nextInt(1, 7)
                            die2 = Random.nextInt(1, 7)
                            delay(70L)
                        }
                        val sum = die1 + die2
                        historySums.add(0, sum)
                        val winAmount = when {
                            sum < 7 -> downBet * 2.0
                            sum == 7 -> sevenBet * 5.0
                            else -> upBet * 2.0
                        }
                        val label = when {
                            sum < 7 -> "Dice $die1+$die2=$sum (DOWN)"
                            sum == 7 -> "Dice $die1+$die2=7 (LUCKY 7!)"
                            else -> "Dice $die1+$die2=$sum (UP)"
                        }
                        viewModel.settleGameRound(CasinoGameType.SEVEN_UP_DOWN, totalBet, winAmount, label)
                        downBet = 0.0
                        sevenBet = 0.0
                        upBet = 0.0
                        rolling = false
                    }
                },
                enabled = !rolling,
                modifier = Modifier
                    .weight(2f)
                    .testTag("roll_dice_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
            ) {
                Text(
                    text = if (rolling) "SHAKING DICE..." else "ROLL DICE (Total Bet: Rs ${(downBet + sevenBet + upBet).toInt()})",
                    color = VelvetMaroonDark,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun DiceFaceBox(value: Int) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CreamWhite)
            .border(2.dp, ImperialGold, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            color = VelvetMaroonDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SevenUpZoneCard(
    title: String,
    odds: String,
    betAmount: Double,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(2.dp, if (betAmount > 0) ImperialGold else CreamWhite.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = CreamWhite, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(odds, color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (betAmount > 0) "Bet: Rs ${betAmount.toInt()}" else "Tap to Place Chip",
                color = PaleGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================================
// GAME 3: RUMMY & TEEN PATTI ROYAL TABLE
// ============================================================================
@Composable
private fun RummyTeenPattiTable(viewModel: CasinoViewModel, selectedChip: Double) {
    val ranks = listOf("A", "K", "Q", "J", "10", "9", "8", "7")
    val suits = listOf("♠", "♥", "♦", "♣")
    var playerCards by remember { mutableStateOf(listOf("A♠", "K♠", "Q♠")) }
    var dealerCards by remember { mutableStateOf(listOf("J♥", "10♦", "9♣")) }
    var dealerRevealed by remember { mutableStateOf(false) }
    var potAmount by remember { mutableStateOf(0.0) }
    var roundStatus by remember { mutableStateOf("Tap Deal / Chaal to challenge the Royal Dealer!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(roundStatus, color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dealer Hand
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ROYAL DEALER", color = MutedRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    dealerCards.forEach { card ->
                        PlayingCardView(if (dealerRevealed) card else "🂠", isHidden = !dealerRevealed)
                    }
                }
            }

            // Center Pot
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(VelvetMaroonDark)
                    .border(1.5.dp, ImperialGold, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("ROYAL POT", color = PaleGold, fontSize = 11.sp)
                Text("Rs ${potAmount.toInt()}", color = EmeraldJade, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }

            // Player Hand
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("YOUR HAND", color = EmeraldJade, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    playerCards.forEach { card ->
                        PlayingCardView(card, isHidden = false)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (!viewModel.canAffordBet(selectedChip)) {
                        viewModel.showToast("Insufficient balance!")
                        return@Button
                    }
                    dealerRevealed = false
                    potAmount = selectedChip * 2
                    playerCards = List(3) { "${ranks.random()}${suits.random()}" }
                    dealerCards = List(3) { "${ranks.random()}${suits.random()}" }
                    roundStatus = "Cards Dealt! Tap SHOW & SETTLE or CHAAL (+Rs ${selectedChip.toInt()})"
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldJade)
            ) {
                Text("New Deal (Rs ${selectedChip.toInt()})", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val stake = if (potAmount > 0) potAmount / 2 else selectedChip
                    if (!viewModel.canAffordBet(stake)) {
                        viewModel.showToast("Insufficient balance!")
                        return@Button
                    }
                    dealerRevealed = true
                    val pScore = playerCards.sumOf { ranks.size - ranks.indexOf(it.dropLast(1)).coerceAtLeast(0) }
                    val dScore = dealerCards.sumOf { ranks.size - ranks.indexOf(it.dropLast(1)).coerceAtLeast(0) }
                    val won = pScore >= dScore
                    val winPayout = if (won) stake * 2.0 else 0.0
                    val summary = if (won) "Your Hand ($pScore pts) beat Dealer ($dScore pts)!" else "Dealer Hand ($dScore pts) won"
                    roundStatus = summary
                    viewModel.settleGameRound(CasinoGameType.RUMMY, stake, winPayout, summary)
                    potAmount = 0.0
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("rummy_show_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
            ) {
                Text("SHOW & SETTLE HAND", color = VelvetMaroonDark, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun PlayingCardView(cardText: String, isHidden: Boolean) {
    val isRed = cardText.endsWith("♥") || cardText.endsWith("♦")
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(78.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isHidden) Color(0xFF7F1D1D) else CreamWhite)
            .border(2.dp, ImperialGold, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isHidden) "♛" else cardText,
            color = if (isHidden) ImperialGold else if (isRed) Color(0xFFDC2626) else Color(0xFF111827),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ============================================================================
// GAME 4: EUROPEAN ROULETTE (0-36 Wheel + Red/Black/Even/Odd/Dozens/Straight)
// ============================================================================
@Composable
private fun EuropeanRouletteTable(viewModel: CasinoViewModel, selectedChip: Double) {
    val redNumbers = setOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
    val bets = remember { mutableStateMapOf<String, Double>() }
    val wheelRotation = remember { Animatable(0f) }
    var lastNumber by remember { mutableIntStateOf(7) }
    var spinning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val betZones = listOf(
        "RED (2x)" to 2,
        "BLACK (2x)" to 2,
        "EVEN (2x)" to 2,
        "ODD (2x)" to 2,
        "1st 12 (3x)" to 3,
        "2nd 12 (3x)" to 3,
        "3rd 12 (3x)" to 3,
        "ZERO 0 (36x)" to 36
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Animated Roulette Wheel
        Column(
            modifier = Modifier.width(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(124.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    rotate(wheelRotation.value) {
                        val slices = 12
                        val sweep = 360f / slices
                        for (i in 0 until slices) {
                            drawArc(
                                color = when {
                                    i == 0 -> EmeraldJade
                                    i % 2 == 0 -> Color(0xFFDC2626)
                                    else -> Color(0xFF1F2937)
                                },
                                startAngle = i * sweep,
                                sweepAngle = sweep,
                                useCenter = true
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ImperialGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$lastNumber",
                        color = VelvetMaroonDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Last Ball: $lastNumber (${if (lastNumber == 0) "GREEN" else if (lastNumber in redNumbers) "RED" else "BLACK"})",
                color = PaleGold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }

        // Right Betting Grid
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            betZones.chunked(4).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowItems.forEach { (label, _) ->
                        val amt = bets[label] ?: 0.0
                        val bgColor = when {
                            label.startsWith("RED") -> Color(0xFFB91C1C)
                            label.startsWith("BLACK") -> Color(0xFF1F2937)
                            label.startsWith("ZERO") -> EmeraldDark
                            else -> RubySurface
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !spinning) {
                                    if (viewModel.canAffordBet(bets.values.sum() + selectedChip)) {
                                        bets[label] = amt + selectedChip
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            border = BorderStroke(1.5.dp, if (amt > 0) ImperialGold else CreamWhite.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(label, color = CreamWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (amt > 0) "Rs ${amt.toInt()}" else "Tap",
                                    color = if (amt > 0) ImperialGold else MutedRose,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { bets.clear() },
                    enabled = !spinning,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
                ) {
                    Text("Clear", color = CreamWhite)
                }
                Button(
                    onClick = {
                        val totalBet = bets.values.sum()
                        if (totalBet <= 0) {
                            viewModel.showToast("Place chips on Red, Black, Dozens, or Zero!")
                            return@Button
                        }
                        spinning = true
                        scope.launch {
                            val rolled = Random.nextInt(0, 37)
                            wheelRotation.animateTo(
                                targetValue = wheelRotation.value + 1080f + (rolled * 10f),
                                animationSpec = tween(1200, easing = FastOutSlowInEasing)
                            )
                            lastNumber = rolled
                            var win = 0.0
                            if (rolled == 0) win += (bets["ZERO 0 (36x)"] ?: 0.0) * 36.0
                            if (rolled in redNumbers) win += (bets["RED (2x)"] ?: 0.0) * 2.0
                            if (rolled != 0 && rolled !in redNumbers) win += (bets["BLACK (2x)"] ?: 0.0) * 2.0
                            if (rolled != 0 && rolled % 2 == 0) win += (bets["EVEN (2x)"] ?: 0.0) * 2.0
                            if (rolled % 2 == 1) win += (bets["ODD (2x)"] ?: 0.0) * 2.0
                            if (rolled in 1..12) win += (bets["1st 12 (3x)"] ?: 0.0) * 3.0
                            if (rolled in 13..24) win += (bets["2nd 12 (3x)"] ?: 0.0) * 3.0
                            if (rolled in 25..36) win += (bets["3rd 12 (3x)"] ?: 0.0) * 3.0

                            val colorName = if (rolled == 0) "GREEN 0" else if (rolled in redNumbers) "RED $rolled" else "BLACK $rolled"
                            viewModel.settleGameRound(CasinoGameType.ROULETTE, totalBet, win, "Ball landed on $colorName")
                            bets.clear()
                            spinning = false
                        }
                    },
                    enabled = !spinning,
                    modifier = Modifier
                        .weight(2f)
                        .testTag("roulette_spin_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                ) {
                    Text(
                        text = if (spinning) "SPINNING WHEEL..." else "SPIN ROULETTE (Bet: Rs ${bets.values.sum().toInt()})",
                        color = VelvetMaroonDark,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// ============================================================================
// GAME 5: CAR ROULETTE (Ferrari 40x, Lambo 30x, Maserati 20x, Porsche 15x, 5x)
// ============================================================================
private data class CarBrand(val name: String, val badge: String, val multiplier: Int)

@Composable
private fun CarRouletteTable(viewModel: CasinoViewModel, selectedChip: Double) {
    val cars = remember {
        listOf(
            CarBrand("Ferrari", "🏎️", 40),
            CarBrand("Lamborghini", "⚡", 30),
            CarBrand("Maserati", "🔱", 20),
            CarBrand("Porsche", "🏁", 15),
            CarBrand("Mercedes", "⭐", 5),
            CarBrand("BMW", "💠", 5),
            CarBrand("Audi", "⭕", 5),
            CarBrand("VW Racing", "🚗", 5)
        )
    }
    val bets = remember { mutableStateMapOf<String, Double>() }
    var activeTrackIdx by remember { mutableIntStateOf(0) }
    var racing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "SUPERCAR RACING TRACK • Active Car: ${cars[activeTrackIdx].badge} ${cars[activeTrackIdx].name} (${cars[activeTrackIdx].multiplier}X)",
            color = ImperialGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )

        cars.chunked(4).forEach { rowCars ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCars.forEach { car ->
                    val idx = cars.indexOf(car)
                    val isHighlighted = idx == activeTrackIdx
                    val amt = bets[car.name] ?: 0.0
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !racing) {
                                if (viewModel.canAffordBet(bets.values.sum() + selectedChip)) {
                                    bets[car.name] = amt + selectedChip
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighlighted) Color(0xFFB45309) else RubySurface
                        ),
                        border = BorderStroke(
                            if (isHighlighted || amt > 0) 2.dp else 1.dp,
                            if (isHighlighted) CreamWhite else if (amt > 0) EmeraldJade else ImperialGold
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${car.badge} ${car.name}", color = CreamWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${car.multiplier}X", color = ImperialGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = if (amt > 0) "Bet: Rs ${amt.toInt()}" else "Tap to Bet",
                                color = if (amt > 0) EmeraldJade else MutedRose,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { bets.clear() },
                enabled = !racing,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B7280))
            ) {
                Text("Clear", color = CreamWhite)
            }
            Button(
                onClick = {
                    val totalBet = bets.values.sum()
                    if (totalBet <= 0) {
                        viewModel.showToast("Place a chip on any car brand to start the race!")
                        return@Button
                    }
                    racing = true
                    scope.launch {
                        // Weighted draw favoring 5x cars slightly more than 40x
                        val weights = listOf(1, 2, 3, 4, 8, 8, 8, 8)
                        val pool = weights.flatMapIndexed { index, w -> List(w) { index } }
                        val winnerIdx = pool.random()
                        val steps = 16 + winnerIdx
                        for (i in 0..steps) {
                            activeTrackIdx = i % cars.size
                            delay(70L)
                        }
                        activeTrackIdx = winnerIdx
                        val winCar = cars[winnerIdx]
                        val winPayout = (bets[winCar.name] ?: 0.0) * winCar.multiplier
                        viewModel.settleGameRound(
                            CasinoGameType.CAR_ROULETTE,
                            totalBet,
                            winPayout,
                            "${winCar.badge} ${winCar.name} (${winCar.multiplier}X)"
                        )
                        bets.clear()
                        racing = false
                    }
                },
                enabled = !racing,
                modifier = Modifier
                    .weight(2f)
                    .testTag("car_roulette_start_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
            ) {
                Text(
                    text = if (racing) "RACING..." else "START ENGINE (Bet: Rs ${bets.values.sum().toInt()})",
                    color = VelvetMaroonDark,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
