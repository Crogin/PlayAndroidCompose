package com.crogin.playandroidcompose.ui.profile.score

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.crogin.playandroidcompose.ui.components.TitleItem
import com.crogin.playandroidcompose.ui.theme.PlayAndroidComposeTheme

class ScoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayAndroidComposeTheme {
                ScoreScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun ScoreScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TitleItem(title = "积分排行", onBackClick = onBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "积分排行")
        }
    }
}
