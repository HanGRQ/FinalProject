package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.repository.FoodRepository
import com.example.finalproject.ui.state.ScanState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult.asStateFlow()

    fun onBarcodeDetected(barcode: String) {
        viewModelScope.launch {
            try {
                _scanState.value = ScanState.Loading

                foodRepository.getFoodDetails(barcode)
                    .onSuccess { food ->
                        foodRepository.saveFoodToFirestore(food)
                        _scanState.value = ScanState.Success(food)
                        _scanResult.value = barcode
                    }
                    .onFailure { error ->
                        _scanState.value = ScanState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetScan() {
        _scanResult.value = null
        _scanState.value = ScanState.Idle
    }
}