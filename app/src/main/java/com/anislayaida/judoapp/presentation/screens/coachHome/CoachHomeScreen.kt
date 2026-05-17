package com.anislayaida.judoapp.presentation.screens.coachHome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.anislayaida.judoapp.data.technique.GradingRequest
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.navigation.NavScreen

private val NavyBg    = Color(0xFF0D1B3E)
private val SurfaceBg = Color(0xFF1A2B55)
private val Gold      = Color(0xFFC9A84C)
private val AppRed    = Color(0xFFC8102E)
private val AppGreen  = Color(0xFF22C55E)

private val beltOrder = listOf("White", "Red", "Yellow", "Orange", "Green", "Blue", "Brown", "Black")

private fun beltColor(belt: String): Color = when {
    belt.startsWith("Black") -> Color(0xFF212121)
    else -> when (belt) {
        "White"  -> Color(0xFFDDDDDD)
        "Red"    -> Color(0xFFB71C1C)
        "Yellow" -> Color(0xFFFFD600)
        "Orange" -> Color(0xFFFF8C00)
        "Green"  -> Color(0xFF1B5E20)
        "Blue"   -> Color(0xFF0D47A1)
        "Brown"  -> Color(0xFF4E342E)
        else     -> Color.Gray
    }
}

private fun beltTextColor(belt: String): Color = when (belt) {
    "White", "Yellow" -> Color(0xFF222222)
    else              -> Color.White
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachHomeScreen(
    text: String = "Coach Panel",
    userRole: UserRole = UserRole.COACH,
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    vm: CoachHomeViewModel = hiltViewModel()
) {
    val gradingRequests by vm.gradingRequests.collectAsStateWithLifecycle()
    val judokas         by vm.judokas.collectAsStateWithLifecycle()
    val searchQuery     by vm.searchQuery.collectAsStateWithLifecycle()
    val message         by vm.message.collectAsStateWithLifecycle()
    val techniques      by vm.techniques.collectAsStateWithLifecycle()
    val techniqueCount  by vm.techniqueCount.collectAsStateWithLifecycle()
    val memberCount     by vm.memberCount.collectAsStateWithLifecycle()
    val pendingCount    by vm.pendingCount.collectAsStateWithLifecycle()

    val filteredTechniques = remember(techniques, searchQuery) { vm.filteredTechniques() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Library", "Judokas", "Gradings")

    if (message != null) {
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            vm.clearMessage()
        }
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Coach Panel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("myJudo Companion", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBg)
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick        = { navController?.navigate(NavScreen.ADD_TECHNIQUE.route) },
                    containerColor = AppRed,
                    shape          = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Technique", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (message != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = SurfaceBg), shape = RoundedCornerShape(8.dp)) {
                    Text(message!!, modifier = Modifier.padding(14.dp), color = Gold, fontSize = 14.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Techniques", techniqueCount, Gold,     Modifier.weight(1f))
                StatCard("Members",    memberCount,    AppGreen, Modifier.weight(1f))
                StatCard("Pending",    pendingCount,   if (pendingCount > 0) AppRed else Color.Gray, Modifier.weight(1f))
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = NavyBg,
                contentColor     = Gold,
                indicator        = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = Gold)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        text     = {
                            BadgedBox(badge = {
                                if (index == 2 && pendingCount > 0) {
                                    Badge(containerColor = AppRed) { Text(pendingCount.toString(), fontSize = 9.sp, color = Color.White) }
                                }
                            }) {
                                Text(title, color = if (selectedTab == index) Gold else Color.LightGray, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> LibraryTab(techniques = filteredTechniques, searchQuery = searchQuery, onSearchChanged = vm::onSearchChanged, onEditTechnique = { uid -> navController?.navigate("${NavScreen.EDIT_TECHNIQUE.route}/$uid") })
                1 -> JudokasTab(judokas = judokas)
                2 -> GradingsTab(requests = gradingRequests, onApprove = vm::approveReadiness, onReject = vm::rejectWithReason, onRecordPass = vm::recordPass, onRecordFail = vm::recordFail, rejectionReasons = vm.rejectionReasons)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = SurfaceBg), shape = RoundedCornerShape(10.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value.toString(), color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(label, color = Color.LightGray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun LibraryTab(techniques: List<Technique>, searchQuery: String, onSearchChanged: (String) -> Unit, onEditTechnique: (String) -> Unit) {
    val grouped = remember(techniques) {
        val map = techniques.groupBy { it.beltLevel }
        beltOrder.mapNotNull { belt -> val list = map[belt]; if (!list.isNullOrEmpty()) belt to list else null }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery, onValueChange = onSearchChanged,
            placeholder = { Text("Search techniques…", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gold) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Gold, unfocusedBorderColor = Color.Gray, cursorColor = Gold),
            shape = RoundedCornerShape(10.dp), singleLine = true
        )
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
            if (searchQuery.isBlank()) {
                grouped.forEach { (belt, list) ->
                    item(key = "header_$belt") { BeltSectionHeader(belt = belt, count = list.size) }
                    items(list, key = { it.uid }) { technique -> TechniqueRow(technique = technique, onEdit = { onEditTechnique(technique.uid) }); Spacer(Modifier.height(6.dp)) }
                    item(key = "spacer_$belt") { Spacer(Modifier.height(8.dp)) }
                }
            } else {
                items(techniques, key = { it.uid }) { technique -> TechniqueRow(technique = technique, onEdit = { onEditTechnique(technique.uid) }); Spacer(Modifier.height(6.dp)) }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun BeltSectionHeader(belt: String, count: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(4.dp), color = beltColor(belt)) {
                Text(belt, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), color = beltTextColor(belt), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text("Belt", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        Text("$count techniques", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp)
    }
}

@Composable
private fun TechniqueRow(technique: Technique, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onEdit() }, colors = CardDefaults.cardColors(containerColor = SurfaceBg), shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(technique.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text("${technique.category} · ${technique.subcategory}", color = Color.LightGray, fontSize = 12.sp)
            }
            Text(technique.nameJapanese, color = Gold.copy(alpha = 0.6f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun JudokasTab(judokas: List<User>) {
    if (judokas.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No judokas registered yet.", color = Color.Gray, fontSize = 14.sp) }; return }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(judokas, key = { it.uid }) { judoka -> JudokaRow(judoka) }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun JudokaRow(user: User) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceBg), shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(user.judoClub.ifEmpty { "No club" }, color = Color.LightGray, fontSize = 12.sp)
            }
            Surface(shape = RoundedCornerShape(6.dp), color = beltColor(user.beltGrade)) {
                Text(user.beltGrade, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), color = beltTextColor(user.beltGrade), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GradingsTab(requests: List<GradingRequest>, onApprove: (GradingRequest) -> Unit, onReject: (GradingRequest, String) -> Unit, onRecordPass: (GradingRequest) -> Unit, onRecordFail: (GradingRequest) -> Unit, rejectionReasons: List<String>) {
    if (requests.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No pending grading requests.", color = Color.Gray, fontSize = 14.sp) }; return }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(requests, key = { it.id }) { request ->
            GradingRequestRow(request = request, onApprove = { onApprove(request) }, onReject = { reason -> onReject(request, reason) }, onRecordPass = { onRecordPass(request) }, onRecordFail = { onRecordFail(request) }, rejectionReasons = rejectionReasons)
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradingRequestRow(request: GradingRequest, onApprove: () -> Unit, onReject: (String) -> Unit, onRecordPass: () -> Unit, onRecordFail: () -> Unit, rejectionReasons: List<String>) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var selectedReason   by remember { mutableStateOf("") }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            containerColor   = SurfaceBg,
            title = { Text("Rejection Reason", color = Color.White, fontWeight = FontWeight.Bold) },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Select a reason:", color = Color.LightGray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    rejectionReasons.forEach { reason ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { selectedReason = reason }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason }, colors = RadioButtonDefaults.colors(selectedColor = Gold, unselectedColor = Color.Gray))
                            Text(reason, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { if (selectedReason.isNotBlank()) { onReject(selectedReason); showRejectDialog = false } }, enabled = selectedReason.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = AppRed)) { Text("Reject", color = Color.White) } },
            dismissButton = { TextButton(onClick = { showRejectDialog = false }) { Text("Cancel", color = Gold) } }
        )
    }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceBg), shape = RoundedCornerShape(10.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(request.judokaName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text("${request.currentBelt} → ${request.requestedBelt}", color = Color.LightGray, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(if (request.stage == "readiness") "Stage 1: Readiness Check" else "Stage 2: Record Result", color = if (request.stage == "readiness") Gold else AppGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))

            if (request.stage == "readiness") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = AppGreen), modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Approve", color = Color.White, fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = { showRejectDialog = true; selectedReason = "" }, border = androidx.compose.foundation.BorderStroke(1.dp, AppRed), modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = AppRed, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Reject", color = AppRed, fontSize = 13.sp)
                    }
                }
            } else {
                Text("Record grading result:", color = Color.LightGray, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onRecordPass, colors = ButtonDefaults.buttonColors(containerColor = AppGreen), modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp)) { Text("Pass ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    Button(onClick = onRecordFail, colors = ButtonDefaults.buttonColors(containerColor = AppRed),   modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp)) { Text("Fail ✗", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }
        }
    }
}