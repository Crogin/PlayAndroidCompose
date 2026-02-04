package com.crogin.playandroidcompose.ui.project

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

class ProjectViewModel(private val TabTreeRepository: TabTreeRepository) : BaseViewModel() {
    private val _TabTrees = mutableStateOf<List<TabTree>>(emptyList())
    val TabTrees: State<List<TabTree>> = _TabTrees
    private val articleStates = mutableStateMapOf<Int, ProjectArticleState>()

    fun loadTabTrees() {
        if (isLoading.value) return
        performNetworkRequest(
            apiCall = { TabTreeRepository.getTabItemTree() },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    _TabTrees.value = response.data
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            }
        )
    }

    fun getArticleState(id: Int): ProjectArticleState {
        return articleStates.getOrPut(id) { ProjectArticleState() }
    }

    fun loadProjectArticles(cid: Int, initial: Boolean) {
        val state = getArticleState(cid)
        if (state.isEnd.value && !initial) return
        if (initial && state.isInitialLoading.value) return
        if (!initial && state.isLoadingMore.value) return

        if (initial) {
            state.isInitialLoading.value = true
        } else {
            state.isLoadingMore.value = true
        }

        val nextPage = if (initial) 0 else state.page.value
        performNetworkRequest(
            apiCall = { TabTreeRepository.getProjectArticleList(cid, nextPage) },
            onSuccess = { response ->
                if (response.errorCode == 0) {
                    state.isEnd.value = response.data.over
                    if (initial) {
                        state.articles.value = response.data.datas
                        state.page.value = 1
                    } else {
                        state.articles.value += response.data.datas
                        state.page.value += 1
                    }
                } else {
                    throw IllegalStateException(response.errorMsg)
                }
            },
            onComplete = {
                if (initial) {
                    state.isInitialLoading.value = false
                } else {
                    state.isLoadingMore.value = false
                }
            }
        )
    }
}

class ProjectArticleState {
    val articles = mutableStateOf<List<Article>>(emptyList())
    val page = mutableIntStateOf(0)
    val isEnd = mutableStateOf(false)
    val isInitialLoading = mutableStateOf(false)
    val isLoadingMore = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
}

/**
 * ProjectViewModel的工厂类，用于创建ProjectViewModel实例
 *
 * @param context 上下文
 */
class ProjectViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            val apiService = PlayAndroidRepository.getInstance(context).apiService
            val TabTreeRepo = TabTreeRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(TabTreeRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
