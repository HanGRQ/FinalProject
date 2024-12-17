package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {
    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onBarcodeDetected(barcode: String) {
        viewModelScope.launch {
            try {
                _scanResult.value = barcode
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun resetScan() {
        _scanResult.value = null
        _error.value = null
    }
}