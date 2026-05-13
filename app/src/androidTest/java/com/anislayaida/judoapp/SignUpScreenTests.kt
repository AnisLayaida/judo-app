package com.anislayaida.judoapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
class SignUpScreenTests {

    private val VALID_FULL_NAME = "Anis Layaida"
    private val VALID_EMAIL     = "anis@judo.com"
    private val VALID_PASSWORD  = "passwordpassword"

    lateinit var createAccountMatcher: SemanticsMatcher
    lateinit var fullNameMatcher:      SemanticsMatcher
    lateinit var emailMatcher:         SemanticsMatcher
    lateinit var passwordMatcher:      SemanticsMatcher
    lateinit var registerMatcher:      SemanticsMatcher
    lateinit var signInLinkMatcher:    SemanticsMatcher

    @Inject lateinit var authRepo: AuthRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        createAccountMatcher = hasText("Create an Account") and hasClickAction()
        fullNameMatcher      = hasText("Your full name")
        emailMatcher         = hasText("your@email.com")
        passwordMatcher      = hasText("••••••••")
        registerMatcher      = hasText("Register")  and hasClickAction()
        signInLinkMatcher    = hasText("Sign in")   and hasClickAction()
        rule.onNode(createAccountMatcher).performClick()
    }

    @Test
    fun `sign up screen shows create account header`() {
        rule.onNodeWithText("Create Account").assertIsDisplayed()
    }

    @Test
    fun `sign up screen shows all expected fields`() {
        rule.onNode(fullNameMatcher).assertExists()
        rule.onNode(emailMatcher).assertExists()
        rule.onNode(passwordMatcher).assertExists()
        rule.onNode(registerMatcher).assertExists()
    }

    @Test
    fun `register button is displayed and clickable`() {
        rule.onNode(registerMatcher).assertIsDisplayed()
        rule.onNode(registerMatcher).assertHasClickAction()
    }

    @Test
    fun `sign in link navigates back to login screen`() {
        rule.onNode(signInLinkMatcher).performClick()
        rule.onNodeWithText("myJudo").assertIsDisplayed()
    }

    @Test
    fun `full name field accepts typed input`() {
        rule.onNode(fullNameMatcher).performTextInput(VALID_FULL_NAME)
        rule.onNodeWithText(VALID_FULL_NAME).assertExists()
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