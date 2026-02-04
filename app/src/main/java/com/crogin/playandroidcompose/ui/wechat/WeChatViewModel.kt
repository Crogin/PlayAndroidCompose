package com.crogin.playandroidcompose.ui.wechat

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crogin.playandroidcompose.base.BaseViewModel
import com.crogin.playandroidcompose.data.model.Article
import com.crogin.playandroidcompose.data.model.TabTree
import com.crogin.playandroidcompose.data.repository.PlayAndroidRepository
import com.crogin.playandroidcompose.data.repository.TabTreeRepository
import kotlinx.coroutines.launch

/**
 * 微信公众号页面的ViewModel
 * 
 * 负责管理微信公众号相关的数据状态，包括：
 * - 微信公众号分类列表
 * - 每个分类下的文章列表
 * - 分页加载和状态管理
 * - 错误处理和加载状态
 */
class WeChatViewModel(private val tabTreeRepository: TabTreeRepository): BaseViewModel() {
    /** 微信公众号分类列表的内部状态 */
    private val _TabTrees = mutableStateOf<List<TabTree>>(emptyList())
    
    /** 微信公众号分类列表的外部只读状态 */
    val TabTrees: State<List<TabTree>> = _TabTrees

    /** 文章状态映射表，按分类ID存储每个分类的文章状态 */
    private val articleStates = mutableStateMapOf<Int, WeChatArticleState>()

    /**
     * 加载微信公众号分类列表
     * 
     * 如果当前正在加载中，则直接返回避免重复请求
     */
    fun loadTabTrees() {
        if (isLoading.value) return
        performNetworkRequest(
            apiCall = { tabTreeRepository.getWeChatTabTree() },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    _TabTrees.value = response.data
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            }
        )
    }

    /**
     * 获取指定分类的文章状态
     * 
     * @param id 微信公众号分类ID
     * @return 对应分类的文章状态对象，如果不存在则创建新的状态对象
     */
    fun getArticleState(id: Int): WeChatArticleState {
        return articleStates.getOrPut(id) { WeChatArticleState() }
    }

    /**
     * 加载微信公众号文章列表
     * 
     * @param id 微信公众号分类ID
     * @param initial 是否为初始加载（true：初始加载，false：加载更多）
     */
    fun loadWeChatArticles(id: Int, initial: Boolean) {
        val state = getArticleState(id)
        
        // 检查加载条件，避免不必要的请求
        if (state.isEnd.value && !initial) return  // 已加载到末尾且不是初始加载
        if (initial && state.isInitialLoading.value) return  // 初始加载中
        if (!initial && state.isLoadingMore.value) return  // 加载更多中

        // 设置加载状态
        if (initial) {
            state.isInitialLoading.value = true
            state.errorMessage.value = null
        } else {
            state.isLoadingMore.value = true
            state.errorMessage.value = null
        }

        // 计算下一页页码
        val nextPage = if (initial) 0 else state.page.value
        
        // 发起网络请求
        viewModelScope.launch {
            try {
                val response = tabTreeRepository.getWeChatArticleList(id, nextPage)
                if (response.errorCode == 0) {
                    state.errorMessage.value = null
                    state.isEnd.value = response.data.over
                    
                    if (initial) {
                        // 初始加载：替换现有列表，页码设为1
                        state.articles.value = response.data.datas
                        state.page.value = 1
                    } else {
                        // 加载更多：追加到现有列表，页码自增
                        state.articles.value = state.articles.value + response.data.datas
                        state.page.value += 1
                    }
                } else {
                    state.errorMessage.value = response.errorMsg
                }
            } catch (e: Exception) {
                state.errorMessage.value = e.message
            } finally {
                // 重置加载状态
                if (initial) {
                    state.isInitialLoading.value = false
                } else {
                    state.isLoadingMore.value = false
                }
            }
        }
    }
}

/**
 * 微信公众号文章状态类
 * 
 * 封装单个微信公众号分类的文章列表状态，包括：
 * - 文章列表数据
 * - 分页信息
 * - 加载状态
 * - 错误信息
 */
class WeChatArticleState {
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

/**
 * WeChatViewModel的工厂类
 * 
 * 负责创建WeChatViewModel实例，注入所需的依赖
 */
class WeChatViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeChatViewModel::class.java)) {
            // 获取API服务实例
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            // 创建TabTreeRepository实例
            val TabTreeRepo = TabTreeRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return WeChatViewModel(TabTreeRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}