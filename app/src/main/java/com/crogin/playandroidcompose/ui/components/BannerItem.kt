package com.crogin.playandroidcompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.crogin.playandroidcompose.data.model.Banner
import kotlinx.coroutines.delay
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

@Composable
fun BannerItem(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit
) {
    if (banners.isEmpty()) {
        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 10.dp)
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "加载中...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(banners.size) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onBannerClick(banners[page])
                    }
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(banners[page].imagePath)
                        .crossfade(true)
                        .build(),
                    contentDescription = banners[page].title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        val transition = rememberInfiniteTransition()
                        val anim by transition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1200, easing = LinearEasing)
                            )
                        )
                        val shimmerColors = listOf(
                            Color.LightGray.copy(alpha = 0.6f),
                            Color.LightGray.copy(alpha = 0.2f),
                            Color.LightGray.copy(alpha = 0.6f)
                        )
                        val brush = Brush.linearGradient(
                            colors = shimmerColors,
                            start = androidx.compose.ui.geometry.Offset.Zero,
                            end = androidx.compose.ui.geometry.Offset(x = anim * 1000f, y = anim * 1000f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "图片加载中",
                                color = Color.White
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "加载失败",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = banners[page].title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    PagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        activeColor = Color.White,
                        inactiveColor = Color.White.copy(alpha = 0.5f),
                        indicatorSize = 8.dp,
                        spacing = 6.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color,
    inactiveColor: Color,
    indicatorSize: Dp,
    spacing: Dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(if (pagerState.currentPage == index) activeColor else inactiveColor)
            )
            if (index != pagerState.pageCount - 1) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BannerItemPreview() {
    MaterialTheme {
        BannerItem(
            banners = listOf(
                Banner(
                    desc = "测试描述1",
                    id = 1,
                    imagePath = "https://via.placeholder.com/800x400",
                    isVisible = 1,
                    order = 1,
                    title = "测试Banner 1",
                    type = 0,
                    url = "https://example.com"
                ),
                Banner(
                    desc = "测试描述2",
                    id = 2,
                    imagePath = "https://via.placeholder.com/800x400",
                    isVisible = 1,
                    order = 2,
                    title = "测试Banner 2",
                    type = 0,
                    url = "https://example.com"
                )
            ),
            onBannerClick = { }
        )
    }
}
