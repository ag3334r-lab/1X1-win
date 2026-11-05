package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CasinoGameType
import com.example.data.model.GameCategory
import com.example.data.model.UserProfile
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
import com.example.ui.theme.RubySurfaceLight
import com.example.ui.theme.VelvetMaroonDark
import com.example.ui.theme.VelvetMaroonMid
import com.example.ui.viewmodel.ActiveModal
import com.example.ui.viewmodel.CasinoViewModel
import com.example.ui.viewmodel.LiveBroadcastWinner

@Composable
fun RoyalLobbyScreen(
    viewModel: CasinoViewModel,
    onSignOut: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val liveBroadcast by viewModel.liveBroadcast.collectAsStateWithLifecycle()
    val activeModal by viewModel.activeModal.collectAsStateWithLifecycle()
    val toastBanner by viewModel.toastBanner.collectAsStateWithLifecycle()
    val isLandscape by viewModel.isLandscapeMode.collectAsStateWithLifecycle()

    val filteredGames = if (selectedCategory == GameCategory.ALL) {
        CasinoGameType.entries
    } else {
        CasinoGameType.entries.filter { it.category == selectedCategory }
    }

    val lobbyBitmap = ImageBitmap.imageResource(id = R.drawable.img_lobby_reference)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF6A1436), VelvetMaroonMid, VelvetMaroonDark),
                    radius = 1350f
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Subtle Ambient Casino Sparkles Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x16FFD700),
                radius = size.minDimension * 0.45f,
                center = Offset(size.width * 0.5f, size.height * 0.4f)
            )
            drawCircle(
                color = Color(0x14FF2E93),
                radius = size.minDimension * 0.35f,
                center = Offset(size.width * 0.85f, size.height * 0.25f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. TOP ROYAL PROFILE & WALLET BAR
            TopRoyalHeaderBar(
                profile = profile,
                onOpenShop = { viewModel.openModal(ActiveModal.PAY_SHOP) },
                onOpenBonusInfo = { viewModel.openModal(ActiveModal.SHARE_BONUS) },
                onOpenSportsBonus = { viewModel.openModal(ActiveModal.SPORTS_BONUS) },
                onOpenVip = { viewModel.openModal(ActiveModal.VIP_CENTER) },
                onOpenMail = { viewModel.openModal(ActiveModal.MAIL_BOX) },
                onOpenSupport = { viewModel.openModal(ActiveModal.SUPPORT_CHAT) },
                onOpenSettings = { viewModel.openModal(ActiveModal.SETTINGS_ADMIN) },
                onRefreshWallet = { viewModel.showToast("Wallet Synced: Rs ${profile.mainBalance.toInt()}") }
            )

            // 2. CATEGORY TABS + FLOATING BIG WIN BROADCAST
            CategoryAndBroadcastRow(
                selectedCategory = selectedCategory,
                onSelectCategory = viewModel::selectCategory,
                liveBroadcast = liveBroadcast,
                onBroadcastClick = {
                    viewModel.openGame(CasinoGameType.RATTLING_GEMS)
                }
            )

            // 3. CENTER PROMO RAIL + 10 GAMES SHOWCASE
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Vertical Promo Rail (Lucky Player, Spin Wheel, 7 Get 1 Free)
                LeftPromoRail(
                    streakDays = profile.streakDays,
                    onLuckyPlayerClick = { viewModel.openModal(ActiveModal.LUCKY_PLAYERS) },
                    onSpinWheelClick = { viewModel.openModal(ActiveModal.SPIN_WHEEL) },
                    onSevenGetOneClick = { viewModel.openModal(ActiveModal.SEVEN_GET_ONE) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 10 Games Grid (2-Row Horizontal in Landscape, 2-Column Vertical in Portrait)
                if (isLandscape) {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("lobby_games_grid"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            RoyalGameCardTile(
                                game = game,
                                lobbyBitmap = lobbyBitmap,
                                onClick = { viewModel.openGame(game) },
                                modifier = Modifier
                                    .width(136.dp)
                                    .fillMaxHeight()
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("lobby_games_grid"),
                        contentPadding = PaddingValues(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            RoyalGameCardTile(
                                game = game,
                                lobbyBitmap = lobbyBitmap,
                                onClick = { viewModel.openGame(game) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(132.dp)
                            )
                        }
                    }
                }
            }

            // 4. BOTTOM CASHIER & UTILITY DOCK (Agent | Notice, Share, Safe, Withdraw | Pay SHOP)
            BottomCashierDock(
                onAgentClick = { viewModel.openModal(ActiveModal.AGENT_REFERRAL) },
                onNoticeClick = { viewModel.openModal(ActiveModal.NOTICE_BOARD) },
                onShareClick = { viewModel.openModal(ActiveModal.SHARE_BONUS) },
                onSafeClick = { viewModel.openModal(ActiveModal.SAFE_VAULT) },
                onWithdrawClick = { viewModel.openModal(ActiveModal.WITHDRAW) },
                onPayShopClick = { viewModel.openModal(ActiveModal.PAY_SHOP) }
            )
        }

        // Toast Banner Overlay
        AnimatedVisibility(
            visible = toastBanner != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = VelvetMaroonDark.copy(alpha = 0.95f),
                border = BorderStroke(1.5.dp, ImperialGold),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = ImperialGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = toastBanner.orEmpty(),
                        color = PaleGold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Modal Dialog Overlays
        if (activeModal != ActiveModal.NONE) {
            CasinoModalDispatcher(
                activeModal = activeModal,
                viewModel = viewModel,
                onDismiss = { viewModel.openModal(ActiveModal.NONE) },
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
private fun TopRoyalHeaderBar(
    profile: UserProfile,
    onOpenShop: () -> Unit,
    onOpenBonusInfo: () -> Unit,
    onOpenSportsBonus: () -> Unit,
    onOpenVip: () -> Unit,
    onOpenMail: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefreshWallet: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF4B0F26), Color(0xFF280613))
                )
            )
            .border(width = 1.dp, color = BurnishedGold.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Player Avatar + Name + ID:XXXXXXX
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFF340819))
                .border(1.5.dp, ImperialGold, RoundedCornerShape(30.dp))
                .clickable(onClick = onOpenSettings)
                .padding(end = 12.dp)
                .testTag("profile_header_chip")
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf( Color(0xFFFF5252), Color(0xFF880E4F))
                        )
                    )
                    .border(2.dp, ImperialGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Player Avatar",
                    tint = ImperialGold,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = profile.displayName,
                    color = PaleGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "ID:${profile.playerId}",
                    color = CreamWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Main PKR Balance Pill + Bonus Pill
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Main Balance Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF1E040E))
                    .border(1.dp, BurnishedGold, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ImperialGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₨",
                        color = VelvetMaroonDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Balance",
                    tint = ImperialGold,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = onRefreshWallet)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${profile.mainBalance.toInt()}",
                    color = CreamWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("main_balance_text")
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Balance",
                    tint = ImperialGold,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(onClick = onOpenShop)
                        .testTag("header_add_cash_button")
                )
            }

            // Bonus Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF1E040E))
                    .border(1.dp, BurnishedGold.copy(alpha = 0.6f), RoundedCornerShape(50))
                    .clickable(onClick = onOpenBonusInfo)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Bonus",
                    color = MutedRose,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rs ${profile.bonusBalance.toInt()}",
                    color = PaleGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = "Bonus Help",
                    tint = ImperialGold,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // SPORTS BONUS Banner Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1E40AF), Color(0xFF0284C7))
                    )
                )
                .border(1.5.dp, ImperialGold, RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenSportsBonus)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Sports Bonus",
                tint = ImperialGold,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "SPORTS",
                    color = CreamWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "BONUS",
                    color = ImperialGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Action Icons: VIP, Mail, Support, Setting
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderActionIcon(
                icon = Icons.Default.Stars,
                label = "VIP ${profile.vipLevel}",
                badgeCount = 0,
                onClick = onOpenVip,
                tag = "header_vip_button"
            )
            HeaderActionIcon(
                icon = Icons.Default.Mail,
                label = "Mail",
                badgeCount = 2,
                onClick = onOpenMail,
                tag = "header_mail_button"
            )
            HeaderActionIcon(
                icon = Icons.Default.HeadsetMic,
                label = "Support",
                badgeCount = 0,
                onClick = onOpenSupport,
                tag = "header_support_button"
            )
            HeaderActionIcon(
                icon = Icons.Default.Settings,
                label = "Setting",
                badgeCount = 0,
                onClick = onOpenSettings,
                tag = "header_setting_button"
            )
        }
    }
}

@Composable
private fun HeaderActionIcon(
    icon: ImageVector,
    label: String,
    badgeCount: Int,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(ImperialGold, BurnishedGold))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = VelvetMaroonDark,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                color = PaleGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$badgeCount",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CategoryAndBroadcastRow(
    selectedCategory: GameCategory,
    onSelectCategory: (GameCategory) -> Unit,
    liveBroadcast: LiveBroadcastWinner,
    onBroadcastClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Category Filter Pills: All, Multiplayer, Skill, Slots, Sports
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameCategory.entries.forEach { category ->
                val isSelected = category == selectedCategory
                val catIcon = when (category) {
                    GameCategory.ALL -> Icons.Default.GridView
                    GameCategory.MULTIPLAYER -> Icons.Default.Groups
                    GameCategory.SKILL -> Icons.Default.Style
                    GameCategory.SLOTS -> Icons.Default.Casino
                    GameCategory.SPORTS -> Icons.Default.SportsBasketball
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(Color(0xFFD81B60), Color(0xFF880E4F)))
                            } else {
                                Brush.horizontalGradient(listOf(Color(0xFF4A1026), Color(0xFF330919)))
                            }
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) ImperialGold else BurnishedGold.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("category_tab_${category.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = catIcon,
                        contentDescription = category.label,
                        tint = ImperialGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.label,
                        color = if (isSelected) CreamWhite else MutedRose,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Live Winner Broadcast Ticker matching right side of screenshot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3E0A1F), Color(0xFF681234), Color(0xFF3E0A1F))
                    )
                )
                .border(1.5.dp, ImperialGold, RoundedCornerShape(24.dp))
                .clickable(onClick = onBroadcastClick)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(ImperialGold, BurnishedGold)))
                    .border(1.5.dp, CreamWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Winner",
                    tint = VelvetMaroonDark,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = liveBroadcast.playerName,
                    color = MutedRose,
                    fontSize = 10.sp
                )
                Text(
                    text = liveBroadcast.gameName,
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = ImperialGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Big Win ",
                        color = PaleGold,
                        fontSize = 10.sp
                    )
                    Text(
                        text = liveBroadcast.winText,
                        color = ImperialGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun LeftPromoRail(
    streakDays: Int,
    onLuckyPlayerClick: () -> Unit,
    onSpinWheelClick: () -> Unit,
    onSevenGetOneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(86.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PromoRailButton(
            title = "Lucky\nPlayer",
            badgeColor = Color(0xFF6D28D9),
            accentColor = ImperialGold,
            icon = Icons.Default.EmojiEvents,
            onClick = onLuckyPlayerClick,
            tag = "promo_lucky_player"
        )
        PromoRailButton(
            title = "Spin\nWheel",
            badgeColor = Color(0xFFB91C1C),
            accentColor = ImperialGold,
            icon = Icons.Default.Casino,
            onClick = onSpinWheelClick,
            tag = "promo_spin_wheel"
        )
        PromoRailButton(
            title = "7 GET\n1 FREE",
            badgeColor = Color(0xFF047857),
            accentColor = PaleGold,
            icon = Icons.Default.CardGiftcard,
            subtitle = "Day $streakDays/7",
            onClick = onSevenGetOneClick,
            tag = "promo_7_get_1"
        )
    }
}

@Composable
private fun PromoRailButton(
    title: String,
    badgeColor: Color,
    accentColor: Color,
    icon: ImageVector,
    subtitle: String? = null,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = badgeColor),
        border = BorderStroke(1.5.dp, ImperialGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                color = CreamWhite,
                fontSize = 10.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = ImperialGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RoyalGameCardTile(
    game: CasinoGameType,
    lobbyBitmap: ImageBitmap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Crop coordinates from the 768x361 reference image for each of the 10 games
    val colStarts = intArrayOf(85, 228, 372, 516, 650)
    val colWidths = intArrayOf(140, 140, 140, 138, 116)
    val rowStarts = intArrayOf(94, 202)
    val rowHeights = intArrayOf(106, 106)

    val srcX = colStarts[game.cropCol.coerceIn(0, 4)]
    val srcW = colWidths[game.cropCol.coerceIn(0, 4)]
    val srcY = rowStarts[game.cropRow.coerceIn(0, 1)]
    val srcH = rowHeights[game.cropRow.coerceIn(0, 1)]

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF5A122C), Color(0xFF2D0715))
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(ImperialGold, BurnishedGold, PaleGold)),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .testTag("game_tile_${game.id}")
    ) {
        // Render the exact cropped game artwork from the user's reference screenshot
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawImage(
                image = lobbyBitmap,
                srcOffset = IntOffset(srcX, srcY),
                srcSize = IntSize(srcW, srcH),
                dstOffset = IntOffset(0, 0),
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )
        }

        // Top-Left "Lucky" Badge if enabled
        if (game.isLucky) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xDD9333EA))
                    .border(1.dp, ImperialGold, RoundedCornerShape(6.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "LUCKY",
                    color = ImperialGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Top-Right "HOT" Ribbon Badge matching the screenshot
        if (game.isHot) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(EmeraldJade, NeonMagenta)
                        )
                    )
                    .border(1.dp, CreamWhite, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "HOT",
                    color = CreamWhite,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Bottom Curved Ribbon Title Banner
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(game.ribbonColorHex).copy(alpha = 0.92f),
                            Color(game.accentColorHex).copy(alpha = 0.88f),
                            Color(game.ribbonColorHex).copy(alpha = 0.92f)
                        )
                    )
                )
                .border(0.8.dp, ImperialGold.copy(alpha = 0.8f))
                .padding(vertical = 3.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = game.title,
                color = CreamWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BottomCashierDock(
    onAgentClick: () -> Unit,
    onNoticeClick: () -> Unit,
    onShareClick: () -> Unit,
    onSafeClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onPayShopClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF420D22), Color(0xFF220510))
                )
            )
            .border(1.dp, BurnishedGold.copy(alpha = 0.7f))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Emerald "Agent" Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(
                        listOf(EmeraldJade, EmeraldDark)
                    )
                )
                .border(1.5.dp, PaleGold, RoundedCornerShape(50))
                .clickable(onClick = onAgentClick)
                .padding(horizontal = 14.dp, vertical = 7.dp)
                .testTag("dock_agent_button")
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Agent",
                tint = CreamWhite,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Agent",
                color = CreamWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Center Gold Icons: Notice, Share, Safe, Withdraw
        Row(
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockActionItem(
                icon = Icons.Default.Campaign,
                label = "Notice",
                onClick = onNoticeClick,
                tag = "dock_notice_button"
            )
            DockActionItem(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = onShareClick,
                tag = "dock_share_button"
            )
            DockActionItem(
                icon = Icons.Default.Lock,
                label = "Safe",
                onClick = onSafeClick,
                tag = "dock_safe_button"
            )
            DockActionItem(
                icon = Icons.Default.CurrencyExchange,
                label = "Withdraw",
                onClick = onWithdrawClick,
                tag = "dock_withdraw_button"
            )
        }

        // Right Metallic Gold "Pay / SHOP" Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(PaleGold, ImperialGold, BurnishedGold)
                    )
                )
                .border(
                    width = 2.dp,
                    color = CreamWhite,
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 16.dp, bottomEnd = 16.dp)
                )
                .clickable(onClick = onPayShopClick)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("dock_pay_button")
        ) {
            Text(
                text = "Pay",
                color = Color(0xFF4A2500),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5C2D0C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Shop",
                    tint = ImperialGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DockActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ImperialGold,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = PaleGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
