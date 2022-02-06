package com.amadydev.amanage.ui.board

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivityCreateBoardHomeBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.ui.board.CreateBoardViewModel.*
import com.amadydev.amanage.ui.board.CreateBoardViewModel.CreateBoardState.*
import com.amadydev.amanage.utils.Constants
import com.amadydev.amanage.utils.Constants.READ_STORAGE_IMAGE_PERMISSION_CODE
import com.amadydev.amanage.utils.Constants.showImageChooser
import com.amadydev.amanage.utils.afterTextChanged
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateBoardHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateBoardHomeBinding
    private val createBoardViewModel: CreateBoardViewModel by viewModels()

    private var mUri: Uri? = null
    private var mBoardImageUrl: String = ""

    private var selectPictureLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { createBoardViewModel.setImageUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        getUser()
        validateBoardName()
        setObservers()
        setListeners(false)
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

    private fun getUser() =
        createBoardViewModel.getUser()

    private fun setObservers() {
        createBoardViewModel.createBoardState.observe(this) {
            when (it) {
                is ImageUri -> {
                    updateImage(it.uri)
                    mUri = it.uri
                }
                is BoardImageUrl -> {
                    mBoardImageUrl = it.url
                    createBoard()
                }
                is CurrentUser -> {}
                is Error -> {
                    showErrorSnackBar(binding.root, it.message)
                }
                is Loading ->
                    showProgressDialog(it.isLoading)
                is NonSuccess ->
                    showErrorSnackBar(binding.root, it.message)
                is Success -> {
                    Toast.makeText(this, it.resourceId, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is IsNameValid ->
                    setListeners(it.isNameValid)
                is NameError ->
                    binding.etBoardName.error = getString(it.resourceId)
            }
        }
    }

    private fun setListeners(isNameValid: Boolean) {
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
            btnCreate.setOnClickListener {
                when {
                    isNameValid -> {
                        mUri?.let { uri ->
                            createBoardViewModel.uploadBoardImage(
                                Constants.BOARD_IMAGE
                                    .plus(System.currentTimeMillis()).plus(".")
                                    .plus(
                                        Constants.getFileExtension(
                                            this@CreateBoardHomeActivity,
                                            uri
                                        )
                                    )
                            )
                        } ?: createBoard()
                    }
                    else -> showErrorSnackBar(it, getString(R.string.form_error))
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

    private fun createBoard() {
        createBoardViewModel.createBoard(
            binding.etBoardName.text.toString(),
            mBoardImageUrl
        )
    }

    private fun validateBoardName() {
        with(binding) {
            etBoardName.afterTextChanged {
                createBoardViewModel.validateBoardName(
                    etBoardName.text.toString()
                )
            }
        }
    }
}