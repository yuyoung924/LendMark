package com.example.lendmark.viewmodel

import androidx.lifecycle.*
import com.example.lendmark.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _foundEmail = MutableLiveData<String?>()
    val foundEmail: LiveData<String?> = _foundEmail

    fun findEmailByPhone(phone: String) {
        viewModelScope.launch {
            val email = userRepository.findEmailByPhone(phone)
            _foundEmail.value = email
        }
    }


    private val _resetPwResult = MutableLiveData<Boolean>()
    val resetPwResult: LiveData<Boolean> = _resetPwResult

    fun sendPasswordReset(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _resetPwResult.value = task.isSuccessful
            }
    }


}
