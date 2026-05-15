package com.anislayaida.judoapp.espresso.referee

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.screens.referee.RefereeScreen
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RefereeDisplayTest {

    @get:Rule val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { JudoAppTheme { RefereeScreen() } }
    }

    @Test
    fun referee_board_shows_initial_timer() {
        rule.onNodeWithText("04:00").assertIsDisplayed()
    }

    @Test
    fun referee_board_shows_ready_state_on_launch() {
        rule.onNodeWithText("準備 · Ready").assertIsDisplayed()
    }

    @Test
    fun white_and_blue_score_panels_are_displayed() {
        rule.onNodeWithText("White").assertIsDisplayed()
        rule.onNodeWithText("Blue").assertIsDisplayed()
    }
}