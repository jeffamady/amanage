package com.amadydev.amanage.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher

object Constants {
    const val USERS: String = "Users"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val BOARDS: String = "Boards"
    const val ASSIGNED_TO: String = "assignedTo"
    const val USER_IMAGE: String = "user_image"
    const val BOARD_IMAGE: String = "board_image"
    const val READ_STORAGE_IMAGE_PERMISSION_CODE = 1
    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAILS: String = "boardDetails"
    const val ID: String = "id"
    const val EMAIL: String = "email"


    fun getFileExtension(activity: Activity, uri: Uri): String? =
        MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))

    fun showImageChooser(activityResultLauncher: ActivityResultLauncher<String>) =
        activityResultLauncher.launch("image/*")

    fun showToast(context: Context, message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()


}