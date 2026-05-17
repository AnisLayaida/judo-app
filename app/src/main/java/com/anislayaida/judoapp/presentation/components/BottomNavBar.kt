package com.anislayaida.judoapp.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.navigation.NavScreen

data class BottomNavItem(
    val screen: NavScreen,
    val label: String,
    val icon: ImageVector
)

internal fun itemsForRole(userRole: UserRole): List<BottomNavItem> = when (userRole) {
    UserRole.JUDOKA -> listOf(
        BottomNavItem(NavScreen.HOME,    "Syllabus", Icons.Default.List),
        BottomNavItem(NavScreen.GRADING, "Grading",  Icons.Default.Star),
        BottomNavItem(NavScreen.TIMER,   "Timer",    Icons.Default.DateRange),
        BottomNavItem(NavScreen.PROFILE, "Profile",  Icons.Default.Person)
    )
    UserRole.COACH -> listOf(
        BottomNavItem(NavScreen.COACH_HOME, "Library", Icons.Default.Home),
        BottomNavItem(NavScreen.TIMER,      "Timer",   Icons.Default.DateRange),
        BottomNavItem(NavScreen.PROFILE,    "Profile", Icons.Default.Person)
    )
    UserRole.REFEREE -> listOf(
        BottomNavItem(NavScreen.REFEREE_HOME, "Score",   Icons.Default.Star),
        BottomNavItem(NavScreen.TIMER,        "Timer",   Icons.Default.DateRange),
        BottomNavItem(NavScreen.PROFILE,      "Profile", Icons.Default.Person)
    )
    UserRole.UNKNOWN -> emptyList()
}

@Composable
fun BottomNavBar(
    userRole: UserRole,
    navController: NavController
) {
    val items = itemsForRole(userRole)
    if (items.isEmpty()) return

    NavigationBar(
        containerColor = Color(0xFF003087),
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = isSelected,
                label = { Text(item.label, fontSize = 9.sp, color = Color.White) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFFFFD700) else Color.White
                    )
                },
                onClick = {
                    navController.navigate(item.screen.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}