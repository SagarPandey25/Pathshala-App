package com.example.vbpathshala.ui.auth.register

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/* -------------------- API MODELS -------------------- */
data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val mobile: String,
    val gender: String
)

data class RegisterResponse(
    val message: String,
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val role: String,
    val created_at: String,
    val updated_at: String
)

/* -------------------- API SERVICE -------------------- */
interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}

/* -------------------- RETROFIT CLIENT -------------------- */
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
class RegisterViewModel : ViewModel() {

    var loading by mutableStateOf(false)
    var success by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
        gender: String,
        password: String
    ) {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                val response = RetrofitClient.api.register(
                    RegisterRequest(
                        first_name = firstName,
                        last_name = lastName,
                        email = email,
                        password = password,
                        mobile = mobile,
                        gender = gender
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    success = true
                } else {
                    // Parse API error if available
                    error = response.errorBody()?.string() ?: "Registration failed"
                }
            } catch (e: Exception) {
                error = e.localizedMessage ?: "Something went wrong"
            } finally {
                loading = false
            }
        }
    }
}

/* -------------------- REGISTER SCREEN -------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, viewModel: RegisterViewModel = viewModel()) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) showSuccessDialog = true
    }

    val backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))

    Scaffold { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Register For VB Pathshala \uD83D\uDC68\u200D\uD83C\uDF93",
                    fontSize = 26.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color(0xFF5A67D8)
                )
                Spacer(Modifier.height(16.dp))

                InputField("First Name", firstName) { firstName = it }
                InputField("Last Name", lastName) { lastName = it }
                InputField("Email", email) { email = it }
                InputField("Mobile No.", mobile) { mobile = it }
                GenderSelector(gender) { gender = it }
                PasswordField("Password", password, passwordVisible, { passwordVisible = !passwordVisible }) { password = it }
                PasswordField("Confirm Password", confirmPassword, confirmPasswordVisible, { confirmPasswordVisible = !confirmPasswordVisible }) { confirmPassword = it }

                inputError?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }
                viewModel.error?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        when {
                            firstName.isEmpty() || lastName.isEmpty() -> inputError = "Enter The Above Details"
                            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> inputError = "Invalid email"
                            mobile.isEmpty() -> inputError = "Enter mobile number"
                            password.length < 6 -> inputError = "Password must be at least 6 characters"
                            password != confirmPassword -> inputError = "Passwords do not match"
                            else -> {
                                inputError = null
                                viewModel.register(firstName, lastName, email, mobile, gender, password)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3CB371)),
                    enabled = !viewModel.loading
                ) {
                    Text(if (viewModel.loading) "Registering..." else "Register", fontSize = 16.sp)
                }

                Spacer(Modifier.height(40.dp))
            }

            /* ---------- SUCCESS POPUP ---------- */
            AnimatedVisibility(
                visible = showSuccessDialog,
                enter = scaleIn(initialScale = 0.7f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFF43E97B), Color(0xFF38F9D7)))
                            )
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(72.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Registration Successful!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                showSuccessDialog = false
                                viewModel.success = false
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Continue to Login", color = Color(0xFF2F855A))
                        }
                    }
                }
            }
        }
    }
}

/* ---------- REUSABLE COMPONENTS ---------- */
@Composable
fun InputField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        singleLine = true
    )
}

@Composable
fun PasswordField(label: String, value: String, visible: Boolean, onToggle: () -> Unit, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = { IconButton(onClick = onToggle) { Icon(if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null) } },
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        singleLine = true
    )
}

@Composable
fun GenderSelector(selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("Gender", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Male ", "Female", "Others").forEach { gender ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (gender == selected) Color(0xFF5A67D8) else Color(0xFFE0E0E0)
                        )
                        .clickable { onSelect(gender) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        gender,
                        color = if (gender == selected) Color.White else Color.Black,
                        fontWeight = if (gender == selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
