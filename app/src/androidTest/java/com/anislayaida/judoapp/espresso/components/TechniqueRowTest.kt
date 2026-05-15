package com.anislayaida.judoapp.espresso.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.presentation.screens.home.TechniqueRow
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import org.junit.Rule
import org.junit.Test

class TechniqueRowTest {

    @get:Rule
    val rule = createComposeRule()

    private val whiteTechnique = Technique(
        uid          = "test-1",
        name         = "O-goshi",
        nameJapanese = "大腰",
        category     = "Nage-waza",
        subcategory  = "Koshi-waza",
        beltLevel    = "White",
        beltOrder    = 0,
        sortOrder    = 1
    )

    private val yellowTechnique = Technique(
        uid          = "test-2",
        name         = "Harai-goshi",
        nameJapanese = "払腰",
        category     = "Nage-waza",
        subcategory  = "Koshi-waza",
        beltLevel    = "Yellow",
        beltOrder    = 2,
        sortOrder    = 4
    )

    @Test
    fun technique_row_displays_english_and_japanese_name() {
        rule.setContent {
            JudoAppTheme { TechniqueRow(technique = whiteTechnique, onClick = {}) }
        }
        rule.onNodeWithText("O-goshi (大腰)").assertIsDisplayed()
    }

    @Test
    fun technique_row_displays_belt_level_tag() {
        rule.setContent {
            JudoAppTheme { TechniqueRow(technique = yellowTechnique, onClick = {}) }
        }
        rule.onNodeWithText("Yellow").assertIsDisplayed()
    }

    @Test
    fun technique_row_displays_subcategory() {
        rule.setContent {
            JudoAppTheme { TechniqueRow(technique = whiteTechnique, onClick = {}) }
        }
        rule.onNodeWithText("Koshi-waza").assertIsDisplayed()
    }

    @Test
    fun technique_row_triggers_click_callback() {
        var clicked = false
        rule.setContent {
            JudoAppTheme {
                TechniqueRow(technique = whiteTechnique, onClick = { clicked = true })
            }
        }
        rule.onNodeWithText("O-goshi (大腰)").performClick()
        assert(clicked)
    }
}