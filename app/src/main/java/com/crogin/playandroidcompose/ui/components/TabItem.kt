package com.crogin.playandroidcompose.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.crogin.playandroidcompose.data.model.TabTree
import kotlinx.coroutines.launch

/**
 * 项目标签页切换组件
 *
 * 这是一个可水平滑动的标签页组件，包含标签栏和对应的内容页面。
 * 用户可以通过点击标签或滑动页面来切换不同的项目分类。
 *
 * @param categories 标签分类列表，每个分类对应一个标签页
 * @param modifier 修饰符，用于自定义组件样式和布局
 * @param pageContent 每个标签页的内容组件，接收当前分类作为参数
 */

@Composable
fun TabItem(
    categories: List<TabTree>,
    modifier: Modifier = Modifier,
    pageContent: @Composable (TabTree) -> Unit
) {
    // 创建分页状态管理，页面数量等于分类数量
    val pagerState = rememberPagerState(pageCount = { categories.size })
    // 创建协程作用域，用于处理动画和异步操作
    val scope = rememberCoroutineScope()

    // 使用垂直列布局，包含标签栏和内容区域
    Column(modifier = modifier) {
        // 可滚动的标签栏，显示所有分类标签
        // selectedTabIndex 绑定当前选中的标签索引
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color.White
                )
            }
        ) {
            // 遍历所有分类，为每个分类创建一个标签
            categories.forEachIndexed { index, category ->
                val selected = pagerState.currentPage == index
                Tab(
                    // 设置标签选中状态（当前页面索引等于标签索引）
                    selected = selected,
                    // 点击标签时的回调函数
                    onClick = {
                        // 在协程中执行页面切换动画
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    // 标签文本内容，显示分类名称
                    text = {
                        Text(
                            text = category.name,
                            color = Color.White,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (selected) 16.sp else 14.sp
                        )
                    }
                )
            }
        }

        // 水平分页器，显示对应标签页的内容
        HorizontalPager(
            // 绑定分页状态
            state = pagerState,
            // 禁用用户手动滑动切换（只能通过点击标签切换）
            userScrollEnabled = false,
            // 设置分页器占满剩余空间
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // 根据当前页面索引显示对应的分类内容
            pageContent(categories[page])
        }
    }
}

/**
 * TabItem组件的预览函数
 *
 * 用于在Android Studio的设计预览中显示组件效果，无需运行应用即可查看UI。
 * 包含示例数据和简单的UI展示，便于开发和调试。
 *
 * @Preview注解参数说明：
 * - showBackground = true：显示背景，便于查看组件边界
 */
@Preview(showBackground = true)
@Composable
fun TabItemPreview() {
    // 创建示例数据 - 模拟项目分类
    val sampleCategories = listOf(
        TabTree(id = 1, name = "完整项目", visible = 1),
        TabTree(id = 2, name = "热门项目", visible = 1),
        TabTree(id = 3, name = "最新项目", visible = 1),
        TabTree(id = 4, name = "推荐项目", visible = 1)
    )

    // 调用TabItem组件进行预览
    TabItem(
        categories = sampleCategories,
        modifier = Modifier.fillMaxSize()
    ) { category ->
        // 为每个标签页提供简单的内容预览
        Box(
            modifier = Modifier
                .fillMaxSize(),
            // 内容居中对齐
            contentAlignment = Alignment.Center
        ) {
            // 显示当前分类的名称作为预览内容
            Text(text = "这是 ${category.name} 的内容预览")
        }
    }
}
