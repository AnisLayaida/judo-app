package com.anislayaida.judoapp.presentation.screens.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.presentation.components.BottomNavBar
import kotlinx.coroutines.delay

private val NavyBg    = Color(0xFF0D1B3E)
private val SurfaceBg = Color(0xFF1A2B55)
private val Gold      = Color(0xFFC9A84C)
private val AppRed    = Color(0xFFC8102E)
private val AppGreen  = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    userRole: UserRole = UserRole.JUDOKA,
    navController: NavController? = null
) {
    val durationOptions = listOf(
        "4:00" to 240,
        "3:00" to 180,
        "2:00" to 120,
        "1:00" to 60
    )

    var selectedDurationIndex by remember { mutableIntStateOf(0) }
    var totalSeconds          by remember { mutableIntStateOf(240) }
    var remainingSeconds      by remember { mutableIntStateOf(240) }
    var isRunning             by remember { mutableStateOf(false) }
    var isFinished            by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            if (remainingSeconds == 0) {
                isRunning = false
                isFinished = true
            }
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    val minutes    = remainingSeconds / 60
    val seconds    = remainingSeconds % 60
    val timeString = "%d:%02d".format(minutes, seconds)

    val timerColor = when {
        isFinished             -> AppRed
        remainingSeconds <= 30 -> AppRed
        remainingSeconds <= 60 -> Gold
        else                   -> AppGreen
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = { Text("Timer", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
            )
        },
        bottomBar = {
            if (navController != null) {
                BottomNavBar(userRole = userRole, navController = navController)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Duration selector ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Match Duration",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durationOptions.forEachIndexed { index, (label, _) ->
                            FilterChip(
                                selected = selectedDurationIndex == index,
                                onClick = {
                                    if (!isRunning) {
                                        selectedDurationIndex = index
                                        totalSeconds = durationOptions[index].second
                                        remainingSeconds = durationOptions[index].second
                                        isFinished = false
                                    }
                                },
                                label = {
                                    Text(
                                        label,
                                        color = if (selectedDurationIndex == index) NavyBg
                                        else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Gold,
                                    containerColor = NavyBg
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Circular timer ────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = SurfaceBg,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = timerColor,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        timeString,
                        color = timerColor,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        when {
                            isFinished                       -> "時間切れ · Time's Up"
                            isRunning                        -> "試合中 · Match Running"
                            remainingSeconds == totalSeconds -> "準備 · Ready"
                            else                             -> "一時停止 · Paused"
                        },
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            // ── Controls ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = {
                        isRunning = false
                        isFinished = false
                        remainingSeconds = totalSeconds
                    },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SurfaceBg
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.LightGray,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Surface(
                    onClick = {
                        if (!isFinished) isRunning = !isRunning
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isFinished) SurfaceBg else AppRed
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause
                            else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isFinished) Color.Gray else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = when {
                                isFinished -> "Finished"
                                isRunning  -> "Pause"
                                else       -> "Start"
                            },
                            color = if (isFinished) Color.Gray else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // ── Info card ─────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "BJA Match Times",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    TimerInfoRow("Senior / Junior (16+)", "4:00")
                    TimerInfoRow("Junior (13–15)",        "3:00")
                    TimerInfoRow("Junior (under 13)",     "2:00")
                    TimerInfoRow("Mini Judo",             "1:00")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TimerInfoRow(label: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray, fontSize = 13.sp)
        Text(time, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}