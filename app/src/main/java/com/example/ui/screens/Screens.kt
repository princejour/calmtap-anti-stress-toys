package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.example.R
import com.example.ads.AdManager
import com.example.ui.components.*
import com.example.viewmodels.CalmViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), MaterialTheme.colorScheme.background))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calmtap_icon_1783351035463),
                    contentDescription = "CalmTap Logo",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("CalmTap", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("RELAXING FIDGET TOYS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp), color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun AdMobBanner() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun HomeScreen(navController: NavController, viewModel: CalmViewModel) {
    val stats by viewModel.userStats.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color.Transparent)))
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.calmtap_icon_1783351035463),
                            contentDescription = "CalmTap Logo",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("CalmTap", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("RELAXING FIDGET TOYS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Hero Card (Free Play)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp))
                        .clickable { navController.navigate("free_play") }
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Instant\nMindfulness", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Experience satisfying touch effects designed for your focus.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate("free_play") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Free Play Mode", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Challenges
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(24.dp))
                            .clickable { navController.navigate("challenges") }
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp)))
                            Column {
                                Text("Challenges", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("Stage ${stats.currentStage}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                            }
                        }
                    }
                    
                    // Rewards
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(24.dp))
                            .clickable { navController.navigate("rewards") }
                            .padding(16.dp)
                    ) {
                        val unlockedCount = 2 + (if(stats.tempThemeUnlocked) 1 else 0) + (if(stats.tempSoundUnlocked) 1 else 0) + (if(stats.tempSkinUnlocked) 1 else 0)
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp)))
                            Column {
                                Text("Rewards", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onTertiaryContainer)
                                Text("${stats.calmCoins} Coins • $unlockedCount Items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text("Settings", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.navigate("privacy") },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text("Privacy", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            AdMobBanner()
        }
    }
}

@Composable
fun FreePlayScreen(navController: NavController) {
    val toys = listOf("Pop It", "Stress Ball", "Bubble Wrap", "Slime", "Sand Flow", "Fidget Spinner", "Breathing Circle", "Rain Mode")
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Free Play") },
                navigationIcon = {
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(toys.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                        navController.navigate("toy/${index}")
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(toys[index], style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}

@Composable
fun ToyScreen(navController: NavController, toyIndex: Int, viewModel: CalmViewModel, adManager: AdManager) {
    val stats by viewModel.userStats.collectAsState()
    val context = LocalContext.current as Activity
    val interstitialEnabled = false // Disabled by default per checklist

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Relax") },
                navigationIcon = {
                    Button(onClick = { 
                        if (interstitialEnabled) {
                            adManager.showInterstitial(context) {
                                navController.popBackStack()
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when(toyIndex) {
                0 -> PopItToy(vibrationEnabled = stats.vibrationEnabled)
                1 -> StressBallToy(vibrationEnabled = stats.vibrationEnabled)
                2 -> BubbleWrapToy()
                3 -> SlimeToy()
                4 -> SandFlowToy()
                5 -> FidgetSpinnerToy()
                6 -> BreathingCircleToy()
                7 -> RainModeToy()
                else -> Text("Toy not found")
            }
        }
    }
}

@Composable
fun ChallengesScreen(navController: NavController, viewModel: CalmViewModel, adManager: AdManager) {
    val stats by viewModel.userStats.collectAsState()
    val context = LocalContext.current as Activity
    var showIntroScreen by remember { mutableStateOf(false) }
    var introMode by remember { mutableStateOf("") } // "extra_chance" or "bonus_reward"

    val handleFail: () -> Unit = {
        if (stats.failedAttempts >= 2) {
            introMode = "extra_chance"
            showIntroScreen = true
        } else {
            viewModel.incrementFailedAttempts()
        }
    }

    val handleSuccess: () -> Unit = {
        introMode = "bonus_reward"
        showIntroScreen = true
    }

    if (showIntroScreen) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(if (introMode == "extra_chance") "Extra Chance" else "Bonus Reward") },
            text = { Text(if (introMode == "extra_chance") "Watch this short ad to get one extra chance and continue this stage." else "Watch this short ad to unlock a bonus for the next stage.") },
            confirmButton = {
                Button(onClick = {
                    showIntroScreen = false
                    adManager.showRewardedInterstitial(context, onRewardEarned = {
                        if (introMode == "extra_chance") {
                            viewModel.resetFailedAttempts()
                        } else {
                            viewModel.addCoins(50)
                            viewModel.unlockTempTheme()
                        }
                    }, onAdDismissed = {
                        if (introMode == "bonus_reward") viewModel.completeStage()
                    })
                }) { Text("Continue with Ad") }
            },
            dismissButton = {
                Button(onClick = {
                    showIntroScreen = false
                    if (introMode == "extra_chance") {
                        viewModel.resetFailedAttempts()
                    } else {
                        viewModel.completeStage()
                    }
                }) { Text(if (introMode == "extra_chance") "Restart Stage" else "Skip and Continue") }
            }
        )
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Stage ${stats.currentStage}") },
                navigationIcon = {
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Failed Attempts: ${stats.failedAttempts}/3", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                when (stats.currentStage % 8) {
                    1 -> ChallengePopIt(vibrationEnabled = stats.vibrationEnabled, onSuccess = handleSuccess)
                    2 -> ChallengeStressBall(onSuccess = handleSuccess, onFail = handleFail)
                    3 -> ChallengeFidgetSpinner(onSuccess = handleSuccess)
                    4 -> ChallengeBreathingCircle(onSuccess = handleSuccess, onFail = handleFail)
                    5 -> ChallengeBubbleWrap(onSuccess = handleSuccess)
                    6 -> ChallengeSlime(onSuccess = handleSuccess, onFail = handleFail)
                    7 -> ChallengeSandFlow(onSuccess = handleSuccess)
                    0 -> ChallengeRainMode(onSuccess = handleSuccess)
                }
            }
        }
    }
}

@Composable
fun RewardsScreen(navController: NavController, viewModel: CalmViewModel) {
    val stats by viewModel.userStats.collectAsState()
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Rewards") },
                navigationIcon = {
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Calm Coins", style = MaterialTheme.typography.titleLarge)
                    Text("${stats.calmCoins}", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                }
            }
            Text("Unlocked Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("- Default Theme")
                Text("- Default Sounds")
                if (stats.tempThemeUnlocked) Text("- Premium Theme (Next Stage)", color = MaterialTheme.colorScheme.primary)
                if (stats.tempSoundUnlocked) Text("- Premium Sounds (Next Stage)", color = MaterialTheme.colorScheme.primary)
                if (stats.tempSkinUnlocked) Text("- Premium Skin (Next Stage)", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.weight(1f))
            AdMobBanner()
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController, viewModel: CalmViewModel) {
    val stats by viewModel.userStats.collectAsState()
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sound", style = MaterialTheme.typography.titleLarge)
                Switch(checked = stats.soundEnabled, onCheckedChange = { viewModel.toggleSound() })
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Vibration", style = MaterialTheme.typography.titleLarge)
                Switch(checked = stats.vibrationEnabled, onCheckedChange = { viewModel.toggleVibration() })
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dark Mode", style = MaterialTheme.typography.titleLarge)
                Switch(checked = stats.darkModeEnabled, onCheckedChange = { viewModel.toggleDarkMode() })
            }
            Button(onClick = { viewModel.resetProgress() }, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Reset Progress")
            }
            Spacer(modifier = Modifier.weight(1f))
            AdMobBanner()
        }
    }
}

@Composable
fun PrivacyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                "Privacy Policy\n\n" +
                "- This app does not require login.\n" +
                "- This app does not collect personal data directly.\n" +
                "- This app can work offline.\n" +
                "- AdMob may use advertising services if ads are enabled.\n" +
                "- This app is for relaxation and entertainment only.\n" +
                "- This app is not a medical app and does not provide medical advice.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
