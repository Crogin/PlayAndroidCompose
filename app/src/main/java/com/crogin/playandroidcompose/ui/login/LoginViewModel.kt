package com.crogin.playandroidcompose.ui.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.model.UserInfo
import com.crogin.playandroidcompose.data.repository.LoginRepository
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

class LoginViewModel(
    private val appContext: Context,
    private val loginRepo: LoginRepository
) : BaseViewModel() {
    private val _userInfo = mutableStateOf<UserInfo?>(null)
    val userInfo: State<UserInfo?> = _userInfo

    fun login(username: String, password: String) {
        if (isLoading.value) return
        performNetworkRequest(
            apiCall = { loginRepo.login(username, password) },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    _userInfo.value = response.data
                    val json = Gson().toJson(response.data)
                    val mmkv = MMKV.defaultMMKV()
                    mmkv.encode("userInfo", json)
                    Toast.makeText(appContext, "登录成功", Toast.LENGTH_SHORT).show()
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            }
        )
    }
}

/**
 * LoginViewModel的工厂类，用于创建LoginViewModel实例
 *
 * @param context 上下文
 */
class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            val loginRepo = LoginRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(context.applicationContext, loginRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
