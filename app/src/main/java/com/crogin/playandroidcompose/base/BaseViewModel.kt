package com.crogin.playandroidcompose.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

open class BaseViewModel : ViewModel() {
    // 处理加载状态
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // 处理错误
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // 用于执行网络请求的协程函数
    protected fun <T> performNetworkRequest(
        apiCall: suspend () -> T,
        onSuccess: (T) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val result = apiCall()  // 执行API调用
                onSuccess(result)
            } catch (e: Exception) {
                _errorMessage.value = e.message  // 错误处理
            } finally {
                _isLoading.value = false
                onComplete()
            }
        }
    }
}
