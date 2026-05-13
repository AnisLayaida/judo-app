package com.anislayaida.judoapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anislayaida.judoapp.data.technique.Technique

private val SurfaceBlue = Color(0xFF1A2B55)
private val Gold        = Color(0xFFC9A84C)

private val beltColourMap: Map<String, Color> = mapOf(
    "White"   to Color(0xFFEEEEEE),
    "Yellow"  to Color(0xFFFFD600),
    "Orange"  to Color(0xFFFF8C00),
    "Red"     to Color(0xFFC8102E),
    "Green"   to Color(0xFF2E7D32),
    "Blue"    to Color(0xFF1565C0),
    "Brown"   to Color(0xFF6D4C41),
    "Black"   to Color(0xFF1C1C1C),
)

private fun beltColor(grade: String): Color =
    beltColourMap[grade] ?: Color(0xFFEEEEEE)

@Composable
fun TechniqueView(
    technique: Technique,
    onClick:   () -> Unit
) {
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
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = technique.subcategory.ifBlank { technique.category },
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.45f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(beltColor(technique.beltLevel).copy(alpha = 0.18f))
                    .border(
                        0.5.dp,
                        beltColor(technique.beltLevel).copy(alpha = 0.4f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text       = technique.beltLevel,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color      = if (technique.beltLevel == "White")
                        Color.White.copy(alpha = 0.8f)
                    else beltColor(technique.beltLevel)
                )
            }
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = "View",
                tint               = Color.White.copy(alpha = 0.3f),
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}