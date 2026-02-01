package com.example.vbpathshala.ui.studentpanel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vbpathshala.data.session.SessionManager

// Modernized Color Palette
private val DeepIndigo = Color(0xFF1A237E)
private val ElectricBlue = Color(0xFF3F51B5)
private val SoftLavender = Color(0xFFE8EAF6)
private val GlassWhite = Color.White.copy(alpha = 0.92f)

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val user = remember { SessionManager.getUser(context) }
    val firstName = user.first_name.ifEmpty { "Student" }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- MODERN BACKGROUND PATTERN ---
        // Decorative blurred blobs for a "Mesh" look
        Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
            drawCircle(color = Color(0xFFD1E3FF), radius = 600f, center = center.copy(x = 0f, y = 0f))
            drawCircle(color = Color(0xFFFFE1F3), radius = 500f, center = center.copy(x = size.width, y = size.height * 0.4f))
        }

        Scaffold(
            containerColor = Color.Transparent, // Make Scaffold transparent to show pattern
            topBar = {
                HeaderSection(
                    name = firstName,
                    onNotificationClick = { /* Handle navigation */ }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Learning Dashboard",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = DeepIndigo
                    ),
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item { DashboardTile("My Profile", Icons.Default.Person, Color(0xFF4361EE)) { navController.navigate("student-detail") } }
                    item { DashboardTile("Results", Icons.Default.AutoGraph, Color(0xFF7209B7)) { /* Navigate */ } }
                    item { DashboardTile("Payments", Icons.Default.AccountBalanceWallet, Color(0xFF4CC9F0)) { /* Navigate */ } }
                    item { DashboardTile("Notes", Icons.Default.CollectionsBookmark, Color(0xFFF72585)) { navController.navigate("notes")  } }
                }

                LogoutButton {
                    SessionManager.clearSession(context)
                    navController.navigate("login") { popUpTo(0) }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(name: String, onNotificationClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(DeepIndigo, ElectricBlue)
                    )
                )
                .padding(top = 54.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Welcome,", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    Text(name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }

                // Glassy Notification Icon
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, "Notifications", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun DashboardTile(title: String, icon: ImageVector, accentColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = GlassWhite,
        shadowElevation = 2.dp,
        modifier = Modifier
            .height(150.dp)
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }

            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DeepIndigo,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F1)),
        elevation = null
    ) {
        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text("Sign Out Account", color = Color.Red, fontWeight = FontWeight.SemiBold)
    }
}