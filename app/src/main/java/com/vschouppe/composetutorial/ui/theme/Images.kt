package com.vschouppe.composetutorial.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vschouppe.composetutorial.R


@Composable
fun JoinLogo(){
    Image(
        painter = painterResource(id = R.drawable.join),
        contentDescription = "join background",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .alpha(0.25f)
            .border(2.dp, Color.Red)
//            .background(Color.Transparent)
    )
}

@Composable
fun FMLogo(){
    Image(
        painter = painterResource(id = R.drawable.fm),
        contentDescription = "fostermoore logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(1000.dp)
            .clip(CircleShape)
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun MyProfilePic(){
    Image(
        painter = painterResource(id = R.drawable.flandrien),
        contentDescription = "flandrien",
        modifier = Modifier
            .size(75.dp)
            .clip(CircleShape)
    )
}