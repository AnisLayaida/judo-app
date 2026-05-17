package com.anislayaida.judoapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.presentation.components.BottomNavBar
import com.anislayaida.judoapp.presentation.components.JudoNavRail
import com.anislayaida.judoapp.presentation.screens.addTechnique.AddTechniqueScreen
import com.anislayaida.judoapp.presentation.screens.coachEditTechnique.EditTechniqueScreen
import com.anislayaida.judoapp.presentation.screens.coachHome.CoachHomeScreen
import com.anislayaida.judoapp.presentation.screens.grading.GradingScreen
import com.anislayaida.judoapp.presentation.screens.home.HomeScreen
import com.anislayaida.judoapp.presentation.screens.login.LoginScreen
import com.anislayaida.judoapp.presentation.screens.profile.ProfileScreen
import com.anislayaida.judoapp.presentation.screens.referee.RefereeScreen
import com.anislayaida.judoapp.presentation.screens.signup.SignUpScreen
import com.anislayaida.judoapp.presentation.screens.techniqueDetail.TechniqueDetailScreen
import com.anislayaida.judoapp.presentation.screens.timer.TimerScreen

private val NAV_CHROME_ROUTES = setOf(
    NavScreen.HOME.route,
    NavScreen.COACH_HOME.route,
    NavScreen.GRADING.route,
    NavScreen.TIMER.route,
    NavScreen.PROFILE.route,
    NavScreen.REFEREE_HOME.route
)

private val Gold = Color(0xFFC9A84C)

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowSizeClass
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val userRole        by authViewModel.userRole.collectAsStateWithLifecycle()
    val startDestination by authViewModel.startDestination.collectAsStateWithLifecycle()

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route
    val showNavChrome     = currentRoute in NAV_CHROME_ROUTES

    if (startDestination == null) {
        Box(
            modifier         = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Gold)
        }
        return
    }

    val appNavHost: @Composable (Modifier) -> Unit = { navModifier ->
        NavHost(
            navController    = navController,
            startDestination = startDestination!!,
            modifier         = navModifier
        ) {
            composable(NavScreen.LOGIN.route) {
                LoginScreen(
                    navigateToSignUpScreen = {
                        navController.navigate(NavScreen.SIGNUP.route)
                    },
                    navigateToHomeScreen = { role ->
                        when (role) {
                            UserRole.COACH   -> navController.navigate(NavScreen.COACH_HOME.route)
                            UserRole.REFEREE -> navController.navigate(NavScreen.REFEREE_HOME.route)
                            else             -> navController.navigate(NavScreen.HOME.route)
                        }
                    },
                    updateRoleForUser = { newRole -> authViewModel.updateRole(newRole) },
                    modifier          = modifier
                )
            }

            composable(NavScreen.SIGNUP.route) {
                SignUpScreen(
                    navigateBack = { navController.popBackStack() },
                    modifier     = modifier
                )
            }

            composable(NavScreen.HOME.route) {
                HomeScreen(
                    userRole      = userRole,
                    navController = navController,
                    isCompact     = isCompact
                )
            }

            composable(NavScreen.COACH_HOME.route) {
                CoachHomeScreen(
                    text          = "Coach Panel",
                    userRole      = userRole,
                    navController = navController,
                    modifier      = modifier,
                    isCompact     = isCompact
                )
            }

            composable(
                route     = "${NavScreen.TECHNIQUE_DETAIL.route}/{techniqueId}",
                arguments = listOf(navArgument("techniqueId") { type = NavType.StringType })
            ) { backStackEntry ->
                val techniqueId = backStackEntry.arguments?.getString("techniqueId") ?: ""
                TechniqueDetailScreen(
                    techniqueId   = techniqueId,
                    navController = navController
                )
            }

            composable(NavScreen.GRADING.route) {
                GradingScreen(
                    userRole      = userRole,
                    navController = navController,
                    isCompact     = isCompact
                )
            }

            composable(NavScreen.TIMER.route) {
                TimerScreen(
                    userRole      = userRole,
                    navController = navController,
                    isCompact     = isCompact
                )
            }

            composable(NavScreen.PROFILE.route) {
                ProfileScreen(
                    userRole      = userRole,
                    navController = navController,
                    isCompact     = isCompact
                )
            }

            composable(NavScreen.REFEREE_HOME.route) {
                RefereeScreen(
                    userRole      = userRole,
                    navController = navController,
                    isCompact     = isCompact
                )
            }

            composable(NavScreen.ADD_TECHNIQUE.route) {
                AddTechniqueScreen(
                    navController = navController
                )
            }

            composable(
                route     = "${NavScreen.TECHNIQUE_DETAIL.route}/edit/{techniqueId}",
                arguments = listOf(navArgument("techniqueId") { type = NavType.StringType })
            ) { backStackEntry ->
                val techniqueId = backStackEntry.arguments?.getString("techniqueId") ?: ""
                EditTechniqueScreen(
                    techniqueId   = techniqueId,
                    navController = navController
                )
            }
        }
    }
    if (isCompact) {
        Scaffold(
            modifier  = modifier,
            bottomBar = {
                if (showNavChrome) {
                    BottomNavBar(
                        userRole      = userRole,
                        navController = navController
                    )
                }
            }
        ) { innerPadding ->
            appNavHost(Modifier.padding(innerPadding))
        }
    } else {
        Row(modifier = modifier.fillMaxSize()) {
            if (showNavChrome) {
                JudoNavRail(
                    userRole      = userRole,
                    navController = navController
                )
            }
            appNavHost(Modifier.weight(1f))
        }
    }
}