package com.vschouppe.artapp.model.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class defines a Mars photo which includes an ID, and the image URL.
 */
@Serializable
data class Album(
    val albums: List<Album>?,
    @SerialName(value = "album")
    val nextPageToken: String?
)
