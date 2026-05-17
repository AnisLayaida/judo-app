package com.anislayaida.judoapp.espresso.timer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.screens.timer.TimerScreen
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerStateTest {

    @get:Rule val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { JudoAppTheme { TimerScreen() } }
    }

    @Test
    fun clicking_start_changes_status_to_match_running() {
        rule.onNode(hasText("Start") and hasClickAction()).performClick()
        rule.waitForIdle()
        rule.onNodeWithText("試合中  ·  Match Running").assertIsDisplayed()
    }

    @Test
    fun clicking_start_changes_button_label_to_pause() {
        rule.onNode(hasText("Start") and hasClickAction()).performClick()
        rule.waitForIdle()
        rule.onNode(hasText("Pause") and hasClickAction()).assertIsDisplayed()
    }

    @Test
    fun reset_after_starting_returns_to_ready_state() {
        rule.onNode(hasText("Start") and hasClickAction()).performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Reset").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("準備  ·  Ready").assertIsDisplayed()
    }

    @Test
    fun reset_after_starting_restores_start_button() {
        rule.onNode(hasText("Start") and hasClickAction()).performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Reset").performClick()
        rule.waitForIdle()
        rule.onNode(hasText("Start") and hasClickAction()).assertIsDisplayed()
    }
}