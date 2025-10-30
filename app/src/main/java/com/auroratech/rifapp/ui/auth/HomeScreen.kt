package com.auroratech.rifapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.auroratech.rifapp.model.Raffle
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.Image
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var raffles by remember { mutableStateOf<List<Raffle>>(emptyList()) }

    // 🔹 Cargar TODAS las rifas disponibles
    LaunchedEffect(Unit) {
        firestore.collection("raffles")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    raffles = it.toObjects(Raffle::class.java)
                }
            }
    }

    Scaffold(
        // 🔹 Barra superior con el icono de configuración
        topBar = {
            TopAppBar(
                title = { Text("RifApp", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        // 🔹 Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bienvenido a RifApp",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("¡Hola! Has iniciado sesión correctamente 🎉")

            Spacer(modifier = Modifier.height(24.dp))

            // 🔹 Navega a Mis Rifas
            Button(
                onClick = { navController.navigate("my_raffles") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mis rifas")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: Rifas en las que participo */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rifas en las que participo")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: Rifas disponibles */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rifas disponibles")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 Título sección rifas disponibles
            Text(
                text = "Rifas disponibles 🎟️",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 🔹 Lista de rifas disponibles
            if (raffles.isEmpty()) {
                Text("Aún no hay rifas disponibles 😕")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(raffles) { raffle ->
                        RaffleCard(raffle)
                    }
                }
            }
        }
    }
}

@Composable
fun RaffleCard(raffle: Raffle) {
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(Modifier.padding(all = 16.dp)) {

            // 🔹 Imagen de la rifa (si existe)
            if (raffle.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = raffle.imageUrl),
                    contentDescription = "Imagen de la rifa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 8.dp)
                )
            }

            // 🔹 Datos de la rifa
            Text(raffle.name, style = MaterialTheme.typography.titleMedium)
            Text("Descripción: ${raffle.description}")
            Text("Valor: $${raffle.price}")
            Text("Fecha: ${raffle.date}")

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Botón para comprar
            Button(
                onClick = {
                    user?.uid?.let { uid: String ->
                        val participantData = mapOf(
                            "raffleId" to raffle.id,
                            "userId" to uid,
                            "raffleName" to raffle.name,
                            "price" to raffle.price,
                            "date" to raffle.date,
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("raffle_participants")
                            .add(participantData)
                            .addOnSuccessListener {
                                println("✅ Participación guardada correctamente")
                            }
                            .addOnFailureListener { e ->
                                println("❌ Error al guardar participación: ${e.message}")
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comprar 🎟️")
            }
        }
    }
}
