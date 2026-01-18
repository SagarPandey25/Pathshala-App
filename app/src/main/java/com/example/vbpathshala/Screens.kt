package com.example.vbpathshala

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Admin : Screen("admin")
}
