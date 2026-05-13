package com.anislayaida.judoapp.presentation.screens.addTechnique

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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

private val NavyBg      = Color(0xFF0D1B3E)
private val SurfaceBg   = Color(0xFF1A2B55)
private val Gold        = Color(0xFFC9A84C)
private val AppRed      = Color(0xFFC8102E)
private val FieldBorder = Color(0xFF2A3F70)

private val categoryOptions   = listOf("Nage-waza", "Ne-waza")
private val beltOptions       = listOf("White", "Yellow", "Orange", "Red", "Green", "Blue", "Brown", "Black")
private val subcategoryOptions = mapOf(
    "Nage-waza" to listOf("Te-waza", "Koshi-waza", "Ashi-waza", "Ma-sutemi-waza", "Yoko-sutemi-waza"),
    "Ne-waza"   to listOf("Osaekomi-waza", "Shime-waza", "Kansetsu-waza")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTechniqueScreen(
    navController: NavController,
    vm: AddTechniqueViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) navController.popBackStack()
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Technique", color = Color.White, fontWeight = FontWeight.Bold)
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
                uiState         = uiState,
                onNameChange    = vm::onNameChange,
                onJapaneseChange = vm::onJapaneseNameChange,
                onCategoryChange = vm::onCategoryChange,
                onSubcategoryChange = vm::onSubcategoryChange,
                onBeltChange    = vm::onBeltLevelChange,
                onDescChange    = vm::onDescriptionChange,
                onRestrictedChange = vm::onRestrictedChange
            )

            if (uiState.errorMessage != null) {
                Text(
                    uiState.errorMessage!!,
                    color    = AppRed,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick  = vm::saveTechnique,
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
                    Text("Save Technique", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniqueFormCard(
    uiState: AddTechniqueUiState,
    onNameChange: (String) -> Unit,
    onJapaneseChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSubcategoryChange: (String) -> Unit,
    onBeltChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onRestrictedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceBg),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            
            FormField(label = "Technique Name (English)") {
                FormTextField(
                    value       = uiState.name,
                    onValueChange = onNameChange,
                    placeholder = "e.g. Major hip throw"
                )
            }

            
            FormField(label = "Japanese Name") {
                FormTextField(
                    value       = uiState.nameJapanese,
                    onValueChange = onJapaneseChange,
                    placeholder = "e.g. O-goshi"
                )
            }

            
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormField(label = "Category", modifier = Modifier.weight(1f)) {
                    FormDropdown(
                        value    = uiState.category,
                        options  = categoryOptions,
                        onSelected = {
                            onCategoryChange(it)
                            onSubcategoryChange("")
                        }
                    )
                }
                FormField(label = "Belt Level", modifier = Modifier.weight(1f)) {
                    FormDropdown(
                        value    = uiState.beltLevel,
                        options  = beltOptions,
                        onSelected = onBeltChange
                    )
                }
            }

            
            FormField(label = "Subcategory") {
                val subOptions = subcategoryOptions[uiState.category] ?: emptyList()
                FormDropdown(
                    value    = uiState.subcategory.ifBlank { "Select subcategory" },
                    options  = subOptions,
                    onSelected = onSubcategoryChange
                )
            }

            
            FormField(label = "Description") {
                OutlinedTextField(
                    value         = uiState.description,
                    onValueChange = onDescChange,
                    placeholder   = { Text("Describe the technique…", color = Color.Gray, fontSize = 13.sp) },
                    modifier      = Modifier.fillMaxWidth(),
                    minLines      = 3,
                    maxLines      = 6,
                    colors        = formTextFieldColors()
                )
            }

            
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Age Restricted", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("Hidden for under-16s", color = Color.Gray, fontSize = 12.sp)
                }
                Switch(
                    checked         = uiState.isRestricted,
                    onCheckedChange = onRestrictedChange,
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor  = Color.White,
                        checkedTrackColor  = AppRed,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
private fun FormField(
    label:    String,
    modifier: Modifier = Modifier,
    content:  @Composable () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        content()
    }
}

@Composable
private fun FormTextField(
    value:        String,
    onValueChange: (String) -> Unit,
    placeholder:  String
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder, color = Color.Gray, fontSize = 13.sp) },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        colors        = formTextFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdown(
    value:     String,
    options:   List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded          = expanded,
        onExpandedChange  = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value         = value,
            onValueChange = {},
            readOnly      = true,
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier      = Modifier.fillMaxWidth().menuAnchor(),
            colors        = formTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded          = expanded,
            onDismissRequest  = { expanded = false },
            modifier          = Modifier.background(SurfaceBg)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(option, color = Color.White, fontSize = 13.sp) },
                    onClick = { onSelected(option); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor        = Color.White,
    unfocusedTextColor      = Color.White,
    focusedBorderColor      = Gold,
    unfocusedBorderColor    = FieldBorder,
    cursorColor             = Gold,
    focusedContainerColor   = NavyBg,
    unfocusedContainerColor = NavyBg,
    focusedTrailingIconColor   = Gold,
    unfocusedTrailingIconColor = Color.Gray
)