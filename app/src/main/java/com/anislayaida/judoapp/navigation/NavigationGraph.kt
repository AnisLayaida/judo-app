package com.anislayaida.judoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.anislayaida.judoapp.data.user.UserRole

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val userRole by authViewModel.userRole.collectAsStateWithLifecycle()

    NavHost(
        navController    = navController,
        startDestination = NavScreen.LOGIN.route
    ) {

        composable(NavScreen.LOGIN.route) {
            LoginScreen(
                navigateToSignUpScreen = {
                    navController.navigate(NavScreen.SIGNUP.route)
                },
                navigateToHomeScreen = {
                    when (userRole) {
                        UserRole.COACH   -> navController.navigate(NavScreen.COACH_HOME.route)
                        UserRole.REFEREE -> navController.navigate(NavScreen.REFEREE_HOME.route)
                        else             -> navController.navigate(NavScreen.HOME.route)
                    }
                },
                updateRoleForUser = { newRole -> authViewModel.updateRole(newRole) },
                modifier = modifier
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
                navController = navController
            )
        }

        composable(NavScreen.COACH_HOME.route) {
            CoachHomeScreen(
                text          = "Coach Panel",
                userRole      = userRole,
                navController = navController,
                modifier      = modifier
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
                navController = navController
            )
        }

        composable(NavScreen.TIMER.route) {
            TimerScreen(
                userRole      = userRole,
                navController = navController
            )
        }

        composable(NavScreen.PROFILE.route) {
            ProfileScreen(
                userRole      = userRole,
                navController = navController
            )
        }

        composable(NavScreen.REFEREE_HOME.route) {
            RefereeScreen(
                userRole      = userRole,
                navController = navController
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