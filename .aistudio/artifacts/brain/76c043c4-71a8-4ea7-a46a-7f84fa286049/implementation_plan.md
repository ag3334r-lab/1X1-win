# Royal Gold Club — 10-in-1 Gaming & Wallet Hub

Royal Gold Club is a full-featured Android gaming and digital wallet platform designed around the classic **Teen Patti Gold** luxury ruby-and-gold lobby aesthetic from your reference image. It delivers 10+ realistic, fully playable games with real-time chip betting, a complete PKR (`Rs`) wallet and Safe vault ecosystem (JazzCash, EasyPaisa, Bank Transfer, Shop packages, and an built-in Admin approval console), player ID profiles, VIP tiers, daily spin wheels, and cloud-synced accounts.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Based on your "All" selection across the clarifying questions, this plan combines every requested capability into a unified, flexible experience so nothing is left out:

- **Confirmed Decision 1 — All 10+ Playable Games Included**: Includes all games shown in your reference lobby (**Zoo Roulette**, **7 Up Down**, **Rummy**, **Roulette**, **Car Roulette**, **Mines**, **Domino**, **Fruit Line**, **777 Bingo**, and **Dragon vs Tiger**), plus **Teen Patti** and **Rattling Gems** accessible via the category filter tabs (`All`, `Multiplayer`, `Skill`, `Slots`, `Sports`).
- **Confirmed Decision 2 — Complete PKR Wallet, Safe Vault, Shop & Admin System**: Supports instant top-up chip bundles in the **Pay / Shop** store, **JazzCash / EasyPaisa / Bank** deposit and withdrawal workflows, a password-protected **Safe Vault** to lock chips safely, and an in-app **Admin / Cashier Mode** toggle so you can approve or instant-credit any deposit or withdrawal request immediately.
- **Confirmed Decision 3 — Adaptive Landscape & Portrait Lobby Mode**: Defaults to the authentic widescreen **Royal Casino Lobby layout** matching your screenshot (with a quick one-tap orientation toggle in Settings so users can switch between Landscape table view and Portrait handheld grid view anytime).

---

### 1. Overview & Core Concept

- **What It Does**: Provides an end-to-end social gaming and e-commerce chip store where players sign in, receive a unique 7-digit **Player ID** (e.g., `ID:8627577`), manage their main **PKR Balance** and **Bonus Balance**, claim daily rewards, transfer funds through local payment rails, and play 10+ interactive table, slot, and arcade games with realistic physics, animations, and instant payouts.
- **Target Audience / Persona**: Players looking for an authentic South Asian royal club dashboard experience with fast-paced rounds, transparent odds, familiar local wallet options (JazzCash / EasyPaisa), and rich visual flair.
- **Key Value**: Every single icon, badge, promotional banner, bottom dock action, and game tile visible in the reference screenshot is interactive and backed by working game engines and persistent wallet ledgers—no static placeholders or dead buttons.

---

### 2. User Experience & Visual Design

![Reference Lobby Layout](/public/assets/aistudio/Games-Inside-Teen-Patti-Gold-App-2-e1777869622463_9dd826c6.webp)

- **Key User Flows**:
  1. **Account & Authentication Flow**: Players sign in with Google (via Jetpack Credential Manager) or continue with an instant profile that generates a persistent 7-digit Player ID, customizable royal avatar, and welcome bonus in `Rs`.
  2. **Main Lobby & Live Winner Ticker**:
     - **Top Header Bar**: Displays the ornate gold-framed Avatar, Player Name, `ID:XXXXXXX`, primary Coin/PKR Balance with quick refresh & `+` add buttons, secondary `Bonus: Rs X` pill with info modal, **Sports Bonus** trophy banner, and action icons for **VIP**, **Mail**, **Support**, and **Setting**.
     - **Category Filter Bar & Live Win Broadcast**: Pill tabs for `All`, `Multiplayer`, `Skill`, `Slots`, and `Sports`, paired on the right with a sparkling floating **Big Win Broadcast Card** (e.g., *"Dragon vs Tiger • Big Win 10K"*).
     - **Left Promo Rail**: Interactive promotional widgets for **Lucky Player** leaderboard, **Spin Wheel** daily fortune wheel, and **7 Get 1 Free** streak tracker.
     - **2-Row Horizontal Game Showcase**: Custom-illustrated game cards with 3D-style metallic gold frames, curved ribbon titles, and glowing `HOT` / `Lucky` badges.
     - **Bottom Utility & Cashier Dock**: Features the emerald **Agent** (referral & commission center) button on the left, central gold icons for **Notice**, **Share**, **Safe**, and **Withdraw**, and the oversized metallic gold **Pay / SHOP** button on the right.
  3. **In-Game Table Experience (All 10+ Games)**:
     - Selecting any game opens a dedicated, themed table or machine screen featuring a top bar with live `Rs` balance, chip denomination selectors (`Rs 10`, `Rs 50`, `Rs 100`, `Rs 500`, `Rs 1K`, `Rs 5K`), round timer/state indicator, animated dealing/spinning/revealing mechanics, recent result history beads, and instant celebratory win overlays.
  4. **Pay / Shop, Safe Vault & Withdraw Flow**:
     - **Pay / Shop**: E-commerce chip store offering PKR recharge packs (`Rs 100` to `Rs 50,000`) with VIP bonus percentages, JazzCash/EasyPaisa/Bank account details, transaction ID input, and an optional "Instant Credit / Admin Approve" switch for testing and management.
     - **Safe Vault**: Deposit or withdraw chips between active wallet and protected Safe storage.
     - **Withdraw**: Bind a JazzCash, EasyPaisa, or Bank account, enter withdrawal amount, view live status (`Pending`, `Approved`, `Completed`), and inspect full transaction history.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: **Royal Ruby & Burnished Gold Casino Lounge**—opulent, high-contrast, tactile metallic bevels, glowing neon badges, and velvet crimson depths.
  - *Color Palette & Mood*:
    - `--background`: Deep Velvet Maroon (`#2B0918` to `#4A1228` radial gradient)
    - `--surface`: Rich Ruby Panel (`#5B1631`) with metallic gold borders (`#F5C242`)
    - `--primary`: Burnished Imperial Gold (`#FFD700` to `#D4AF37` gradient)
    - `--secondary`: Emerald Jade Accent (`#10B981` for Agent, Safe, and HOT badges)
    - `--accent`: Electric Magenta & Cyan (`#FF2E93` / `#00F0FF` for game ribbons and win flares)
  - *Typography & Hierarchy*: Bold display serif/slab headers (`Cinzel` / `Montserrat` bold weights) for game ribbons, jackpots, and numeric counters, paired with clean, high-legibility UI labels (`Poppins`) so small badges and `Rs` balances remain crisp at any screen size.
  - *Interactive Feedback & Motion*: Spring-physics chip tosses, spinning roulette wheels and slot reels, card flip animations, shimmering `HOT` badge pulses, and floating `+Rs` payout toasts.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Complete Custom Game Engines for All 10+ Games**
  - *Chosen Approach*: Build self-contained, deterministic-yet-fair interactive game engines in Jetpack Compose Canvas and state machines for every single game on the dashboard.
  - *Why*: Ensures every game tile opens a genuinely playable, distinct game with real betting rules, odds multipliers, and wallet integration rather than generic placeholders.
  - *Games Breakdown*:
    1. **Zoo Roulette**: 8-animal betting board (Lion, Eagle, Panda, Monkey, Rabbit, Swallow, Pigeon, Peacock) + Shark jackpot with animated perimeter light chaser and 2x–24x multipliers.
    2. **7 Up Down**: Classic 2-dice shaker game—bet on `2–6 (Down, 2x)`, `7 (Lucky Seven, 5x)`, or `8–12 (Up, 2x)` with animated 3D dice roll and history strip.
    3. **Rummy / Teen Patti Royal**: 13-card / 3-card royal table against AI dealers with Pack, Chaal, Blind/Seen, Show, Meld sorting, and pot payouts.
    4. **European Roulette**: Classic 0–36 spinning wheel with Red/Black, Odd/Even, Dozens, and Straight number betting.
    5. **Car Roulette**: Luxury sports car wheel (Ferrari, Lamborghini, Maserati, Porsche, BMW, Mercedes, Audi, VW) with 5x–40x multipliers and racing track light animation.
    6. **Mines**: 5x5 grid with configurable bomb count (1–10 mines); each safe gold gem revealed increases the cashout multiplier with instant "Cash Out Now" tension.
    7. **Domino (Gaple / QuiQui)**: Tile-matching table where player and table opponents play matching pip ends or compare 9-point pairs for the pot.
    8. **Fruit Line (5-Reel Slot)**: Classic fruit & 777 slot machine (Cherries, Lemons, Bells, Watermelon, WILD, 777) with multi-line paytable and free spin triggers.
    9. **777 Bingo**: Fast-paced 5x5 Bingo card with live 30-ball draw, auto-daubing, line/full-house pattern multipliers, and extra ball purchase.
    10. **Dragon vs Tiger & Rattling Gems**: Fast 1-card showdown (`Dragon`, `Tiger`, `Tie 8x`) plus a gem-matching multiplier arcade game.

- **Decision 2: Cloud Persistence (Firestore + Google Sign-In) with Instant Local Sync**
  - *Chosen Approach*: Use **Firebase Authentication (Google Sign-In)** and **Cloud Firestore** to persist user profiles, wallet balances (`mainBalance`, `bonusBalance`, `safeBalance`), transactions, and game history, paired with reactive state flows.
  - *Why*: Guarantees that a player's `Rs` balance, Player ID, VIP level, Safe vault, and withdrawal requests are safely stored and synced in real time.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        MainActivity & Auth Gate                             │
│   ┌───────────────────────────┐       ┌─────────────────────────────────┐   │
│   │  RoyalAuthScreen          │──────▶│  MainCasinoAppShell             │   │
│   │  (Google Sign-In / ID)    │       │  (Landscape / Portrait Toggle)  │   │
│   └───────────────────────────┘       └────────────────┬────────────────┘   │
└────────────────────────────────────────────────────────┼────────────────────┘
                                                         │
        ┌────────────────────────────────────────────────┼────────────────┐
        ▼                                                ▼                ▼
┌──────────────────────────────┐  ┌──────────────────────────┐  ┌─────────────────────────┐
│      RoyalLobbyScreen        │  │   10+ Game Table Engines │  │  Wallet & Modal Overlays│
│ ┌──────────────────────────┐ │  │ ├─ ZooRouletteScreen     │  │ ├─ PayShopModal (Buy)   │
│ │ TopProfileHeaderBar      │ │  │ ├─ SevenUpDownScreen     │  │ ├─ WithdrawModal        │
│ │ CategoryFilter & BigWin  │ │  │ ├─ RummyTeenPattiScreen  │  │ ├─ SafeVaultModal       │
│ │ LeftPromoRail (Spin/7+1) │ │  │ ├─ RouletteWheelScreen   │  │ ├─ SpinWheel & 7Get1    │
│ │ Horizontal2RowGameGrid   │ │  │ ├─ CarRouletteScreen     │  │ ├─ AgentReferralModal   │
│ │ BottomCashierDock        │ │  │ ├─ MinesGameScreen       │  │ ├─ VipMailSupportModal  │
│ └──────────────────────────┘ │  │ ├─ DominoTableScreen     │  │ └─ AdminCashierModal    │
└──────────────────────────────┘  │ ├─ FruitLineSlotScreen   │  └─────────────────────────┘
                                  │ ├─ Bingo777Screen        │
                                  │ └─ DragonTigerGemScreen  │
                                  └─────────────┬────────────┘
                                                │
                                                ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CasinoViewModel & WalletGameEngine                       │
│   • Atomic bet deduction, payout calculation, VIP XP & Bonus conversion     │
│   • Live Big Win ticker generator & round history tracker                   │
└───────────────────────────────────────┬─────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│               CasinoRepository (Cloud Firestore + Local Cache)              │
│   • /users/{uid} (playerId, mainBalance, bonusBalance, safeBalance, vip)    │
│   • /users/{uid}/transactions/{txId} (type, method, amount, status)         │
│   • /users/{uid}/gameHistory/{roundId} (gameId, betAmount, winAmount, time) │
└─────────────────────────────────────────────────────────────────────────────┘
```

- **Data Model & State**:
  - **UserProfile**: `uid`, `playerId` (7-digit numeric string, e.g., `"8627577"`), `displayName`, `avatarIndex`, `mainBalance` (Double, PKR), `bonusBalance` (Double, PKR), `safeBalance` (Double, PKR), `vipLevel` (Int, 1–10), `vipPoints` (Long), `streakDays` (Int), `lastSpinEpochDay` (Long), `referralCode`, `boundPaymentMethod` (JazzCash / EasyPaisa / Bank), `boundAccountNumber`, `boundAccountName`.
  - **WalletTransaction**: `id`, `userId`, `type` (`DEPOSIT`, `WITHDRAW`, `SAFE_IN`, `SAFE_OUT`, `BONUS_CLAIM`), `method` (`JAZZCASH`, `EASYPAISA`, `BANK`, `SHOP_PACK`), `amount`, `status` (`PENDING`, `APPROVED`, `REJECTED`), `referenceNumber`, `createdAt`.
  - **GameRoundRecord**: `id`, `userId`, `gameName`, `betAmount`, `payoutAmount`, `outcomeSummary`, `createdAt`.

- **Interactive Component & State Mapping**:
  - **Lobby Category Filter (`All`, `Multiplayer`, `Skill`, `Slots`, `Sports`)**: Dynamically filters the 2-row horizontal game carousel with spring transitions.
  - **Game Betting Cycle**: Selecting a chip (`Rs 10` – `Rs 5000`) and tapping a bet zone validates `mainBalance + bonusBalance >= betAmount`, deducts the stake immediately, triggers the game round animation, computes payouts, updates the player's Firestore/local wallet atomically, and appends the result to the game's history strip and the lobby's Big Win ticker.
  - **Pay / Withdraw & Admin Approval**: Submitting a deposit or withdrawal creates a `WalletTransaction`. Users can either enable **Instant Auto-Approval** or review/approve pending requests inside the built-in **Admin Cashier Panel** accessible from Settings.
  - **Left Rail Promos (`Lucky Player`, `Spin Wheel`, `7 Get 1 Free`)**: Interactive modals where players spin an 8-slice prize wheel for instant `Rs` rewards, claim 7-day check-in bonuses, and inspect top winners.
