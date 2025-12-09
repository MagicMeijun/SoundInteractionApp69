package com.soundinteractionapp.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class UserProfile(
    val uid: String = "",
    val account: String = "",
    val displayName: String = "使用者",
    val photoUrl: String = "", // ✅ 儲存 Resource ID 的字串
    val bio: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val badges: List<String> = emptyList()
)

class ProfileViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous: StateFlow<Boolean> = _isAnonymous.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val user = auth.currentUser ?: return@launch

            _isAnonymous.value = user.isAnonymous

            if (user.isAnonymous) {
                _userProfile.value = UserProfile(
                    uid = user.uid,
                    account = "",
                    displayName = "訪客",
                    photoUrl = "",
                    bio = "",
                    createdAt = formatDate(System.currentTimeMillis()),
                    updatedAt = formatDate(System.currentTimeMillis()),
                    badges = emptyList()
                )
            } else {
                try {
                    val doc = firestore.collection("users").document(user.uid).get().await()

                    if (doc.exists()) {
                        val createdAtTimestamp = doc.getTimestamp("createdAt")
                        val updatedAtTimestamp = doc.getTimestamp("updatedAt")

                        val createdAtString = createdAtTimestamp?.let {
                            formatDate(it.toDate().time)
                        } ?: formatDate(System.currentTimeMillis())

                        val updatedAtString = updatedAtTimestamp?.let {
                            formatDate(it.toDate().time)
                        } ?: createdAtString

                        _userProfile.value = UserProfile(
                            uid = user.uid,
                            account = doc.getString("account") ?: "",
                            displayName = doc.getString("displayName") ?: "使用者",
                            photoUrl = doc.getString("photoUrl") ?: "",
                            bio = doc.getString("bio") ?: "",
                            createdAt = createdAtString,
                            updatedAt = updatedAtString,
                            badges = (doc.get("badges") as? List<*>)
                                ?.mapNotNull { it as? String }
                                ?: emptyList()
                        )
                    } else {
                        _userProfile.value = UserProfile(
                            uid = user.uid,
                            account = "",
                            displayName = "使用者",
                            photoUrl = "",
                            bio = "",
                            createdAt = formatDate(System.currentTimeMillis()),
                            updatedAt = formatDate(System.currentTimeMillis()),
                            badges = emptyList()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _userProfile.value = UserProfile(
                        uid = user.uid,
                        account = "",
                        displayName = "使用者",
                        photoUrl = "",
                        bio = "",
                        createdAt = formatDate(System.currentTimeMillis()),
                        updatedAt = formatDate(System.currentTimeMillis()),
                        badges = emptyList()
                    )
                }
            }
        }
    }

    // ✅ 新增：更新頭像（儲存 Resource ID）
    fun updateAvatar(avatarResIdString: String) {
        if (_isAnonymous.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser ?: return@launch

                // ✅ 儲存頭像 Resource ID 到 Firestore
                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "photoUrl" to avatarResIdString,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                // ✅ 更新本地狀態
                _userProfile.value = _userProfile.value.copy(
                    photoUrl = avatarResIdString,
                    updatedAt = formatDate(System.currentTimeMillis())
                )

                println("✅ [更新頭像] 成功: Resource ID = $avatarResIdString")
            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ [更新頭像] 失敗: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateDisplayName(newName: String) {
        if (_isAnonymous.value) return

        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: return@launch

                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "displayName" to newName,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                loadUserProfile()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateBio(newBio: String) {
        if (_isAnonymous.value) return

        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: return@launch

                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "bio" to newBio,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                _userProfile.value = _userProfile.value.copy(
                    bio = newBio,
                    updatedAt = formatDate(System.currentTimeMillis())
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ 保留舊的上傳方法（如果之後需要）
    fun uploadProfileImage(uri: Uri, context: Context) {
        if (_isAnonymous.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser ?: return@launch
                val storageRef = storage.reference
                    .child("profile_images/${user.uid}.jpg")

                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "photoUrl" to downloadUrl,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                    .await()

                _userProfile.value = _userProfile.value.copy(
                    photoUrl = downloadUrl,
                    updatedAt = formatDate(System.currentTimeMillis())
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (_isAnonymous.value) {
            onResult(false, "訪客無法變更密碼")
            return
        }

        if (oldPassword == newPassword) {
            onResult(false, "新密碼不能與目前密碼相同")
            return
        }

        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: run {
                    onResult(false, "使用者未登入")
                    return@launch
                }

                val account = _userProfile.value.account
                if (account.isEmpty()) {
                    onResult(false, "無法取得帳號資訊")
                    return@launch
                }

                try {
                    println("🔐 [變更密碼] 開始驗證目前密碼...")
                    println("🔐 [變更密碼] 帳號: $account@app.local")

                    val credential = EmailAuthProvider.getCredential("$account@app.local", oldPassword)
                    user.reauthenticate(credential).await()

                    println("✅ [變更密碼] 目前密碼驗證成功")
                } catch (e: Exception) {
                    println("❌ [變更密碼] 驗證失敗: ${e.message}")
                    e.printStackTrace()

                    val errorMessage = when {
                        e.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true ||
                                e.message?.contains("invalid-credential", ignoreCase = true) == true ||
                                e.message?.contains("auth credential is incorrect", ignoreCase = true) == true ||
                                e.message?.contains("credential is incorrect", ignoreCase = true) == true ||
                                e.message?.contains("password is invalid", ignoreCase = true) == true ||
                                e.message?.contains("wrong-password", ignoreCase = true) == true ||
                                e.message?.contains("invalid-password", ignoreCase = true) == true ->
                            "目前密碼錯誤，請重新輸入"

                        e.message?.contains("user-not-found", ignoreCase = true) == true ->
                            "帳號不存在"

                        e.message?.contains("too-many-requests", ignoreCase = true) == true ->
                            "嘗試次數過多，請稍後再試"

                        e.message?.contains("network", ignoreCase = true) == true ->
                            "網路連接失敗，請檢查網路"

                        e.message?.contains("malformed", ignoreCase = true) == true ->
                            "驗證資料格式錯誤，請重新登入後再試"

                        e.message?.contains("expired", ignoreCase = true) == true ->
                            "登入已過期，請重新登入後再試"

                        else -> "目前密碼驗證失敗，請確認密碼是否正確"
                    }
                    onResult(false, errorMessage)
                    return@launch
                }

                try {
                    user.updatePassword(newPassword).await()
                    println("✅ [變更密碼] 密碼更新成功")
                } catch (e: Exception) {
                    println("❌ [變更密碼] 更新失敗: ${e.message}")

                    val errorMessage = when {
                        e.message?.contains("weak-password", ignoreCase = true) == true ->
                            "新密碼強度不足"

                        e.message?.contains("requires-recent-login", ignoreCase = true) == true ->
                            "登入時間過久，請重新登入後再試"

                        else -> "密碼更新失敗，請稍後再試"
                    }
                    onResult(false, errorMessage)
                    return@launch
                }

                try {
                    firestore.collection("users").document(user.uid)
                        .update("updatedAt", FieldValue.serverTimestamp())
                        .await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                onResult(true, null)

            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = when {
                    e.message?.contains("network", ignoreCase = true) == true -> "網路連接失敗"
                    else -> "操作失敗：${e.localizedMessage ?: "未知錯誤"}"
                }
                onResult(false, errorMessage)
            }
        }
    }

    fun deleteAccount(
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (_isAnonymous.value) {
            onResult(false, "訪客無法刪除帳號")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = auth.currentUser ?: run {
                    _isLoading.value = false
                    onResult(false, "使用者未登入")
                    return@launch
                }

                val uid = user.uid
                val account = _userProfile.value.account

                if (account.isEmpty()) {
                    _isLoading.value = false
                    onResult(false, "無法取得帳號資訊")
                    return@launch
                }

                try {
                    println("🔐 [刪除帳號] 開始重新驗證用戶...")
                    println("🔐 [刪除帳號] 帳號: $account@app.local")
                    println("🔐 [刪除帳號] UID: $uid")

                    val credential = EmailAuthProvider.getCredential("$account@app.local", password)
                    user.reauthenticate(credential).await()

                    println("✅ [刪除帳號] 重新驗證成功")
                } catch (e: Exception) {
                    println("❌ [刪除帳號] 重新驗證失敗: ${e.message}")
                    println("❌ [刪除帳號] 錯誤類型: ${e.javaClass.simpleName}")
                    e.printStackTrace()

                    val errorMessage = when {
                        e.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true ||
                                e.message?.contains("invalid-credential", ignoreCase = true) == true ||
                                e.message?.contains("auth credential is incorrect", ignoreCase = true) == true ||
                                e.message?.contains("credential is incorrect", ignoreCase = true) == true ||
                                e.message?.contains("password is invalid", ignoreCase = true) == true ||
                                e.message?.contains("wrong-password", ignoreCase = true) == true ||
                                e.message?.contains("invalid-password", ignoreCase = true) == true ->
                            "密碼錯誤，請重新輸入"

                        e.message?.contains("user-not-found", ignoreCase = true) == true ->
                            "帳號不存在"

                        e.message?.contains("too-many-requests", ignoreCase = true) == true ->
                            "嘗試次數過多，請稍後再試"

                        e.message?.contains("network", ignoreCase = true) == true ->
                            "網路連接失敗，請檢查網路"

                        e.message?.contains("malformed", ignoreCase = true) == true ->
                            "驗證資料格式錯誤，請重新登入後再試"

                        e.message?.contains("expired", ignoreCase = true) == true ||
                                e.message?.contains("has expired", ignoreCase = true) == true ->
                            "登入已過期，請重新登入後再試"

                        else -> "密碼驗證失敗，請確認密碼是否正確"
                    }

                    _isLoading.value = false
                    onResult(false, errorMessage)
                    return@launch
                }

                var firestoreDeleted = false
                var retryCount = 0
                val maxRetries = 3

                while (!firestoreDeleted && retryCount < maxRetries) {
                    try {
                        println("🔍 [刪除帳號] [嘗試 ${retryCount + 1}/$maxRetries] 開始刪除 Firestore 資料...")
                        println("🔍 [刪除帳號] User UID: $uid")
                        println("🔍 [刪除帳號] Document Path: users/$uid")

                        val docRef = firestore.collection("users").document(uid)

                        val docSnapshot = docRef.get().await()
                        if (docSnapshot.exists()) {
                            println("✅ [刪除帳號] 文檔存在，準備刪除")
                            docRef.delete().await()

                            kotlinx.coroutines.delay(500)
                            val verifyDoc = docRef.get().await()
                            if (!verifyDoc.exists()) {
                                println("✅ [刪除帳號] Firestore 資料刪除成功並已驗證: $uid")
                                firestoreDeleted = true
                            } else {
                                println("⚠️ [刪除帳號] 刪除後文檔仍存在，重試中...")
                                retryCount++
                                if (retryCount < maxRetries) {
                                    kotlinx.coroutines.delay(1000)
                                }
                            }
                        } else {
                            println("⚠️ [刪除帳號] 文檔不存在（可能已被刪除）: $uid")
                            firestoreDeleted = true
                        }
                    } catch (e: Exception) {
                        println("❌ [刪除帳號] Firestore 刪除失敗 (嘗試 ${retryCount + 1}): ${e.message}")
                        println("❌ [刪除帳號] 錯誤類型: ${e.javaClass.simpleName}")
                        e.printStackTrace()

                        if (e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                            _isLoading.value = false
                            onResult(false, "Firestore 權限不足，請聯繫管理員檢查安全規則")
                            return@launch
                        }

                        retryCount++
                        if (retryCount < maxRetries) {
                            kotlinx.coroutines.delay(2000)
                        }
                    }
                }

                if (!firestoreDeleted) {
                    println("⚠️ [刪除帳號] Firestore 資料刪除失敗，但繼續執行 Auth 刪除")
                }

                try {
                    storage.reference
                        .child("profile_images/$uid.jpg")
                        .delete()
                        .await()

                    println("✅ [刪除帳號] Storage 頭像刪除成功")
                } catch (e: Exception) {
                    println("⚠️ [刪除帳號] Storage 刪除失敗（可能不存在）: ${e.message}")
                }

                try {
                    user.delete().await()
                    println("✅ [刪除帳號] Firebase Auth 帳號刪除成功")
                } catch (e: Exception) {
                    println("❌ [刪除帳號] Auth 刪除失敗: ${e.message}")

                    val errorMessage = when {
                        e.message?.contains("requires-recent-login", ignoreCase = true) == true ->
                            "登入時間過久，請重新登入後再試"

                        e.message?.contains("network", ignoreCase = true) == true ->
                            "網路連接失敗，請檢查網路"

                        else -> "刪除帳號失敗：${e.localizedMessage}"
                    }
                    _isLoading.value = false
                    onResult(false, errorMessage)
                    return@launch
                }

                _userProfile.value = UserProfile()
                _isAnonymous.value = false

                println("✅ [刪除帳號] 帳號刪除完成")
                onResult(true, null)

            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ [刪除帳號] 未預期的錯誤: ${e.message}")

                val errorMessage = when {
                    e.message?.contains("network", ignoreCase = true) == true -> "網路連接失敗"
                    else -> "操作失敗，請稍後再試"
                }
                onResult(false, errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日", Locale.TAIWAN)
        return sdf.format(Date(timestamp))
    }
}