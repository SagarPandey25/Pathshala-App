package com.example.vbpathshala.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vbpathshala.navigation.Screen

@Composable
fun LandingScreen(navController: NavHostController) {

    // 🌈 Background Gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF8E2DE2),
            Color(0xFF4A00E0),
            Color(0xFF00C6FF)
        )
    )

    val loginGradient = Brush.horizontalGradient(
        listOf(Color(0xFFFF512F), Color(0xFFDD2476))
    )

    val registerGradient = Brush.horizontalGradient(
        listOf(Color(0xFF00B09B), Color(0xFF96C93D))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "VB Pathshala 🎓",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4A00E0),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Learn • Grow • Succeed",
                fontSize = 15.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 🔴 LOGIN BUTTON
            GradientButton(
                text = "Login",
                gradient = loginGradient
            ) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Landing.route) { inclusive = true }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🟢 REGISTER BUTTON
            GradientButton(
                text = "Create Account",
                gradient = registerGradient
            ) {
                navController.navigate(Screen.Register.route)
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(gradient, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
/* ------------------student SCREEN ------------------ */

@Composable
fun StudentHomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎓 Welcome Student",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/* ------------------ ADMIN SCREEN ------------------ */

@Composable
fun AdminHomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🛠 Welcome Admin",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
