package com.amadydev.amanage.ui.myprofile

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivityMyProfileBinding
import com.amadydev.amanage.ui.BaseActivity

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
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
}