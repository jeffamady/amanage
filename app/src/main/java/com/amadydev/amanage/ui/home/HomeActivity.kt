package com.amadydev.amanage.ui.home

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.databinding.ActivityHomeBinding
import com.amadydev.amanage.databinding.ContentHomeBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.ui.board.BoardAdapter
import com.amadydev.amanage.ui.board.CreateBoardHomeActivity
import com.amadydev.amanage.ui.intro.IntroActivity
import com.amadydev.amanage.ui.myprofile.MyProfileActivity
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    BoardAdapter.OnBoardClickListener {
    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private val myProfileLauncher =
        registerForActivityResult(StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK)
                getUser()
        }

    private val createBoardLauncher =
        registerForActivityResult(StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK)
                getBoards()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        setupObservers()
        getUser()
        setListeners()
    }


    private fun setListeners() {
        with(binding) {
            navView.setNavigationItemSelectedListener(this@HomeActivity)

            iNav.fabCreateBoard.setOnClickListener {
                createBoardLauncher.launch(
                    Intent(
                        this@HomeActivity,
                        CreateBoardHomeActivity::class.java
                    )
                )
            }
        }
    }

    private fun getUser() {
        homeViewModel.getUser()
    }

    private fun getBoards() {
        homeViewModel.getBoards()
    }

    private fun setupObservers() {
        homeViewModel.homeState.observe(this) {
            when (it) {
                is HomeViewModel.HomeState.NavUser -> {
                    showUserDetails(it.user)
                }
                is HomeViewModel.HomeState.BoardList ->
                    showBoards(it.boardList)
                is HomeViewModel.HomeState.Loading ->
                    showProgressDialog(it.isLoading)
                HomeViewModel.HomeState.Error ->
                    showErrorSnackBar(binding.root, getString(R.string.board_error))
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
                myProfileLauncher.launch(Intent(this, MyProfileActivity::class.java))
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

    private fun showBoards(boardList: List<Board>) {
        val rvBoards = findViewById<RecyclerView>(R.id.rv_boards)
        val tvNoBoards = findViewById<TextView>(R.id.tv_no_boards)
        rvBoards.isVisible = true
        tvNoBoards.isVisible = false

        rvBoards.layoutManager = LinearLayoutManager(this@HomeActivity)
        rvBoards.setHasFixedSize(true)

        val adapter =
            BoardAdapter(this@HomeActivity, boardList, this@HomeActivity)

        rvBoards.adapter = adapter

    }

    override fun onBoardClicked(board: Board) {
        Toast.makeText(this, board.name.plus(" Clicked"), Toast.LENGTH_SHORT).show()
    }
}