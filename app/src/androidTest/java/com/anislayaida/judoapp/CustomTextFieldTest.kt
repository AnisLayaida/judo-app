package com.anislayaida.judoapp

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
    fun `text field displays label`() {
        rule.setContent {
            var value by remember { mutableStateOf("") }
            CustomTextField(value = value, onValueChange = { value = it }, label = LABEL)
        }
        rule.onNodeWithText(LABEL).assertExists()
    }

    @Test
    fun `text field accepts and displays typed input`() {
        rule.setContent {
            var value by remember { mutableStateOf("") }
            CustomTextField(value = value, onValueChange = { value = it }, label = LABEL)
        }
        rule.onNodeWithText(LABEL).performTextInput(INPUT_TEXT)
        rule.onNodeWithText(INPUT_TEXT).assertExists()
    }

    @Test
    fun `text field is enabled by default`() {
        rule.setContent {
            var value by remember { mutableStateOf("") }
            CustomTextField(value = value, onValueChange = { value = it }, label = LABEL)
        }
        rule.onNodeWithText(LABEL).assertIsEnabled()
    }
}