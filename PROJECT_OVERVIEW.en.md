# PlayAndroidCompose Project Overview

PlayAndroidCompose is an Android client built with Jetpack Compose, using the WanAndroid API for content browsing and user features. The main tabs live in a single-activity navigation structure, while login, registration, favorites, score, and shares are handled by dedicated activities. This makes it a practical Compose learning and architecture practice project.

## Key Features
- Home feed with banner carousel
- WeChat articles and project collections
- Profile center, login, and registration
- Favorites, score ranking, and shared articles
- WebView article detail page

## Tech Stack
- UI: Jetpack Compose + Material 3
- Networking: Retrofit + OkHttp + Gson
- Images: Coil
- Local storage: MMKV
- Architecture: ViewModel + state management

## Entry Points
- Home / WeChat / Project / Profile: NavigationSuiteScaffold in MainActivity
- Auth: LoginActivity / RegisterActivity
- Article detail: WebViewActivity
