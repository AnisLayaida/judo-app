package com.anislayaida.judoapp.presentation.screens.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
private val SurfaceMid  = Color(0xFF152444)
private val Gold        = Color(0xFFC9A84C)
private val AppRed      = Color(0xFFC8102E)
private val AppGreen    = Color(0xFF22C55E)
private val AmberWarn   = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    userRole: UserRole = UserRole.JUDOKA,
    navController: NavController? = null,
    isCompact: Boolean = true
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
        targetValue   = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f,
        animationSpec = tween(durationMillis = 500),
        label         = "progress"
    )

    val minutes    = remainingSeconds / 60
    val seconds    = remainingSeconds % 60
    val timeString = "%d:%02d".format(minutes, seconds)

    val timerColor = when {
        isFinished             -> AppRed
        remainingSeconds <= 30 -> AppRed
        remainingSeconds <= 60 -> AmberWarn
        else                   -> AppGreen
    }

    val statusLabel = when {
        isFinished                       -> "時間切れ  ·  Time's Up"
        isRunning                        -> "試合中  ·  Match Running"
        remainingSeconds == totalSeconds -> "準備  ·  Ready"
        else                             -> "一時停止  ·  Paused"
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "柔道  Timer",
                            color         = Color.White,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            "British Judo Association",
                            color    = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBg)
            )
        },
        bottomBar = {
            if (isCompact && navController != null) {
                BottomNavBar(userRole = userRole, navController = navController)
            }
        }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .background(SurfaceBg)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                durationOptions.forEachIndexed { index, (label, _) ->
                    val isSelected = selectedDurationIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Gold else NavyBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Gold else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (!isRunning) {
                                    selectedDurationIndex = index
                                    totalSeconds          = durationOptions[index].second
                                    remainingSeconds      = durationOptions[index].second
                                    isFinished            = false
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            color      = if (isSelected) NavyBg else Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))

            Spacer(Modifier.height(40.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.size(280.dp)
            ) {
                CircularProgressIndicator(
                    progress    = { 1f },
                    modifier    = Modifier.fillMaxSize(),
                    color       = Color.White.copy(alpha = 0.06f),
                    strokeWidth = 14.dp,
                    strokeCap   = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress    = { progress },
                    modifier    = Modifier.fillMaxSize(),
                    color       = timerColor,
                    strokeWidth = 14.dp,
                    strokeCap   = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text          = timeString,
                        color         = timerColor,
                        fontSize      = 62.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text      = statusLabel,
                        color     = Color.White.copy(alpha = 0.45f),
                        fontSize  = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(SurfaceBg)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .clickable {
                            isRunning        = false
                            isFinished       = false
                            remainingSeconds = totalSeconds
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint               = Color.White.copy(alpha = 0.6f),
                        modifier           = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isFinished) SurfaceBg
                            else if (isRunning) SurfaceMid
                            else AppRed
                        )
                        .border(
                            width = 1.dp,
                            color = when {
                                isFinished -> Color.White.copy(alpha = 0.1f)
                                isRunning  -> AppRed.copy(alpha = 0.5f)
                                else       -> Color.Transparent
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { if (!isFinished) isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector        = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint               = when {
                                isFinished -> Color.White.copy(alpha = 0.3f)
                                isRunning  -> AppRed
                                else       -> Color.White
                            },
                            modifier           = Modifier.size(26.dp)
                        )
                        Text(
                            text = when {
                                isFinished -> "Finished"
                                isRunning  -> "Pause"
                                else       -> "Start"
                            },
                            color      = when {
                                isFinished -> Color.White.copy(alpha = 0.3f)
                                isRunning  -> AppRed
                                else       -> Color.White
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBg),
                shape  = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        "BJA Match Times",
                        color         = Gold,
                        fontWeight    = FontWeight.Bold,
                        fontSize      = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(14.dp))

                    listOf(
                        Triple("Senior / Junior (16+)", "4:00", 240),
                        Triple("Junior (13–15)",         "3:00", 180),
                        Triple("Junior (under 13)",      "2:00", 120),
                        Triple("Mini Judo",              "1:00",  60)
                    ).forEachIndexed { index, (label, time, secs) ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color    = Color.White.copy(alpha = 0.06f)
                            )
                        }
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isRunning) {
                                        selectedDurationIndex = index
                                        totalSeconds          = secs
                                        remainingSeconds      = secs
                                        isFinished            = false
                                    }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text     = label,
                                color    = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                            Text(
                                text       = time,
                                color      = if (selectedDurationIndex == index) Gold else Color.White.copy(alpha = 0.4f),
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}