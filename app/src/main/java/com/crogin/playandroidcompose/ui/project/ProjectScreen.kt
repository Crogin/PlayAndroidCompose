package com.crogin.playandroidcompose.ui.project

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import com.crogin.playandroidcompose.ui.components.ArticleItem
import com.crogin.playandroidcompose.ui.components.TabItem
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.webview.WebViewActivity

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProjectScreen() {
    val context = LocalContext.current
    val projectViewModel: ProjectViewModel = viewModel(factory = ProjectViewModelFactory(context))

    val isLoading by projectViewModel.isLoading
    val errorMessage by projectViewModel.errorMessage
    val categories by projectViewModel.TabTrees

    LaunchedEffect(Unit) {
        if (categories.isEmpty()) projectViewModel.loadTabTrees()
    }

    Scaffold(
        topBar = {
            TitleItem(title = "项目合集")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

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
                            onClick = { projectViewModel.loadTabTrees() }
                        ) {
                            Text(text = "重试")
                        }
                    }
                }

                categories.isEmpty() -> {
                    Text(
                        text = "暂无分类",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    TabItem(
                        categories = categories,
                        modifier = Modifier.fillMaxSize()
                    ) { category ->
                        val articleState = projectViewModel.getArticleState(category.id)
                        val articles by articleState.articles
                        val isInitialLoading by articleState.isInitialLoading
                        val isLoadingMore by articleState.isLoadingMore
                        val isEnd by articleState.isEnd
                        val articleError by articleState.errorMessage

                        LaunchedEffect(category.id) {
                            if (articles.isEmpty()) {
                                projectViewModel.loadProjectArticles(category.id, initial = true)
                            }
                        }

                        when {
                            isInitialLoading && articles.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            articleError != null && articles.isEmpty() -> {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = articleError.orEmpty())
                                    Button(
                                        onClick = { projectViewModel.loadProjectArticles(category.id, initial = true) }
                                    ) {
                                        Text(text = "点击重试")
                                    }
                                }
                            }

                            else -> {
                                val listState = rememberSaveable(category.id, saver = LazyListState.Saver) {
                                    LazyListState()
                                }
                                val scope = rememberCoroutineScope()
                                val pullRefreshState = rememberPullRefreshState(
                                    refreshing = isInitialLoading,
                                    onRefresh = {
                                        scope.launch {
                                            listState.scrollToItem(0)
                                        }
                                        projectViewModel.loadProjectArticles(category.id, initial = true)
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .pullRefresh(pullRefreshState)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            ,
                                        state = listState
                                    ) {
                                        itemsIndexed(articles) { index, article ->
                                            Spacer(modifier = Modifier.size(8.dp))

                                            ArticleItem(
                                                article = article,
                                                onClick = {
                                                    val intent = Intent(context, WebViewActivity::class.java)
                                                    intent.putExtra(WebViewActivity.EXTRA_TYPE, 1)
                                                    intent.putExtra(WebViewActivity.EXTRA_ID, article.id)
                                                    intent.putExtra(WebViewActivity.EXTRA_TITLE, article.title)
                                                    intent.putExtra(WebViewActivity.EXTRA_URL, article.link)
                                                    context.startActivity(intent)
                                                }
                                            )

                                            if (index == articles.lastIndex && !isEnd && articleError == null) {
                                                projectViewModel.loadProjectArticles(category.id, initial = false)
                                            }
                                        }

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
                                                            projectViewModel.loadProjectArticles(category.id, initial = false)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
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
