package com.vschouppe.artapp.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vschouppe.artapp.network.GoogleApi
import com.vschouppe.artapp.network.GoogleApiService
import kotlinx.coroutines.launch

class ArtViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var artUiState: String by mutableStateOf("")
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getGoogleAlbums()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    private fun getGoogleAlbums() {
        viewModelScope.launch {
            val list = GoogleApi.retrofitService.getAlbums("Bearer AIzaSyDoubVjKzanO3-bZI2HerAOCxmzaVYBJIY")
            artUiState = list
        }

    }
}
