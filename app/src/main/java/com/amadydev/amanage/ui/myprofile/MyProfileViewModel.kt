package com.amadydev.amanage.ui.myprofile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.R
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(private val db: FirestoreDB) : ViewModel() {
    private val _myProfileState = MutableLiveData<MyProfileState>()
    val myProfileState: LiveData<MyProfileState> = _myProfileState

    private lateinit var mUri: Uri

    private var mProfileUser: User = User()

    fun getUser() {
        _myProfileState.value = MyProfileState.Loading(true)
        db.loadUserData(this)
    }

    fun updateProfileUser(loggedUser: User) {
        _myProfileState.value = MyProfileState.Loading(false)
        _myProfileState.value = MyProfileState.ProfileUser(loggedUser)
        mProfileUser = loggedUser
    }

    fun setImageUri(uri: Uri) {
        mUri = uri
        _myProfileState.value = MyProfileState.ImageUri(uri)
    }

    fun uploadUserImage(path: String) {
        _myProfileState.value = MyProfileState.Loading(true)

        val sRef: StorageReference = Firebase.storage.reference.child(path)
        sRef.putFile(mUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                _myProfileState.value = MyProfileState.ProfileImageUrl(uri.toString())
                Log.i(
                    "Downloadable Image Url",
                    uri.toString()
                )
            }
        }.addOnFailureListener {
            it.message?.let { message -> MyProfileState.NonSuccess(message) }
            _myProfileState.value = MyProfileState.Loading(false)
        }
    }

    fun profileUpdateSuccess(isSuccess: Boolean) {
        _myProfileState.value = MyProfileState.Loading(false)
        when {
            isSuccess -> _myProfileState.value =
                MyProfileState.Success(R.string.profile_updated)
            else -> {
                _myProfileState.value = MyProfileState.Error(R.string.profile_update_failed)
            }
        }

    }

    fun updateUserProfileData(
        imageUrl: String,
        name: String,
        phoneNumber: String
    ) {
        val userHashMap = HashMap<String, Any>()
        var dataChanged = false

        if (imageUrl.isNotEmpty() && imageUrl != mProfileUser.image) {
            userHashMap[Constants.IMAGE] = imageUrl
            dataChanged = true
        }

        if (name.isNotEmpty() && name != mProfileUser.name) {
            userHashMap[Constants.NAME] = name
            dataChanged = true
        }

        if (phoneNumber.isNotEmpty() && phoneNumber != mProfileUser.mobile.toString()) {
            userHashMap[Constants.MOBILE] = phoneNumber.toLong()
            dataChanged = true
        }

        if (dataChanged)
            db.updateUserProfileData(this, userHashMap)
        if (!dataChanged)
            _myProfileState.value = MyProfileState.Error(R.string.no_new_change)
        _myProfileState.value = MyProfileState.Loading(false)
    }

    sealed class MyProfileState {
        data class Success(val resourceId: Int) : MyProfileState()
        data class ProfileUser(val user: User) : MyProfileState()
        data class Loading(val isLoading: Boolean) : MyProfileState()
        data class ImageUri(val uri: Uri) : MyProfileState()
        data class ProfileImageUrl(val url: String) : MyProfileState()
        data class NonSuccess(val message: String) : MyProfileState()
        data class Error(val resourceId: Int) : MyProfileState()
    }
}