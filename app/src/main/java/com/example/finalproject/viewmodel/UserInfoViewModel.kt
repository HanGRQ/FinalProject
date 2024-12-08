package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class UserInfoViewModel : ViewModel() {
    var userName by mutableStateOf("")
    var userGoal by mutableStateOf("")
    var userGender by mutableStateOf("")
    var userHeight by mutableStateOf(175) // 默认值
    var userWeight by mutableStateOf(60)  // 默认值
    var targetWeight by mutableStateOf(72) // 默认值
}
