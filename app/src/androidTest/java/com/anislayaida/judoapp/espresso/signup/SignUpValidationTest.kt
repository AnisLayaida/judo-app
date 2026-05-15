package com.anislayaida.judoapp.espresso.signup

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.anislayaida.judoapp.MainActivity
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.di.FirebaseModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(FirebaseModule::class)
class SignUpValidationTest {

    @Inject lateinit var authRepo: AuthRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        rule.onNode(hasText("Create an Account") and hasClickAction()).performClick()
    }

    @Test
    fun clicking_register_with_no_input_stays_on_sign_up_screen() {
        rule.onNode(hasText("Register") and hasClickAction()).performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Create Account").assertIsDisplayed()
    }

    @Test
    fun register_button_is_enabled_when_app_is_not_loading() {
        rule.onNode(hasText("Register") and hasClickAction()).assertIsEnabled()
    }
}