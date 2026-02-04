package com.crogin.playandroidcompose.ui.home

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.model.Banner
import com.crogin.playandroidcompose.data.model.Article
import com.crogin.playandroidcompose.data.repository.ArticleRepository
import com.crogin.playandroidcompose.data.repository.BannerRepository
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository

class HomeViewModel(
    private val bannerRepository: BannerRepository,
    private val articleRepository: ArticleRepository
) : BaseViewModel() {
    private val _banners = mutableStateOf<List<Banner>>(emptyList())
    val banners: State<List<Banner>> = _banners

    private val _articles = mutableStateOf<List<Article>>(emptyList())
    val articles: State<List<Article>> = _articles

    private val _page = mutableIntStateOf(0)
    val page: State<Int> = _page

    private val _isEnd = mutableStateOf(false)
    val isEnd: State<Boolean> = _isEnd

    private val _isInitialLoading = mutableStateOf(false)
    val isInitialLoading: State<Boolean> = _isInitialLoading

    private val _isLoadingMore = mutableStateOf(false)
    val isLoadingMore: State<Boolean> = _isLoadingMore

    fun loadBanners() {
        performNetworkRequest(
            apiCall = { bannerRepository.getBannerList() },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    _banners.value = response.data
                } else {
                    _banners.value = emptyList()
                }
            }
        )
    }

    fun loadArticles(initial: Boolean = false) {
        if (_isEnd.value) return
        if (initial && _isInitialLoading.value) return
        if (!initial && _isLoadingMore.value) return

        if (initial) {
            _isInitialLoading.value = true
        } else {
            _isLoadingMore.value = true
        }

        val nextPage = if (initial) 0 else _page.value

        performNetworkRequest(
            apiCall = {
                articleRepository.getArticleList(nextPage)
            },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    _isEnd.value = response.data.over
                    if (initial) {
                        _articles.value = response.data.datas
                        _page.value = 1
                    } else {
                        _articles.value += response.data.datas
                        _page.value += 1
                    }
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            },
            onComplete = {
                if (initial) {
                    _isInitialLoading.value = false
                } else {
                    _isLoadingMore.value = false
                }
            }
        )
    }
}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            val bannerRepo = BannerRepository(apiService)
            val articleRepo = ArticleRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(bannerRepo, articleRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
