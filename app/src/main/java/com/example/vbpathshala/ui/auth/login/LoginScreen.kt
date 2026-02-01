package com.example.vbpathshala.ui.auth.login

import android.util.Patterns
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vbpathshala.data.session.SessionManager
import com.example.vbpathshala.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/* ---------------- API ---------------- */

data class LoginRequest(val email: String, val password: String)

/* ✅ UPDATED USER MODEL */
data class User(
    val id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val role: String,
    val created_at: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<LoginResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://edu-backend-m610.onrender.com/api/"
    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}

/* ---------------- VIEWMODEL ---------------- */

class LoginViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _response = MutableStateFlow<LoginResponse?>(null)
    val response: StateFlow<LoginResponse?> = _response

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val res = RetrofitClient.api.login(LoginRequest(email, password))
                if (res.isSuccessful && res.body() != null) {
                    _response.value = res.body()
                    _success.value = true
                } else {
                    _error.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                _error.value = "Server error. Try again"
            }
            _loading.value = false
        }
    }
}

/* ---------------- UI ---------------- */

@Composable
fun LoginScreen(navController: NavHostController) {

    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()

    val loading by viewModel.loading.collectAsState()
    val success by viewModel.success.collectAsState()
    val response by viewModel.response.collectAsState()
    val apiError by viewModel.error.collectAsState()

    val isDark = isSystemInDarkTheme()

    /* Floating animation */
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    /* Auto Login */
    LaunchedEffect(Unit) {
        if (SessionManager.isLoggedIn(context)) {
            val user = SessionManager.getUser(context)

            navController.navigate(
                if (user.role == "Admin")
                    Screen.Admin.route
                else
                    Screen.Home.route
            ) { popUpTo(0) }
        }
    }

    /* Navigate after login */
    LaunchedEffect(success) {
        if (success && response != null) {

            // ✅ SAVE FULL USER DATA
            SessionManager.saveSession(
                context,
                response!!.token,
                response!!.user
            )

            navController.navigate(
                if (response!!.user.role == "Admin")
                    Screen.Admin.route
                else
                    Screen.Home.route
            ) { popUpTo(0) }
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }

    val bgGradient = if (isDark)
        Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43)))
    else
        Brush.verticalGradient(listOf(Color(0xFF2193B0), Color(0xFF6DD5ED)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
        contentAlignment = Alignment.Center
    ) {

        /* Glow */
        Box(
            modifier = Modifier
                .size(360.dp, 520.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            if (isDark) Color(0xFF00F5FF).copy(0.35f)
                            else Color(0xFF6A11CB).copy(0.25f),
                            Color.Transparent
                        )
                    )
                )
                .blur(80.dp)
        )

        /* Floating Card */
        Column(
            modifier = Modifier
                .offset(y = floatY.dp)
                .padding(24.dp)
                .shadow(30.dp, RoundedCornerShape(28.dp))
                .border(
                    2.dp,
                    Brush.linearGradient(
                        listOf(Color(0xFF00F5FF), Color(0xFFFF00FF))
                    ),
                    RoundedCornerShape(28.dp)
                )
                .background(
                    if (isDark) Color(0xFF121212) else Color.White,
                    RoundedCornerShape(28.dp)
                )
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "VB PATHSHALA",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.Cyan else Color(0xFF6A00FF)
            )

            Text(
                "Smart Learning Platform",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(26.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation =
                    if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            if (showPass) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (inputError != null) {
                Spacer(Modifier.height(8.dp))
                Text(inputError!!, color = Color.Red, fontSize = 13.sp)
            }

            if (apiError != null) {
                Spacer(Modifier.height(6.dp))
                Text(apiError!!, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() ->
                            inputError = "Email and password are required"

                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                            inputError = "Enter a valid email address"

                        else -> {
                            inputError = null
                            viewModel.login(email.trim(), password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF512F), Color(0xFFDD2476))
                            ),
                            RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (loading) "Logging in..." else "LOGIN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row {
                Text("Don’t have an account? ")
                Text(
                    "Register",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A00FF),
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
        }
    }
}

