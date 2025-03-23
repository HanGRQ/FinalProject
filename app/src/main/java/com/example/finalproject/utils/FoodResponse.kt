package com.example.finalproject.utils

data class FoodResponse(
    val barcode: String = "",
    val product_name: String = "",
    val energy_kj: Double = 0.0,
    val energy_kcal: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val sugars: Double = 0.0,
    val fat: Double = 0.0,
    val proteins: Double = 0.0,
    val scan_date: String = ""
)