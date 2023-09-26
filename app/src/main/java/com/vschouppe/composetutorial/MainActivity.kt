package com.vschouppe.composetutorial

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vschouppe.composetutorial.data.Message
import com.vschouppe.composetutorial.data.getMessage
import com.vschouppe.composetutorial.ui.theme.ComposeTutorialTheme
import com.vschouppe.composetutorial.ui.theme.JoinLogo
import com.vschouppe.composetutorial.ui.theme.MyProfilePic

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val helloMessage = Message("Vince", "I am making an android app", "good on you")
                    val allMessages = getMessage()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
////                            FMLogo()
                        JoinLogo()
                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ){
                            Box(
                                modifier = Modifier
                                    .weight(0.1f)
                                    .fillMaxSize()
                                    .padding(start = 30.dp, top = 20.dp, bottom = 20.dp)
                            ) {
                                Welcome()
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.9f)
                            ){
                                LazyColumn (
                                    modifier = Modifier
                                        .animateContentSize()
                                ){
                                    items(allMessages){ message ->
                                        MessageCard(message = message)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MessageCard (message : Message) {
    val textStyleHead = TextStyle(
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Magenta,
        letterSpacing = 0.1.sp,
        lineHeight = 30.sp
    )
    val textStyle = TextStyle(
        fontSize = 20.sp,
        color = Color.White,
    )

    Row (modifier = Modifier
        .padding(all = 8.dp)
        .background(Color.Transparent)
//        .border(2.dp, Color.Black)
        ){
        MyProfilePic()
        var isExpanded by remember{ mutableStateOf(false) }

        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface
        )

        Column (modifier = Modifier
            .clickable { isExpanded = !isExpanded }) {
            Text (
                text = "${message.text}",
                style = textStyleHead
            )
            if (isExpanded){
                Surface (
                    color = surfaceColor
                ){
                    Divider()
                    Text (
                        text = "${message.story}",
                        style = textStyle,
                        color = MaterialTheme.colorScheme.contentColorFor(Color.Yellow)
                    )
                }
            }
            Divider()
            Text (
                text = "author: ${message.author}"
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    uiMode = Configuration.UI_MODE_TYPE_DESK
)
@Composable
fun PreviewMS(){
    ComposeTutorialTheme {
        Surface {
            MessageCard(message = Message("Vince", "I am making a mobile app","Good on you"))
        }
    }
}

@Composable
fun Welcome(){
    val textStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Red,
        letterSpacing = 0.1.sp,
        lineHeight = 30.sp
    )
    Text(
        text = "JOIN THE CHAT",
        style = textStyle
    )
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeTutorialTheme {
        Greeting("Android")
    }
}

