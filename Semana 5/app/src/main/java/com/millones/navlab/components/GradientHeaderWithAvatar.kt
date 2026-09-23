package com.millones.navlab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun GradientHeaderWithAvatar(
    avatarUrl: String,
    gradientColors: List<Color>,
    direction: GradientDirection = GradientDirection.VERTICAL,
    roundedBottom: Boolean = false,
    userName: String? = null,
    avatarSize: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    val brush = when (direction) {
        GradientDirection.VERTICAL -> Brush.verticalGradient(gradientColors)
        GradientDirection.HORIZONTAL -> Brush.horizontalGradient(gradientColors)
    }

    val shape = if (roundedBottom) {
        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    } else {
        RoundedCornerShape(0.dp)
    }

    if (roundedBottom) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(shape)
                    .background(brush)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 80.dp)
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar de alumno",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .background(Color.LightGray, CircleShape)
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(brush)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar de usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .background(Color.LightGray, CircleShape)
                )
                if (!userName.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}