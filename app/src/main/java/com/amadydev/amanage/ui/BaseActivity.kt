package com.amadydev.amanage.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amadydev.amanage.R
import com.amadydev.amanage.ui.intro.IntroActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExit = false

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog =
            Dialog(this).apply {
                setContentView(R.layout.dialog_progress)
                findViewById<TextView>(R.id.tv_progress).text = text
                show()
            }
    }

    fun hideProgressDialog() =
        mProgressDialog.dismiss()

    fun getCurrentUserId(): String? =
        FirebaseAuth.getInstance().currentUser?.uid

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

    fun showErrorSnackBar(message: String) {
        Snackbar.make(
            findViewById(R.id.content),
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