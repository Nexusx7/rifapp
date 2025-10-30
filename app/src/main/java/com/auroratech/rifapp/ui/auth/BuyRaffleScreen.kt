package com.auroratech.rifapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyRaffleScreen(
    navController: NavController,
    raffleId: String,
    raffleTitle: String
) {
    val compra = mapOf(
        "raffleId" to raffleId,
        "raffleTitle" to raffleTitle,
        "userId" to user.uid,
        "userEmail" to user.email,
        "selectedNumber" to selectedNumber,
        "paid" to false, // 🔹 nuevo campo
        "timestamp" to System.currentTimeMillis()
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Comprar rifa 🎟️") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Rifa: $raffleTitle", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedNumber,
                onValueChange = { selectedNumber = it },
                label = { Text("Número elegido (1–100)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedNumber.isNotBlank() && user != null) {
                        loading = true
                        val compra = mapOf(
                            "raffleId" to raffleId,
                            "raffleTitle" to raffleTitle,
                            "userId" to user.uid,
                            "userEmail" to user.email,
                            "selectedNumber" to selectedNumber,
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("raffle_participants")
                            .add(compra)
                            .addOnSuccessListener {
                                loading = false
                                message = "✅ Compra registrada correctamente"
                            }
                            .addOnFailureListener { e ->
                                loading = false
                                message = "❌ Error al registrar la compra: ${e.message}"
                            }
                    } else {
                        message = "Por favor elige un número válido"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Guardando..." else "Confirmar compra 🎟️")
            }

            Spacer(Modifier.height(16.dp))
            if (message.isNotEmpty()) Text(message)
        }
    }
}
