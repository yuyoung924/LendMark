package com.example.lendmark.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lendmark.utils.Event

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 🔥 로그인 성공 시 Boolean이 아닌 uid를 반환해야 함
    private val _loginResult = MutableLiveData<Event<String?>>()
    val loginResult: LiveData<Event<String?>> get() = _loginResult

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    // 🔐 로그인
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = Event("Please enter both email and password.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid

                if (uid == null) {
                    _errorMessage.value = Event("Login failed: UID is null.")
                    _loginResult.value = Event(null)
                    return@addOnSuccessListener
                }

                // Firestore 사용자 데이터 확인
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            // 🔥 여기서 uid를 넘김
                            _loginResult.value = Event(uid)
                        } else {
                            _errorMessage.value = Event("User data not found in Firestore.")
                            _loginResult.value = Event(null)
                        }
                    }
                    .addOnFailureListener {
                        _errorMessage.value = Event("Failed to fetch user data: ${it.message}")
                        _loginResult.value = Event(null)
                    }
            }
            .addOnFailureListener {
                _errorMessage.value = Event("Login failed: ${it.message}")
                _loginResult.value = Event(null)
            }
    }

    // 🔥 Firestore의 mustChangePassword 값 감시
    private val _mustChangePassword = MutableLiveData<Boolean>()
    val mustChangePassword: LiveData<Boolean> get() = _mustChangePassword

    fun checkMustChangePassword(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val need = doc.getBoolean("mustChangePassword") ?: false
                _mustChangePassword.value = need
            }
            .addOnFailureListener {
                _mustChangePassword.value = false
            }
    }
}
