package com.example.finalproject.ui.state

import com.example.finalproject.utils.FoodResponse

sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Success(val food: FoodResponse) : ScanState()
    data class Error(val message: String) : ScanState()
}