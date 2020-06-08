package com.example.sportyapp.data.field

data class Field(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val sportsHall: Boolean,
    val disciplines: ArrayList<Int>
)