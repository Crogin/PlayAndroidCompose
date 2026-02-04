package com.crogin.playandroidcompose.ui.profile.favoutite

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.model.Article
import com.crogin.playandroidcompose.data.repository.ArticleRepository
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository
import com.crogin.playandroidcompose.ui.wechat.WeChatArticleState

class FavouriteViewModel( private val repository: ArticleRepository): BaseViewModel() {
    private val articleStates = mutableStateMapOf<Int, WeChatArticleState>()
    // 暴露状态属性供外部访问
    private val favouriteState = FavouriteArticleState()
    val articles = favouriteState.articles
    val page = favouriteState.page
    val isEnd = favouriteState.isEnd
    val isInitialLoading = favouriteState.isInitialLoading
    val isLoadingMore = favouriteState.isLoadingMore
    /**
     * 加载收藏文章列表
     *
     * @param initial 是否为初始加载（true：初始加载，false：加载更多）
     */
    fun loadFavouriteArticles(initial: Boolean) {
        // 检查加载条件，避免不必要的请求
        if (isEnd.value && !initial) return  // 已加载到末尾且不是初始加载
        if (initial && isInitialLoading.value) return  // 初始加载中
        if (!initial && isLoadingMore.value) return  // 加载更多中

        // 设置加载状态
        if (initial) {
            isInitialLoading.value = true
        } else {
            isLoadingMore.value = true
        }

        //计算下一页页码
        val nextPage = if (initial) 0 else page.value

        performNetworkRequest(
            apiCall = { repository.getCollectArticle(nextPage) },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    isEnd.value = response.data.over
                    if (initial) {
                        articles.value = response.data.datas
                        page.value = 1
                    } else {
                        articles.value += response.data.datas
                        page.value += 1
                    }
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            },
            onComplete = {
                if (initial) {
                    isInitialLoading.value = false
                } else {
                    isLoadingMore.value = false
                }
            }
        )
    }
}

/**
 * 收藏文章状态类
 *
 * 封装收藏文章列表状态，包括：
 * - 文章列表数据
 * - 分页信息
 * - 加载状态
 * - 错误信息
 */
class FavouriteArticleState {
    /** 文章列表数据 */
    val articles = mutableStateOf<List<Article>>(emptyList())

    /** 当前页码 */
    val page = mutableIntStateOf(0)

    /** 是否已加载到最后一页 */
    val isEnd = mutableStateOf(false)

    /** 初始加载状态 */
    val isInitialLoading = mutableStateOf(false)

    /** 加载更多状态 */
    val isLoadingMore = mutableStateOf(false)

    /** 错误信息 */
    val errorMessage = mutableStateOf<String?>(null)
}

class FavouriteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel  ::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            @Suppress("UNCHECKED_CAST")
            return FavouriteViewModel(ArticleRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
