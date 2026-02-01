package com.example.vbpathshala.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vbpathshala.data.session.SessionManager
import com.example.vbpathshala.viewmodel.AuthViewModel

@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    val startDestination = remember {
        if (SessionManager.isLoggedIn(context)) {
            val user = SessionManager.getUser(context)
            if (user.role == "Admin") Screen.Admin.route else Screen.Home.route
        } else {
            Screen.Landing.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Landing.route) {
            com.example.vbpathshala.ui.landing.LandingScreen(navController)
        }
        composable(Screen.Login.route) {
            com.example.vbpathshala.ui.auth.login.LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            com.example.vbpathshala.ui.auth.register.RegisterScreen(navController)
        }
        composable(Screen.Home.route) {
            com.example.vbpathshala.ui.studentpanel.HomeScreen(navController)
        }
        composable(Screen.Admin.route) {
            com.example.vbpathshala.ui.admin.AdminScreen(navController)
        }
        composable(Screen.StudentDetail.route) {
            com.example.vbpathshala.ui.studentpanel.studentdetail.StudentDetailScreen(navController)
        }
        composable(Screen.Notes.route) {
            com.example.vbpathshala.ui.studentpanel.notes.NotesScreen(navController)
        }
    }
}