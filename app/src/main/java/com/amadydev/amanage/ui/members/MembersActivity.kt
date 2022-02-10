package com.amadydev.amanage.ui.members

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.databinding.ActivityMembersBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.utils.Constants.BOARD_DETAILS
import com.amadydev.amanage.utils.Constants.showToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersActivity : BaseActivity(), MembersAdapter.OnMemberClickListener {
    private lateinit var binding: ActivityMembersBinding
    private val membersViewModel: MembersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBoardDetails()
        setObservers()
    }

    private fun setObservers() {
        membersViewModel.membersState.observe(this) {
            when (it) {
                is MembersViewModel.MembersState.Success ->
                    setupActionBar(it.board.image)
                is MembersViewModel.MembersState.Loading ->
                    showProgressDialog(it.isLoading)
                is MembersViewModel.MembersState.Users ->
                    setupMembersList(it.usersList)
                MembersViewModel.MembersState.Error ->
                    showErrorSnackBar(binding.root, getString(R.string.sorry))
            }
        }
    }


    private fun setupMembersList(membersList: List<User>) {
        with(binding.rvMembersList) {
            layoutManager = LinearLayoutManager(this@MembersActivity)
            setHasFixedSize(true)
            adapter = MembersAdapter(
                this@MembersActivity,
                membersList,
                this@MembersActivity
            )
        }
    }

    private fun getBoardDetails() {
        if (intent.hasExtra(BOARD_DETAILS))
            intent.getParcelableExtra<Board>(BOARD_DETAILS)?.let {
                membersViewModel.getBoardDetailsAngAssignedMembersList(it)
            }
    }


    private fun setupActionBar(image: String) {
        with(binding.toolbar) {
            setSupportActionBar(this)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
                actionBar.title = getString(R.string.members)
            }

            Glide.with(this)
                .load(image)
                .error(R.drawable.ic_task_image)
                .centerCrop()
                .into(binding.ivToolbar)

            setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun onMemberClicked(user: User) {
        showToast(this, user.name.plus(" Clicked"))
    }
}