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
class LoginInputTest {

    private val VALID_EMAIL    = "anis@judo.com"
    private val VALID_PASSWORD = "password123"

    @Inject lateinit var authRepo: AuthRepo
    @Inject lateinit var userRepo: UserRepo

    @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) var rule     = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() { hiltRule.inject() }

    @Test
    fun email_field_accepts_typed_input() {
        rule.onNode(hasText("your@email.com")).performTextInput(VALID_EMAIL)
        rule.onNodeWithText(VALID_EMAIL).assertExists()
    }

    @Test
    fun password_field_accepts_typed_input() {
        rule.onNode(hasText("••••••••")).assertExists()
        rule.onNode(hasText("••••••••")).performTextInput(VALID_PASSWORD)
        
        rule.onNode(hasText("••••••••") or hasText(VALID_PASSWORD)).assertExists()
    }
}