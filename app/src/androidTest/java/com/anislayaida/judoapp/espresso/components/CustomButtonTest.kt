package com.anislayaida.judoapp.espresso.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.components.CustomButton
import org.junit.Rule
import org.junit.Test

class CustomButtonTest {

    @get:Rule
    val rule = createComposeRule()

    private val TEXT_DISPLAY  = "Test Button"
    private val buttonMatcher = hasText(TEXT_DISPLAY) and hasClickAction()

    @Test
    fun button_displays_text_and_is_enabled_by_default() {
        rule.setContent { CustomButton(text = TEXT_DISPLAY, onClick = {}) }
        rule.onNode(buttonMatcher).assertExists()
        rule.onNode(buttonMatcher).assertIsEnabled()
    }

    @Test
    fun button_is_not_enabled_when_enabled_is_false() {
        rule.setContent { CustomButton(text = TEXT_DISPLAY, onClick = {}, enabled = false) }
        rule.onNode(buttonMatcher).assertIsNotEnabled()
    }

    @Test
    fun button_executes_callback_when_clicked() {
        var clicked = false
        rule.setContent { CustomButton(text = TEXT_DISPLAY, onClick = { clicked = true }) }
        rule.onNode(buttonMatcher).performClick()
        assert(clicked)
    }
}