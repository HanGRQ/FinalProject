package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserInfoViewModel : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userGoal = MutableStateFlow("")
    val userGoal: StateFlow<String> = _userGoal.asStateFlow()

    private val _userGender = MutableStateFlow("")
    val userGender: StateFlow<String> = _userGender.asStateFlow()

    private val _userHeight = MutableStateFlow(0f)
    val userHeight: StateFlow<Float> = _userHeight.asStateFlow()

    private val _userWeight = MutableStateFlow(0f)
    val userWeight: StateFlow<Float> = _userWeight.asStateFlow()

    private val _targetWeight = MutableStateFlow(0f)
    val targetWeight: StateFlow<Float> = _targetWeight.asStateFlow()

    // 更新函数
    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun updateUserGoal(goal: String) {
        _userGoal.value = goal
    }

    fun updateUserGender(gender: String) {
        _userGender.value = gender
    }

    fun updateUserHeight(height: Float) {
        _userHeight.value = height
    }

    fun updateUserWeight(weight: Float) {
        _userWeight.value = weight
    }

    fun updateTargetWeight(weight: Float) {
        _targetWeight.value = weight
    }

    fun saveUserInfo() {
        println("Saving user info:")
        println("Name: ${userName.value}")
        println("Goal: ${userGoal.value}")
        println("Gender: ${userGender.value}")
        println("Height: ${userHeight.value}")
        println("Weight: ${userWeight.value}")
        println("Target Weight: ${targetWeight.value}")
    }
}