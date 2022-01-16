package com.amadydev.amanage.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivityIntroBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.ui.signin.SignInActivity
import com.amadydev.amanage.ui.signup.SignUpActivity

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        with(binding) {
            btnSignUp.setOnClickListener {
                startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
            }

            btnSignIn.setOnClickListener {
                startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
            }
        }
    }
}