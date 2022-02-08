package com.amadydev.amanage.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val title: String = "",
    val createdBy: String = ""
) : Parcelable
