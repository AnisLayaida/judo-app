package com.anislayaida.judoapp.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anislayaida.judoapp.data.user.UserRole

@Composable
fun JudoNavRail(
    userRole: UserRole,
    navController: NavController
) {
    val items = itemsForRole(userRole)
    if (items.isEmpty()) return

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationRail(
        containerColor = Color(0xFF1A2B55),
        contentColor = Color.White
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        items.forEach { item ->
            val isSelected = currentRoute == item.screen.route

            NavigationRailItem(
                selected = isSelected,
                label = {
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        color = if (isSelected) Color(0xFFC9A84C) else Color.White
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFFC9A84C) else Color.White
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = Color(0xFF0D1B3E)
                ),
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