package com.auroratech.rifapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaffleParticipantsScreen(
    navController: NavController,
    raffleId: String,
    raffleTitle: String
) {
    val firestore = FirebaseFirestore.getInstance()
    var participants by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // 🔹 Escuchar los participantes de esa rifa
    LaunchedEffect(raffleId) {
        firestore.collection("raffle_participants")
            .whereEqualTo("raffleId", raffleId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    participants = it.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        data + ("id" to doc.id)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Participantes - $raffleTitle") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (participants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no hay participantes 😕")
                }
            } else {
                LazyColumn {
                    items(participants) { participant ->
                        ParticipantCard(participant, firestore)
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantCard(participant: Map<String, Any>, firestore: FirebaseFirestore) {
    var paid by remember { mutableStateOf(participant["paid"] as? Boolean ?: false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("🎟️ Número: ${participant["selectedNumber"]}")
            Text("👤 ${participant["userEmail"]}")
            Text("Estado: ${if (paid) "Pagado ✅" else "Pendiente 💸"}")

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Marcar como pagado:")
                Switch(
                    checked = paid,
                    onCheckedChange = { newValue ->
                        paid = newValue
                        participant["id"]?.let { docId ->
                            firestore.collection("raffle_participants")
                                .document(docId.toString())
                                .update("paid", newValue)
                        }
                    }
                )
            }
        }
    }
}
