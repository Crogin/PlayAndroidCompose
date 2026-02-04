package com.crogin.playandroidcompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.crogin.playandroidcompose.R
import com.crogin.playandroidcompose.ui.profile.ProfileItem
import com.crogin.playandroidcompose.ui.profile.ProfileItemType

@Composable
fun ProfileItem(
    item: ProfileItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 5.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        // 左侧圆形图标底
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = item.icon,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary            )
        }

        Spacer(Modifier.width(5.dp))

        Text(
            text = item.title,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFB5BECD),
            modifier = Modifier.size(30.dp)
        )

        Spacer(Modifier.width(12.dp))

    }
}

//预览
@Preview(showBackground = true)
@Composable
fun ProfileItemPreview() {
    ProfileItem(
        item = ProfileItem(
            title = "账号信息",
            icon = painterResource(id = R.drawable.profile),
            type = ProfileItemType.SCORE),
        onClick = {}
    )
}
