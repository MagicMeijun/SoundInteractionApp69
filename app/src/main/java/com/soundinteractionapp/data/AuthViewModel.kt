// file: data/AuthViewModel.kt
package com.soundinteractionapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // App 啟動時檢查登入狀態
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        _authState.value = AuthState.Loading
        if (auth.currentUser != null) {
            _currentUser.value = auth.currentUser
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // 註冊
    fun signUp(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
                _authState.value = AuthState.Authenticated
                onComplete(true, null)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "註冊失敗")
                onComplete(false, e.message)
            }
        }
    }

    // 登入
    fun signIn(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
                _authState.value = AuthState.Authenticated
                onComplete(true, null)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "登入失敗")
                onComplete(false, e.message)
            }
        }
    }

    // 匿名登入（訪客）
    fun signInAnonymously(onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInAnonymously().await()
                _currentUser.value = result.user
                _authState.value = AuthState.Authenticated
                onComplete(true, null)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "訪客登入失敗")
                onComplete(false, e.message)
            }
        }
    }

    // 登出
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }
}