package com.example.vbpathshala.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {

    val primaryBlue = Color(0xFF0D47A1)
    val lightBlue = Color(0xFF1976D2)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryBlue, lightBlue)
                        ),
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 🔙 Back Arrow
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 🏷️ Title
                    Text(
                        text = "STUDENT BLOCK",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 🔔 Notification Icon
                    IconButton(onClick = { navController.navigate("notification_route") }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Row 1 ----------
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DashboardCard(
                    title = "Student Details",
                    icon = Icons.Default.AccountCircle,
                    color = primaryBlue,
                    onClick = { navController.navigate("student_details_route") }
                )
                DashboardCard(
                    title = "Performance",
                    icon = Icons.Default.School,
                    color = primaryBlue,
                    onClick = { navController.navigate("performance_route") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Row 2 ----------
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DashboardCard(
                    title = "Fees Details",
                    icon = Icons.Default.AttachMoney,
                    color = primaryBlue,
                    onClick = { navController.navigate("fees_route") }
                )
                DashboardCard(
                    title = "Notes",
                    icon = Icons.Default.Description,
                    color = primaryBlue,
                    onClick = { navController.navigate("notes_route") }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ---------- LOGOUT ----------
            Button(
                onClick = {
                    // ✅ Clear backstack and go to login
                    navController.navigate("login_route") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "LOG OUT",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RowScope.DashboardCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}
