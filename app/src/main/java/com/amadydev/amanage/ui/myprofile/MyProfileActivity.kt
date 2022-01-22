package com.amadydev.amanage.ui.myprofile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.databinding.ActivityMyProfileBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.ui.myprofile.MyProfileViewModel.MyProfileState.*
import com.bumptech.glide.Glide

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private val myProfileViewModel: MyProfileViewModel by viewModels()

    private var mUri: Uri? = null

    private var selectPictureLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { myProfileViewModel.setImageUri(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        getUser()
        setupObservers()
        setListeners(false)
    }

    private fun setupActionBar() {
        with(binding.appBarLayout[0] as Toolbar) {
            setSupportActionBar(this)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
                actionBar.title = getString(R.string.my_profile_title)
            }
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun getUser() = myProfileViewModel.getUser()

    private fun setupObservers() {
        myProfileViewModel.myProfileState.observe(this) {
            when (it) {
                is ProfileUser ->
                    setUserData(it.user)
                is Loading -> {
                    showProgressAndData(it.isLoading)
                }
                is ImageUri -> {
                    updateImage(it.uri)
                    mUri = it.uri
                }
                is Error ->
                    showErrorSnackBar(binding.root, it.message)
                is ImageChanged -> {
                    setListeners(it.isChanged)
                }
                is ProfileImageUrl -> {}
                is Success -> {
                    Toast.makeText(this, it.resourceId, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .error(R.drawable.ic_user_place_holder)
            .centerCrop()
            .into(binding.ivUser)
    }

    private fun setUserData(user: User) {
        with(binding) {
            Glide.with(this@MyProfileActivity)
                .load(user.image)
                .error(R.drawable.ic_user_place_holder)
                .centerCrop()
                .into(ivUser)

            etUserName.setText(user.name)
            etEmail.setText(user.email)
            if (user.mobile != 0L) {
                etPhone.setText(user.mobile.toString())
            }
        }
    }

    private fun setListeners(isChanged: Boolean) {
        with(binding) {
            ivUser.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@MyProfileActivity, Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    //Show image chooser
                    showImageChooser()
                } else {
                    ActivityCompat.requestPermissions(
                        this@MyProfileActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_IMAGE_PERMISSION_CODE
                    )
                }
            }
            btnUpdate.setOnClickListener {
                when {
                    isChanged -> {
                        mUri?.let { uri ->
                            myProfileViewModel.uploadUserImage(
                                getString(R.string.image_user)
                                    .plus(System.currentTimeMillis()).plus(".")
                                    .plus(getFileExtension(uri)), uri
                            )
                        }
                    }
                    else -> {
                        showErrorSnackBar(root, getString(R.string.sorry))
                    }
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
                showImageChooser()
            } else {
                showErrorSnackBar(binding.root, getString(R.string.error_permission_storage))
            }
        }
    }

    private fun showImageChooser() {
        myProfileViewModel.changeImage()
        selectPictureLauncher.launch("image/*")
    }

    private fun showProgressAndData(isLoading: Boolean) {
        binding.iProgress.root.isVisible = isLoading
    }

    private fun getFileExtension(uri: Uri): String? =
        MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri))

    companion object {
        private const val READ_STORAGE_IMAGE_PERMISSION_CODE = 1
    }
}