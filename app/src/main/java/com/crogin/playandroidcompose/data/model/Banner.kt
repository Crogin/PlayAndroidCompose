package com.crogin.playandroidcompose.data.model

data class Banner(
    var desc: String = "",
    var id: Int = 0,
    var imagePath: String = "",
    var isVisible: Int = 0,
    var order: Int = 0,
    var title: String = "",
    var type: Int = 0,
    var url: String = ""
)

data class ArticleListResponse(
    val curPage: Int = 0,
    val datas: List<Article> = emptyList(),
    val over: Boolean = false,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0
)