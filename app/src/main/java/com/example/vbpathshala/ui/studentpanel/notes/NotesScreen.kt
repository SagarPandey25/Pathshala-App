package com.example.vbpathshala.ui.studentpanel.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vbpathshala.data.network.AuthInterceptor.AuthInterceptor
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.*

// ────────────────────────────────────────────────
//  RETROFIT CLIENT (FIXED)
// ────────────────────────────────────────────────

object RetrofitClient {

    private const val BASE_URL = "https://edu-backend-m610.onrender.com/api/"

    fun api(context: Context): NotesApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))   // ← same auth interceptor as admin!
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotesApiService::class.java)
    }
}
// ────────────────────────────────────────────────
//  MODELS
// ────────────────────────────────────────────────

data class NoteItem(
    val id: String,
    val title: String?,
    val description: String?,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("content_type") val contentType: String,
    @SerializedName("file_size") val fileSize: Long,
    @SerializedName("s3_key") val s3Key: String,
    @SerializedName("uploaded_by") val uploadedBy: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?
)

data class NoteEntry(
    val note: NoteItem,
    @SerializedName("download_url") val downloadUrl: String
)

data class NotesResponse(
    val message: String,
    val notes: List<NoteEntry>
)

// ────────────────────────────────────────────────
//  API INTERFACE
// ────────────────────────────────────────────────

interface NotesApiService {
    @GET("notes")
    suspend fun getUserNotes(): Response<NotesResponse>
}

// ────────────────────────────────────────────────
//  VIEWMODEL
// ────────────────────────────────────────────────

class NotesViewModel(
    private val context: Context
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntry>>(emptyList())
    val notes: StateFlow<List<NoteEntry>> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchNotes()
    }

    fun fetchNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val api = RetrofitClient.api(context)
                val response = api.getUserNotes()

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        _notes.value = body.notes
                        if (body.notes.isEmpty()) {
                            _errorMessage.value = "No notes available yet"
                        }
                    } ?: run {
                        _errorMessage.value = "Empty response from server"
                    }
                } else {
                    _errorMessage.value = when (response.code()) {
                        401 -> "Session expired. Please log in again."
                        403 -> "You don't have permission to view this content."
                        404 -> "Resource not found"
                        else -> "Server error: ${response.code()} ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load notes: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        fetchNotes()
    }
}

// ────────────────────────────────────────────────
//  VIEWMODEL FACTORY
// ────────────────────────────────────────────────

class NotesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// ────────────────────────────────────────────────
//  MAIN SCREEN
// ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    val viewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(context)
    )

    val notes by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->

        when {
            isLoading && notes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Try Again")
                        }
                    }
                }
            }

            notes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "No notes yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Your uploaded study materials will appear here",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { entry ->
                        NoteCard(
                            entry = entry,
                            onClick = {
                                openPdf(context, entry.downloadUrl, entry.note.fileName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    entry: NoteEntry,
    onClick: () -> Unit
) {
    val note = entry.note

    val sizeMb = remember(note.fileSize) {
        "%.1f MB".format(note.fileSize / 1_048_576.0)
    }

    val dateStr = remember(note.createdAt) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(note.createdAt) ?: return@remember note.createdAt
            SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            note.createdAt.substring(0, 10)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title?.takeIf { it.isNotBlank() } ?: note.fileName,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = note.fileName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$sizeMb • $dateStr",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun openPdf(context: Context, url: String, fileName: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(url), "application/pdf")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}