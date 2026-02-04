package com.crogin.playandroidcompose.ui.wechat

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.launch
import com.crogin.playandroidcompose.ui.components.ArticleItem
import com.crogin.playandroidcompose.ui.components.TabItem
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.webview.WebViewActivity

/**
 * 微信公众号合集页面
 * 
 * 主要功能：
 * - 显示微信公众号分类列表
 * - 支持下拉刷新和上拉加载更多
 * - 点击文章跳转到WebView页面
 * - 错误处理和重试机制
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeChatScreen() {
    // 获取当前上下文和ViewModel实例
    val context = LocalContext.current
    val weChatViewModel: WeChatViewModel = viewModel(factory = WeChatViewModelFactory(context))

    // 观察ViewModel中的状态变化
    val isLoading by weChatViewModel.isLoading           // 分类加载状态
    val errorMessage by weChatViewModel.errorMessage     // 错误信息
    val categories by weChatViewModel.TabTrees           // 微信公众号分类列表

    // 页面初始化时加载分类数据
    LaunchedEffect(Unit) {
        if (categories.isEmpty()) weChatViewModel.loadTabTrees()
    }

    // 使用Scaffold布局，包含顶部标题栏
    Scaffold(
        topBar = {
            TitleItem(title = "微信公众号合集")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 根据不同的状态显示不同的UI
            when {
                // 加载中状态：显示进度条
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // 错误状态：显示错误信息
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage.orEmpty()
                        )
                        Button(
                            onClick = { weChatViewModel.loadTabTrees() }
                        ) {
                            Text(text = "重试")
                        }
                    }
                }
                // 空数据状态：显示提示信息
                categories.isEmpty() -> {
                    Text(
                        text = "暂无分类",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 正常状态：显示分类标签页和文章列表
                else -> {
                    // 项目标签页组件，用于切换不同的微信公众号分类
                    TabItem(
                        categories = categories,
                        modifier = Modifier.fillMaxSize()
                    ) { category ->
                        // 获取当前分类的文章状态
                        val articleState = weChatViewModel.getArticleState(category.id)
                        val articles by articleState.articles           // 文章列表
                        val isInitialLoading by articleState.isInitialLoading  // 初始加载状态
                        val isLoadingMore by articleState.isLoadingMore        // 加载更多状态
                        val isEnd by articleState.isEnd                        // 是否已加载全部
                        val articleError by articleState.errorMessage          // 文章加载错误信息

                        // 当切换分类时，如果文章列表为空则加载数据
                        LaunchedEffect(category.id) {
                            if (articles.isEmpty()) {
                                weChatViewModel.loadWeChatArticles(category.id, initial = true)
                            }
                        }

                        // 根据文章加载状态显示不同的UI
                        when {
                            // 初始加载且文章列表为空：显示加载进度条
                            isInitialLoading && articles.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            // 加载错误且文章列表为空：显示错误信息
                            articleError != null && articles.isEmpty() -> {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = articleError.orEmpty())
                                    Button(
                                        onClick = { weChatViewModel.loadWeChatArticles(category.id, initial = true) }
                                    ) {
                                        Text(text = "点击重试")
                                    }
                                }
                            }

                            // 正常状态：显示文章列表
                            else -> {
                                // 保存列表状态，确保切换标签页时滚动位置不会丢失
                                val listState = rememberSaveable(category.id, saver = LazyListState.Saver) {
                                    LazyListState()
                                }
                                val scope = rememberCoroutineScope()
                                // 下拉刷新状态管理
                                val pullRefreshState = rememberPullRefreshState(
                                    refreshing = isInitialLoading,
                                    onRefresh = {
                                        // 下拉刷新时滚动到顶部
                                        scope.launch {
                                            listState.scrollToItem(0)
                                        }
                                        weChatViewModel.loadWeChatArticles(category.id, initial = true)
                                    }
                                )
                                // 包含下拉刷新功能的容器
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .pullRefresh(pullRefreshState)
                                ) {
                                    // 文章列表
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        state = listState
                                    ) {
                                        // 遍历文章列表，显示每个文章项
                                        itemsIndexed(articles) { index, article ->
                                            Spacer(modifier = Modifier.size(8.dp))

                                            ArticleItem(
                                                article = article,
                                                onClick = {
                                                    // 点击文章跳转到WebView页面
                                                    val intent = Intent(context, WebViewActivity::class.java)
                                                    intent.putExtra(WebViewActivity.EXTRA_TITLE, article.title)
                                                    intent.putExtra(WebViewActivity.EXTRA_URL, article.link)
                                                    context.startActivity(intent)
                                                }
                                            )

                                            // 滚动到底部时自动加载更多
                                            if (index == articles.lastIndex && !isEnd && articleError == null) {
                                                weChatViewModel.loadWeChatArticles(category.id, initial = false)
                                            }
                                        }

                                        // 加载更多时的进度指示器
                                        if (isLoadingMore && articleError == null) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                }
                                            }
                                        }

                                        // 加载更多失败时的错误提示和重试按钮
                                        if (articleError != null && articles.isNotEmpty() && !isLoadingMore) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .height(70.dp)
                                                        .fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "加载失败，点击重试",
                                                        color = MaterialTheme.colorScheme.primary,
                                                        textDecoration = TextDecoration.Underline,
                                                        modifier = Modifier.clickable {
                                                            weChatViewModel.loadWeChatArticles(category.id, initial = false)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    // 下拉刷新指示器
                                    PullRefreshIndicator(
                                        refreshing = isInitialLoading,
                                        state = pullRefreshState,
                                        modifier = Modifier.align(Alignment.TopCenter)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 添加各种预览功能
@Preview(showBackground = true)
@Composable
fun WeChatScreenPreview() {
    WeChatScreen()
}