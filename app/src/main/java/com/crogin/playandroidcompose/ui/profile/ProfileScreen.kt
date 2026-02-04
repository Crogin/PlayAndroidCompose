package com.crogin.playandroidcompose.ui.profile

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.crogin.playandroidcompose.R
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.login.LoginActivity
import com.crogin.playandroidcompose.ui.profile.favoutite.FavoritesActivity
import com.crogin.playandroidcompose.ui.profile.score.ScoreActivity
import com.crogin.playandroidcompose.ui.profile.share.SharesActivity
import com.crogin.playandroidcompose.ui.webview.WebViewActivity
import com.crogin.playandroidcompose.ui.components.ProfileItem
import com.crogin.playandroidcompose.ui.profile.setting.MoreActivity

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit = {},
    onItemClick: (ProfileItem) -> Unit = {}
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))
    val userInfo by profileViewModel.userInfo
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var currentPage by rememberSaveable { mutableStateOf(ProfilePage.MENU) }
    val items = listOf(
        ProfileItem("积分排行", painterResource(id = R.drawable.ic_score), ProfileItemType.SCORE),
        ProfileItem("我的收藏", painterResource(id = R.drawable.love), ProfileItemType.FAVORITES),
        ProfileItem("我分享的文章", painterResource(id = R.drawable.wechat), ProfileItemType.SHARES),
        ProfileItem("开源网站(www.WanAndroid.com)", painterResource(id = R.drawable.website), ProfileItemType.WEBSITE),
        ProfileItem("更多", painterResource(id = R.drawable.more), ProfileItemType.MORE),
    )
    LaunchedEffect(Unit) {
        profileViewModel.loadCachedProfile()
        profileViewModel.loadProfile()
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                profileViewModel.loadCachedProfile()
                profileViewModel.loadProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    when (currentPage) {
        ProfilePage.MENU -> {
            Scaffold(
                topBar = {
                    TitleItem(title = "")
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        Spacer(Modifier.height(10.dp))
                        Header(
                            nickname = userInfo?.username ?: "未登录",
                            userId = userInfo?.id?.toString() ?: "未知",
                            iconUrl = userInfo?.icon?.takeIf { it.isNotBlank() },
                            onEditProfile = onEditProfile,
                            onLoginClick = {
                                if (userInfo != null) {
                                    showLogoutDialog = true
                                } else {
                                    val intent = Intent(context, LoginActivity::class.java)
                                    context.startActivity(intent)
                                }
                            }
                        )
                        if (showLogoutDialog) {
                            AlertDialog(
                                onDismissRequest = { showLogoutDialog = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showLogoutDialog = false
                                            profileViewModel.logout()
                                            val intent = Intent(context, LoginActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Text(text = "退出登录")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showLogoutDialog = false }) {
                                        Text(text = "取消")
                                    }
                                },
                                title = { Text(text = "退出登录") },
                                text = { Text(text = "确认退出当前账号吗？") }
                            )
                        }
                        Spacer(Modifier.height(24.dp))

                        items.forEach { item ->
                            ProfileItem(
                                item = item,
                                onClick = {
                                    when (item.type) {
                                        ProfileItemType.WEBSITE -> {
                                            val intent = Intent(context, WebViewActivity::class.java)
                                            intent.putExtra(WebViewActivity.EXTRA_TITLE, "开源网站")
                                            intent.putExtra(WebViewActivity.EXTRA_URL, "https://www.wanandroid.com/")
                                            context.startActivity(intent)
                                        }
                                        ProfileItemType.SCORE -> {
                                            val intent = Intent(context, ScoreActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                        ProfileItemType.FAVORITES -> {
                                            val intent = Intent(context, FavoritesActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                        ProfileItemType.SHARES -> {
                                            val intent = Intent(context, SharesActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                        ProfileItemType.MORE -> {
                                            val intent = Intent(context, MoreActivity::class.java)
                                            context.startActivity(intent)
                                        }
                                    }
                                    onItemClick(item)
                                }
                            )
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
        ProfilePage.MORE -> MoreActivity::class.java
    }
    }
@Composable
private fun Header(
    nickname: String,
    userId: String,
    iconUrl: String?,
    onEditProfile: () -> Unit,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLoginClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        Surface(
            modifier = Modifier.size(88.dp),
            color = Color(0xFFDDEAFB),
            shape = CircleShape
        ) {
            if (iconUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = Color(0xFF2D2D2D)
                    )
                }
            }
        }

        Spacer(Modifier.width(18.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = nickname,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "编辑",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEditProfile() },
                    tint = Color(0xFF7B86A8)
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "ID:$userId",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.width(20.dp))
    }
}



data class ProfileItem(
    val title: String,
    val icon: Painter,
    val type: ProfileItemType
)

enum class ProfileItemType {
    SCORE,
    FAVORITES,
    SHARES,
    WEBSITE,
    MORE
}

enum class ProfilePage {
    MENU,
    MORE
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}
