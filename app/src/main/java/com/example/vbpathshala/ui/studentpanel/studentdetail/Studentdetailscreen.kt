package com.example.vbpathshala.ui.studentpanel.studentdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// Modern Theme Palette
private val PrimaryBlue = Color(0xFF4361EE)
private val SurfaceGray = Color(0xFFF8FAFC)
private val TextMain = Color(0xFF1E293B)
private val TextSub = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(navController: NavHostController) {
    val context = LocalContext.current
    val user = remember { SessionManager.getUser(context) }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = SurfaceGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Student Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- PROFILE HEADER ---
            Spacer(modifier = Modifier.height(20.dp))
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PrimaryBlue, Color(0xFF4CC9F0)))),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulling real initial or icon
                    Text(
                        text = user.first_name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Name & Email
            Text(
                text = "${user.first_name} ${user.last_name}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Text(
                text = user.email,
                fontSize = 14.sp,
                color = TextSub
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- INFORMATION SECTIONS ---
            InfoSection(title = "Academic Details") {
                InfoRow(label = "Student ID", value = user.id, icon = Icons.Default.Badge)
                InfoRow(label = "Current Role", value = user.role, icon = Icons.Default.School)
            }

            Spacer(modifier = Modifier.height(20.dp))

            InfoSection(title = "Account Security") {
                InfoRow(label = "Member Since", value = user.created_at, icon = Icons.Default.CalendarToday)
                InfoRow(label = "Account Status", value = "Active", icon = Icons.Default.VerifiedUser)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- DESTRUCTIVE ACTIONS ---
            Button(
                onClick = {
                    SessionManager.clearSession(context)
                    navController.navigate("login") { popUpTo(0) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F1)),
                shape = RoundedCornerShape(16.dp),
                elevation = null
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(8.dp))
                Text("Logout from Device", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SurfaceGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextSub, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = TextSub)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
        }
    }
}