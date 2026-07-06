# CalmTap Development Workflow

## 1. Project Setup
- The project is a native Android application built with Kotlin and Jetpack Compose.
- Gradle is configured with necessary dependencies (Room, Compose Navigation, Google Play Services Ads).
- The app uses the official uploaded icon as the launcher icon.

## 2. App Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Local Persistence**: Room Database (`UserStats`)
- **State Management**: ViewModel (`CalmViewModel`) with StateFlow
- **Ad Monetization**: Google AdMob (`AdManager`)

## 3. Screen Structure
- **SplashScreen**: Displays logo and transitions to home.
- **HomeScreen**: Main menu with access to Free Play, Challenges, Rewards, Settings, and Privacy.
- **FreePlayScreen**: Grid of available toys.
- **ToyScreen**: Individual interactive toy sandbox.
- **ChallengesScreen**: Stage-based challenges with specific success/fail conditions.
- **RewardsScreen**: Displays earned Calm Coins and temporary premium items.
- **SettingsScreen**: Toggles for sound, vibration, dark mode, and progress reset.
- **PrivacyScreen**: Explains offline nature and lack of personal data collection.

## 4. Free Play Implementation
Interactive toys available without restrictions:
- Pop It
- Stress Ball
- Bubble Wrap
- Slime
- Sand Flow
- Fidget Spinner
- Breathing Circle
- Rain Mode
Ads are strictly prohibited during active interaction.

## 5. Calm Challenges Implementation
- A stage-based mode with specific goals (e.g., "Pop 10 bubbles").
- **Stage System**: Loops through 8 distinct challenges, incrementing the stage counter upon success.
- **Failure System**:
  - 1st fail: Normal retry.
  - 2nd fail: Normal retry.
  - 3rd fail: Triggers the "Extra Chance" rewarded interstitial flow.

## 6. Rewarded Interstitial Ads System
- Managed by `AdManager` using AdMob.
- Always preceded by an Intro Screen (AlertDialog) explaining the reward with clear skip/cancel options.
- **Extra Chance**: Offered after 3 consecutive failures.
- **Bonus Reward**: Offered upon stage completion to earn 50 Calm Coins and unlock temporary premium features.

## 7. Rewards System
- Tracks `calmCoins`.
- Tracks temporary unlocks: `tempThemeUnlocked`, `tempSoundUnlocked`, `tempSkinUnlocked`.
- These temporary unlocks are granted via the post-stage rewarded interstitial and reset upon the next stage completion.

## 8. Settings System
- Toggles stored in Room Database:
  - `soundEnabled`
  - `vibrationEnabled`
  - `darkModeEnabled`
- Allows resetting all progress (coins, stage, attempts).

## 9. Privacy Policy
- An in-app screen assuring users that no personal data is collected directly, the app works offline, and it makes no medical claims.

## 10. Testing Checklist
- [ ] Verify app launch without crash.
- [ ] Verify toy interactions (touch, drag, tap).
- [ ] Ensure no ads during active play.
- [ ] Verify 3rd failure triggers rewarded ad intro.
- [ ] Verify stage completion triggers bonus ad intro.
- [ ] Toggle dark mode and verify UI updates.

## 11. Publishing Checklist
- [ ] Replace test AdMob IDs with real production IDs.
- [ ] Verify app icon appearance on multiple launcher densities.
- [ ] Generate signed release AAB/APK.
- [ ] Complete Google Play Store data safety forms (no data collection).
