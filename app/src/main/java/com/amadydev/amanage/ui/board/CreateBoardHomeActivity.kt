package com.amadydev.amanage.ui.board

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivityCreateBoardHomeBinding

class CreateBoardHomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateBoardHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardHomeBinding.inflate(layoutInflater)
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
                actionBar.title = getString(R.string.create_board_title)
            }
            setNavigationOnClickListener { onBackPressed() }
        }
    }
}