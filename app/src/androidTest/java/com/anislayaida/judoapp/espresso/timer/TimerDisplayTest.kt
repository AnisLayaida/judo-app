package com.anislayaida.judoapp.espresso.timer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.screens.timer.TimerScreen
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerDisplayTest {

    @get:Rule val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { JudoAppTheme { TimerScreen() } }
    }

    @Test
    fun timer_displays_four_minutes_on_launch() {
        rule.onAllNodes(hasText("4:00")).onFirst().assertIsDisplayed()
    }

    @Test
    fun timer_shows_ready_state_on_launch() {
        rule.onNodeWithText("準備  ·  Ready").assertIsDisplayed()
    }

    @Test
    fun match_duration_card_is_visible() {
        rule.onNodeWithText("BJA Match Times").assertIsDisplayed()
    }
}