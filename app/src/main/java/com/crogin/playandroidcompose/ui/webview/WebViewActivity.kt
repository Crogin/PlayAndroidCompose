package com.crogin.playandroidcompose.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.theme.PlayAndroidComposeTheme

class WebViewActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_URL = "extra_url"
        // 文章类型，0其他，1为网站的收藏文章，2为微信公众号的外部文章
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        val url = intent.getStringExtra(EXTRA_URL) ?: ""
        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        val id = intent.getIntExtra(EXTRA_ID, 0)
        setContent {
            PlayAndroidComposeTheme {
                WebViewScreen(title = title, url = url, id = id, type = type, onBack = { finish() })
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(title: String, url: String, id: Int, type: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: WebViewViewModel = viewModel(
        factory = WebViewViewModelFactory(context)
    )
    var progress by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            if (type == 1) {
                TitleItem(
                    title = title,
                    onBackClick = onBack,
                    menu = {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            modifier = Modifier
                                .width(50.dp)          // 固定宽度50dp
                                .fillMaxHeight()       // 占据整个高度
                                .padding(vertical = 15.dp)  // 垂直内边距
                                .clickable {
                                    viewModel.collectArticle(id, context)
                                },  // 添加点击事件
                            contentDescription = "favourite",  // 无障碍描述
                            tint = MaterialTheme.colorScheme.onPrimary  // 使用主题的onPrimary颜色
                        )
                    }
                )
            }else{
                TitleItem(
                title = title,
                onBackClick = onBack,
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                progress = 0
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                            }
                        }
                        loadUrl(url)
                    }
                },
                update = { it.loadUrl(url) },
                modifier = Modifier.fillMaxSize()
            )

            if (progress in 1..99) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WebViewScreenPreview() {
    PlayAndroidComposeTheme {
        WebViewScreen(title = "Preview Title", url = "https://www.baidu.com", id = 0, type = 0, onBack = {})
    }
}
