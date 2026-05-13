package com.anislayaida.judoapp.presentation.screens.profile

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

private fun beltColor(belt: String): Color = when {
    belt.startsWith("Black") -> Color(0xFF1A1A1A)
    else -> when (belt) {
        "White"  -> Color(0xFFDDDDDD)
        "Red"    -> Color(0xFFCC0000)
        "Yellow" -> Color(0xFFFFD700)
        "Orange" -> Color(0xFFFF6600)
        "Green"  -> Color(0xFF22C55E)
        "Blue"   -> Color(0xFF1A6EBF)
        "Brown"  -> Color(0xFF6B3A2A)
        else     -> Color.Gray
    }
}

private fun beltTextColor(belt: String): Color = when {
    belt.startsWith("Black")                    -> Color(0xFFFFD700)
    belt == "White"                             -> Color(0xFF333333)
    belt == "Yellow"                            -> Color(0xFF333333)
    else                                        -> Color.White
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
        parts.size >= 2              -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 && parts[0].isNotEmpty() -> parts[0].first().uppercase()
        else                         -> "?"
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
                    Text("Profile", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
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

                // ── Avatar + name ─────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Belt-coloured initials avatar
                        val belt = user?.beltGrade ?: "White"
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape    = CircleShape,
                            color    = beltColor(belt)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text       = getInitials(user?.fullName ?: ""),
                                    color      = beltTextColor(belt),
                                    fontSize   = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            user?.fullName ?: "—",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            user?.email ?: "—",
                            color    = Color.LightGray,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Gold.copy(alpha = 0.15f)
                        ) {
                            Text(
                                roleLabel(userRole),
                                modifier   = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color      = Gold,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ── Belt grade ────────────────────────────────────
                ProfileInfoCard(title = "Grade") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = beltColor(belt = user?.beltGrade ?: "White")
                    ) {
                        Text(
                            user?.beltGrade ?: "White",
                            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            color      = beltTextColor(user?.beltGrade ?: "White"),
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                    }
                }

                // ── Details ───────────────────────────────────────
                ProfileInfoCard(title = "Details") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProfileRow(label = "Club",          value = user?.judoClub ?: "—")
                        ProfileRow(label = "Date of Birth", value = user?.dateOfBirth ?: "—")
                    }
                }

                Spacer(Modifier.height(4.dp))

                // ── Sign out ──────────────────────────────────────
                Button(
                    onClick  = { showLogout = true },
                    colors   = ButtonDefaults.buttonColors(containerColor = AppRed),
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
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
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray, fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}