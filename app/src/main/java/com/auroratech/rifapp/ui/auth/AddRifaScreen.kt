package com.auroratech.rifapp.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.auroratech.rifapp.model.Raffle
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddRifaScreen(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    // 🔹 Selector de imagen
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Nueva Rifa", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la rifa") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // 🔹 Mostrar imagen seleccionada (si existe)
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .size(180.dp)
                    .padding(8.dp)
            )
        }

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Seleccionar imagen")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (titulo.isBlank() || descripcion.isBlank()) return@Button
                uploading = true

                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val currentUserId = user?.uid ?: "anonimo"

                // 🔹 Si hay imagen, la subimos primero
                if (imageUri != null) {
                    val imageRef = storage.reference.child("raffle_images/${UUID.randomUUID()}.jpg")
                    imageRef.putFile(imageUri!!)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                saveRaffle(
                                    navController,
                                    firestore,
                                    titulo,
                                    descripcion,
                                    precio,
                                    currentUserId,
                                    currentDate,
                                    downloadUrl.toString()
                                )
                                uploading = false
                            }
                        }
                        .addOnFailureListener {
                            uploading = false
                        }
                } else {
                    saveRaffle(
                        navController,
                        firestore,
                        titulo,
                        descripcion,
                        precio,
                        currentUserId,
                        currentDate,
                        ""
                    )
                    uploading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uploading
        ) {
            Text(if (uploading) "Guardando..." else "Guardar Rifa")
        }
    }
}

// 🔹 Guardar Rifa en Firestore
private fun saveRaffle(
    navController: NavController,
    firestore: FirebaseFirestore,
    titulo: String,
    descripcion: String,
    precio: String,
    creatorId: String,
    fecha: String,
    imageUrl: String
) {
    val nuevaRifa = Raffle(
        id = UUID.randomUUID().toString(),
        name = titulo,
        description = descripcion,
        price = precio.toDoubleOrNull() ?: 0.0,
        creatorId = creatorId,
        date = fecha,
        imageUrl = imageUrl,
        timestamp = System.currentTimeMillis()
    )

    firestore.collection("raffles")
        .document(nuevaRifa.id)
        .set(nuevaRifa)
        .addOnSuccessListener {
            navController.popBackStack() // volver a la lista
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}
