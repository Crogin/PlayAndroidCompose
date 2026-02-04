package com.crogin.playandroidcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyListState
import com.crogin.playandroidcompose.ui.home.HomeScreen
import com.crogin.playandroidcompose.ui.profile.ProfileScreen
import com.crogin.playandroidcompose.ui.project.ProjectScreen
import com.crogin.playandroidcompose.ui.wechat.WeChatScreen
import com.crogin.playandroidcompose.ui.theme.PlayAndroidComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayAndroidComposeTheme {
                PlayAndroidComposeApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun PlayAndroidComposeApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val homeListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val stateHolder = rememberSaveableStateHolder()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { dest->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(id = dest.iconRes),
                            contentDescription = dest.label,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    label = { Text(dest.label) },
                    selected = dest == currentDestination,
                    onClick = { currentDestination = dest }
                )
            }
        }
    ) {
        stateHolder.SaveableStateProvider(currentDestination) {
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(listState = homeListState)
                AppDestinations.WECHAT -> WeChatScreen()
                AppDestinations.PROJECT -> ProjectScreen()
                AppDestinations.PROFILE -> ProfileScreen()
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val iconRes: Int,
) {
    HOME("首页", R.drawable.home),
    WECHAT("微信文章", R.drawable.wechat),
    PROJECT("项目合集", R.drawable.project),
    PROFILE("个人", R.drawable.profile),
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlayAndroidComposeTheme {
    }
}
