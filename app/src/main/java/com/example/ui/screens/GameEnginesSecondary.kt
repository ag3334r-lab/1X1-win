package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CasinoGameType
import com.example.ui.theme.BurnishedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldJade
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.MutedRose
import com.example.ui.theme.PaleGold
import com.example.ui.theme.RubySurface
import com.example.ui.theme.VelvetMaroonDark
import com.example.ui.viewmodel.CasinoViewModel
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================================================================
// GAME 6: MINES (5x5 Grid with Configurable Bombs & Instant Cashout Multiplier)
// ============================================================================
@Composable
fun MinesVaultTable(viewModel: CasinoViewModel, selectedChip: Double) {
    var bombCount by remember { mutableIntStateOf(3) }
    var roundActive by remember { mutableStateOf(false) }
    var activeStake by remember { mutableStateOf(selectedChip) }
    val bombPositions = remember { mutableStateListOf<Int>() }
    val revealedTiles = remember { mutableStateListOf<Int>() }
    var explodedTile by remember { mutableIntStateOf(-1) }

    val safeRevealedCount = revealedTiles.count { it !in bombPositions }
    val currentMultiplier = if (safeRevealedCount == 0) {
        1.0
    } else {
        1.0 + (safeRevealedCount * (0.28 + bombCount * 0.08))
    }
    val cashoutValue = activeStake * currentMultiplier

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Controls Panel
        Column(
            modifier = Modifier.width(210.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("MINES GOLD VAULT", color = ImperialGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text("Select Mines Count:", color = PaleGold, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(3, 5, 7).forEach { count ->
                    FilterChip(
                        selected = bombCount == count,
                        onClick = { if (!roundActive) bombCount = count },
                        label = { Text("$count 💣") }
                    )
                }
            }
            Text(
                text = "Current Multiplier: ${"%.2f".format(currentMultiplier)}X",
                color = EmeraldJade,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (!roundActive) {
                Button(
                    onClick = {
                        if (!viewModel.canAffordBet(selectedChip)) {
                            viewModel.showToast("Insufficient balance!")
                            return@Button
                        }
                        activeStake = selectedChip
                        revealedTiles.clear()
                        explodedTile = -1
                        bombPositions.clear()
                        val shuffled = (0 until 25).shuffled().take(bombCount)
                        bombPositions.addAll(shuffled)
                        roundActive = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mines_start_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                ) {
                    Text("START MINES (Rs ${selectedChip.toInt()})", color = VelvetMaroonDark, fontWeight = FontWeight.ExtraBold)
                }
            } else {
                Button(
                    onClick = {
                        roundActive = false
                        viewModel.settleGameRound(
                            CasinoGameType.MINES,
                            activeStake,
                            cashoutValue,
                            "Cashed Out $safeRevealedCount Gems (${"%.2f".format(currentMultiplier)}x)"
                        )
                    },
                    enabled = safeRevealedCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mines_cashout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldJade)
                ) {
                    Text(
                        text = "CASH OUT Rs ${cashoutValue.toInt()}",
                        color = VelvetMaroonDark,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Right 5x5 Vault Grid
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (r in 0 until 5) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (c in 0 until 5) {
                        val idx = r * 5 + c
                        val isRevealed = idx in revealedTiles || (!roundActive && explodedTile != -1 && idx in bombPositions)
                        val isBomb = idx in bombPositions
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        idx == explodedTile -> CrimsonDanger
                                        isRevealed && isBomb -> Color(0xFF7F1D1D)
                                        isRevealed -> EmeraldDark
                                        else -> RubySurface
                                    }
                                )
                                .border(1.5.dp, if (isRevealed && !isBomb) ImperialGold else BurnishedGold, RoundedCornerShape(8.dp))
                                .clickable(enabled = roundActive && idx !in revealedTiles) {
                                    revealedTiles.add(idx)
                                    if (isBomb) {
                                        explodedTile = idx
                                        roundActive = false
                                        viewModel.settleGameRound(
                                            CasinoGameType.MINES,
                                            activeStake,
                                            0.0,
                                            "Hit a Bomb on tile #${idx + 1}!"
                                        )
                                    }
                                }
                                .testTag("mine_tile_$idx"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when {
                                    isRevealed && isBomb -> "💣"
                                    isRevealed -> "💎"
                                    else -> "?"
                                },
                                color = ImperialGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// GAME 7: DOMINO (Royal Gaple & QuiQui 9x Showdown)
// ============================================================================
@Composable
fun DominoRoyalTable(viewModel: CasinoViewModel, selectedChip: Double) {
    var selectedSide by remember { mutableStateOf("PLAYER") }
    var playerTile1 by remember { mutableStateOf(3 to 6) }
    var playerTile2 by remember { mutableStateOf(4 to 5) }
    var bankerTile1 by remember { mutableStateOf(2 to 5) }
    var bankerTile2 by remember { mutableStateOf(3 to 4) }
    var statusText by remember { mutableStateOf("Bet on Player (2x), Banker (2x), or QuiQui 9-9 Tie (8x)!") }

    val playerPoints = (playerTile1.first + playerTile1.second + playerTile2.first + playerTile2.second) % 10
    val bankerPoints = (bankerTile1.first + bankerTile1.second + bankerTile2.first + bankerTile2.second) % 10

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(statusText, color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DominoHandBox("PLAYER HAND ($playerPoints pts)", playerTile1, playerTile2, EmeraldJade)
            Text("VS", color = ImperialGold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            DominoHandBox("BANKER HAND ($bankerPoints pts)", bankerTile1, bankerTile2, ElectricCyan)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("PLAYER" to "PLAYER WIN (2X)", "TIE" to "QUIQUI TIE (8X)", "BANKER" to "BANKER WIN (2X)").forEach { (key, label) ->
                val active = selectedSide == key
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedSide = key },
                    colors = CardDefaults.cardColors(
                        containerColor = if (active) EmeraldDark else RubySurface
                    ),
                    border = BorderStroke(if (active) 2.dp else 1.dp, ImperialGold)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = CreamWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Button(
            onClick = {
                if (!viewModel.canAffordBet(selectedChip)) {
                    viewModel.showToast("Insufficient balance!")
                    return@Button
                }
                playerTile1 = Random.nextInt(0, 7) to Random.nextInt(0, 7)
                playerTile2 = Random.nextInt(0, 7) to Random.nextInt(0, 7)
                bankerTile1 = Random.nextInt(0, 7) to Random.nextInt(0, 7)
                bankerTile2 = Random.nextInt(0, 7) to Random.nextInt(0, 7)

                val pPts = (playerTile1.first + playerTile1.second + playerTile2.first + playerTile2.second) % 10
                val bPts = (bankerTile1.first + bankerTile1.second + bankerTile2.first + bankerTile2.second) % 10
                val outcomeWinner = when {
                    pPts > bPts -> "PLAYER"
                    bPts > pPts -> "BANKER"
                    else -> "TIE"
                }
                val multiplier = if (outcomeWinner == "TIE") 8.0 else 2.0
                val winAmt = if (selectedSide == outcomeWinner) selectedChip * multiplier else 0.0
                val summary = "Domino Player $pPts vs Banker $bPts ($outcomeWinner)"
                statusText = summary
                viewModel.settleGameRound(CasinoGameType.DOMINO, selectedChip, winAmt, summary)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("domino_deal_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
        ) {
            Text(
                text = "DEAL DOMINO TILES (Bet Rs ${selectedChip.toInt()} on $selectedSide)",
                color = VelvetMaroonDark,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun DominoHandBox(
    label: String,
    tile1: Pair<Int, Int>,
    tile2: Pair<Int, Int>,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(tile1, tile2).forEach { (top, bottom) ->
                Column(
                    modifier = Modifier
                        .width(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CreamWhite)
                        .border(2.dp, ImperialGold, RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$top", color = VelvetMaroonDark, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(1.5.dp)
                            .background(VelvetMaroonDark)
                    )
                    Text("$bottom", color = VelvetMaroonDark, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }
    }
}

// ============================================================================
// GAME 8: FRUIT LINE (5-Reel 777 & WILD Slot Machine)
// ============================================================================
@Composable
fun FruitLineSlotTable(viewModel: CasinoViewModel, selectedChip: Double) {
    val symbols = listOf("🍒", "🍋", "🍇", "🍉", "🔔", "💎", "7️⃣")
    var reels by remember {
        mutableStateOf(
            List(3) { List(5) { symbols.random() } }
        )
    }
    var spinning by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf("Match 3+ Fruits, WILD 💎, or 7️⃣7️⃣7️⃣ on the middle payline!") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(lastResult, color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        // 5x3 Slot Reels Frame
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VelvetMaroonDark),
            border = BorderStroke(2.5.dp, ImperialGold)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                reels.forEachIndexed { rowIdx, rowSymbols ->
                    val isCenterPayline = rowIdx == 1
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCenterPayline) Color(0x44FFD700) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        rowSymbols.forEach { sym ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RubySurface)
                                    .border(
                                        width = if (isCenterPayline) 2.dp else 1.dp,
                                        color = if (isCenterPayline) ImperialGold else BurnishedGold,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sym, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (!viewModel.canAffordBet(selectedChip)) {
                    viewModel.showToast("Insufficient balance!")
                    return@Button
                }
                spinning = true
                scope.launch {
                    repeat(8) {
                        reels = List(3) { List(5) { symbols.random() } }
                        delay(75L)
                    }
                    val middleLine = reels[1]
                    val counts = middleLine.groupingBy { it }.eachCount()
                    val wildCount = counts["💎"] ?: 0
                    val maxSame = counts.filterKeys { it != "💎" }.values.maxOrNull() ?: 0
                    val effectiveMatches = maxSame + wildCount
                    val hasTriple7 = (counts["7️⃣"] ?: 0) + wildCount >= 3

                    val multiplier = when {
                        hasTriple7 -> 15.0
                        effectiveMatches >= 5 -> 20.0
                        effectiveMatches == 4 -> 6.0
                        effectiveMatches == 3 -> 3.0
                        effectiveMatches == 2 -> 1.5
                        else -> 0.0
                    }
                    val payout = selectedChip * multiplier
                    val outcome = "Payline ${middleLine.joinToString(" ")} (${multiplier}x)"
                    lastResult = if (payout > 0) "BIG SLOT WIN: Rs ${payout.toInt()}! ($outcome)" else outcome
                    viewModel.settleGameRound(CasinoGameType.FRUIT_LINE, selectedChip, payout, outcome)
                    spinning = false
                }
            },
            enabled = !spinning,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fruit_spin_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
        ) {
            Text(
                text = if (spinning) "SPINNING REELS..." else "SPIN 777 FRUIT LINE (Bet: Rs ${selectedChip.toInt()})",
                color = VelvetMaroonDark,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ============================================================================
// GAME 9: 777BINGO (5x5 Card + Live 20-Ball Turbo Draw)
// ============================================================================
@Composable
fun Bingo777Table(viewModel: CasinoViewModel, selectedChip: Double) {
    var boardNumbers by remember {
        mutableStateOf((1..50).shuffled().take(25))
    }
    var drawnBalls by remember { mutableStateOf(setOf<Int>()) }
    var drawing by remember { mutableStateOf(false) }
    var bingoMessage by remember { mutableStateOf("Draw 20 Lucky Balls to complete Bingo Lines (Up to 25x)!") }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(220.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("777 ROYAL BINGO", color = ImperialGold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(bingoMessage, color = PaleGold, fontSize = 12.sp)
            Text(
                text = "Payouts: 1 Line = 2X • 2 Lines = 5X • 3+ Lines = 15X",
                color = EmeraldJade,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    if (!viewModel.canAffordBet(selectedChip)) {
                        viewModel.showToast("Insufficient balance!")
                        return@Button
                    }
                    drawing = true
                    boardNumbers = (1..45).shuffled().take(25)
                    drawnBalls = emptySet()
                    scope.launch {
                        val pool = (1..45).shuffled().take(20)
                        val currentSet = mutableSetOf<Int>()
                        pool.forEach { ball ->
                            currentSet.add(ball)
                            drawnBalls = currentSet.toSet()
                            delay(45L)
                        }
                        // Count completed rows and cols
                        var completedLines = 0
                        for (r in 0 until 5) {
                            if ((0 until 5).all { c -> boardNumbers[r * 5 + c] in drawnBalls }) completedLines++
                        }
                        for (c in 0 until 5) {
                            if ((0 until 5).all { r -> boardNumbers[r * 5 + c] in drawnBalls }) completedLines++
                        }
                        if ((0 until 5).all { i -> boardNumbers[i * 5 + i] in drawnBalls }) completedLines++

                        val mult = when {
                            completedLines >= 3 -> 15.0
                            completedLines == 2 -> 5.0
                            completedLines == 1 -> 2.0
                            drawnBalls.intersect(boardNumbers.toSet()).size >= 12 -> 1.5
                            else -> 0.0
                        }
                        val win = selectedChip * mult
                        val summary = "$completedLines Bingo Lines (${drawnBalls.intersect(boardNumbers.toSet()).size} hits)"
                        bingoMessage = "Result: $summary • Won Rs ${win.toInt()}"
                        viewModel.settleGameRound(CasinoGameType.BINGO_777, selectedChip, win, summary)
                        drawing = false
                    }
                },
                enabled = !drawing,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bingo_draw_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
            ) {
                Text(
                    text = if (drawing) "DRAWING BALLS..." else "PLAY 777 BINGO (Rs ${selectedChip.toInt()})",
                    color = VelvetMaroonDark,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // 5x5 Bingo Card Grid
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (r in 0 until 5) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (c in 0 until 5) {
                        val num = boardNumbers[r * 5 + c]
                        val hit = num in drawnBalls
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (hit) EmeraldJade else RubySurface)
                                .border(1.5.dp, if (hit) CreamWhite else ImperialGold, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$num",
                                color = if (hit) VelvetMaroonDark else CreamWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// GAME 10: DRAGON VS TIGER & RATTLING GEMS 2X ARENA
// ============================================================================
@Composable
fun DragonTigerAndGemsTable(viewModel: CasinoViewModel, selectedChip: Double) {
    var selectedSide by remember { mutableStateOf("DRAGON") }
    var dragonCard by remember { mutableIntStateOf(12) }
    var tigerCard by remember { mutableIntStateOf(9) }
    var gemBonusMultiplier by remember { mutableIntStateOf(1) }
    var battleText by remember { mutableStateOf("Bet on DRAGON (2x), TIE (8x), or TIGER (2x) + Rattling Gems 2X Boost!") }

    val cardNames = mapOf(1 to "A", 11 to "J", 12 to "Q", 13 to "K")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(battleText, color = ImperialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dragon Card
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🐉 DRAGON", color = Color(0xFFEF4444), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(94.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CreamWhite)
                        .border(2.5.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cardNames[dragonCard] ?: dragonCard}♠",
                        color = VelvetMaroonDark,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Center Rattling Gem Multiplier Orb
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(VelvetMaroonDark)
                    .border(2.dp, ElectricCyan, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text("💎 GEM BOOST", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("${gemBonusMultiplier}X", color = ImperialGold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }

            // Tiger Card
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🐅 TIGER", color = ImperialGold, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(94.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CreamWhite)
                        .border(2.5.dp, ImperialGold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cardNames[tigerCard] ?: tigerCard}♥",
                        color = Color(0xFFDC2626),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Bet Selection Zones: DRAGON, TIE 8X, TIGER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                Triple("DRAGON", "🐉 DRAGON (2X)", Color(0xFF991B1B)),
                Triple("TIE", "⚡ TIE (8X)", EmeraldDark),
                Triple("TIGER", "🐅 TIGER (2X)", Color(0xFFB45309))
            ).forEach { (side, label, color) ->
                val isSelected = selectedSide == side
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedSide = side }
                        .testTag("dt_zone_${side.lowercase()}"),
                    colors = CardDefaults.cardColors(containerColor = color),
                    border = BorderStroke(if (isSelected) 2.5.dp else 1.dp, if (isSelected) ImperialGold else CreamWhite)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = CreamWhite, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }
            }
        }

        Button(
            onClick = {
                if (!viewModel.canAffordBet(selectedChip)) {
                    viewModel.showToast("Insufficient balance!")
                    return@Button
                }
                dragonCard = Random.nextInt(1, 14)
                tigerCard = Random.nextInt(1, 14)
                gemBonusMultiplier = if (Random.nextInt(100) < 25) 2 else 1

                val winner = when {
                    dragonCard > tigerCard -> "DRAGON"
                    tigerCard > dragonCard -> "TIGER"
                    else -> "TIE"
                }
                val baseMult = if (winner == "TIE") 8.0 else 2.0
                val winPayout = if (selectedSide == winner) selectedChip * baseMult * gemBonusMultiplier else 0.0
                val summary = "Dragon ${cardNames[dragonCard] ?: dragonCard} vs Tiger ${cardNames[tigerCard] ?: tigerCard} ($winner • ${gemBonusMultiplier}x Gem)"
                battleText = summary
                viewModel.settleGameRound(CasinoGameType.RATTLING_GEMS, selectedChip, winPayout, summary)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dt_deal_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
        ) {
            Text(
                text = "DEAL DRAGON VS TIGER (Bet Rs ${selectedChip.toInt()} on $selectedSide)",
                color = VelvetMaroonDark,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
