package com.example.vbpathshala.ui.admin
import android.util.Log

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vbpathshala.navigation.Screen
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import com.example.vbpathshala.data.network.AuthInterceptor.AuthInterceptor
import retrofit2.http.Part
import okhttp3.OkHttpClient


/* ---------------- API ---------------- */

interface UploadApi {
    @Multipart
    @POST("notes/upload")
    suspend fun uploadNote(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody
    ): Response<UploadResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://edu-backend-m610.onrender.com/api/"

    fun api(context: Context): UploadApi {

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UploadApi::class.java)
    }
}


/* ---------------- MODELS ---------------- */

data class UploadResponse(val note: Note, val download_url: String)

data class Note(
    val id: String,
    val title: String,
    val file_name: String,
    val s3_key: String,
    val content_type: String,
    val file_size: Long
)

/* ---------------- UTIL ---------------- */

fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val contentResolver = context.contentResolver

    val mimeType = contentResolver.getType(uri) ?: "application/pdf"

    val fileName = contentResolver
        .query(uri, null, null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex("_display_name")
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "upload.pdf"

    val inputStream = contentResolver.openInputStream(uri)!!
    val requestBody =
        inputStream.readBytes().toRequestBody(mimeType.toMediaType())

    return MultipartBody.Part.createFormData(
        "file",        // MUST be exactly "file"
        fileName,      // REAL filename
        requestBody
    )
}

/* ---------------- UI ---------------- */

@Composable
fun AdminScreen(navController: NavHostController) {

    var showNotes by remember { mutableStateOf(false) }

    var fileName by remember { mutableStateOf("") }
    var selectedFileText by remember { mutableStateOf("No file selected") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedUri = uri
                selectedFileText = uri.lastPathSegment ?: "PDF Selected"
            }
        }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFF001F80))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // 🔙 BACK
                    IconButton(onClick = {
                        if (showNotes) showNotes = false
                        else navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }

                    Text(
                        text = if (showNotes) "NOTES SECTION" else "ADMIN PANEL",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 🚪 LOGOUT
                    IconButton(onClick = {

                        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()

                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }

                    }) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->

        if (!showNotes) {

            /* ------------ DASHBOARD ------------ */

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AdminOptionCard("Notes", Icons.Default.Note) {
                        showNotes = true
                    }
                    AdminOptionCard("Test", Icons.Default.List) {
                        navController.navigate("admin_test")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AdminOptionCard("Quiz", Icons.Default.QuestionAnswer) {
                        navController.navigate("admin_quiz")
                    }
                    AdminOptionCard("Progress", Icons.Default.Analytics) {
                        navController.navigate("admin_progress")
                    }
                }
            }

        } else {

            /* ------------ NOTES SECTION ------------ */

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    "Upload Notes PDF",
                    color = Color(0xFF001F80),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Enter File Name") },
                    shape = RoundedCornerShape(50.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = { filePickerLauncher.launch("application/pdf") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F80)),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text("SELECT", color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (selectedUri == null || fileName.isEmpty()) {
                                Toast.makeText(context, "Select file & enter name", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            scope.launch {
                                try {
                                    Log.d("UploadNote", "Upload started")

                                    val filePart = uriToMultipart(context, selectedUri!!)
                                    val titlePart = fileName.toRequestBody("text/plain".toMediaType())

                                    Log.d("UploadNote", "File name: $fileName")
                                    Log.d("UploadNote", "Calling upload API")

                                    val response = RetrofitClient
                                        .api(context)
                                        .uploadNote(filePart, titlePart)

                                    if (response.isSuccessful) {
                                        Log.d("UploadNote", "Upload success: ${response.code()}")
                                        Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show()

                                        fileName = ""
                                        selectedFileText = "No file selected"
                                        selectedUri = null
                                    } else {
                                        Log.e(
                                            "UploadNote",
                                            "Upload failed: ${response.code()} - ${response.errorBody()?.string()}"
                                        )
                                        Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: Exception) {
                                    Log.e("UploadNote", "Exception during upload", e)
                                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                                }

                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F80)),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text("UPLOAD", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = selectedFileText,
                    color = Color(0xFF001F80),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/* ------------ CARD UI ------------ */

@Composable
fun RowScope.AdminOptionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color(0xFF001F80), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, color = Color(0xFF001F80), fontWeight = FontWeight.Bold)
        }
    }
}
