package com.soundinteractionapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val isAnonymous: Boolean) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String?) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        _authState.value = AuthState.Loading
        val user = auth.currentUser
        if (user != null) {
            _currentUser.value = user
            _authState.value = AuthState.Authenticated(isAnonymous = user.isAnonymous)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // ✅ 註冊：帳號格式驗證（英數混合）
    fun signUp(account: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // ✅ 驗證帳號格式
                if (!isValidAccount(account)) {
                    throw Exception("帳號格式錯誤：需包含英文和數字，至少 4 個字元")
                }

                // ✅ 使用「帳號@app.local」作為 Firebase Auth 的 email
                val result = auth.createUserWithEmailAndPassword("$account@app.local", password).await()
                val user = result.user ?: throw Exception("帳號建立失敗")

                _currentUser.value = user

                // ✅ 建立 Firestore 資料
                try {
                    createUserProfile(user, account)
                } catch (e: Exception) {
                    try {
                        user.delete().await()
                    } catch (deleteError: Exception) {
                        deleteError.printStackTrace()
                    }
                    throw Exception("建立用戶資料失敗: ${e.message}")
                }

                _authState.value = AuthState.Authenticated(isAnonymous = false)
                onComplete(true, null)

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("email address is already in use", true) == true ->
                        "此帳號已被註冊"
                    e.message?.contains("帳號格式錯誤", true) == true ->
                        e.message
                    e.message?.contains("password", true) == true ->
                        "密碼格式不正確，至少需要 6 個字元"
                    e.message?.contains("network", true) == true ->
                        "網路連接失敗，請檢查網路"
                    else -> e.message ?: "註冊失敗"
                }
                _authState.value = AuthState.Error(errorMessage)
                onComplete(false, errorMessage)
            }
        }
    }

    // ✅ 登入：使用帳號登入
    fun signIn(account: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword("$account@app.local", password).await()
                _currentUser.value = result.user

                result.user?.let { user ->
                    ensureUserProfileExists(user, account)
                }

                _authState.value = AuthState.Authenticated(isAnonymous = false)
                onComplete(true, null)

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("no user record", true) == true ||
                            e.message?.contains("user not found", true) == true ->
                        "此帳號尚未註冊"
                    e.message?.contains("password is invalid", true) == true ||
                            e.message?.contains("wrong-password", true) == true ||
                            e.message?.contains("invalid-credential", true) == true ->
                        "密碼錯誤"
                    e.message?.contains("network", true) == true ->
                        "網路連接失敗，請檢查網路"
                    else -> e.message ?: "登入失敗"
                }
                _authState.value = AuthState.Error(errorMessage)
                onComplete(false, errorMessage)
            }
        }
    }

    fun signInAnonymously(onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInAnonymously().await()
                _currentUser.value = result.user

                _authState.value = AuthState.Authenticated(isAnonymous = true)
                onComplete(true, null)

            } catch (e: Exception) {
                val errorMessage = e.message ?: "訪客登入失敗"
                _authState.value = AuthState.Error(errorMessage)
                onComplete(false, errorMessage)
            }
        }
    }

    // ✅ 建立使用者資料（使用 account）
    private suspend fun createUserProfile(user: FirebaseUser, account: String) {
        val userProfile = hashMapOf(
            "uid" to user.uid,
            "account" to account,  // ✅ 儲存帳號
            "displayName" to account,  // ✅ 預設暱稱為帳號
            "photoUrl" to "",
            "bio" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
            "badges" to emptyList<String>()
        )

        firestore.collection("users")
            .document(user.uid)
            .set(userProfile)
            .await()
    }

    private suspend fun ensureUserProfileExists(user: FirebaseUser, account: String) {
        try {
            val doc = firestore.collection("users").document(user.uid).get().await()

            if (!doc.exists()) {
                createUserProfile(user, account)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ✅ 帳號格式驗證：至少 4 字元，需包含英文和數字
    private fun isValidAccount(account: String): Boolean {
        if (account.length < 4) return false
        val hasLetter = account.any { it.isLetter() }
        val hasDigit = account.any { it.isDigit() }
        val isAlphanumeric = account.all { it.isLetterOrDigit() }
        return hasLetter && hasDigit && isAlphanumeric
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    fun resetAuthState() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun isLoggedIn(): Boolean {
        return _currentUser.value != null && auth.currentUser != null
    }

    fun getCurrentUserDisplayName(): String {
        val user = auth.currentUser ?: return "訪客"
        return if (user.isAnonymous) {
            "訪客"
        } else {
            user.email?.substringBefore("@") ?: "使用者"
        }
    }

    fun isAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous == true
    }
}