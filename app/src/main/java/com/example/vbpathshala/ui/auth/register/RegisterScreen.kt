package com.example.vbpathshala.ui.auth.register

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vbpathshala.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* -------------------- VIEWMODEL -------------------- */
class RegisterViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun register(
        first: String,
        last: String,
        email: String,
        mobile: String,
        gender: String,
        password: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // 🔐 Dummy registration (offline)
            if (email == "test@gmail.com") {
                _error.value = "Email already registered"
            } else {
                _success.value = true
            }

            _loading.value = false
        }
    }
}

/* -------------------- COMPOSE SCREEN -------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }

    val viewModel: RegisterViewModel = viewModel()
    val loading by viewModel.loading.collectAsState()
    val success by viewModel.success.collectAsState()
    val apiError by viewModel.error.collectAsState()

    // Navigate after success
    LaunchedEffect(success) {
        if (success) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2575FC), Color(0xFF6A11CB))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2575FC))
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---- First & Last Name ----
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ---- Email ----
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ---- Mobile Number ----
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // ---- Gender Selection ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Gender:", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                listOf("Male", "Female", "Other").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == option,
                            onClick = { gender = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFFFD700)
                            )
                        )
                        Text(option, color = Color.White)
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---- Password ----
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ---- Confirm Password ----
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ---- Input / API Errors ----
            if (inputError != null) Text(inputError!!, color = Color.Yellow)
            if (apiError != null) Text(apiError!!, color = Color.Red)

            Spacer(Modifier.height(16.dp))

            // ---- Register Button ----
            Button(
                onClick = {
                    when {
                        firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
                                mobile.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            inputError = "Please fill all fields"
                            return@Button
                        }
                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            inputError = "Invalid email"
                            return@Button
                        }
                        mobile.length != 10 -> {
                            inputError = "Mobile number must be 10 digits"
                            return@Button
                        }
                        password.length < 6 -> {
                            inputError = "Password must be at least 6 characters"
                            return@Button
                        }
                        password != confirmPassword -> {
                            inputError = "Passwords do not match"
                            return@Button
                        }
                        else -> inputError = null
                    }

                    viewModel.register(
                        first = firstName.trim(),
                        last = lastName.trim(),
                        email = email.trim(),
                        mobile = mobile.trim(),
                        gender = gender,
                        password = password
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                enabled = !loading
            ) {
                Text(if (loading) "Registering..." else "Register", fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text("Already have an account? Login", color = Color.White)
            }
        }
    }
}
