package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CasinoGameType
import com.example.data.model.GameCategory
import com.example.data.model.GameRoundRecord
import com.example.data.model.ShopPack
import com.example.data.model.UserProfile
import com.example.data.model.WalletTransaction
import com.example.data.repository.CasinoRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ActiveModal {
    NONE,
    PAY_SHOP,
    SAFE_VAULT,
    WITHDRAW,
    VIP_CENTER,
    MAIL_BOX,
    SUPPORT_CHAT,
    SETTINGS_ADMIN,
    SPIN_WHEEL,
    SEVEN_GET_ONE,
    LUCKY_PLAYERS,
    AGENT_REFERRAL,
    NOTICE_BOARD,
    SHARE_BONUS,
    SPORTS_BONUS
}

data class LiveBroadcastWinner(
    val playerName: String,
    val gameName: String,
    val winText: String,
    val avatarSeed: Int
)

class CasinoViewModel(
    private val repository: CasinoRepository,
    private val currentUserId: String,
    initialDisplayName: String = "RoyalPlayer"
) : ViewModel() {

    private val _localProfile = MutableStateFlow(
        UserProfile(
            id = currentUserId,
            userId = currentUserId,
            playerId = repository.generatePlayerId(currentUserId),
            displayName = initialDisplayName.ifBlank { "Guest${repository.generatePlayerId(currentUserId)}" },
            avatarIndex = 0,
            mainBalance = 1000.0,
            bonusBalance = 100.0,
            safeBalance = 250.0,
            vipLevel = 1,
            vipPoints = 100L,
            streakDays = 1,
            lastSpinDay = 0L,
            referralCode = "RGC${repository.generatePlayerId(currentUserId).takeLast(4)}"
        )
    )
    val userProfile: StateFlow<UserProfile> = _localProfile.asStateFlow()

    val transactions: StateFlow<List<WalletTransaction>> = repository.observeTransactions(currentUserId)
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing transactions", firestoreError ?: error)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val gameHistory: StateFlow<List<GameRoundRecord>> = repository.observeGameHistory(currentUserId)
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing game history", firestoreError ?: error)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _selectedCategory = MutableStateFlow(GameCategory.ALL)
    val selectedCategory: StateFlow<GameCategory> = _selectedCategory.asStateFlow()

    private val _activeGame = MutableStateFlow<CasinoGameType?>(null)
    val activeGame: StateFlow<CasinoGameType?> = _activeGame.asStateFlow()

    private val _activeModal = MutableStateFlow(ActiveModal.NONE)
    val activeModal: StateFlow<ActiveModal> = _activeModal.asStateFlow()

    private val _selectedChip = MutableStateFlow(50.0)
    val selectedChip: StateFlow<Double> = _selectedChip.asStateFlow()

    private val _toastBanner = MutableStateFlow<String?>(null)
    val toastBanner: StateFlow<String?> = _toastBanner.asStateFlow()

    private val _autoApproveCashier = MutableStateFlow(true)
    val autoApproveCashier: StateFlow<Boolean> = _autoApproveCashier.asStateFlow()

    private val _isLandscapeMode = MutableStateFlow(true)
    val isLandscapeMode: StateFlow<Boolean> = _isLandscapeMode.asStateFlow()

    private val _liveBroadcast = MutableStateFlow(
        LiveBroadcastWinner("Guest2757761", "Dragon vs Tiger", "10K", 0)
    )
    val liveBroadcast: StateFlow<LiveBroadcastWinner> = _liveBroadcast.asStateFlow()

    val shopPacks = listOf(
        ShopPack("pack_100", 100.0, 10.0, 20L, "+10% Bonus"),
        ShopPack("pack_500", 500.0, 75.0, 120L, "+15% HOT"),
        ShopPack("pack_1000", 1000.0, 200.0, 300L, "+20% VIP"),
        ShopPack("pack_3000", 3000.0, 750.0, 1000L, "+25% Royal"),
        ShopPack("pack_5000", 5000.0, 1500.0, 2000L, "+30% Best"),
        ShopPack("pack_10000", 10000.0, 3500.0, 5000L, "+35% Jackpot")
    )

    init {
        viewModelScope.launch {
            repository.getOrCreateProfile(initialDisplayName, 0).onSuccess { created ->
                _localProfile.value = created
            }
        }
        viewModelScope.launch {
            repository.observeUserProfile(currentUserId)
                .catch { e ->
                    val fsError = generateSequence(e) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (fsError == null && e is CancellationException) throw e
                    Log.w(TAG, "Profile stream error", fsError ?: e)
                }
                .collect { remote ->
                    if (remote != null) {
                        _localProfile.value = remote
                    }
                }
        }
        viewModelScope.launch {
            val sampleBroadcasts = listOf(
                LiveBroadcastWinner("Guest2757761", "Dragon vs Tiger", "10K", 0),
                LiveBroadcastWinner("KingAli786", "Zoo Roulette", "24K", 1),
                LiveBroadcastWinner("RoyalQueen99", "Mines", "8.5K", 2),
                LiveBroadcastWinner("ZeeshanGold", "Car Roulette", "40K", 3),
                LiveBroadcastWinner("LuckyStar55", "7 Up Down", "15K", 4),
                LiveBroadcastWinner("SultanPK01", "777BINGO", "18K", 1)
            )
            var idx = 0
            while (true) {
                delay(5500L)
                idx = (idx + 1) % sampleBroadcasts.size
                _liveBroadcast.value = sampleBroadcasts[idx]
            }
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastBanner.value = message
            delay(2800L)
            if (_toastBanner.value == message) {
                _toastBanner.value = null
            }
        }
    }

    fun selectCategory(category: GameCategory) {
        _selectedCategory.value = category
    }

    fun openGame(game: CasinoGameType?) {
        _activeGame.value = game
    }

    fun openModal(modal: ActiveModal) {
        _activeModal.value = modal
    }

    fun selectChip(chipValue: Double) {
        _selectedChip.value = chipValue
    }

    fun toggleAutoApprove(enabled: Boolean) {
        _autoApproveCashier.value = enabled
        showToast(if (enabled) "Instant Cashier Approval: ON" else "Pending Admin Review Mode: ON")
    }

    fun toggleOrientationMode() {
        _isLandscapeMode.value = !_isLandscapeMode.value
    }

    fun canAffordBet(amount: Double): Boolean {
        val p = _localProfile.value
        return amount > 0 && (p.mainBalance + p.bonusBalance) >= amount
    }

    fun settleGameRound(
        game: CasinoGameType,
        betAmount: Double,
        winAmount: Double,
        outcomeText: String
    ) {
        val current = _localProfile.value
        val totalAvailable = current.mainBalance + current.bonusBalance
        if (betAmount > totalAvailable) {
            showToast("Insufficient Rs Balance! Tap Pay/Shop to recharge.")
            return
        }

        // Deduct from bonus first up to 20% of bet, remainder from mainBalance
        val bonusUsed = minOf(current.bonusBalance, betAmount * 0.20)
        val mainUsed = betAmount - bonusUsed
        val newBonus = (current.bonusBalance - bonusUsed).coerceAtLeast(0.0)
        val newMain = (current.mainBalance - mainUsed + winAmount).coerceAtLeast(0.0)
        val newVipPoints = current.vipPoints + (betAmount / 10.0).toLong().coerceAtLeast(1L)
        val newVipLevel = (1 + (newVipPoints / 500L).toInt()).coerceIn(1, 10)

        val updated = current.copy(
            mainBalance = newMain,
            bonusBalance = newBonus,
            vipPoints = newVipPoints,
            vipLevel = newVipLevel
        )
        _localProfile.value = updated

        if (winAmount >= betAmount && winAmount > 0) {
            _liveBroadcast.value = LiveBroadcastWinner(
                playerName = current.displayName,
                gameName = game.title,
                winText = "Rs ${winAmount.toInt()}",
                avatarSeed = current.avatarIndex
            )
            showToast("WON Rs ${winAmount.toInt()} in ${game.title}! ($outcomeText)")
        } else {
            showToast("$outcomeText • Bet Rs ${betAmount.toInt()}")
        }

        viewModelScope.launch {
            repository.updateWalletAndStats(
                mainBalance = updated.mainBalance,
                bonusBalance = updated.bonusBalance,
                safeBalance = updated.safeBalance,
                vipLevel = updated.vipLevel,
                vipPoints = updated.vipPoints,
                streakDays = updated.streakDays,
                lastSpinDay = updated.lastSpinDay
            )
            repository.recordGameRound(
                gameId = game.id,
                gameTitle = game.title,
                betAmount = betAmount,
                winAmount = winAmount,
                outcomeText = outcomeText
            )
        }
    }

    fun buyShopPack(pack: ShopPack, method: String, referenceTid: String) {
        val current = _localProfile.value
        val instant = _autoApproveCashier.value
        val status = if (instant) "COMPLETED" else "PENDING"
        val updated = if (instant) {
            val newPoints = current.vipPoints + pack.vipPoints
            val newVip = (1 + (newPoints / 500L).toInt()).coerceIn(1, 10)
            current.copy(
                mainBalance = current.mainBalance + pack.pricePkr,
                bonusBalance = current.bonusBalance + pack.bonusPkr,
                vipPoints = newPoints,
                vipLevel = newVip
            )
        } else {
            current
        }
        _localProfile.value = updated
        val ref = referenceTid.ifBlank { "PACK-${Random.nextInt(100000, 999999)}" }

        viewModelScope.launch {
            if (instant) {
                repository.updateWalletAndStats(
                    mainBalance = updated.mainBalance,
                    bonusBalance = updated.bonusBalance,
                    safeBalance = updated.safeBalance,
                    vipLevel = updated.vipLevel,
                    vipPoints = updated.vipPoints,
                    streakDays = updated.streakDays,
                    lastSpinDay = updated.lastSpinDay
                )
            }
            repository.createTransaction(
                type = "DEPOSIT",
                method = method,
                amount = pack.pricePkr,
                status = status,
                referenceNote = "$ref (+Rs ${pack.bonusPkr.toInt()} Bonus)"
            )
        }
        showToast(
            if (instant) "Rs ${pack.pricePkr.toInt()} + Rs ${pack.bonusPkr.toInt()} Bonus Added!"
            else "Deposit Rs ${pack.pricePkr.toInt()} submitted (Pending Admin Approval)"
        )
    }

    fun transferSafeVault(amount: Double, depositToSafe: Boolean) {
        if (amount <= 0) {
            showToast("Enter a valid Rs amount")
            return
        }
        val current = _localProfile.value
        if (depositToSafe && current.mainBalance < amount) {
            showToast("Not enough Main Balance to lock in Safe")
            return
        }
        if (!depositToSafe && current.safeBalance < amount) {
            showToast("Not enough Safe Vault Balance to withdraw")
            return
        }
        val updated = if (depositToSafe) {
            current.copy(
                mainBalance = current.mainBalance - amount,
                safeBalance = current.safeBalance + amount
            )
        } else {
            current.copy(
                mainBalance = current.mainBalance + amount,
                safeBalance = current.safeBalance - amount
            )
        }
        _localProfile.value = updated
        viewModelScope.launch {
            repository.updateWalletAndStats(
                mainBalance = updated.mainBalance,
                bonusBalance = updated.bonusBalance,
                safeBalance = updated.safeBalance,
                vipLevel = updated.vipLevel,
                vipPoints = updated.vipPoints,
                streakDays = updated.streakDays,
                lastSpinDay = updated.lastSpinDay
            )
            repository.createTransaction(
                type = if (depositToSafe) "SAFE_DEPOSIT" else "SAFE_WITHDRAW",
                method = "SAFE_VAULT",
                amount = amount,
                status = "COMPLETED",
                referenceNote = if (depositToSafe) "Locked in Safe Vault" else "Unlocked to Main Wallet"
            )
        }
        showToast(
            if (depositToSafe) "Locked Rs ${amount.toInt()} in Safe Vault"
            else "Moved Rs ${amount.toInt()} to Main Wallet"
        )
    }

    fun requestWithdrawal(
        amount: Double,
        method: String,
        accountNumber: String,
        accountHolder: String
    ) {
        val current = _localProfile.value
        if (amount < 100.0) {
            showToast("Minimum withdrawal is Rs 100")
            return
        }
        if (accountNumber.isBlank() || accountHolder.isBlank()) {
            showToast("Please enter account number and holder name")
            return
        }
        if (current.mainBalance < amount) {
            showToast("Insufficient Main Balance for withdrawal")
            return
        }
        val instant = _autoApproveCashier.value
        val status = if (instant) "APPROVED" else "PENDING"
        val updated = current.copy(
            mainBalance = current.mainBalance - amount,
            paymentMethod = method,
            accountNumber = accountNumber,
            accountHolder = accountHolder
        )
        _localProfile.value = updated

        viewModelScope.launch {
            repository.updateProfileIdentityAndBank(
                displayName = updated.displayName,
                avatarIndex = updated.avatarIndex,
                paymentMethod = method,
                accountNumber = accountNumber,
                accountHolder = accountHolder
            )
            repository.updateWalletAndStats(
                mainBalance = updated.mainBalance,
                bonusBalance = updated.bonusBalance,
                safeBalance = updated.safeBalance,
                vipLevel = updated.vipLevel,
                vipPoints = updated.vipPoints,
                streakDays = updated.streakDays,
                lastSpinDay = updated.lastSpinDay
            )
            repository.createTransaction(
                type = "WITHDRAW",
                method = method,
                amount = amount,
                status = status,
                referenceNote = "$method • $accountNumber ($accountHolder)"
            )
        }
        showToast(
            if (instant) "Withdrawal of Rs ${amount.toInt()} to $method Approved!"
            else "Withdrawal of Rs ${amount.toInt()} submitted (Pending Review)"
        )
    }

    fun approvePendingTransaction(tx: WalletTransaction) {
        if (tx.status != "PENDING") return
        val current = _localProfile.value
        val updated = if (tx.type == "DEPOSIT") {
            current.copy(mainBalance = current.mainBalance + tx.amount)
        } else {
            current
        }
        _localProfile.value = updated
        viewModelScope.launch {
            if (tx.type == "DEPOSIT") {
                repository.updateWalletAndStats(
                    mainBalance = updated.mainBalance,
                    bonusBalance = updated.bonusBalance,
                    safeBalance = updated.safeBalance,
                    vipLevel = updated.vipLevel,
                    vipPoints = updated.vipPoints,
                    streakDays = updated.streakDays,
                    lastSpinDay = updated.lastSpinDay
                )
            }
            repository.updateTransactionStatus(
                txId = tx.id,
                newStatus = "APPROVED",
                referenceNote = "${tx.referenceNote} [Admin Approved]"
            )
        }
        showToast("Transaction Rs ${tx.amount.toInt()} Approved!")
    }

    fun claimSpinWheelReward(prizeAmount: Double) {
        val current = _localProfile.value
        val updated = current.copy(
            mainBalance = current.mainBalance + prizeAmount,
            bonusBalance = current.bonusBalance + (prizeAmount * 0.25),
            lastSpinDay = current.lastSpinDay + 1L
        )
        _localProfile.value = updated
        viewModelScope.launch {
            repository.updateWalletAndStats(
                mainBalance = updated.mainBalance,
                bonusBalance = updated.bonusBalance,
                safeBalance = updated.safeBalance,
                vipLevel = updated.vipLevel,
                vipPoints = updated.vipPoints,
                streakDays = updated.streakDays,
                lastSpinDay = updated.lastSpinDay
            )
            repository.createTransaction(
                type = "BONUS",
                method = "SPIN_WHEEL",
                amount = prizeAmount,
                status = "COMPLETED",
                referenceNote = "Lucky Wheel Prize +Rs ${prizeAmount.toInt()}"
            )
        }
        showToast("Lucky Spin Won Rs ${prizeAmount.toInt()}!")
    }

    fun claimSevenGetOneBonus(dayReward: Double) {
        val current = _localProfile.value
        val nextStreak = if (current.streakDays >= 7) 1 else current.streakDays + 1
        val extraFree = if (current.streakDays == 7) 500.0 else 0.0
        val totalReward = dayReward + extraFree
        val updated = current.copy(
            mainBalance = current.mainBalance + dayReward,
            bonusBalance = current.bonusBalance + extraFree,
            streakDays = nextStreak
        )
        _localProfile.value = updated
        viewModelScope.launch {
            repository.updateWalletAndStats(
                mainBalance = updated.mainBalance,
                bonusBalance = updated.bonusBalance,
                safeBalance = updated.safeBalance,
                vipLevel = updated.vipLevel,
                vipPoints = updated.vipPoints,
                streakDays = updated.streakDays,
                lastSpinDay = updated.lastSpinDay
            )
            repository.createTransaction(
                type = "BONUS",
                method = "DAILY_BONUS",
                amount = totalReward,
                status = "COMPLETED",
                referenceNote = "7 Get 1 Free Check-in Day ${current.streakDays}"
            )
        }
        showToast("Claimed Rs ${totalReward.toInt()} Daily Streak Bonus!")
    }

    fun updateProfileDetails(name: String, avatarIdx: Int) {
        val current = _localProfile.value
        val updated = current.copy(
            displayName = name.ifBlank { current.displayName },
            avatarIndex = avatarIdx.coerceIn(0, 5)
        )
        _localProfile.value = updated
        viewModelScope.launch {
            repository.updateProfileIdentityAndBank(
                displayName = updated.displayName,
                avatarIndex = updated.avatarIndex,
                paymentMethod = updated.paymentMethod,
                accountNumber = updated.accountNumber,
                accountHolder = updated.accountHolder
            )
        }
        showToast("Profile updated!")
    }

    private companion object {
        const val TAG = "CasinoVM"
    }
}
