package com.anislayaida.judoapp.navigation

enum class NavScreen(val route: String) {
    LOGIN("login"),
    SIGNUP("signup"),
    HOME("home"),
    COACH_HOME("coach_home"),
    ADD_TECHNIQUE("add_technique"),
    TECHNIQUE_DETAIL("technique_detail"),
    EDIT_TECHNIQUE("technique_detail/edit"),
    GRADING("grading"),
    TIMER("timer"),
    PROFILE("profile"),
    REFEREE_HOME("referee_home")
}