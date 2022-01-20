package com.amadydev.amanage.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.databinding.ActivityHomeBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.ui.intro.IntroActivity
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        setupObservers()
        getUser()
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun getUser() {
        homeViewModel.getUser()
    }

    private fun setupObservers() {
        homeViewModel.homeState.observe(this) {
            when (it) {
                is HomeViewModel.HomeState.NavUser -> {
                    showUserDetails(it.user)
                }
            }
        }
    }

    private fun showUserDetails(user: User) {
        val ivUser = findViewById<ImageView>(R.id.nav_civ_user)
        val tvUsername = findViewById<TextView>(R.id.nav_tv_username)
        Glide.with(this)
            .load(user.image)
            .error(R.drawable.ic_user_place_holder)
            .centerCrop()
            .into(ivUser)
        tvUsername.text = user.name
    }

    private fun setupActionBar() {
        with(binding.iNav.toolbarHomeActivity) {
            setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_action_navigation_menu)
            setNavigationOnClickListener {
                // Toggle drawer
                toggleDrawer()
            }
        }
    }

    private fun toggleDrawer() {
        with(binding.drawerLayout) {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
            } else {
                openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onBackPressed() {
        with(binding.drawerLayout) {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
            } else {
                doubleBackToExit()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                Toast.makeText(this, "My profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_sign_out -> {
                Firebase.auth.signOut()
                Intent(this, IntroActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                    finish()
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}