package com.amadydev.amanage.ui.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivitySignInBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.ui.home.HomeActivity
import com.amadydev.amanage.ui.signin.SignInViewModel.SignInState.*
import com.amadydev.amanage.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        validateData()
        setListeners(false)
        setObservers()
    }

    private fun setObservers() {
        signInViewModel.signInState.observe(this) {
            with(binding) {
                when (it) {
                    is EmailError ->
                        etEmail.error = getString(it.resourceId)
                    Error ->
                        showErrorSnackBar(root, getString(R.string.sorry))
                    is IsFormValid ->
                        setListeners(it.isFormValid)
                    is Loading ->
                        showProgressDialog(it.isLoading)
                    is NonSuccess ->
                        showErrorSnackBar(root, it.message)
                    is PasswordError ->
                        etPassword.error = getString(it.resourceId)
                    is Success -> {
                        Toast.makeText(this@SignInActivity, it.resourceId, Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }

        }
    }

    private fun setListeners(isFormValid: Boolean) {
        binding.btnSignIn.setOnClickListener {
            when {
                isFormValid -> {
                    signInViewModel.loginUser(
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                }
                else ->
                    showErrorSnackBar(it, getString(R.string.form_error))
            }

        }
    }

    private fun validateData() {
        with(binding) {
            etEmail.afterTextChanged {
                signInViewModel.validateForm(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
            etPassword.afterTextChanged {
                signInViewModel.validateForm(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }
}