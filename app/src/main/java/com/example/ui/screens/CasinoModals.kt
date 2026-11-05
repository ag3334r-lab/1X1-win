package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BurnishedGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldJade
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.MutedRose
import com.example.ui.theme.PaleGold
import com.example.ui.theme.RubySurface
import com.example.ui.theme.VelvetMaroonDark
import com.example.ui.viewmodel.ActiveModal
import com.example.ui.viewmodel.CasinoViewModel
import kotlin.random.Random
import kotlinx.coroutines.launch

@Composable
fun CasinoModalDispatcher(
    activeModal: ActiveModal,
    viewModel: CasinoViewModel,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val gameHistory by viewModel.gameHistory.collectAsStateWithLifecycle()
    val autoApprove by viewModel.autoApproveCashier.collectAsStateWithLifecycle()
    val isLandscape by viewModel.isLandscapeMode.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC0D0206))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxWidth(0.92f)
                .padding(12.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = RubySurface),
            border = BorderStroke(2.dp, Brush.linearGradient(listOf(ImperialGold, BurnishedGold, PaleGold)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                val modalTitle = when (activeModal) {
                    ActiveModal.PAY_SHOP -> "ROYAL PAY • CHIP SHOP (PKR)"
                    ActiveModal.SAFE_VAULT -> "ROYAL SAFE VAULT (CHIP LOCKER)"
                    ActiveModal.WITHDRAW -> "INSTANT WITHDRAW (JAZZCASH / EASYPAISA / BANK)"
                    ActiveModal.SPIN_WHEEL -> "LUCKY FORTUNE SPIN WHEEL"
                    ActiveModal.SEVEN_GET_ONE -> "7 GET 1 FREE DAILY CHECK-IN"
                    ActiveModal.LUCKY_PLAYERS -> "LUCKY PLAYER LEADERBOARD"
                    ActiveModal.VIP_CENTER -> "ROYAL VIP CLUB & PRIVILEGES"
                    ActiveModal.AGENT_REFERRAL -> "AGENT REFERRAL & COMMISSION"
                    ActiveModal.SETTINGS_ADMIN -> "ACCOUNT, ORIENTATION & ADMIN CASHIER"
                    ActiveModal.MAIL_BOX -> "ROYAL SYSTEM MAILBOX"
                    ActiveModal.SUPPORT_CHAT -> "24/7 VIP LIVE SUPPORT"
                    ActiveModal.NOTICE_BOARD -> "OFFICIAL ANNOUNCEMENTS & TOURNAMENTS"
                    ActiveModal.SHARE_BONUS -> "SHARE & BONUS REWARDS"
                    ActiveModal.SPORTS_BONUS -> "SPORTS & WEEKLY REBATE BONUS"
                    ActiveModal.NONE -> ""
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = modalTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = ImperialGold,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("modal_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PaleGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (activeModal) {
                    ActiveModal.PAY_SHOP -> {
                        var selectedMethod by remember { mutableStateOf("JAZZCASH") }
                        var tidInput by remember { mutableStateOf("") }
                        Text(
                            text = "Current Balance: Rs ${profile.mainBalance.toInt()} • Bonus: Rs ${profile.bonusBalance.toInt()}",
                            color = EmeraldJade,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("JAZZCASH", "EASYPAISA", "BANK").forEach { rail ->
                                FilterChip(
                                    selected = selectedMethod == rail,
                                    onClick = { selectedMethod = rail },
                                    label = { Text(rail) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = tidInput,
                            onValueChange = { tidInput = it },
                            label = { Text("Optional 11-Digit TID / Sender Account") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.shopPacks.chunked(3).forEach { rowPacks ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowPacks.forEach { pack ->
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    viewModel.buyShopPack(pack, selectedMethod, tidInput)
                                                }
                                                .testTag("buy_pack_${pack.pricePkr.toInt()}"),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = VelvetMaroonDark),
                                            border = BorderStroke(1.5.dp, ImperialGold)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = pack.badgeText,
                                                    color = EmeraldJade,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Rs ${pack.pricePkr.toInt()}",
                                                    color = ImperialGold,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                                Text(
                                                    text = "+Rs ${pack.bonusPkr.toInt()} Free",
                                                    color = PaleGold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ActiveModal.SAFE_VAULT -> {
                        var safeAmountText by remember { mutableStateOf("200") }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            VaultStatBox("Main Wallet", "Rs ${profile.mainBalance.toInt()}", ImperialGold)
                            VaultStatBox("Safe Vault", "Rs ${profile.safeBalance.toInt()}", EmeraldJade)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = safeAmountText,
                            onValueChange = { safeAmountText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Transfer Amount (Rs)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val amt = safeAmountText.toDoubleOrNull() ?: 0.0
                                    viewModel.transferSafeVault(amt, depositToSafe = true)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldJade)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Deposit to Safe", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    val amt = safeAmountText.toDoubleOrNull() ?: 0.0
                                    viewModel.transferSafeVault(amt, depositToSafe = false)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                            ) {
                                Text("Take from Safe", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    ActiveModal.WITHDRAW -> {
                        var method by remember { mutableStateOf(profile.paymentMethod.ifBlank { "JAZZCASH" }) }
                        var accountNum by remember { mutableStateOf(profile.accountNumber.ifBlank { "03001234567" }) }
                        var holderName by remember { mutableStateOf(profile.accountHolder.ifBlank { profile.displayName }) }
                        var withdrawAmt by remember { mutableStateOf("500") }

                        Text(
                            text = "Withdrawable Balance: Rs ${profile.mainBalance.toInt()} • Safe: Rs ${profile.safeBalance.toInt()}",
                            color = ImperialGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("JAZZCASH", "EASYPAISA", "BANK").forEach { m ->
                                FilterChip(
                                    selected = method == m,
                                    onClick = { method = m },
                                    label = { Text(m) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = accountNum,
                            onValueChange = { accountNum = it },
                            label = { Text("$method Account / Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = holderName,
                            onValueChange = { holderName = it },
                            label = { Text("Account Holder Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = withdrawAmt,
                            onValueChange = { withdrawAmt = it.filter { c -> c.isDigit() } },
                            label = { Text("Withdraw Amount (Min Rs 100)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val amt = withdrawAmt.toDoubleOrNull() ?: 0.0
                                viewModel.requestWithdrawal(amt, method, accountNum, holderName)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_withdraw_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                        ) {
                            Text("Withdraw Now to $method", color = VelvetMaroonDark, fontWeight = FontWeight.ExtraBold)
                        }

                        if (transactions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Recent Wallet Ledger:", color = PaleGold, fontWeight = FontWeight.Bold)
                            transactions.take(4).forEach { tx ->
                                Text(
                                    text = "• ${tx.type} (${tx.method}): Rs ${tx.amount.toInt()} — [${tx.status}]",
                                    color = MutedRose,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    ActiveModal.SPIN_WHEEL -> {
                        val scope = rememberCoroutineScope()
                        val rotation = remember { Animatable(0f) }
                        var spinning by remember { mutableStateOf(false) }
                        val prizes = listOf(50.0, 100.0, 250.0, 500.0, 1000.0, 75.0, 150.0, 2000.0)
                        var lastPrize by remember { mutableStateOf<Double?>(null) }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(160.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    rotate(rotation.value) {
                                        val sweep = 360f / prizes.size
                                        prizes.forEachIndexed { idx, _ ->
                                            drawArc(
                                                color = if (idx % 2 == 0) Color(0xFFD97706) else Color(0xFF9F1239),
                                                startAngle = idx * sweep,
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
                                    Text("SPIN", color = VelvetMaroonDark, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            if (lastPrize != null) {
                                Text(
                                    text = "Congratulations! You won Rs ${lastPrize!!.toInt()}!",
                                    color = EmeraldJade,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = {
                                    if (!spinning) {
                                        spinning = true
                                        val chosen = prizes.random()
                                        scope.launch {
                                            rotation.animateTo(
                                                targetValue = rotation.value + 1080f + Random.nextInt(360),
                                                animationSpec = tween(1400, easing = FastOutSlowInEasing)
                                            )
                                            lastPrize = chosen
                                            viewModel.claimSpinWheelReward(chosen)
                                            spinning = false
                                        }
                                    }
                                },
                                enabled = !spinning,
                                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                            ) {
                                Text(if (spinning) "Spinning..." else "Spin Golden Wheel Now!", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    ActiveModal.SEVEN_GET_ONE -> {
                        Text(
                            text = "Consecutive Check-In: Day ${profile.streakDays} of 7 (Day 7 grants +Rs 500 Extra Bonus!)",
                            color = PaleGold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (1..7).forEach { day ->
                                val reward = day * 50.0
                                val isCurrent = day == profile.streakDays
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrent) EmeraldJade else VelvetMaroonDark
                                    ),
                                    border = BorderStroke(1.dp, ImperialGold)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("D$day", fontSize = 10.sp, color = CreamWhite, fontWeight = FontWeight.Bold)
                                        Text("Rs ${reward.toInt()}", fontSize = 10.sp, color = ImperialGold)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.claimSevenGetOneBonus(profile.streakDays * 50.0) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                        ) {
                            Text("Claim Day ${profile.streakDays} Bonus Now", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    ActiveModal.SETTINGS_ADMIN -> {
                        var nameEdit by remember { mutableStateOf(profile.displayName) }
                        Text("Player ID: ${profile.playerId} • Referral Code: ${profile.referralCode}", color = ElectricCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nameEdit,
                            onValueChange = { nameEdit = it },
                            label = { Text("Display Nickname") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lobby Layout: ${if (isLandscape) "Landscape Widescreen" else "Portrait Grid"}", color = CreamWhite)
                            Button(
                                onClick = { viewModel.toggleOrientationMode() },
                                colors = ButtonDefaults.buttonColors(containerColor = BurnishedGold)
                            ) {
                                Text("Switch View", color = VelvetMaroonDark)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Admin Instant Deposit/Withdraw Approval", color = EmeraldJade)
                            Switch(
                                checked = autoApprove,
                                onCheckedChange = { viewModel.toggleAutoApprove(it) }
                            )
                        }
                        val pendingTxs = transactions.filter { it.status == "PENDING" }
                        if (pendingTxs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pending Admin Cashier Requests:", color = ImperialGold, fontWeight = FontWeight.Bold)
                            pendingTxs.forEach { tx ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${tx.type} Rs ${tx.amount.toInt()} (${tx.method})", color = CreamWhite, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.approvePendingTransaction(tx) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldJade)
                                    ) {
                                        Text("Approve", color = VelvetMaroonDark, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { viewModel.updateProfileDetails(nameEdit, profile.avatarIndex) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                            ) {
                                Text("Save Profile", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onSignOut,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
                            ) {
                                Text("Sign Out", color = CreamWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = "Player ID: ${profile.playerId} • VIP Level ${profile.vipLevel} (${profile.vipPoints} XP)\nReferral Code: ${profile.referralCode} (Earn 30% Weekly Agent Commission!)",
                            color = PaleGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (gameHistory.isNotEmpty()) {
                            Text("Recent Game Rounds:", color = ImperialGold, fontWeight = FontWeight.Bold)
                            gameHistory.take(5).forEach { round ->
                                Text(
                                    text = "• ${round.gameTitle}: Bet Rs ${round.betAmount.toInt()} → Won Rs ${round.winAmount.toInt()} (${round.outcomeText})",
                                    color = CreamWhite,
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            Text(
                                text = "• Daily VIP Rebate & Sports Bonus active on all 10 games!\n• Share your Agent code ${profile.referralCode} with friends to earn instant Rs 200 bonus chips.",
                                color = EmeraldJade
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.claimSpinWheelReward(100.0)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                        ) {
                            Text("Claim Rs 100 Activity Reward", color = VelvetMaroonDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultStatBox(title: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(VelvetMaroonDark)
            .border(1.dp, color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(title, color = MutedRose, fontSize = 12.sp)
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}
