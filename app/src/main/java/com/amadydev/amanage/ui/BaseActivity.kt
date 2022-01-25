package com.amadydev.amanage.ui

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.amadydev.amanage.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExit = false
    private lateinit var mProgressDialog: Dialog


    fun showProgressDialog(show: Boolean, text: String? = null) {
        when {
            show -> mProgressDialog =
                Dialog(this).apply {
                    setContentView(R.layout.dialog_progress)
                    text?.let { findViewById<TextView>(R.id.tv_progress).text = it }
                    setCancelable(false)
                    show()
                }
            !show -> mProgressDialog.dismiss()
        }
    }

    fun doubleBackToExit() {
        if (doubleBackToExit) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExit = true

        Toast.makeText(
            this,
            getString(R.string.please_click_back),
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExit = false }, 2000
        )
    }

    fun showErrorSnackBar(v: View, message: String) {
        Snackbar.make(
            v,
            message, Snackbar.LENGTH_LONG
        ).apply {
            view.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.snackbar_error_color
                )
            )
            show()
        }

    }
}