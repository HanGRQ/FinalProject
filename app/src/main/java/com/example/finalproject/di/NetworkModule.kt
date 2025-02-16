package com.example.finalproject.di

import com.example.finalproject.api.OpenFoodFactsService
import com.example.finalproject.repository.FoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOpenFoodFactsService(): OpenFoodFactsService {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsService::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodRepository(
        service: OpenFoodFactsService,
        firestore: FirebaseFirestore
    ): FoodRepository {
        return FoodRepository(service, firestore)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}