package com.anislayaida.judoapp.presentation.screens.grading

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.anislayaida.judoapp.data.technique.GradingRequest
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.presentation.components.BottomNavBar

private val NavyBg    = Color(0xFF0D1B3E)
private val SurfaceBg = Color(0xFF1A2B55)
private val Gold      = Color(0xFFC9A84C)
private val AppRed    = Color(0xFFC8102E)
private val AppGreen  = Color(0xFF22C55E)

private fun beltColor(belt: String): Color = when {
    belt.startsWith("Black") -> Color(0xFF1A1A1A)
    else -> when (belt) {
        "White"  -> Color(0xFFEEEEEE)
        "Red"    -> Color(0xFFCC0000)
        "Yellow" -> Color(0xFFFFD700)
        "Orange" -> Color(0xFFFF6600)
        "Green"  -> Color(0xFF22C55E)
        "Blue"   -> Color(0xFF1A6EBF)
        "Brown"  -> Color(0xFF6B3A2A)
        else     -> Color.Gray
    }
}

private fun beltTextColor(belt: String): Color = when {
    belt.startsWith("Black")                    -> Color(0xFFFFD700)
    belt == "White" || belt == "Yellow"         -> Color(0xFF333333)
    else                                        -> Color.White
}

private fun statusColor(status: String): Color = when (status) {
    "readiness_approved" -> AppGreen
    "passed"             -> AppGreen
    "rejected", "failed" -> AppRed
    "cancelled"          -> Color.Gray
    else                 -> Gold
}

private fun statusLabel(status: String): String = when (status) {
    "pending"            -> "Pending"
    "readiness_approved" -> "Ready — Awaiting Result"
    "rejected"           -> "Rejected"
    "passed"             -> "Passed ✓"
    "failed"             -> "Failed"
    "cancelled"          -> "Cancelled"
    else                 -> status.replaceFirstChar { it.uppercase() }
}


private val kyuSyllabusPdfs: Map<String, Pair<String, String>> = mapOf(
    "White"  to Pair("Red",    "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/9th-Kyu-Red-Belt-Syllabus.pdf"),
    "Red"    to Pair("Yellow", "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/6th-Kyu-Yellow-Belt-Syllabus.pdf"),
    "Yellow" to Pair("Orange", "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/5th-Kyu-Orange-Belt-Syllabus.pdf"),
    "Orange" to Pair("Green",  "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/4th-Kyu-Green-Belt-Syllabus.pdf"),
    "Green"  to Pair("Blue",   "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/3rd-Kyu-Blue-Belt-Syllabus.pdf"),
    "Blue"   to Pair("Brown",  "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/2nd-Kyu-Brown-Belt-Syllabus.pdf")
)

private val monSyllabusPdfs: Map<String, Pair<String, String>> = mapOf(
    "White"  to Pair("Red (1st–3rd Mon)",     "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/Mon-1-3-Syllabus.pdf"),
    "Red"    to Pair("Yellow (4th–6th Mon)",  "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/Mon-4-6-Syllabus.pdf"),
    "Yellow" to Pair("Orange (7th–9th Mon)",  "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/Mon-7-9-Syllabus.pdf"),
    "Orange" to Pair("Green (10th–12th Mon)", "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/Mon-10-12-Syllabus.pdf"),
    "Green"  to Pair("Blue (13th–15th Mon)",  "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/Mon-13-15-Syllabus.pdf"),
    "Blue"   to Pair("Brown (16th–18th Mon)", "https://www.britishjudo.org.uk/wp-content/uploads/2022/09/Mon-16-18-Syllabus.pdf")
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradingScreen(
    userRole: UserRole = UserRole.JUDOKA,
    navController: NavController? = null,
    vm: GradingViewModel = hiltViewModel()
) {
    val currentBelt by vm.currentBelt.collectAsStateWithLifecycle()
    val judokaName  by vm.judokaName.collectAsStateWithLifecycle()
    val isUnder16   by vm.isUnder16.collectAsStateWithLifecycle()
    val requests    by vm.requests.collectAsStateWithLifecycle()
    val isLoading   by vm.isLoading.collectAsStateWithLifecycle()
    val message     by vm.message.collectAsStateWithLifecycle()

    val context = LocalContext.current

    if (message != null) {
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            vm.clearMessage()
        }
    }

    val nextGrade    = vm.nextGradeLabel(currentBelt)
    val canRequest   = vm.canRequest()
    val cooldownMsg  = vm.cooldownMessage()
    val syllabusPdfs = if (isUnder16) monSyllabusPdfs else kyuSyllabusPdfs
    val syllabus     = syllabusPdfs[currentBelt]

    val hasPending           = requests.any { it.status == "pending" }
    val hasReadinessApproved = requests.any { it.status == "readiness_approved" }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title  = { Text("Grading", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
            )
        },
        bottomBar = {
            if (navController != null) {
                BottomNavBar(userRole = userRole, navController = navController)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isUnder16) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Gold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "Junior · Mon Grade System",
                                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    color      = Gold,
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        Text(judokaName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Current Grade", color = Color.LightGray, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = beltColor(currentBelt)) {
                            Text(
                                currentBelt,
                                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                                color      = beltTextColor(currentBelt),
                                fontWeight = FontWeight.Bold,
                                fontSize   = 18.sp
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (nextGrade != null) {
                            Text("Next: $nextGrade", color = Color.LightGray, fontSize = 13.sp)
                        } else {
                            Text(
                                "最高段位 — Highest grade achieved",
                                color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold
                            )
                        }


                        if (cooldownMsg != null) {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint     = AppRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(cooldownMsg, color = AppRed, fontSize = 12.sp)
                            }
                        }


                        if (hasReadinessApproved) {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AppGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "✓ Coach approved — grading in progress",
                                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color      = AppGreen,
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick  = { vm.requestGrading() },
                            enabled  = !isLoading && nextGrade != null && canRequest && !hasPending && !hasReadinessApproved,
                            colors   = ButtonDefaults.buttonColors(containerColor = AppRed),
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    when {
                                        nextGrade == null        -> "Grade Complete"
                                        hasReadinessApproved     -> "Grading In Progress"
                                        hasPending               -> "Request Pending"
                                        cooldownMsg != null      -> "Cooldown Active"
                                        else                     -> "Request Grading"
                                    },
                                    color = Color.White, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }


            if (message != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
                        shape    = RoundedCornerShape(8.dp)
                    ) {
                        Text(message!!, modifier = Modifier.padding(14.dp), color = Gold, fontSize = 14.sp)
                    }
                }
            }


            item {
                Text(
                    "Grading History",
                    color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (requests.isEmpty()) {
                item { Text("No grading requests yet.", color = Color.Gray, fontSize = 13.sp) }
            } else {
                items(requests, key = { it.id }) { request ->
                    GradingRequestCard(
                        request  = request,
                        onCancel = if (request.status == "pending") {
                            { vm.cancelGradingRequest(request.id) }
                        } else null
                    )
                }
            }


            item {
                Text(
                    "Grading Syllabus",
                    color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (syllabus != null) {
                val (targetBelt, pdfUrl) = syllabus
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                                context.startActivity(intent)
                            },
                        colors = CardDefaults.cardColors(containerColor = SurfaceBg),
                        shape  = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint     = Gold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        "BJA ${if (isUnder16) "Mon" else "Kyu"} — $targetBelt",
                                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp
                                    )
                                    Text(
                                        "Your next grading — tap to open",
                                        color = Gold, fontSize = 11.sp
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }


            item {
                Text("Previous Syllabuses", color = Color.LightGray, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            }

            val beltOrder          = syllabusPdfs.keys.toList()
            val currentIndex       = beltOrder.indexOf(currentBelt)
            val previousSyllabuses = syllabusPdfs.entries.filter { (belt, _) ->
                val idx = beltOrder.indexOf(belt)
                idx >= 0 && idx < currentIndex
            }

            if (previousSyllabuses.isEmpty()) {
                item { Text("No previous syllabuses — keep training!", color = Color.Gray, fontSize = 12.sp) }
            } else {
                items(previousSyllabuses, key = { it.key }) { (_, pair) ->
                    val (targetBelt, pdfUrl) = pair
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                            context.startActivity(intent)
                        },
                        colors = CardDefaults.cardColors(containerColor = SurfaceBg),
                        shape  = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Column {
                                    Text("BJA — $targetBelt", color = Color.White, fontSize = 13.sp)
                                    Text("Completed grade", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun GradingRequestCard(
    request:  GradingRequest,
    onCancel: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${request.currentBelt} → ${request.requestedBelt}",
                        color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                            .format(java.util.Date(request.timestamp)),
                        color = Color.Gray, fontSize = 12.sp
                    )

                    Text(
                        if (request.stage == "readiness") "Stage 1: Readiness" else "Stage 2: Result",
                        color = Color.LightGray, fontSize = 11.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor(request.status).copy(alpha = 0.2f)
                ) {
                    Text(
                        statusLabel(request.status),
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color      = statusColor(request.status),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (request.rejectionReason.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AppRed.copy(alpha = 0.1f)
                ) {
                    Text(
                        "Reason: ${request.rejectionReason}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color    = AppRed,
                        fontSize = 12.sp
                    )
                }
            }

            if (onCancel != null) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick        = onCancel,
                    border         = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                    modifier       = Modifier.fillMaxWidth(),
                    shape          = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Cancel Request", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}