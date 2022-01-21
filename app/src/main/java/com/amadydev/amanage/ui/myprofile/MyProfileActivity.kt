package com.amadydev.amanage.ui.myprofile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.databinding.ActivityMyProfileBinding
import com.amadydev.amanage.ui.BaseActivity
import com.bumptech.glide.Glide

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private val myProfileViewModel : MyProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        getUser()
        setupObservers()
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
                is MyProfileViewModel.MyProfileState.ProfileUser ->
                    setUserData(it.user)
            }
        }
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
}