package com.anislayaida.judoapp.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

private val NavyBg      = Color(0xFF0D1B3E)
private val SurfaceBg   = Color(0xFF1A2B55)
private val Gold        = Color(0xFFC9A84C)
private val AppRed      = Color(0xFFC8102E)
private val FieldBorder = Color(0xFF2A3F70)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    vm: LoginViewModel = hiltViewModel(),
    updateRoleForUser: (UserRole) -> Unit,
    navigateToSignUpScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboard = LocalSoftwareKeyboardController.current

    
    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            updateRoleForUser(uiState.userRole)
            navigateToHomeScreen()
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(60.dp))

                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AppRed, Color(0xFF8B0000))
                            )
                        )
                        .border(2.dp, Gold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "柔",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "myJudo",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    "C O M P A N I O N   A P P",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    letterSpacing = 3.sp
                )

                Spacer(Modifier.height(48.dp))

                
                FieldLabel("Email")
                Spacer(Modifier.height(6.dp))
                StyledTextField(
                    value = uiState.email,
                    onValueChange = vm::onEmailChange,
                    placeholder = "your@email.com",
                    isPassword = false
                )

                Spacer(Modifier.height(16.dp))

                
                FieldLabel("Password")
                Spacer(Modifier.height(6.dp))
                StyledTextField(
                    value = uiState.password,
                    onValueChange = vm::onPasswordChange,
                    placeholder = "••••••••",
                    isPassword = true
                )

                Spacer(Modifier.height(8.dp))

                Spacer(Modifier.height(8.dp))

                
                Button(
                    onClick = {
                        keyboard?.hide()
                        vm.signIn()
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
                            "Sign In",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorder)
                    Text("  or  ", color = Color.Gray, fontSize = 13.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorder)
                }

                Spacer(Modifier.height(20.dp))

                
                OutlinedButton(
                    onClick = navigateToSignUpScreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FieldBorder)
                ) {
                    Text("Create an Account", color = Color.White, fontSize = 15.sp)
                }

                Spacer(Modifier.weight(1f))

                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        "British Judo Association",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(Modifier.size(24.dp, 2.dp).background(AppRed))
                        Spacer(Modifier.width(2.dp))
                        Box(Modifier.size(24.dp, 2.dp).background(Color.White.copy(alpha = 0.4f)))
                        Spacer(Modifier.width(2.dp))
                        Box(Modifier.size(24.dp, 2.dp).background(Color(0xFF003087)))
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        color = Gold,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean
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