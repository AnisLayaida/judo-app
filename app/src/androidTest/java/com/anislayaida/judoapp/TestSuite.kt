package com.anislayaida.judoapp

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CustomButtonTest::class,
    CustomTextFieldTest::class,
    LoginScreenTests::class,
    SignUpScreenTests::class
)
class TestSuite