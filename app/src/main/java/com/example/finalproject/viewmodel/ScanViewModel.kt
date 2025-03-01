package com.example.finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.repository.FoodRepository
import com.example.finalproject.ui.state.ScanState
import com.example.finalproject.utils.FoodResponse
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

    /**
     * 处理条形码检测，将数据存入用户专属 `users/{userId}/scanned_foods`
     */
    fun onBarcodeDetected(userId: String, barcode: String) {
        viewModelScope.launch {
            try {
                _scanState.value = ScanState.Loading

                foodRepository.getFoodDetails(barcode)
                    .onSuccess { food ->
                        // ✅ 确保存入当前用户的 `scanned_foods` 集合
                        foodRepository.saveFoodToUserFirestore(userId, food)

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



    /**
     * 重置扫描状态
     */
    fun resetScan() {
        _scanResult.value = null
        _scanState.value = ScanState.Idle
    }


}
