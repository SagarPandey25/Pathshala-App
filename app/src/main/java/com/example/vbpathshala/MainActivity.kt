package com.example.vbpathshala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.vbpathshala.navigation.AppNavHost
import com.example.vbpathshala.ui.theme.VBPathshalaTheme
import com.example.vbpathshala.viewmodel.AuthViewModel


class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VBPathshalaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController, authViewModel = authViewModel)

                }
            }
        }
    }
}
