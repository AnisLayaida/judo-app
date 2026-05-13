package com.anislayaida.judoapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
class LoginScreenTests {

    private val VALID_EMAIL    = "anis@judo.com"
    private val VALID_PASSWORD = "password123"

    lateinit var emailMatcher:         SemanticsMatcher
    lateinit var passwordMatcher:      SemanticsMatcher
    lateinit var signInMatcher:        SemanticsMatcher
    lateinit var createAccountMatcher: SemanticsMatcher

    @Inject lateinit var authRepo: AuthRepo
    @Inject lateinit var userRepo: UserRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        emailMatcher         = hasText("your@email.com")
        passwordMatcher      = hasText("••••••••")
        signInMatcher        = hasText("Sign In")         and hasClickAction()
        createAccountMatcher = hasText("Create an Account") and hasClickAction()
    }

    @Test
    fun `login screen shows all expected controls on launch`() {
        rule.onNode(emailMatcher).assertExists()
        rule.onNode(passwordMatcher).assertExists()
        rule.onNode(signInMatcher).assertExists()
        rule.onNode(createAccountMatcher).assertExists()
    }

    @Test
    fun `sign in button is displayed and clickable`() {
        rule.onNode(signInMatcher).assertIsDisplayed()
        rule.onNode(signInMatcher).assertHasClickAction()
    }

    @Test
    fun `myJudo title is displayed on login screen`() {
        rule.onNodeWithText("myJudo").assertIsDisplayed()
    }

    @Test
    fun `create an account button navigates to sign up screen`() {
        rule.onNode(createAccountMatcher).performClick()
        rule.onNode(signInMatcher).assertDoesNotExist()
    }

    @Test
    fun `email field accepts typed input`() {
        rule.onNode(emailMatcher).performTextInput(VALID_EMAIL)
        rule.onNodeWithText(VALID_EMAIL).assertExists()
    }

    @Test
    fun `password field accepts typed input`() {
        rule.onNode(passwordMatcher).performTextInput(VALID_PASSWORD)
        rule.onNode(passwordMatcher).assertExists()
    }
}