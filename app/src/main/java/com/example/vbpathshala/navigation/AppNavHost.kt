package com.example.vbpathshala.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vbpathshala.viewmodel.AuthViewModel


@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel) {

    NavHost(
        navController = navController,
        startDestination = com.example.vbpathshala.navigation.Screen.Landing.route
    ) {
        composable(com.example.vbpathshala.navigation.Screen.Landing.route) {
            com.example.vbpathshala.ui.landing.LandingScreen(navController)
        }

        composable(com.example.vbpathshala.navigation.Screen.Home.route) {
            com.example.vbpathshala.ui.studentpanel.HomeScreen(navController)
        }
        composable(com.example.vbpathshala.navigation.Screen.Admin.route) {
            com.example.vbpathshala.ui.admin.AdminScreen(navController)
        }


        composable(com.example.vbpathshala.navigation.Screen.Login.route) {
            com.example.vbpathshala.ui.auth.login.LoginScreen(navController)
        }

        composable(com.example.vbpathshala.navigation.Screen.Register.route) {
            com.example.vbpathshala.ui.auth.register.RegisterScreen(navController)
        }
        composable(com.example.vbpathshala.navigation.Screen.StudentDetail.route) {
            com.example.vbpathshala.ui.studentpanel.studentdetail.StudentDetailScreen(navController)
        }
        composable(com.example.vbpathshala.navigation.Screen.Notes.route) {
            com.example.vbpathshala.ui.studentpanel.notes.NotesScreen(navController)
        }
    }

}