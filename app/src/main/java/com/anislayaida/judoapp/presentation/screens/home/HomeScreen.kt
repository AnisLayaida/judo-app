package com.anislayaida.judoapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.anislayaida.judoapp.data.technique.Technique
import com.anislayaida.judoapp.data.user.User
import com.anislayaida.judoapp.data.user.UserRole
import com.anislayaida.judoapp.navigation.NavScreen
import com.anislayaida.judoapp.ui.theme.Gold
import com.anislayaida.judoapp.ui.theme.Navy
import com.anislayaida.judoapp.ui.theme.SurfaceBlue

private val beltColourMap: Map<String, Color> = mapOf(
    "White"   to Color(0xFFEEEEEE),
    "Yellow"  to Color(0xFFFFD600),
    "Orange"  to Color(0xFFFF8C00),
    "Red"     to Color(0xFFC8102E),
    "Green"   to Color(0xFF2E7D32),
    "Blue"    to Color(0xFF1565C0),
    "Brown"   to Color(0xFF6D4C41),
    "Black"   to Color(0xFF1C1C1C),
    "1st Dan" to Color(0xFF1C1C1C),
    "2nd Dan" to Color(0xFF1C1C1C),
    "3rd Dan" to Color(0xFF1C1C1C),
    "4th Dan" to Color(0xFFC8102E),
    "5th Dan" to Color(0xFFC8102E),
    "6th Dan" to Color(0xFFFF8C00),
)

private val beltTagBgMap: Map<String, Color> = mapOf(
    "White"  to Color(0xFFDDDDDD),
    "Yellow" to Color(0xFFFFD600),
    "Orange" to Color(0xFFFF8C00),
    "Red"    to Color(0xFFB71C1C),
    "Green"  to Color(0xFF1B5E20),
    "Blue"   to Color(0xFF0D47A1),
    "Brown"  to Color(0xFF4E342E),
    "Black"  to Color(0xFF212121),
)

private val beltTagTextMap: Map<String, Color> = mapOf(
    "White"  to Color(0xFF222222),
    "Yellow" to Color(0xFF222222),
    "Orange" to Color(0xFFFFFFFF),
    "Red"    to Color(0xFFFFFFFF),
    "Green"  to Color(0xFFFFFFFF),
    "Blue"   to Color(0xFFFFFFFF),
    "Brown"  to Color(0xFFFFFFFF),
    "Black"  to Color(0xFFFFFFFF),
)

private fun beltColor(grade: String): Color  = beltColourMap[grade] ?: Color(0xFFEEEEEE)
private fun beltTagBg(grade: String): Color  = beltTagBgMap[grade]  ?: Color(0xFFDDDDDD)
private fun beltTagText(grade: String): Color = beltTagTextMap[grade] ?: Color(0xFF222222)

private const val NAGE_WAZA = "Nage-waza"
private const val NE_WAZA   = "Ne-waza"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userRole: UserRole,
    vm: HomeViewModel = hiltViewModel()
) {
    val currentUser    by vm.currentUser.collectAsStateWithLifecycle()
    val techniques     by vm.filteredTechniques.collectAsStateWithLifecycle()
    val availableBelts by vm.availableBelts.collectAsStateWithLifecycle()
    val selectedBelt   by vm.selectedBelt.collectAsStateWithLifecycle()
    val isLoading      by vm.isLoading.collectAsStateWithLifecycle()

    val nageWaza = techniques.filter { it.category == NAGE_WAZA }
    val neWaza   = techniques.filter { it.category == NE_WAZA }

    Scaffold(
        topBar         = { HomeTopBar(user = currentUser) },
        containerColor = Navy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BeltFilterRow(
                belts          = availableBelts,
                selectedBelt   = selectedBelt,
                onBeltSelected = vm::onBeltSelected
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (isLoading) {
                    item {
                        Box(
                            modifier         = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Gold)
                        }
                    }
                } else {
                    if (nageWaza.isNotEmpty()) {
                        item { TechniqueSectionHeader(label = "Nage-waza", count = nageWaza.size) }
                        items(nageWaza, key = { it.uid }) { technique ->
                            TechniqueRow(
                                technique = technique,
                                onClick   = {
                                    navController.navigate(
                                        "${NavScreen.TECHNIQUE_DETAIL.route}/${technique.uid}"
                                    )
                                }
                            )
                        }
                    }

                    if (neWaza.isNotEmpty()) {
                        item { TechniqueSectionHeader(label = "Ne-waza", count = neWaza.size) }
                        items(neWaza, key = { it.uid }) { technique ->
                            TechniqueRow(
                                technique = technique,
                                onClick   = {
                                    navController.navigate(
                                        "${NavScreen.TECHNIQUE_DETAIL.route}/${technique.uid}"
                                    )
                                }
                            )
                        }
                    }

                    if (!isLoading && techniques.isEmpty()) {
                        item { EmptyState() }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(user: User?) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text          = "柔道  Syllabus",
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = 0.5.sp
                )
                if (user != null) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(beltColor(user.beltGrade))
                                .then(
                                    if (user.beltGrade == "White")
                                        Modifier.border(0.5.dp, Color.Gray, CircleShape)
                                    else Modifier
                                )
                        )
                        Text(
                            text     = "${user.fullName} · ${user.beltGrade}",
                            fontSize = 12.sp,
                            color    = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBlue)
    )
}

@Composable
private fun BeltFilterRow(
    belts: List<String>,
    selectedBelt: String?,
    onBeltSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceBlue)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedBelt == null,
            onClick  = { onBeltSelected("__all__") },
            label    = { Text("All", fontSize = 12.sp) },
            colors   = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Gold,
                selectedLabelColor     = Navy,
                containerColor         = Color.White.copy(alpha = 0.1f),
                labelColor             = Color.White.copy(alpha = 0.7f)
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled             = true,
                selected            = selectedBelt == null,
                borderColor         = Color.White.copy(alpha = 0.2f),
                selectedBorderColor = Gold
            )
        )

        belts.forEach { belt ->
            val isSelected = selectedBelt == belt
            FilterChip(
                selected    = isSelected,
                onClick     = { onBeltSelected(belt) },
                label       = { Text(belt, fontSize = 12.sp) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(beltColor(belt))
                            .then(
                                if (belt == "White")
                                    Modifier.border(0.5.dp, Color.Gray, CircleShape)
                                else Modifier
                            )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Gold,
                    selectedLabelColor     = Navy,
                    containerColor         = Color.White.copy(alpha = 0.1f),
                    labelColor             = Color.White.copy(alpha = 0.7f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled             = true,
                    selected            = isSelected,
                    borderColor         = Color.White.copy(alpha = 0.2f),
                    selectedBorderColor = Gold
                )
            )
        }
    }
}

@Composable
private fun TechniqueSectionHeader(label: String, count: Int) {
    val englishTranslation = when (label) {
        "Nage-waza" -> "Throws"
        "Ne-waza"   -> "Groundwork"
        else        -> ""
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = label,               fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f), letterSpacing = 0.5.sp)
            Text(text = "·",                 fontSize = 13.sp, color = Color.White.copy(alpha = 0.3f))
            Text(text = englishTranslation,  fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f))
        }
        Text(text = "$count", fontSize = 12.sp, color = Color.White.copy(alpha = 0.3f))
    }
}

@Composable
fun TechniqueRow(technique: Technique, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(beltColor(technique.beltLevel))
                    .then(
                        if (technique.beltLevel == "White")
                            Modifier.border(0.8.dp, Color.Gray, CircleShape)
                        else Modifier
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "${technique.name} (${technique.nameJapanese})",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = technique.subcategory.ifBlank { technique.category },
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.45f)
                )
            }
            BeltTag(grade = technique.beltLevel)
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = "View technique",
                tint               = Color.White.copy(alpha = 0.3f),
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun BeltTag(grade: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(beltTagBg(grade))
            .then(
                if (grade == "Black")
                    Modifier.border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = grade, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = beltTagText(grade))
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "礼", fontSize = 48.sp, color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "No techniques match this filter", fontSize = 14.sp, color = Color.White.copy(alpha = 0.3f))
        }
    }
}