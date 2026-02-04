package com.crogin.playandroidcompose.ui.regisiter

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.model.UserInfo
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository
import com.crogin.playandroidcompose.data.repository.RegisterRepository

class RegisterViewModel(private val registerRepository: RegisterRepository) : BaseViewModel(){
    private val _userInfo = mutableStateOf<UserInfo?>(null)
    val userInfo: State<UserInfo?> = _userInfo

    fun register(username: String, password: String, rePassword: String) {
        performNetworkRequest(
            apiCall = { registerRepository.register(username, password, rePassword) },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    _userInfo.value = response.data
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            }
        )
    }
}

/**
 * RegisterViewModel
 *
 * @param context 上下文
 */
class RegisterViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(RegisterRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
