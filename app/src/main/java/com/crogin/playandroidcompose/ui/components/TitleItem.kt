package com.crogin.playandroidcompose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 标题栏组件
 * 
 * 一个通用的顶部标题栏，支持以下功能：
 * - 显示标题文字
 * - 可选的返回按钮
 * - 可选的右侧菜单区域
 * - 适配状态栏高度
 * 
 * @param title 标题文字，默认为空字符串
 * @param menu 右侧菜单区域的可组合函数，可为null
 * @param onBackClick 返回按钮点击回调，为null时不显示返回按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleItem(
    title: String = "",
    menu: (@Composable RowScope.() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    ){

    // 使用Row布局实现水平排列的标题栏
    Row(
        modifier = Modifier
            .statusBarsPadding()  // 适配状态栏高度
            .fillMaxWidth()       // 占据整个宽度
            .height(56.dp)        // 固定高度为56dp（标准标题栏高度）
            .background(color = MaterialTheme.colorScheme.primary),  // 使用主题主色作为背景
        verticalAlignment = Alignment.CenterVertically  // 垂直居中对齐
    ) {
        // 左侧区域：返回按钮或占位空间
        if (onBackClick != null) {
            // 显示返回按钮，支持点击
            Icon(
                Icons.Default.ArrowBack,
                modifier = Modifier
                    .width(50.dp)          // 固定宽度50dp
                    .fillMaxHeight()       // 占据整个高度
                    .clickable { onBackClick.invoke() }  // 添加点击事件
                    .padding(vertical = 15.dp),  // 垂直内边距
                contentDescription = "back",  // 无障碍描述
                tint = MaterialTheme.colorScheme.onPrimary  // 使用主题的onPrimary颜色
            )
        } else {
            // 没有返回按钮时，使用占位空间保持布局平衡
            Box(modifier = Modifier.width(50.dp))
        }
        
        // 中间区域：标题文字
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,  // 使用主题的onPrimary颜色
            modifier = Modifier
                .weight(1f),  // 占据剩余空间，实现居中效果
            textAlign = TextAlign.Center,  // 文字居中对齐
            softWrap = false,               // 禁止自动换行
            overflow = TextOverflow.Ellipsis  // 超出部分显示省略号
        )
        
        // 右侧区域：菜单或占位空间
        menu?.let {
            // 有菜单时，显示自定义菜单内容
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp),  // 水平内边距
                verticalAlignment = Alignment.CenterVertically,  // 垂直居中对齐
                content = it  // 传入的自定义菜单内容
            )
        } ?: Box(modifier = Modifier.width(50.dp))  // 没有菜单时，使用占位空间保持布局平衡
    }
}

/**
 * 标题栏预览函数
 * 
 * 用于在Android Studio的预览面板中显示TitleItem组件的外观
 */
@Preview(showSystemUi = true)
@Composable
fun TitleBarPreview() {
    Column {
        TitleItem(
            title = "标题",
        ) {
        }
    }
}