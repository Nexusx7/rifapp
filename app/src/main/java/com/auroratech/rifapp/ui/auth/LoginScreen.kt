package com.auroratech.rifapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auroratech.rifapp.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onPhoneLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // 🔹 Detecta si el modo actual es claro u oscuro
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val textColor = if (isLightTheme) Color.Black else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 🔹 Logo o imagen principal
        Image(
            painter = painterResource(id = R.drawable.bicicleta),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Iniciar sesión",
            color = textColor,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // 🔹 Campo correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", color = textColor) },
            textStyle = LocalTextStyle.current.copy(color = textColor),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = textColor,
                unfocusedBorderColor = textColor.copy(alpha = 0.5f),
                cursorColor = textColor,
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        )

        // 🔹 Campo contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = textColor) },
            textStyle = LocalTextStyle.current.copy(color = textColor),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = textColor,
                unfocusedBorderColor = textColor.copy(alpha = 0.5f),
                cursorColor = textColor,
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        )

        Spacer(Modifier.height(16.dp))

        // 🔹 Botón principal
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor ingresa correo y contraseña"
                    return@Button
                }

                loading = true
                errorMessage = null

                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener { task ->
                        loading = false
                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            errorMessage = task.exception?.message
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(Modifier.height(12.dp))

        // 🔹 Botón para login con teléfono
        OutlinedButton(
            onClick = onPhoneLogin,
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text("Iniciar con número de teléfono", color = textColor)
        }

        Spacer(Modifier.height(12.dp))

        // 🔹 Enlace para registrarse
        TextButton(
            onClick = onNavigateToRegister,
            enabled = !loading
        ) {
            Text("¿No tienes cuenta? Regístrate aquí", color = textColor)
        }

        // 🔹 Mensaje de error
        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = Color.Red, textAlign = TextAlign.Center)
        }
    }
}
