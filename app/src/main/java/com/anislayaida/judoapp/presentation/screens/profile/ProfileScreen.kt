package com.anislayaida.judoapp.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.navigation.NavScreen
import com.anislayaida.judoapp.presentation.components.BottomNavBar

private val NavyBg    = Color(0xFF0D1B3E)
private val SurfaceBg = Color(0xFF1A2B55)
private val Gold      = Color(0xFFC9A84C)
private val AppRed    = Color(0xFFC8102E)

private fun beltTagBg(belt: String): Color = when {
    belt.startsWith("Black") -> Color(0xFF212121)
    else -> when (belt) {
        "White"  -> Color(0xFFDDDDDD)
        "Red"    -> Color(0xFFB71C1C)
        "Yellow" -> Color(0xFFFFD600)
        "Orange" -> Color(0xFFFF8C00)
        "Green"  -> Color(0xFF1B5E20)
        "Blue"   -> Color(0xFF0D47A1)
        "Brown"  -> Color(0xFF4E342E)
        else     -> Color.Gray
    }
}

private fun beltTagText(belt: String): Color = when {
    belt.startsWith("Black") -> Color(0xFFFFD700)
    belt == "White"          -> Color(0xFF222222)
    belt == "Yellow"         -> Color(0xFF222222)
    else                     -> Color.White
}

private fun beltAvatarBg(belt: String): Color = when {
    belt.startsWith("Black") -> Color(0xFF212121)
    else -> when (belt) {
        "White"  -> Color(0xFFDDDDDD)
        "Red"    -> Color(0xFFB71C1C)
        "Yellow" -> Color(0xFFFFD600)
        "Orange" -> Color(0xFFFF8C00)
        "Green"  -> Color(0xFF1B5E20)
        "Blue"   -> Color(0xFF0D47A1)
        "Brown"  -> Color(0xFF4E342E)
        else     -> Color(0xFF1A2B55)
    }
}

private fun roleLabel(role: UserRole): String = when (role) {
    UserRole.JUDOKA  -> "Judoka"
    UserRole.COACH   -> "Coach"
    UserRole.REFEREE -> "Referee"
    UserRole.UNKNOWN -> "Unknown"
}

private fun getInitials(fullName: String): String {
    val parts = fullName.trim().split(" ")
    return when {
        parts.size >= 2 && parts[1].isNotEmpty() -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 && parts[0].isNotEmpty() -> parts[0].first().uppercase()
        else                                     -> "?"
    }
}


private fun formatDateOfBirth(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return when {
        digits.length == 8 -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
        raw.contains("/")  -> raw 
        else               -> raw
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userRole: UserRole = UserRole.JUDOKA,
    navController: NavController? = null,
    vm: ProfileViewModel = hiltViewModel()
) {
    val user      by vm.user.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    var showLogout by remember { mutableStateOf(false) }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            containerColor   = SurfaceBg,
            title = {
                Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to sign out?", color = Color.LightGray)
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.signOut()
                        navController?.navigate(NavScreen.LOGIN.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    Text("Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) {
                    Text("Cancel", color = Gold)
                }
            }
        )
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Profile",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                        Text(
                            "myJudo Companion",
                            color    = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBg)
            )
        },
        bottomBar = {
            if (navController != null) {
                BottomNavBar(userRole = userRole, navController = navController)
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
                    shape    = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val belt = user?.beltGrade ?: "White"

                        
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(beltAvatarBg(belt))
                                .then(
                                    if (belt == "White")
                                        Modifier.border(1.dp, Color.Gray, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = getInitials(user?.fullName ?: ""),
                                color      = beltTagText(belt),
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            user?.fullName ?: "—",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            user?.email ?: "—",
                            color    = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(10.dp))

                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Gold.copy(alpha = 0.15f))
                                .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(
                                roleLabel(userRole),
                                color      = Gold,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                
                ProfileInfoCard(title = "Grade") {
                    val belt = user?.beltGrade ?: "White"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(beltTagBg(belt))
                            .then(
                                if (belt.startsWith("Black"))
                                    Modifier.border(
                                        0.5.dp,
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(6.dp)
                                    )
                                else Modifier
                            )
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            belt,
                            color      = beltTagText(belt),
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                    }
                }

                
                ProfileInfoCard(title = "Details") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileRow(
                            label = "Club",
                            value = user?.judoClub?.ifEmpty { "—" } ?: "—"
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                        ProfileRow(
                            label = "Date of Birth",
                            value = formatDateOfBirth(user?.dateOfBirth ?: "—")
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                
                Button(
                    onClick  = { showLogout = true },
                    colors   = ButtonDefaults.buttonColors(containerColor = AppRed),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sign Out",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                color      = Gold,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = Color.LightGray, fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}