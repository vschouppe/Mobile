package com.vschouppe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vschouppe.model.VanTip
import com.vschouppe.model.VanTipRepository
import com.vschouppe.ui.theme.VanTipsTheme

@Composable
fun VanTipsList(
    tips: List<VanTip>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        contentPadding = contentPadding
    ){
        itemsIndexed(tips){index, tip ->
            VanTipItem(
                index = index,
                tip = tip
            )
        }
    }
}
@Preview
@Composable
fun VanTipsListPreview(
) {
    VanTipsTheme{
        VanTipsList(VanTipRepository.vantips)
    }
}

@Composable
fun VanTipItem(
    index: Int,
    tip: VanTip
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        modifier = Modifier
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
            Text(
                text = stringResource(id = R.string.vantip_card_title) + " " + (index + 1),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(10.dp)
            )
            Image(
                painter = painterResource(id = tip.image),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth
            )
            Column(verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)){
                Text(
                    text = stringResource(id = tip.name),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(6.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = tip.description),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun VanTipItemPReview() {
    VanTipsTheme {
        VanTipItem( 4,
            VanTip(R.string.vantip_clean, R.string.vantip_info_clean, R.drawable.clean_result)
        )
    }
}

@Composable
fun VanTipPicture(
    picture: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = picture),
        contentDescription = null,
        alignment = Alignment.Center,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}

@Preview
@Composable
fun VanTipPicturePreview() {
    VanTipPicture(R.drawable.window_result)
}

