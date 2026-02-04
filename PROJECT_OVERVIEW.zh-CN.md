# PlayAndroidCompose 项目介绍

PlayAndroidCompose 是一个基于 Jetpack Compose 的 Android 客户端项目，围绕 WanAndroid 接口实现资讯浏览与用户功能。项目主导航采用单 Activity 多页面结构，登录、注册、收藏、积分与分享等流程使用独立 Activity，适合作为 Compose 实战与架构练习项目。

## 主要功能
- 首页资讯列表与轮播图
- 微信文章与项目合集列表
- 个人中心、登录与注册
- 收藏、积分排行、分享文章
- WebView 文章详情页

## 技术栈
- UI：Jetpack Compose + Material 3
- 网络：Retrofit + OkHttp + Gson
- 图片：Coil
- 本地存储：MMKV
- 架构：ViewModel + 状态管理

## 模块入口
- 首页 / 微信文章 / 项目合集 / 个人中心：MainActivity 的 NavigationSuiteScaffold
- 登录与注册：LoginActivity / RegisterActivity
- 文章详情：WebViewActivity

## 项目结构
```
app/src/main/java/com/crogin/playandroidcompose
├─ base/                 基础类
├─ data/
│  ├─ api/               接口与响应
│  ├─ model/             数据模型
│  └─ repository/        数据仓库与网络封装
├─ ui/
│  ├─ components/        通用组件
│  ├─ home/              首页
│  ├─ login/             登录
│  ├─ profile/           个人中心
│  ├─ project/           项目合集
│  ├─ regisiter/         注册
│  ├─ theme/             主题
│  ├─ webview/           文章详情
│  └─ wechat/            微信文章
├─ utils/                工具类
└─ MainActivity.kt       应用入口
```
