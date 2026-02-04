package com.crogin.playandroidcompose.ui.webview

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.repository.ArticleRepository
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository

class WebViewViewModel(private val repository: ArticleRepository, private val context: Context): BaseViewModel() {
    fun collectArticle(id: Int, context: Context){
        performNetworkRequest(
            apiCall = { repository.collectArticle(id) },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    // 收藏成功
                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, response.errorMsg, Toast.LENGTH_SHORT).show()
                    throw IllegalStateException(response.errorMsg)
                }
            }
        )
    }
}

class WebViewViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WebViewViewModel::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            @Suppress("UNCHECKED_CAST")
            return WebViewViewModel(ArticleRepository(apiService), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}