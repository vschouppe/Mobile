package com.vschouppe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vschouppe.lemonapp.ui.theme.LemonAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LemonAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    getMeSomeLemonade()
                }
            }
        }
    }
}


@Composable
fun getMeSomeLemonade(){
    var lemonPhase by remember{ mutableStateOf(1) }
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier
            .background(Color(1.0f, 0.8f, 0.0f))
            .weight(0.1f)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(style = MaterialTheme.typography.displayMedium,
                text = stringResource(id = R.string.app_name))
        }
        Row(modifier = Modifier
            .weight(0.9f),
            horizontalArrangement =  Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            when (lemonPhase){
                1 -> lemonImageAndText(
                    text = R.string.tap_lemon_tree,
                    resource = R.drawable.lemon_tree,
                    onImageClick = {lemonPhase = 2}
                )
                2 -> lemonImageAndText(
                    text = R.string.tap_lemon,
                    resource = R.drawable.lemon_squeeze,
                    onImageClick = {lemonPhase = 3}
                )
                3 -> lemonImageAndText(
                    text = R.string.tap_lemonade,
                    resource = R.drawable.lemon_drink,
                    onImageClick = {lemonPhase = 4}
                )
                4 -> lemonImageAndText(
                    text = R.string.tap_empty_glass,
                    resource = R.drawable.lemon_restart,
                    onImageClick = {lemonPhase = 1}
                )
            }
        }
    }
}

@Composable
fun lemonImageAndText(
    resource : Int,
    text : Int,
    onImageClick: () ->  Unit
){
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Row(modifier = Modifier){
            Image( modifier = Modifier
                .background(Color(0.7f, 0.9f, 0.7f))
                .clip(RoundedCornerShape(10.dp)),
                painter = painterResource(id = resource),
                contentDescription = null,
                contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier
                .background(Color.Transparent) // Set a transparent background
                .border(1.dp, Color.Transparent),
            onClick = onImageClick
        ) {
            Text(text = stringResource(id = text))
        }
    }
}

@Preview
@Composable
fun getMeSomeLemonadePreview(){
    getMeSomeLemonade()
}