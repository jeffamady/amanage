package com.amadydev.amanage.ui.board

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateBoardViewModel : ViewModel() {
    private val _createBoardState = MutableLiveData<CreateBoardState>()
    val createBoardState: LiveData<CreateBoardState> = _createBoardState

    fun setImageUri(uri: Uri) {
        _createBoardState.value = CreateBoardState.ImageUri(uri)
    }

    sealed class CreateBoardState {
        data class ImageUri(val uri: Uri) : CreateBoardState()
    }
}