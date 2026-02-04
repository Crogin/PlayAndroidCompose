package com.crogin.playandroidcompose.ui.profile.favoutite

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crogin.playandroidcompose.ui.components.ArticleItem
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.theme.PlayAndroidComposeTheme
import com.crogin.playandroidcompose.ui.webview.WebViewActivity
import kotlinx.coroutines.launch

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayAndroidComposeTheme {
                FavoritesScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun FavoritesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val favoriteViewModel: FavouriteViewModel = viewModel(factory = FavouriteViewModelFactory(context))
    val errorMessage by favoriteViewModel.errorMessage
    val articles by favoriteViewModel.articles
    val isInitialLoading by favoriteViewModel.isInitialLoading
    val isLoadingMore by favoriteViewModel.isLoadingMore
    val isEnd by favoriteViewModel.isEnd

    LaunchedEffect(Unit) {
        if (articles.isEmpty()) {
            favoriteViewModel.loadFavouriteArticles(initial = true)
        }
    }

    Scaffold(
        topBar = {
            TitleItem(title = "我的收藏", onBackClick = onBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when{
                isInitialLoading && articles.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null && articles.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = errorMessage.orEmpty())
                        Button(
                            onClick = { favoriteViewModel.loadFavouriteArticles(initial = true) }
                        ) {
                            Text(text = "点击重试")
                        }
                    }
                }
                articles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "暂无收藏")
                    }
                }
                else -> {
                    val listState = rememberSaveable(saver = LazyListState.Saver) {
                        LazyListState()
                    }
                    val scope = rememberCoroutineScope()
                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = isInitialLoading,
                        onRefresh = {
                            scope.launch {
                                listState.scrollToItem(0)
                            }
                            favoriteViewModel.loadFavouriteArticles(initial = true)
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState
                        ) {
                            itemsIndexed(articles) { index, article ->
                                Spacer(modifier = Modifier.size(8.dp))
                                ArticleItem(
                                    article = article,
                                    onClick = {
                                        val intent = Intent(context, WebViewActivity::class.java)
                                        intent.putExtra(WebViewActivity.EXTRA_TITLE, article.title)
                                        intent.putExtra(WebViewActivity.EXTRA_URL, article.link)
                                        context.startActivity(intent)
                                    }
                                )
                                if (index == articles.lastIndex && !isEnd && !isLoadingMore && errorMessage == null) {
                                    favoriteViewModel.loadFavouriteArticles(initial = false)
                                }
                            }
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
                            if (errorMessage != null && articles.isNotEmpty() && !isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .height(70.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(
                                            onClick = { favoriteViewModel.loadFavouriteArticles(initial = false) }
                                        ) {
                                            Text(text = "加载失败，点击重试")
                                        }
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

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    PlayAndroidComposeTheme {
        FavoritesScreen(onBack = { })
    }
}
