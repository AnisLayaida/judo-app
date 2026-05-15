package com.anislayaida.judoapp.espresso.referee

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.screens.referee.RefereeScreen
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RefereeControlsTest {

    @get:Rule val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { JudoAppTheme { RefereeScreen() } }
    }

    @Test
    fun start_button_is_present_and_clickable() {
        rule.onNode(hasText("Start") and hasClickAction()).assertExists()
    }

    @Test
    fun reset_button_is_present_and_clickable() {
        rule.onNode(hasText("Reset") and hasClickAction()).assertExists()
    }

    @Test
    fun scoring_buttons_are_present_for_both_sides() {
        rule.onAllNodes(hasText("Ippon") and hasClickAction())
            .assertCountEquals(2)
    }
}