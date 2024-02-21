/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vschouppe.artapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vschouppe.artapp.GooglePhotosApplication
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface GoogleAlbumUiState {
    data class Success(val albums: String) : GoogleAlbumUiState
    object Error : GoogleAlbumUiState
    object Loading : GoogleAlbumUiState
}

//class GooglePhotoViewModel(private val googlePhotoRepository: GooglePhotosRepository) : ViewModel() {
class GooglePhotoViewModel() : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var googleAlbumUiState: GoogleAlbumUiState by mutableStateOf(GoogleAlbumUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getAlbums()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getAlbums() {
        viewModelScope.launch {
            googleAlbumUiState = GoogleAlbumUiState.Loading
            googleAlbumUiState = try {
//                val listResult = googlePhotoRepository.getGoogleAlbums()
                val listResult = listOf<String>("fake","fake2")
                GoogleAlbumUiState.Success(
                    "Success: ${listResult.size} albums retrieved"
                )
            } catch (e: IOException) {
                GoogleAlbumUiState.Error
            } catch (e: HttpException) {
                GoogleAlbumUiState.Error
            }
        }
    }

    /**
     * Factory for [MarsViewModel] that takes [MarsPhotosRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GooglePhotosApplication)
                val googlePhotosRepository = application.container.googlePhotoRepository
//                GooglePhotoViewModel(googlePhotoRepository = googlePhotosRepository)
                GooglePhotoViewModel()
            }
        }
    }
}
