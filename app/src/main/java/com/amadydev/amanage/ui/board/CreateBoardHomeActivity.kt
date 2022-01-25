package com.amadydev.amanage.ui.board

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivityCreateBoardHomeBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.utils.Constants.READ_STORAGE_IMAGE_PERMISSION_CODE
import com.amadydev.amanage.utils.Constants.showImageChooser
import com.bumptech.glide.Glide

class CreateBoardHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateBoardHomeBinding
    private val createBoardViewModel: CreateBoardViewModel by viewModels()

    private var selectPictureLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { createBoardViewModel.setImageUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setObservers()
        setListeners()
    }

    private fun setupActionBar() {
        with(binding.appBarLayout[0] as Toolbar) {
            setSupportActionBar(this)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
                actionBar.title = getString(R.string.create_board_title)
            }
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setObservers() {
        createBoardViewModel.createBoardState.observe(this) {
            when (it) {
                is CreateBoardViewModel.CreateBoardState.ImageUri ->
                    updateImage(it.uri)
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            ivBoard.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@CreateBoardHomeActivity, Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    //Show image chooser
                    showImageChooser(selectPictureLauncher)
                } else {
                    ActivityCompat.requestPermissions(
                        this@CreateBoardHomeActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_IMAGE_PERMISSION_CODE
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_IMAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                //Show image chooser
                showImageChooser(selectPictureLauncher)
            } else {
                showErrorSnackBar(binding.root, getString(R.string.error_permission_storage))
            }
        }
    }

    private fun updateImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .error(R.drawable.ic_user_place_holder)
            .centerCrop()
            .into(binding.ivBoard)
    }
}