package com.example.vbpathshala.navigation

/**
 * Defines all routes (navigation destinations) in the Pathshala App.
 * Use these sealed class objects to navigate between screens.
 */
sealed class Screen(val route: String) {

    // Auth-related screens
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object Admin : Screen("admin")
    object StudentDetail : Screen("student-detail")
    object Notes : Screen("notes")

    // Main app screens
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Courses : Screen("courses")
    object Teachers : Screen("teachers")
}
