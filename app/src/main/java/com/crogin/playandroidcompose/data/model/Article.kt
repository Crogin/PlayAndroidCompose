package com.crogin.playandroidcompose.data.model

import com.google.gson.annotations.SerializedName

data class Article(
    val id: Int = 0,
    val title: String,
    val author: String,
    val niceDate: String,
    val chapterName: String,
    val link: String,
    val shareUser: String = ""
)