package com.anislayaida.judoapp.presentation.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anislayaida.judoapp.data.user.UserRole
import androidx.compose.foundation.clickable

private val NavyBg      = Color(0xFF0D1B3E)
private val SurfaceBg   = Color(0xFF1A2B55)
private val Gold        = Color(0xFFC9A84C)
private val AppRed      = Color(0xFFC8102E)
private val FieldBorder = Color(0xFF2A3F70)

private val beltOptions = listOf(
    "White · 9th Kyu",
    "Red · 6th Kyu",
    "Yellow · 5th Kyu",
    "Orange · 4th Kyu",
    "Green · 3rd Kyu",
    "Blue · 2nd Kyu",
    "Brown · 1st Kyu",
    "Black - 1st Dan",
    "Black - 2nd Dan",
    "Black - 3rd Dan"
)

private val roleOptions = listOf("Judoka", "Coach", "Referee")

private fun stringToRole(value: String): UserRole = when (value) {
    "Coach"   -> UserRole.COACH
    "Referee" -> UserRole.REFEREE
    else      -> UserRole.JUDOKA
}

private fun roleToString(role: UserRole): String = when (role) {
    UserRole.COACH   -> "Coach"
    UserRole.REFEREE -> "Referee"
    else             -> "Judoka"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    vm: SignUpViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState         by vm.uiState.collectAsStateWithLifecycle()
    val clubSuggestions by vm.clubSuggestions.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.isSignedUp) {
        if (uiState.isSignedUp) {
            snackbarHostState.showSnackbar("Account created! Please verify your email.")
            navigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NavyBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(NavyBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(48.dp))

                
                Text(
                    "Create Account",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Join the mat. Start your journey.",
                    color = Gold.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(32.dp))

                
                SignUpLabel("Full Name")
                Spacer(Modifier.height(6.dp))
                SignUpTextField(
                    value = uiState.fullName,
                    onValueChange = vm::onFullNameChange,
                    placeholder = "Your full name"
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SignUpLabel("Date of Birth")
                        Spacer(Modifier.height(6.dp))
                        SignUpTextField(
                            value = uiState.dateOfBirth,
                            onValueChange = vm::onDateOfBirthChange,
                            placeholder = "DD / MM / YYYY"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        SignUpLabel("Role")
                        Spacer(Modifier.height(6.dp))
                        SignUpDropdown(
                            value = roleToString(uiState.role),
                            options = roleOptions,
                            onSelected = { vm.onRoleChange(stringToRole(it)) },
                            placeholder = "Select role"
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                
                SignUpLabel("Email")
                Spacer(Modifier.height(6.dp))
                SignUpTextField(
                    value = uiState.email,
                    onValueChange = vm::onEmailChange,
                    placeholder = "your@email.com"
                )

                Spacer(Modifier.height(16.dp))

                
                SignUpLabel("Password")
                Spacer(Modifier.height(6.dp))
                SignUpTextField(
                    value = uiState.password,
                    onValueChange = vm::onPasswordChange,
                    placeholder = "••••••••",
                    isPassword = true
                )

                Spacer(Modifier.height(16.dp))

                
                SignUpLabel("Belt / Grade")
                Spacer(Modifier.height(6.dp))
                SignUpDropdown(
                    value = uiState.beltGrade,
                    options = beltOptions,
                    onSelected = vm::onBeltGradeChange,
                    placeholder = "Select your grade"
                )

                Spacer(Modifier.height(16.dp))

                
                SignUpLabel("Judo Club")
                Spacer(Modifier.height(6.dp))
                ClubSearchField(
                    value = uiState.judoClub,
                    onValueChange = vm::onJudoClubChange,
                    suggestions = clubSuggestions,
                    onSuggestionSelected = vm::selectClub
                )

                Spacer(Modifier.height(32.dp))

                
                Button(
                    onClick = {
                        keyboard?.hide()
                        vm.signUp()
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Register",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Already have an account? ",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                    TextButton(
                        onClick = navigateBack,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Sign in",
                            color = Gold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClubSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("Type to search your club…", color = Color.Gray, fontSize = 14.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Gold,
                unfocusedBorderColor = FieldBorder,
                cursorColor = Gold,
                focusedContainerColor = SurfaceBg,
                unfocusedContainerColor = SurfaceBg
            ),
            singleLine = true
        )

        if (suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBg),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column {
                    suggestions.forEach { club ->
                        Text(
                            text = club,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionSelected(club) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        if (club != suggestions.last()) {
                            HorizontalDivider(color = FieldBorder)
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun SignUpLabel(text: String) {
    Text(
        text,
        color = Gold,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Gold,
            unfocusedBorderColor = FieldBorder,
            cursorColor = Gold,
            focusedContainerColor = SurfaceBg,
            unfocusedContainerColor = SurfaceBg
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpDropdown(
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Gold,
                unfocusedBorderColor = FieldBorder,
                cursorColor = Gold,
                focusedContainerColor = SurfaceBg,
                unfocusedContainerColor = SurfaceBg,
                focusedTrailingIconColor = Gold,
                unfocusedTrailingIconColor = Color.Gray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceBg)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White, fontSize = 14.sp) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}