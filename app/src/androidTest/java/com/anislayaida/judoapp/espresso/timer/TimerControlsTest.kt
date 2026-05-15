package com.anislayaida.judoapp.espresso.timer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.screens.timer.TimerScreen
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerControlsTest {

    @get:Rule val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { JudoAppTheme { TimerScreen() } }
    }

    @Test
    fun start_button_is_present_and_clickable() {
        rule.onNode(hasText("Start") and hasClickAction()).assertExists()
    }

    @Test
    fun reset_button_is_present() {
        rule.onNodeWithContentDescription("Reset").assertExists()
    }

    @Test
    fun duration_chips_are_displayed() {
        rule.onNode(hasText("4:00") and hasClickAction()).assertExists()
        rule.onNode(hasText("3:00") and hasClickAction()).assertExists()
        rule.onNode(hasText("2:00") and hasClickAction()).assertExists()
        rule.onNode(hasText("1:00") and hasClickAction()).assertExists()
    }
}