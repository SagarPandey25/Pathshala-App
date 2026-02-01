package com.example.vbpathshala.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import com.example.vbpathshala.ui.auth.landing.LandingScreen
import com.example.vbpathshala.ui.auth.login.LoginScreen
import com.example.vbpathshala.ui.auth.register.RegisterScreen
import com.example.vbpathshala.ui.studentpanel.HomeScreen
import com.example.vbpathshala.ui.studentpanel.notes.NotesScreen
import com.example.vbpathshala.ui.studentpanel.studentdetail.StudentDetailScreen
import com.example.vbpathshala.viewmodel.AuthViewModel
//import com.example.vbpathshala.ui.home.AdminScreen


@Composable
fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = Screen.Landing.route) {
        composable(Screen.StudentDetail.route) { StudentDetailScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Notes.route) { NotesScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Admin.route) { AdminScreen(navController) }
    }
}

@Composable
fun AdminScreen(x0: NavHostController) {
    TODO("Not yet implemented")
} 
