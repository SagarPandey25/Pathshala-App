package com.example.vbpathshala.ui.auth.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vbpathshala.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/* -------------------- API MODELS -------------------- */

data class LoginRequest(val email: String, val password: String)

data class User(
    val id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val role: String,
    val created_at: String,
    val updated_at: String
)

data class LoginResponse(val message: String, val token: String, val user: User)

/* -------------------- API -------------------- */

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

/* -------------------- RETROFIT -------------------- */

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

/* -------------------- VIEWMODEL -------------------- */

class LoginViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    _role.value = response.body()!!.user.role   // 👈 STORE ROLE
                    _success.value = true
                } else {
                    _error.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Something went wrong"
            }
            _loading.value = false
        }
    }
}

/* -------------------- COMPOSE SCREEN -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {

    val context = androidx.compose.ui.platform.LocalContext.current

    /* 🔐 AUTO LOGIN */
    LaunchedEffect(Unit) {
        val TAG = "SessionCheck"

        Log.d(TAG, "LaunchedEffect started")

        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val role = prefs.getString("role", "")

        Log.d(TAG, "isLoggedIn = $isLoggedIn")
        Log.d(TAG, "role = $role")

        if (isLoggedIn) {
            if (role == "admin") {
                Log.d(TAG, "Navigating to Admin screen")
                navController.navigate(Screen.Admin.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else {
                Log.d(TAG, "Navigating to Home screen")
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        } else {
            Log.d(TAG, "User is not logged in")
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetSent by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    val viewModel: LoginViewModel = viewModel()
    val loading by viewModel.loading.collectAsState()
    val success by viewModel.success.collectAsState()
    val apiError by viewModel.error.collectAsState()

    /* ✅ AFTER LOGIN SUCCESS → ASK ROLE */
    LaunchedEffect(success) {
        if (success) showRoleDialog = true
    }


    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2575FC), Color(0xFF6A11CB))
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = Color(0xFFFFA07A),
        unfocusedLabelColor = Color(0xFFADD8E6),
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color(0xCCFFFFFF),
        unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
        focusedContainerColor = Color.White.copy(alpha = 0.95f),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)) {
                        append("Login to ")
                    }
                    withStyle(
                        SpanStyle(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFD700), Color(0xFFFF4500))
                            ),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    ) { append("VB Pathshala") }
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontWeight = FontWeight.Bold) },
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color(0xFFFFA07A))
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = { showResetDialog = true }) {
                Text("Forgot Password?", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
            }

            if (inputError != null) Text(inputError!!, color = Color.Yellow, fontWeight = FontWeight.Bold)
            if (apiError != null) Text(apiError ?: "", color = Color.Red, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    when {
                        email.isBlank() && password.isBlank() -> inputError = "Please enter email and password"
                        email.isBlank() -> inputError = "Please enter email"
                        password.isBlank() -> inputError = "Please enter password"
                        else -> {
                            inputError = null
                            viewModel.login(email.trim(), password)
                        }
                    }
                },
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF8C00), Color(0xFFFF3CAC))
                            ),
                            shape = RoundedCornerShape(25.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (loading) "Logging in..." else "LOGIN", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("Don’t have an account? Register Now", color = Color.White.copy(alpha = 0.8f))
            }
        }
    }

    /* -------- ROLE CHOOSE DIALOG -------- */
    if (showRoleDialog) {
        Dialog(onDismissRequest = {}) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Continue As", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))

                    // ---- ADMIN BUTTON ----
                    Button(
                        onClick = {
                            val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("isLoggedIn", true).putString("role", "admin").apply()
                            showRoleDialog = false

                            navController.navigate(Screen.Admin.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F80))
                    ) { Text("ADMIN", color = Color.White) }

                    Spacer(Modifier.height(12.dp))

                    // ---- STUDENT BUTTON ----
                    Button(
                        onClick = {
                            val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            prefs.edit().putBoolean("isLoggedIn", true).putString("role", "user").apply()
                            showRoleDialog = false

                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A11CB))
                    ) { Text("STUDENT", color = Color.White) }
                }
            }
        }
    }

    /* -------- RESET PASSWORD DIALOG -------- */
    if (showResetDialog) {
        Dialog(onDismissRequest = { showResetDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Reset Password", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Enter your email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { resetSent = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Send Reset Link")
                    }

                    if (resetSent) Text("📩 Reset link sent!", color = Color(0xFF008000), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
