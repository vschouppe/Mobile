package com.vschouppe.artapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface ArtAppUiState {
    data class albumsLoaded(val albums: String) : ArtAppUiState
    object Error : ArtAppUiState
    object Loading : ArtAppUiState
}

class ArtAppViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var artAppUiState: ArtAppUiState by mutableStateOf(ArtAppUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
//        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
//    fun getAlbums() {
//        viewModelScope.launch {
//            marsUiState = MarsUiState.Loading
//            marsUiState = try {
//                val listResult = MarsApi.retrofitService.getPhotos()
//                MarsUiState.Success(
//                    "Success: ${listResult.size} Mars photos retrieved"
//                )
//            } catch (e: IOException) {
//                MarsUiState.Error
//            } catch (e: HttpException) {
//                MarsUiState.Error
//            }
//        }
//    }
}
