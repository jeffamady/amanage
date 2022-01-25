package com.amadydev.amanage.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher

object Constants {
    const val USERS: String = "Users"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val BOARDS: String = "boards"
    const val USER_IMAGE: String = "user_image"
    const val BOARD_IMAGE: String = "board_image"
    const val READ_STORAGE_IMAGE_PERMISSION_CODE = 1


    fun getFileExtension(activity: Activity, uri: Uri): String? =
        MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))

    fun showImageChooser(activityResultLauncher: ActivityResultLauncher<String>) =
        activityResultLauncher.launch("image/*")

}