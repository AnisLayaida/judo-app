package com.anislayaida.judoapp.presentation.screens.techniqueDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.anislayaida.judoapp.data.technique.NoteForTechnique
import com.anislayaida.judoapp.data.technique.Technique
import java.text.SimpleDateFormat
import java.util.*

private val NavyBg    = Color(0xFF0D1B3E)
private val SurfaceBg = Color(0xFF1A2B55)
private val Gold      = Color(0xFFC9A84C)
private val AppRed    = Color(0xFFC8102E)

private fun beltTagColor(belt: String): Color = when (belt) {
    "White"  -> Color(0xFFEEEEEE)
    "Red"    -> Color(0xFFCC0000)
    "Yellow" -> Color(0xFFFFD700)
    "Orange" -> Color(0xFFFF6600)
    "Green"  -> Color(0xFF22C55E)
    "Blue"   -> Color(0xFF1A6EBF)
    "Brown"  -> Color(0xFF6B3A2A)
    else     -> Color(0xFF1A1A1A)
}

private fun beltTextColor(belt: String): Color = when (belt) {
    "White", "Yellow" -> Color(0xFF333333)
    else              -> Color.White
}

private fun parseSteps(description: String): List<String> {
    if (description.isBlank()) return emptyList()
    return description
        .split(Regex("(?<=[.!?])\\s+"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

// Maps technique name to its Wikimedia Commons GIF path
private fun wikimediaGifUrl(techniqueName: String): String? {
    val path = when (techniqueName) {
        "O-goshi"           -> "6/6b/O-goshi.gif"
        "Ippon-seoi-nage"   -> "3/3e/Ippon_seoi_nage.gif"
        "O-soto-gari"       -> "6/62/O-soto-gari.gif"
        "O-uchi-gari"       -> "2/22/O-uchi-gari.gif"
        "Tai-otoshi"        -> "3/35/Tai-otoshi.gif"
        "Harai-goshi"       -> "0/0e/Harai-goshi.gif"
        "Uchi-mata"         -> "8/8e/Uchi-mata.gif"
        "De-ashi-barai"     -> "1/1e/De-ashi-barai.gif"
        "Ko-uchi-gari"      -> "5/58/Ko-uchi-gari.gif"
        "Morote-seoi-nage"  -> "4/4e/Morote_seoi_nage.gif"
        "Tomoe-nage"        -> "7/7e/Tomoe-nage.gif"
        "Juji-gatame"       -> "3/3c/Juji-gatame.gif"
        "Kesa-gatame"       -> "5/5e/Kesa-gatame.gif"
        else                -> null
    } ?: return null
    return "https://upload.wikimedia.org/wikipedia/commons/$path"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniqueDetailScreen(
    techniqueId:   String,
    navController: NavHostController,
    vm: TechniqueDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(techniqueId) { vm.load(techniqueId) }

    val technique by vm.technique.collectAsStateWithLifecycle()
    val notes     by vm.notes.collectAsStateWithLifecycle()
    val noteText  by vm.newNoteText.collectAsStateWithLifecycle()
    val isSaving  by vm.isSaving.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        technique?.name ?: "Technique",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(bottom = 32.dp)
        ) {
            item {
                if (technique != null) {
                    TechniqueHeroCard(technique = technique!!)
                } else {
                    Box(
                        modifier         = Modifier.fillMaxWidth().height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Gold)
                    }
                }
            }

            // Demonstration card — always shown, GIF or kanji fallback
            technique?.let { t ->
                item { TechniqueGifCard(technique = t) }
            }

            technique?.description?.let { desc ->
                val steps = parseSteps(desc)
                if (steps.size > 1) {
                    item { StepByStepCard(steps = steps) }
                }
            }

            item {
                Text(
                    "My Notes",
                    color      = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    modifier   = Modifier.padding(top = 4.dp)
                )
            }

            item {
                NoteInputCard(
                    noteText  = noteText,
                    isSaving  = isSaving,
                    onChanged = vm::onNoteTextChanged,
                    onSave    = { vm.saveNote(techniqueId) }
                )
            }

            if (notes.isEmpty()) {
                item {
                    Text(
                        "No notes yet. Add one above.",
                        color    = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                items(notes, key = { it.id }) { note ->
                    NoteCard(
                        note     = note,
                        onDelete = { vm.deleteNote(techniqueId, note.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TechniqueHeroCard(technique: Technique) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (technique.nameJapanese.isNotEmpty()) {
                Text(
                    text          = technique.nameJapanese,
                    fontSize      = 36.sp,
                    fontWeight    = FontWeight.Light,
                    color         = Gold,
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(technique.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(6.dp), color = beltTagColor(technique.beltLevel)) {
                    Text(
                        technique.beltLevel,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color      = beltTextColor(technique.beltLevel),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(technique.category, color = Color.LightGray, fontSize = 13.sp)
                if (technique.subcategory.isNotEmpty()) {
                    Text("·", color = Color.Gray, fontSize = 13.sp)
                    Text(technique.subcategory, color = Color.LightGray, fontSize = 13.sp)
                }
            }
            if (technique.description.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(Modifier.height(16.dp))
                Text(technique.description, color = Color.LightGray, fontSize = 14.sp, lineHeight = 22.sp)
            }
        }
    }
}

@Composable
private fun TechniqueGifCard(technique: Technique) {
    val gifUrl = wikimediaGifUrl(technique.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Technique Demonstration",
                color      = Gold,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp
            )
            Spacer(Modifier.height(10.dp))

            if (gifUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(gifUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Demonstration of ${technique.name}",
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale       = ContentScale.Fit,
                    loading = {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Gold, strokeWidth = 2.dp)
                        }
                    },
                    error = {
                        // If URL fails, fall back to kanji display
                        KanjiFallback(technique = technique)
                    }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Source: Wikimedia Commons",
                    color    = Color.Gray,
                    fontSize = 10.sp
                )
            } else {
                // No mapped URL — show kanji fallback
                KanjiFallback(technique = technique)
            }
        }
    }
}

@Composable
private fun KanjiFallback(technique: Technique) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(NavyBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                technique.nameJapanese,
                color      = Gold,
                fontSize   = 48.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(Modifier.height(8.dp))
            Text(
                technique.name,
                color    = Color.LightGray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun StepByStepCard(steps: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Step-by-Step Breakdown", color = Gold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(
                    imageVector        = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint               = Gold
                )
            }
            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    steps.forEachIndexed { index, step ->
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment     = Alignment.Top
                        ) {
                            Box(
                                modifier         = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Gold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(step, color = Color.LightGray, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteInputCard(
    noteText:  String,
    isSaving:  Boolean,
    onChanged: (String) -> Unit,
    onSave:    () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value         = noteText,
                onValueChange = onChanged,
                placeholder   = { Text("Add a note…", color = Color.Gray) },
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White,
                    focusedBorderColor   = Gold,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor          = Gold
                ),
                maxLines = 4
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick  = onSave,
                enabled  = noteText.isNotBlank() && !isSaving,
                colors   = ButtonDefaults.buttonColors(containerColor = AppRed),
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Save Note", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: NoteForTechnique, onDelete: () -> Unit) {
    val dateStr = remember(note.timestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(note.timestamp))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.content, color = Color.White, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(dateStr, color = Color.Gray, fontSize = 11.sp)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete note", tint = AppRed, modifier = Modifier.size(18.dp))
            }
        }
    }
}