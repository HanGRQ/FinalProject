package com.example.finalproject.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.finalproject.models.OpenFoodFactsResponse

interface OpenFoodFactsService {
    @GET("api/v2/product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String): Response<OpenFoodFactsResponse>
}