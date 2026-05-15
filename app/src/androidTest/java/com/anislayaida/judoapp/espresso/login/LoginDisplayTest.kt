package com.anislayaida.judoapp.espresso.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.anislayaida.judoapp.MainActivity
import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.user.UserRepo
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
class LoginDisplayTest {

    @Inject lateinit var authRepo: AuthRepo
    @Inject lateinit var userRepo: UserRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() { hiltRule.inject() }

    @Test
    fun login_screen_shows_all_expected_controls_on_launch() {
        rule.onNode(hasText("your@email.com")).assertExists()
        rule.onNode(hasText("••••••••")).assertExists()
        rule.onNode(hasText("Sign In") and hasClickAction()).assertExists()
        rule.onNode(hasText("Create an Account") and hasClickAction()).assertExists()
    }

    @Test
    fun sign_in_button_is_displayed_and_clickable() {
        rule.onNode(hasText("Sign In") and hasClickAction()).assertIsDisplayed()
        rule.onNode(hasText("Sign In") and hasClickAction()).assertHasClickAction()
    }

    @Test
    fun myJudo_title_is_displayed_on_login_screen() {
        rule.onNodeWithText("myJudo").assertIsDisplayed()
    }
}