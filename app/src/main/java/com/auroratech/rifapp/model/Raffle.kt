package com.auroratech.rifapp.model

data class Raffle(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val date: String = "",
    val creatorId: String = "",
    val description: String = "",
    val imageUrl: String = "",       // 🔹 NUEVO CAMPO
    val timestamp: Long = 0L      // 🔹 agregado
)
