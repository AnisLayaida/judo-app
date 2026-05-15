package com.anislayaida.judoapp

import com.anislayaida.judoapp.espresso.components.CustomButtonTest
import com.anislayaida.judoapp.espresso.components.CustomTextFieldTest
import com.anislayaida.judoapp.espresso.components.TechniqueRowTest
import com.anislayaida.judoapp.espresso.login.LoginDisplayTest
import com.anislayaida.judoapp.espresso.login.LoginInputTest
import com.anislayaida.judoapp.espresso.login.LoginNavigationTest
import com.anislayaida.judoapp.espresso.login.LoginValidationTest
import com.anislayaida.judoapp.espresso.navigation.CoachNavTest
import com.anislayaida.judoapp.espresso.navigation.JudokaNavTest
import com.anislayaida.judoapp.espresso.navigation.RefereeNavTest
import com.anislayaida.judoapp.espresso.referee.RefereeControlsTest
import com.anislayaida.judoapp.espresso.referee.RefereeDisplayTest
import com.anislayaida.judoapp.espresso.referee.RefereeScoreTest
import com.anislayaida.judoapp.espresso.signup.SignUpDisplayTest
import com.anislayaida.judoapp.espresso.signup.SignUpInputTest
import com.anislayaida.judoapp.espresso.signup.SignUpNavigationTest
import com.anislayaida.judoapp.espresso.signup.SignUpValidationTest
import com.anislayaida.judoapp.espresso.timer.TimerControlsTest
import com.anislayaida.judoapp.espresso.timer.TimerDisplayTest
import com.anislayaida.judoapp.espresso.timer.TimerStateTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CustomButtonTest::class,
    CustomTextFieldTest::class,
    TechniqueRowTest::class,
    LoginDisplayTest::class,
    LoginInputTest::class,
    LoginNavigationTest::class,
    LoginValidationTest::class,
    SignUpDisplayTest::class,
    SignUpInputTest::class,
    SignUpNavigationTest::class,
    SignUpValidationTest::class,
    JudokaNavTest::class,
    CoachNavTest::class,
    RefereeNavTest::class,
    RefereeDisplayTest::class,
    RefereeControlsTest::class,
    RefereeScoreTest::class,
    TimerDisplayTest::class,
    TimerControlsTest::class,
    TimerStateTest::class
)
class TestSuite