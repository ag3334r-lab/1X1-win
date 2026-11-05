package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val playerId: String = "8627577",
    val displayName: String = "RoyalPlayer",
    val avatarIndex: Int = 0,
    val mainBalance: Double = 500.0,
    val bonusBalance: Double = 50.0,
    val safeBalance: Double = 0.0,
    val vipLevel: Int = 1,
    val vipPoints: Long = 0L,
    val streakDays: Int = 1,
    val lastSpinDay: Long = 0L,
    val referralCode: String = "RGC8627",
    val paymentMethod: String = "JAZZCASH",
    val accountNumber: String = "",
    val accountHolder: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WalletTransaction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "DEPOSIT", // DEPOSIT, WITHDRAW, SAFE_DEPOSIT, SAFE_WITHDRAW, BONUS, SHOP_BUY
    val method: String = "JAZZCASH", // JAZZCASH, EASYPAISA, BANK, SAFE_VAULT, DAILY_BONUS, SPIN_WHEEL, SHOP_PACK
    val amount: Double = 100.0,
    val status: String = "COMPLETED", // PENDING, APPROVED, COMPLETED, REJECTED
    val referenceNote: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GameRoundRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val gameId: String = "",
    val gameTitle: String = "",
    val betAmount: Double = 0.0,
    val winAmount: Double = 0.0,
    val outcomeText: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

enum class GameCategory(val label: String) {
    ALL("All"),
    MULTIPLAYER("Multiplayer"),
    SKILL("Skill"),
    SLOTS("Slots"),
    SPORTS("Sports")
}

enum class CasinoGameType(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: GameCategory,
    val isHot: Boolean,
    val isLucky: Boolean,
    val ribbonColorHex: Long,
    val accentColorHex: Long,
    val cropCol: Int,
    val cropRow: Int
) {
    ZOO_ROULETTE(
        id = "zoo_roulette",
        title = "Zoo Roulette",
        subtitle = "Lion • Eagle • Shark 24x",
        category = GameCategory.MULTIPLAYER,
        isHot = true,
        isLucky = true,
        ribbonColorHex = 0xFF7C3AED,
        accentColorHex = 0xFFF59E0B,
        cropCol = 0,
        cropRow = 0
    ),
    SEVEN_UP_DOWN(
        id = "seven_up_down",
        title = "7 Up Down",
        subtitle = "2-6 Down • 7 Lucky 5x • 8-12 Up",
        category = GameCategory.MULTIPLAYER,
        isHot = true,
        isLucky = false,
        ribbonColorHex = 0xFFBE123C,
        accentColorHex = 0xFFFBBF24,
        cropCol = 1,
        cropRow = 0
    ),
    RUMMY(
        id = "rummy",
        title = "Rummy",
        subtitle = "13-Card & Teen Patti Royal",
        category = GameCategory.SKILL,
        isHot = true,
        isLucky = false,
        ribbonColorHex = 0xFFDB2777,
        accentColorHex = 0xFF38BDF8,
        cropCol = 2,
        cropRow = 0
    ),
    ROULETTE(
        id = "roulette",
        title = "Roulette",
        subtitle = "European 36x Royal Wheel",
        category = GameCategory.MULTIPLAYER,
        isHot = false,
        isLucky = false,
        ribbonColorHex = 0xFFDC2626,
        accentColorHex = 0xFF10B981,
        cropCol = 3,
        cropRow = 0
    ),
    CAR_ROULETTE(
        id = "car_roulette",
        title = "Car Roulette",
        subtitle = "Ferrari • Lambo • Maserati 40x",
        category = GameCategory.SPORTS,
        isHot = true,
        isLucky = false,
        ribbonColorHex = 0xFFD97706,
        accentColorHex = 0xFFEF4444,
        cropCol = 4,
        cropRow = 0
    ),
    MINES(
        id = "mines",
        title = "Mines",
        subtitle = "5x5 Gold Gems & Bombs",
        category = GameCategory.SKILL,
        isHot = true,
        isLucky = false,
        ribbonColorHex = 0xFF0284C7,
        accentColorHex = 0xFFFACC15,
        cropCol = 0,
        cropRow = 1
    ),
    DOMINO(
        id = "domino",
        title = "Domino",
        subtitle = "Royal Gaple & QuiQui 9x",
        category = GameCategory.SKILL,
        isHot = true,
        isLucky = false,
        ribbonColorHex = 0xFFB45309,
        accentColorHex = 0xFFEF4444,
        cropCol = 1,
        cropRow = 1
    ),
    FRUIT_LINE(
        id = "fruit_line",
        title = "Fruit line",
        subtitle = "777 WILD Jackpot Reels",
        category = GameCategory.SLOTS,
        isHot = false,
        isLucky = true,
        ribbonColorHex = 0xFF9F1239,
        accentColorHex = 0xFFA855F7,
        cropCol = 2,
        cropRow = 1
    ),
    BINGO_777(
        id = "bingo_777",
        title = "777BINGO",
        subtitle = "Live Turbo Ball Draw",
        category = GameCategory.SLOTS,
        isHot = true,
        isLucky = false,
        ribbonColorHex = 0xFFBE185D,
        accentColorHex = 0xFFFDE047,
        cropCol = 3,
        cropRow = 1
    ),
    RATTLING_GEMS(
        id = "rattling_gems",
        title = "Dragon & Gems",
        subtitle = "Dragon vs Tiger + 2X Gems",
        category = GameCategory.SLOTS,
        isHot = true,
        isLucky = true,
        ribbonColorHex = 0xFF1D4ED8,
        accentColorHex = 0xFFEC4899,
        cropCol = 4,
        cropRow = 1
    )
}

data class ShopPack(
    val id: String,
    val pricePkr: Double,
    val bonusPkr: Double,
    val vipPoints: Long,
    val badgeText: String
)
