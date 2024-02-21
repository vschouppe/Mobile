
package com.vschouppe.artapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
@Serializable
data class GoogleAlbums (
//    val mediaItems: Array<MediaItems>
//    @SerialName(value = "img_src")
    @SerialName(value = "albums")
    val albums: String,
    @SerialName(value = "nextPageToken")
    val nextPageToken: String
)
