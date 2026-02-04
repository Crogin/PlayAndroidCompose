package com.crogin.playandroidcompose.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * 首页骨架屏，用于初始加载时的占位显示
 *
 * @param modifier 修饰符，用于控制组件的样式和布局
 */
@Composable
private fun HomeSkeleton(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush() // 获取闪烁动画画笔
    LazyColumn(
        modifier = modifier
            .padding(vertical = 12.dp),
    ) {
        // 轮播图骨架
        item {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                brush = brush
            )
            Spacer(modifier = Modifier.size(12.dp))
        }
        // 文章列表骨架（6个）
        items(6) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                brush = brush
            )
            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

/**
 * 轮播图骨架屏，用于轮播图数据加载时的占位显示
 */
@Composable
private fun BannerSkeleton() {
    val brush = rememberShimmerBrush() // 获取闪烁动画画笔
    SkeletonBlock(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        brush = brush
    )
}

/**
 * 通用骨架块组件，用于各种骨架屏的基础块
 *
 * @param modifier 修饰符，用于控制组件的样式和布局
 * @param brush 画笔，用于绘制骨架块的背景和动画效果
 */
@Composable
private fun SkeletonBlock(modifier: Modifier, brush: Brush) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)) // 设置圆角
            .background(brush) // 设置背景画笔
    )
}

/**
 * 创建闪烁动画画笔，用于骨架屏的动态效果
 *
 * @return 返回一个线性渐变的画笔，带有无限循环的闪烁动画
 */
@Composable
private fun rememberShimmerBrush(): Brush {
    // 创建无限循环动画
    val transition = rememberInfiniteTransition()
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing)
        )
    )

    // 定义闪烁颜色列表
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.4f),
    )

    // 创建线性渐变画笔，根据动画值动态改变渐变方向
    return Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = anim * 1000f, y = anim * 1000f)
    )
}