package com.auroratech.rifapp.ui.auth

import com.auroratech.rifapp.model.Raffle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRifasScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var myRaffles by remember { mutableStateOf<List<Raffle>>(emptyList()) }

    // 🔹 Cargar rifas del usuario actual
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            firestore.collection("raffles")
                .whereEqualTo("creatorId", uid)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        myRaffles = it.toObjects(Raffle::class.java)
                    }
                }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_raffle") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar rifa")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Rifas") }
            )
        }
    ) { padding ->
        if (myRaffles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no has creado ninguna rifa 🎟️")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(myRaffles) { raffle ->
                    RaffleCardEditable(raffle, onDelete = {
                        firestore.collection("raffles").document(raffle.id).delete()
                    })
                }
            }
        }
    }
    // 🔹 Rifas en las que participo
    var purchasedRaffles by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            firestore.collection("raffle_participants")
                .whereEqualTo("userId", uid)
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        purchasedRaffles = it.documents.mapNotNull { doc -> doc.data }
                    }
                }
        }
    }

    Text(
        text = "Rifas en las que participo",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp)
    )

    if (purchasedRaffles.isEmpty()) {
        Text(
            text = "No has comprado rifas aún 😅",
            modifier = Modifier.padding(start = 16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(purchasedRaffles) { rifa ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.elevatedCardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🎟️ ${rifa["raffleTitle"]}")
                        Text("Número elegido: ${rifa["selectedNumber"]}")
                        Text("Comprador: ${rifa["userEmail"]}")
                    }
                }
            }
        }
    }
}

@Composable
fun RaffleCardEditable(raffle: Raffle, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (raffle.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(raffle.imageUrl),
                        contentDescription = "Imagen de la rifa",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 12.dp)
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text(raffle.name, style = MaterialTheme.typography.titleMedium)
                    Text("Valor: $${raffle.price}")
                    Text("Fecha: ${raffle.date}")
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar rifa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

