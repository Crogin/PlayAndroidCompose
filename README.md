PlayAndroidCompose 是一个基于 Jetpack Compose 的 Android 客户端项目，围绕 WanAndroid 接口实现资讯浏览与用户功能。项目主导航采用单 Activity 多页面结构，登录、注册、收藏、积分与分享等流程使用独立 Activity，适合作为 Compose 实战与架构练习项目。

项目使用了https://www.wanandroid.com 提供的开放api来完成，在此十分感谢鸿洋大佬

项目使用了ai协作开发，编写基类，架构设计，代码注释，适合新手查看练习，项目仍有一些功能等待后续完善。

主要功能
首页资讯列表与轮播图
微信文章与项目合集列表
个人中心、登录与注册
收藏、积分排行、分享文章
WebView 文章详情页

技术栈
UI：Jetpack Compose + Material 3
网络：Retrofit + OkHttp + Gson
图片：Coil
本地存储：MMKV
架构：ViewModel + 状态管理
