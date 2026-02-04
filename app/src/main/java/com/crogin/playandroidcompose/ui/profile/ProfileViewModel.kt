package com.crogin.playandroidcompose.ui.profile

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.model.UserInfo
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository
import com.crogin.playandroidcompose.data.repository.ProfileRepository
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

class ProfileViewModel(
    private val appContext: Context,
    private val profileRepository: ProfileRepository
) : BaseViewModel() {
    private val _userInfo = mutableStateOf<UserInfo?>(null)
    val userInfo: State<UserInfo?> = _userInfo

    fun loadCachedProfile() {
        val mmkv = MMKV.defaultMMKV()
        val json = mmkv.decodeString("userInfo", null)
        if (!json.isNullOrEmpty()) {
            _userInfo.value = Gson().fromJson(json, UserInfo::class.java)
        }
    }

    fun loadProfile() {
        if (isLoading.value) return
        performNetworkRequest(
            apiCall = { profileRepository.getProfile() },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    val info = response.data.userInfo
                    _userInfo.value = info
                    val json = Gson().toJson(info)
                    val mmkv = MMKV.defaultMMKV()
                    mmkv.encode("userInfo", json)
                } else if (response.errorCode == -1001) {
                    _userInfo.value = null
                    val mmkv = MMKV.defaultMMKV()
                    mmkv.removeValueForKey("userInfo")
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            }
        )
    }

    fun logout() {
        _userInfo.value = null
        val mmkv = MMKV.defaultMMKV()
        mmkv.removeValueForKey("userInfo")
        mmkv.removeValueForKey("session")
    }
}

/**
 * ProfileViewModel
 *
 * @param context 上下文
 */
class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context.applicationContext, ProfileRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
