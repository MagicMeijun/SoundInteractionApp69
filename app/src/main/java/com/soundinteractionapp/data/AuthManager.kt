package com.soundinteractionapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * 管理使用者登入、註冊與資料存取的類別。
 */
class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 獲取當前使用者
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // 檢查是否已登入
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // 匿名登入 (快速開始)
    suspend fun signInAnonymously(): Result<FirebaseUser?> {
        return try {
            val result = auth.signInAnonymously().await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 註冊帳號 (Email/Password)
    suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            // 儲存使用者額外資料 (如名字) 到 Firestore
            if (user != null) {
                saveUserData(user.uid, name)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 登入帳號
    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 登出
    fun signOut() {
        auth.signOut()
    }

    // 儲存使用者資料到 Firestore
    private suspend fun saveUserData(uid: String, name: String) {
        val userMap = hashMapOf(
            "name" to name,
            "created_at" to System.currentTimeMillis()
        )
        try {
            db.collection("users").document(uid).set(userMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 獲取使用者名字
    suspend fun getUserName(uid: String): String? {
        return try {
            val document = db.collection("users").document(uid).get().await()
            document.getString("name")
        } catch (e: Exception) {
            null
        }
    }

    // 更新使用者資料
    suspend fun updateUserData(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 獲取完整使用者資料
    suspend fun getUserData(uid: String): Result<Map<String, Any>?> {
        return try {
            val document = db.collection("users").document(uid).get().await()
            Result.success(document.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}