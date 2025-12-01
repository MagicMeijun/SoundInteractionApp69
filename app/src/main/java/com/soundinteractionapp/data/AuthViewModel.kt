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
    object Initial : AuthState()  // ✅ 添加 Initial 状态
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

    // ✅ 改进的注册方法 - 返回更详细的错误信息
    fun signUp(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
                _authState.value = AuthState.Authenticated
                onComplete(true, null)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("email address is already in use") == true ->
                        "此電子郵件已被註冊"
                    e.message?.contains("password") == true ->
                        "密碼格式不正確，至少需要 6 個字元"
                    e.message?.contains("email") == true ->
                        "電子郵件格式不正確"
                    e.message?.contains("network") == true ->
                        "網路連接失敗，請檢查網路"
                    else -> e.message ?: "註冊失敗"
                }
                _authState.value = AuthState.Error(errorMessage)
                onComplete(false, errorMessage)
            }
        }
    }

    // ✅ 改进的登入方法 - 返回更详细的错误信息
    fun signIn(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
                _authState.value = AuthState.Authenticated
                onComplete(true, null)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("no user record") == true ||
                            e.message?.contains("user not found") == true ->
                        "此電子郵件尚未註冊"
                    e.message?.contains("password is invalid") == true ||
                            e.message?.contains("wrong-password") == true ->
                        "密碼錯誤"
                    e.message?.contains("email") == true ->
                        "電子郵件格式不正確"
                    e.message?.contains("network") == true ->
                        "網路連接失敗，請檢查網路"
                    else -> e.message ?: "登入失敗"
                }
                _authState.value = AuthState.Error(errorMessage)
                onComplete(false, errorMessage)
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
                val errorMessage = e.message ?: "訪客登入失敗"
                _authState.value = AuthState.Error(errorMessage)
                onComplete(false, errorMessage)
            }
        }
    }

    // 登出
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    // ✅ 新增：重置認證狀態（用於清除错误信息）
    fun resetAuthState() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    // ✅ 新增：检查是否已登入
    fun isLoggedIn(): Boolean {
        return _currentUser.value != null && auth.currentUser != null
    }

    // ✅ 新增：获取当前用户邮箱
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    // ✅ 新增：检查是否为匿名用户
    fun isAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous == true
    }
}