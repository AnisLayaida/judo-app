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
class LoginValidationTest {

    @Inject lateinit var authRepo: AuthRepo
    @Inject lateinit var userRepo: UserRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() { hiltRule.inject() }

    @Test
    fun clicking_sign_in_with_no_input_stays_on_login_screen() {
        rule.onNode(hasText("Sign In") and hasClickAction()).performClick()
        rule.waitForIdle()
        rule.onNodeWithText("myJudo").assertIsDisplayed()
    }

    @Test
    fun sign_in_button_is_enabled_when_app_is_not_loading() {
        rule.onNode(hasText("Sign In") and hasClickAction()).assertIsEnabled()
    }
}