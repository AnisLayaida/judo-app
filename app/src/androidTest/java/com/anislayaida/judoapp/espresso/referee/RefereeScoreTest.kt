package com.anislayaida.judoapp.espresso.referee

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.screens.referee.RefereeScreen
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RefereeScoreTest {

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        rule.setContent { JudoAppTheme { RefereeScreen() } }
    }

    @Test
    fun white_ippon_ends_match_with_white_winning() {
        rule.onAllNodes(hasText("Ippon") and hasClickAction()).onFirst().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("一本 · White wins!").assertIsDisplayed()
    }

    @Test
    fun blue_ippon_ends_match_with_blue_winning() {
        rule.onAllNodes(hasText("Ippon") and hasClickAction()).onLast().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("一本 · Blue wins!").assertIsDisplayed()
    }
}