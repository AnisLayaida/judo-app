package com.anislayaida.judoapp.espresso.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.navigation.NavScreen
import com.anislayaida.judoapp.presentation.components.BottomNavBar
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class JudokaNavTest {

    @get:Rule val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent {
            JudoAppTheme {
                val navController = rememberNavController()
                Column {
                    NavHost(navController = navController, startDestination = NavScreen.HOME.route) {
                        composable(NavScreen.HOME.route)    { Text("Syllabus screen") }
                        composable(NavScreen.GRADING.route) { Text("Grading screen") }
                        composable(NavScreen.TIMER.route)   { Text("Timer screen") }
                        composable(NavScreen.PROFILE.route) { Text("Profile screen") }
                    }
                    BottomNavBar(userRole = UserRole.JUDOKA, navController = navController)
                }
            }
        }
    }

    @Test
    fun judoka_sees_syllabus_tab() {
        rule.onNodeWithText("Syllabus").assertIsDisplayed()
    }

    @Test
    fun judoka_sees_grading_tab() {
        rule.onNodeWithText("Grading").assertIsDisplayed()
    }

    @Test
    fun judoka_sees_timer_tab() {
        rule.onNodeWithText("Timer").assertIsDisplayed()
    }

    @Test
    fun judoka_sees_profile_tab() {
        rule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun judoka_does_not_see_coach_library_tab() {
        rule.onNodeWithText("Library").assertDoesNotExist()
    }

    @Test
    fun judoka_does_not_see_referee_score_tab() {
        rule.onNodeWithText("Score").assertDoesNotExist()
    }
}