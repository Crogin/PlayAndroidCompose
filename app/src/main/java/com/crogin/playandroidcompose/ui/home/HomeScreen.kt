package com.crogin.playandroidcompose.ui.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crogin.playandroidcompose.ui.components.BannerItem
import androidx.compose.ui.unit.dp
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.webview.WebViewActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyListState
import com.crogin.playandroidcompose.ui.components.ArticleItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.style.TextDecoration

/**
 * 首页主界面
 *
 * 负责展示：
 * 1. 顶部轮播图（Banner）
 * 2. 文章列表（支持下拉刷新、上拉加载更多）
 * 3. 加载状态（初始加载、加载更多、空数据、错误重试）
 *
 * 交互：
 * - 点击 Banner 或文章 Item 跳转至 WebViewActivity 展示详情
 * - 列表滑动到底部自动触发“加载更多”
 * - 错误时展示重试按钮/文字
 *
 * @param listState 由外部传入的 LazyListState，用于监听滚动事件并触发分页加载
 */
@Composable
fun HomeScreen(listState: LazyListState) {
    // 获取当前 Android Context，用于启动 Activity
    val context = LocalContext.current

    // 通过 ViewModel 获取首页数据
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    // 订阅 ViewModel 中的各种状态
    val banners by homeViewModel.banners                // 轮播图数据
    val isInitialLoading by homeViewModel.isInitialLoading // 初始加载中
    val isLoadingMore by homeViewModel.isLoadingMore    // 加载更多中
    val isEnd by homeViewModel.isEnd                    // 是否已加载完全部数据
    val articles by homeViewModel.articles              // 文章列表
    val errorMessage by homeViewModel.errorMessage      // 错误信息

    // 首次进入页面时加载数据
    LaunchedEffect(Unit) {
        if (banners.isEmpty()) homeViewModel.loadBanners()
        if (articles.isEmpty()) homeViewModel.loadArticles(initial = true)
    }

    // 使用 Scaffold 构建基础布局，顶部标题栏由 TitleItem 提供
    Scaffold(
        topBar = {
            TitleItem(
                title = "首页",
            )
        },
    ) { paddingValues ->
        // 根据当前状态展示不同 UI
        when {
            // 初始加载中且文章为空：展示全屏加载圈
            isInitialLoading && articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 发生错误且文章和轮播图都为空：展示错误信息与重试按钮
            errorMessage != null && articles.isEmpty() && banners.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("加载失败: \n $errorMessage")
                        Spacer(modifier = Modifier.size(12.dp))
                        Button(
                            onClick = {
                                homeViewModel.loadArticles(initial = true)
                                homeViewModel.loadBanners()
                            }
                        ) {
                            Text("重试")
                        }
                    }
                }
            }

            // 文章为空且非初始加载状态：展示“暂无文章”
            articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无文章")
                }
            }

            // 正常展示列表：包含轮播图、文章列表、加载更多/错误 Footer
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = listState
            ) {
                // 轮播图区域
                item {
                    Spacer(modifier = Modifier.size(12.dp))
                    if (banners.isNotEmpty()) {
                        BannerItem(
                            banners = banners,
                            onBannerClick = { banner ->
                                // 点击 Banner 跳转 WebView
                                val intent = Intent(context, WebViewActivity::class.java)
                                intent.putExtra(WebViewActivity.EXTRA_TITLE, banner.title)
                                intent.putExtra(WebViewActivity.EXTRA_URL, banner.url)
                                context.startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                    }
                }

                // 文章列表
                itemsIndexed(articles) { index, article ->
                    ArticleItem(
                        article = article,
                        onClick = {
                            // 点击文章跳转 WebView
                            val intent = Intent(context, WebViewActivity::class.java)
                            intent.putExtra(WebViewActivity.EXTRA_TITLE, article.title)
                            intent.putExtra(WebViewActivity.EXTRA_URL, article.link)
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.size(8.dp))

                    // 如果当前项是最后一项，且满足加载更多条件，则触发加载更多
                    if (index == articles.lastIndex && !isEnd && !isLoadingMore && errorMessage == null) {
                        homeViewModel.loadArticles(initial = false)
                    }
                }

                // 加载更多 Footer：展示加载圈
                if (isLoadingMore) {
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

                // 错误 Footer：展示“加载失败，点击重试”
                if (errorMessage != null && articles.isNotEmpty() && !isLoadingMore) {
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
                                    homeViewModel.loadArticles(initial = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
