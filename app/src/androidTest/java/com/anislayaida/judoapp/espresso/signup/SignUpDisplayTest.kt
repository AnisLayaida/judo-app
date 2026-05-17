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
class SignUpDisplayTest {

    @Inject lateinit var authRepo: AuthRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        rule.onNode(hasText("Create an Account") and hasClickAction()).performClick()
        rule.waitForIdle()
    }

    @Test
    fun sign_up_screen_shows_create_account_header() {
        rule.onNodeWithText("Create Account").assertIsDisplayed()
    }

    @Test
    fun sign_up_screen_shows_all_expected_fields() {
        rule.onNode(hasText("Your full name")).assertExists()
        rule.onNode(hasText("your@email.com")).assertExists()
        rule.onNode(hasText("••••••••")).assertExists()
        rule.onNode(hasText("Register") and hasClickAction()).assertExists()
    }

    @Test
    fun register_button_is_displayed_and_clickable() {
        rule.onNode(hasText("Register") and hasClickAction()).performScrollTo()
        rule.waitForIdle()
        rule.onNode(hasText("Register") and hasClickAction()).assertIsDisplayed()
        rule.onNode(hasText("Register") and hasClickAction()).assertHasClickAction()
    }
}