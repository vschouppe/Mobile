package com.vschouppe.artapp.zoe

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vschouppe.artapp.R
import com.vschouppe.artapp.data.ArtInfo
import com.vschouppe.artapp.data.myArtCollection


@Composable
fun ArtWindow(navigation: () -> Unit,
              modifier: Modifier = Modifier) {
    var pictureNumber by remember { mutableStateOf(0) }
    Column (modifier = Modifier
        .fillMaxHeight()){
        Row (horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f))
        {
            Text(text = stringResource(id = R.string.app_name_title),
                style = MaterialTheme.typography.displaySmall,
                color = Color.Green
            )
        }
        Row (horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f))
        {
            ArtScreen(myArtCollection.get(pictureNumber).drawable)
        }
        Row (horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f))
        {
            ArtInfoScreen(myArtCollection.get(pictureNumber).artInfo)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row (horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f))
        {
            Log.d("Button row", "value of pictureNumber $pictureNumber")
            Log.d("Button row", "value of myArtCollection.size $myArtCollection.size")
            ButtonsScreen(
                {if(pictureNumber > 0) pictureNumber-- else pictureNumber= myArtCollection.size-1},
                {if(pictureNumber < myArtCollection.size-1) pictureNumber++ else pictureNumber=0})
        }
        Row (horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)){
            Button(
                onClick = navigation,
                shape = MaterialTheme.shapes.medium
            ){
                Text(text = stringResource(id = R.string.screen_profile_back))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtWindowPreview() {
    ArtWindow({})
}


@Composable
fun ArtScreen(
    resource: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment= Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(25.dp))  {
        Image(painter = painterResource(id = resource),
            contentDescription = null,
            modifier = Modifier.size(500.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ArtScreenPreview() {
    ArtScreen(R.drawable.zoe1)
}
@Composable
fun ArtInfoScreen(artInfo : ArtInfo, modifier: Modifier = Modifier) {
    Column (
        verticalArrangement= Arrangement.Center,
        horizontalAlignment= Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)){
        Text(text = stringResource(id = artInfo.title),
            style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.heightIn(10.dp))
        Text(text = "Created by: " + stringResource(id = artInfo.artist),
            style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun ArtInfoScreenPreview() {
    ArtInfoScreen(myArtCollection.get(0).artInfo)
}
@Composable
fun ButtonsScreen(previous : () -> Unit,
                  next : () -> Unit,
                  modifier: Modifier = Modifier
) {
    Log.d("ButtonsScreen", "value of previous $previous")
    Log.d("ButtonsScreen", "value of next $next")
    Row(horizontalArrangement= Arrangement.Center,
        verticalAlignment= Alignment.CenterVertically,
        modifier = modifier){
        Button(
            onClick = previous,
            shape = MaterialTheme.shapes.medium
        ){
            Text(text = stringResource(id = R.string.button_previous))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = next,
            shape = MaterialTheme.shapes.medium
        ){
            Text(text = stringResource(id = R.string.button_next))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ButtonsScreenPreview() {
    var pictureNumber = 1
    ButtonsScreen(
        {if(pictureNumber==0) myArtCollection.size-1 else pictureNumber--},
        {if(pictureNumber== myArtCollection.size-1) pictureNumber=0 else pictureNumber++})
}