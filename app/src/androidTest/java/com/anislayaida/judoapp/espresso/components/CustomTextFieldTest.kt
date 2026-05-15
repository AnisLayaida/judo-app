package com.anislayaida.judoapp.espresso.components

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.presentation.components.CustomTextField
import org.junit.Rule
import org.junit.Test

class CustomTextFieldTest {

    @get:Rule
    val rule = createComposeRule()

    private val LABEL      = "Email"
    private val INPUT_TEXT = "anis@judo.com"

    @Test
    fun text_field_displays_label() {
        rule.setContent {
            var value by remember { mutableStateOf("") }
            CustomTextField(value = value, onValueChange = { value = it }, label = LABEL)
        }
        rule.onNodeWithText(LABEL).assertExists()
    }

    @Test
    fun text_field_accepts_and_displays_typed_input() {
        rule.setContent {
            var value by remember { mutableStateOf("") }
            CustomTextField(value = value, onValueChange = { value = it }, label = LABEL)
        }
        rule.onNodeWithText(LABEL).performTextInput(INPUT_TEXT)
        rule.onNodeWithText(INPUT_TEXT).assertExists()
    }

    @Test
    fun text_field_is_enabled_by_default() {
        rule.setContent {
            var value by remember { mutableStateOf("") }
            CustomTextField(value = value, onValueChange = { value = it }, label = LABEL)
        }
        rule.onNodeWithText(LABEL).assertIsEnabled()
    }
}