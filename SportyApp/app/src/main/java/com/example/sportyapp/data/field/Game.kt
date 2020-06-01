package com.example.sportyapp.data.field

data class Game(
    val id: Long,
    val duration: Long,
    val date: Long,
    val owner: Long,
    val players: List<Long>,
    val isPublic: Boolean,
    val fieldID: Long,
    val sport: Long
)