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
class SignUpNavigationTest {

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
    fun sign_in_link_navigates_back_to_login_screen() {
        rule.onNodeWithText("Sign in").performScrollTo()
        rule.waitForIdle()
        rule.onNodeWithText("Sign in").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("myJudo").assertIsDisplayed()
    }
}