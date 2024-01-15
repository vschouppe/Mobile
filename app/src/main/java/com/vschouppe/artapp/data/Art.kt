package com.vschouppe.artapp.data

import androidx.annotation.DrawableRes
import com.vschouppe.R

val myArtCollection = listOf(
    R.drawable.zoe1 to ArtInfo(R.string.title_Zoe1,R.string.artist_Zoe1),
    R.drawable.zoe2 to ArtInfo(R.string.title_Zoe2,R.string.artist_Zoe2),
    R.drawable.zoe3 to ArtInfo(R.string.title_Zoe3,R.string.artist_Zoe3),
).map { Art(it.first, it.second) }

data class Art(
    @DrawableRes val drawable: Int,
    val artInfo: ArtInfo
)

data class ArtInfo(val title: Int, val artist: Int)