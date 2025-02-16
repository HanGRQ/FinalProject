package com.example.finalproject.models

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponse(
    val code: String,
    val product: ProductData
)

data class ProductData(
    val product_name: String?,
    val nutriments: Nutriments?
)

data class Nutriments(
    @SerializedName("energy-kcal_100g") val energyKcal: Double?,   // 正确的字段名
    @SerializedName("energy-kj_100g") val energyKj: Double?,     // 正确的字段名
    @SerializedName("carbohydrates_100g") val carbohydrates: Double?,
    @SerializedName("sugars_100g") val sugars: Double?,          // 文档中是 sugars 不是 sugar
    @SerializedName("fat_100g") val fat: Double?,
    @SerializedName("proteins_100g") val proteins: Double?
)