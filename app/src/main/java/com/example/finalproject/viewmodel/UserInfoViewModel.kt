package com.example.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        auth.currentUser?.uid?.let { uid ->
            _userId.value = uid
            fetchUserName(uid)
        }
    }

    fun setUserId(uid: String) {
        _userId.value = uid
        fetchUserName(uid)
    }

    private fun fetchUserName(uid: String) {
        viewModelScope.launch {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: "User"
                    _userName.value = name
                }
        }
    }
}
