package com.example.lendmark.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    // 전화번호로 userId 조회하는 함수
    suspend fun findEmailByPhone(phone: String): String? {
        val query = db.collection("users")
            .whereEqualTo("phone", phone)
            .get()
            .await()

        return if (!query.isEmpty) {
            query.documents[0].getString("email")
        } else {
            null
        }
    }


}
