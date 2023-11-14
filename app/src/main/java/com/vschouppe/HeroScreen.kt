package com.vschouppe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vschouppe.model.Hero
import com.vschouppe.model.HeroesRepository
import com.vschouppe.ui.theme.SuperHeroesTheme

class HeroScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperHeroesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HeroApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroApp(){
    Scaffold(
        topBar = {
            HeroTopBar()
        }
    ) { it ->
        LazyColumn(
            contentPadding = it,
            ) {
            items(HeroesRepository.heroes) {
                HeroItem(
                    hero = it,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    }
}

@Preview
@Composable
fun HeroAppPreview(){
    SuperHeroesTheme {
        HeroApp()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroTopBar(){
    CenterAlignedTopAppBar(title = {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ){
            Text(text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displayLarge)
        }
    })
}

@Preview
@Composable
fun HeroTopBarPreview(){
    SuperHeroesTheme {
        HeroTopBar()
    }
}


@Composable
fun HeroItem(
    hero: Hero,
    modifier: Modifier = Modifier) {
    Card (
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ){
        Row(
            modifier = Modifier
                .padding(16.dp)
                .sizeIn(minHeight = 72.dp)
                .fillMaxWidth()
        ){
            Column(modifier =  Modifier
                .weight(1f)
            ){
                Text(text = stringResource(id = hero.name),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(text = stringResource(id = hero.description),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(
                Modifier.width(16.dp)
            )
            Box(modifier = Modifier
                .size(72.dp)
                . clip(RoundedCornerShape(8.dp))){
                HeroPic(image = hero.image,
                    modifier = Modifier
                        .clip(shape = MaterialTheme.shapes.small))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeroItemPreview() {
    SuperHeroesTheme {
        HeroItem(
            Hero(
                name = R.string.hero2,
                description = R.string.description2,
                image = R.drawable.android_superhero2
            )
        )
    }
}

@Composable
fun HeroPic(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = image),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize())
}

@Preview(showBackground = true)
@Composable
fun HeroPicPreview() {
    SuperHeroesTheme {
        HeroPic(R.drawable.android_superhero1)
    }
}

