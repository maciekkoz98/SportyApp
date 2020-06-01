package com.example.sportyapp.data.game

data class Game (
    val id: Long,
    val duration: Long,
    val date: Long,
    val owner: Long,
    val players: List<Int>,
    val isPublic: Boolean,
    val fieldID: Long,
    val sport: Long
)