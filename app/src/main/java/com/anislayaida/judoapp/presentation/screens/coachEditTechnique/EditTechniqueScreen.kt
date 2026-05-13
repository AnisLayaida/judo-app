package com.anislayaida.judoapp.presentation.screens.coachEditTechnique

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.anislayaida.judoapp.presentation.screens.addTechnique.TechniqueFormCard
import com.anislayaida.judoapp.presentation.screens.addTechnique.AddTechniqueUiState

private val NavyBg  = Color(0xFF0D1B3E)
private val Gold    = Color(0xFFC9A84C)
private val AppRed  = Color(0xFFC8102E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTechniqueScreen(
    techniqueId:   String,
    navController: NavController,
    vm: EditTechniqueViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(techniqueId) { vm.load(techniqueId) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) navController.popBackStack()
    }

    
    val formState = AddTechniqueUiState(
        name         = uiState.name,
        nameJapanese = uiState.nameJapanese,
        category     = uiState.category,
        subcategory  = uiState.subcategory,
        beltLevel    = uiState.beltLevel,
        description  = uiState.description,
        isRestricted = uiState.isRestricted,
        isLoading    = uiState.isLoading,
        isSaved      = uiState.isSaved,
        errorMessage = uiState.errorMessage
    )

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Technique", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            TechniqueFormCard(
                uiState             = formState,
                onNameChange        = vm::onNameChange,
                onJapaneseChange    = vm::onJapaneseNameChange,
                onCategoryChange    = vm::onCategoryChange,
                onSubcategoryChange = vm::onSubcategoryChange,
                onBeltChange        = vm::onBeltLevelChange,
                onDescChange        = vm::onDescriptionChange,
                onRestrictedChange  = vm::onRestrictedChange
            )

            if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = AppRed, fontSize = 13.sp)
            }

            Button(
                onClick  = vm::updateTechnique,
                enabled  = !uiState.isLoading,
                colors   = ButtonDefaults.buttonColors(containerColor = AppRed),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(10.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Update Technique", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}