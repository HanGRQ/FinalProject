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

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _userHeight = MutableStateFlow("")
    val userHeight: StateFlow<String> = _userHeight

    private val _userWeight = MutableStateFlow("")
    val userWeight: StateFlow<String> = _userWeight

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        auth.currentUser?.let { user ->
            _userId.value = user.uid
            _userEmail.value = user.email ?: "Unknown Email"
            fetchUserInfo(user.uid)
        }
    }

    fun setUserId(uid: String?) {
        _userId.value = uid
        if (uid != null) {
            fetchUserInfo(uid)
        }
    }

    private fun fetchUserInfo(uid: String) {
        viewModelScope.launch {
            db.collection("users").document(uid)
                .collection("user_settings")
                .document("height")
                .get()
                .addOnSuccessListener { document ->
                    _userHeight.value = document.getDouble("value")?.toString() ?: "--"
                }

            db.collection("users").document(uid)
                .collection("weight_entries")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val latestWeight = result.documents.first().getDouble("weight")
                        _userWeight.value = latestWeight?.toString() ?: "--"
                    } else {
                        _userWeight.value = "--"
                    }
                }
        }
    }
}
