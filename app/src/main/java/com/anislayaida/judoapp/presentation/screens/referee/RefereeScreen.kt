package com.anislayaida.judoapp.presentation.screens.referee

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.presentation.components.BottomNavBar
import kotlinx.coroutines.delay

private val NavyBg      = Color(0xFF0D1B3E)
private val SurfaceBg   = Color(0xFF1A2B55)
private val Gold        = Color(0xFFC9A84C)
private val AppRed      = Color(0xFFC8102E)
private val AppGreen    = Color(0xFF22C55E)
private val PlayerWhite = Color(0xFFEEEEEE)
private val PlayerBlue  = Color(0xFF3A8FD4)

data class PlayerScore(
    val ippon:  Int = 0,
    val wazari: Int = 0,
    val yuko:   Int = 0,
    val shido:  Int = 0
) {
    val effectiveIppon: Int     get() = ippon + (wazari / 2)
    val hasWon:         Boolean get() = effectiveIppon >= 1
    val hansokuMake:    Boolean get() = shido >= 3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefereeScreen(
    userRole:      UserRole      = UserRole.REFEREE,
    navController: NavController? = null,
    isCompact:     Boolean        = true
) {
    var whiteScore       by remember { mutableStateOf(PlayerScore()) }
    var blueScore        by remember { mutableStateOf(PlayerScore()) }
    var totalSeconds     by remember { mutableIntStateOf(240) }
    var remainingSeconds by remember { mutableIntStateOf(240) }
    var isRunning        by remember { mutableStateOf(false) }
    var isFinished       by remember { mutableStateOf(false) }
    var winner           by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(whiteScore, blueScore) {
        when {
            whiteScore.hasWon && !blueScore.hasWon  -> { winner = "White"; isRunning = false; isFinished = true }
            blueScore.hasWon  && !whiteScore.hasWon -> { winner = "Blue";  isRunning = false; isFinished = true }
            whiteScore.hansokuMake                  -> { winner = "Blue";  isRunning = false; isFinished = true }
            blueScore.hansokuMake                   -> { winner = "White"; isRunning = false; isFinished = true }
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            if (remainingSeconds == 0) {
                isRunning = false; isFinished = true
                winner = when {
                    whiteScore.effectiveIppon > blueScore.effectiveIppon  -> "White"
                    blueScore.effectiveIppon  > whiteScore.effectiveIppon -> "Blue"
                    whiteScore.wazari > blueScore.wazari                  -> "White"
                    blueScore.wazari  > whiteScore.wazari                 -> "Blue"
                    whiteScore.yuko   > blueScore.yuko                    -> "White"
                    blueScore.yuko    > whiteScore.yuko                   -> "Blue"
                    blueScore.shido   > whiteScore.shido                  -> "White"
                    whiteScore.shido  > blueScore.shido                   -> "Blue"
                    else                                                   -> "Draw"
                }
            }
        }
    }

    fun resetMatch() {
        whiteScore = PlayerScore(); blueScore = PlayerScore()
        remainingSeconds = totalSeconds
        isRunning = false; isFinished = false; winner = null
    }

    val minutes    = remainingSeconds / 60
    val seconds    = remainingSeconds % 60
    val timeString = "%02d:%02d".format(minutes, seconds)

    val timerColor = when {
        isFinished             -> AppRed
        remainingSeconds <= 30 -> AppRed
        remainingSeconds <= 60 -> Gold
        else                   -> Color.White
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title   = { Text("Referee Board", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    if (isRunning) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Live",
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color      = AppGreen,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    IconButton(onClick = { resetMatch() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
            )
        },
        bottomBar = {
            if (isCompact && navController != null) {
                BottomNavBar(userRole = userRole, navController = navController)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (winner != null) {
                val bannerColor by animateColorAsState(
                    targetValue   = when (winner) { "White" -> PlayerWhite; "Blue" -> PlayerBlue; else -> Gold },
                    animationSpec = tween(500),
                    label         = "banner"
                )
                Surface(modifier = Modifier.fillMaxWidth(), color = bannerColor) {
                    Text(
                        text       = if (winner == "Draw") "引き分け · Draw" else "一本 · $winner wins!",
                        modifier   = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        color      = if (winner == "White") NavyBg else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        textAlign  = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(text = timeString, color = timerColor, fontSize = 80.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            Text(
                text     = when {
                    isFinished -> "終了 · End"
                    isRunning  -> "試合中 · Live"
                    else       -> "準備 · Ready"
                },
                color    = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.padding(horizontal = 16.dp)
            ) {
                listOf("4:00" to 240, "3:00" to 180, "2:00" to 120, "1:00" to 60).forEach { (label, secs) ->
                    FilterChip(
                        selected = totalSeconds == secs,
                        onClick  = {
                            if (!isRunning && !isFinished) {
                                totalSeconds = secs; remainingSeconds = secs
                            }
                        },
                        label  = { Text(label, fontSize = 12.sp, color = if (totalSeconds == secs) NavyBg else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gold,
                            containerColor         = SurfaceBg
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick  = { if (!isFinished) isRunning = !isRunning },
                    enabled  = !isFinished,
                    colors   = ButtonDefaults.buttonColors(containerColor = if (isRunning) Gold else SurfaceBg),
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape    = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        if (isRunning) "Pause" else "Start",
                        color      = if (isRunning) NavyBg else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
                Button(
                    onClick  = { resetMatch() },
                    colors   = ButtonDefaults.buttonColors(containerColor = SurfaceBg),
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape    = RoundedCornerShape(24.dp)
                ) {
                    Text("Reset", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier         = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    ScorePanel(
                        label     = "White",
                        color     = PlayerWhite,
                        textColor = Color.White,
                        barColor  = PlayerWhite,
                        score     = whiteScore,
                        isWinner  = winner == "White",
                        modifier  = Modifier.weight(1f)
                    )
                    Text("VS", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    ScorePanel(
                        label     = "Blue",
                        color     = PlayerBlue,
                        textColor = PlayerBlue,
                        barColor  = PlayerBlue,
                        score     = blueScore,
                        isWinner  = winner == "Blue",
                        modifier  = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Tap to score", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))

            Column(
                modifier            = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Ippon"  to Pair({ if (!isFinished) whiteScore = whiteScore.copy(ippon  = whiteScore.ippon  + 1) },
                        { if (!isFinished) blueScore  = blueScore.copy(ippon   = blueScore.ippon   + 1) }),
                    "Wazari" to Pair({ if (!isFinished) whiteScore = whiteScore.copy(wazari = whiteScore.wazari + 1) },
                        { if (!isFinished) blueScore  = blueScore.copy(wazari  = blueScore.wazari  + 1) }),
                    "Yuko"   to Pair({ if (!isFinished) whiteScore = whiteScore.copy(yuko   = whiteScore.yuko   + 1) },
                        { if (!isFinished) blueScore  = blueScore.copy(yuko    = blueScore.yuko    + 1) }),
                    "Shido"  to Pair({ if (!isFinished) whiteScore = whiteScore.copy(shido  = whiteScore.shido  + 1) },
                        { if (!isFinished) blueScore  = blueScore.copy(shido   = blueScore.shido   + 1) })
                ).forEach { (label, actions) ->
                    val buttonColor = when (label) {
                        "Ippon"  -> Color(0xFF1E3A2F)
                        "Wazari" -> Color(0xFF1E2A3A)
                        "Yuko"   -> Color(0xFF1E2A3A)
                        else     -> Color(0xFF2A1E1E)
                    }
                    val labelColor = if (label == "Shido") Gold else Color.White
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick  = actions.first,
                            enabled  = !isFinished,
                            colors   = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape    = RoundedCornerShape(10.dp)
                        ) {
                            Text(label, color = labelColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Button(
                            onClick  = actions.second,
                            enabled  = !isFinished,
                            colors   = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape    = RoundedCornerShape(10.dp)
                        ) {
                            Text(label, color = PlayerBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("3 shidos = Hansoku-make (disqualification)", color = Color.White.copy(alpha = 0.25f), fontSize = 11.sp)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScorePanel(
    label:    String,
    color:    Color,
    textColor: Color,
    barColor: Color,
    score:    PlayerScore,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue   = if (isWinner) Gold else Color.Transparent,
        animationSpec = tween(300),
        label         = "panelBorder"
    )
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(12.dp),
        border   = androidx.compose.foundation.BorderStroke(width = if (isWinner) 2.dp else 0.dp, color = borderColor)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.width(48.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
            Text(label, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ScoreBox(label = "Ippon",  value = score.ippon)
                ScoreBox(label = "Wazari", value = score.wazari)
                ScoreBox(label = "Yuko",   value = score.yuko)
            }
            Text("Shido penalties", color = Color.LightGray, fontSize = 10.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    val filled = index < score.shido
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (filled) Gold.copy(alpha = 0.2f) else Color.Transparent)
                            .border(1.dp, if (filled) Gold else Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "${index + 1}",
                            color      = if (filled) Gold else Color.White.copy(alpha = 0.3f),
                            fontSize   = 11.sp,
                            fontWeight = if (filled) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBox(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value.toString(),
            color      = if (value > 0) Color.White else Color.White.copy(alpha = 0.3f),
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(label, color = Color.LightGray, fontSize = 10.sp)
    }
}