package com.amadydev.amanage.ui.signup

import android.os.Bundle
import androidx.activity.viewModels
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivitySignUpBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        validateData()
        setListeners(false)
        setObservers()
    }

    private fun setListeners(isFormValid: Boolean) {
        binding.btnSignUp.setOnClickListener {
            when {
                isFormValid -> {
                    signUpViewModel.registerUser(
                        this,
                        binding.etName.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                }
                else ->  showErrorSnackBar(it, "You have to complete all")
            }

        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setObservers() {
        signUpViewModel.signUpState.observe(this) {
            with(binding) {
                when (it) {
                    is SignUpViewModel.SignUpState.NameError ->
                        etName.error = getString(it.resourceId)
                    is SignUpViewModel.SignUpState.EmailError ->
                        etEmail.error = getString(it.resourceId)
                    is SignUpViewModel.SignUpState.PasswordError ->
                        etPassword.error = getString(it.resourceId)
                    is SignUpViewModel.SignUpState.Success -> {
                        setListeners(it.isFormValid)
                    }
                    is SignUpViewModel.SignUpState.Error -> {}
                    is SignUpViewModel.SignUpState.Loading -> {}
                }
            }
        }
    }

    private fun validateData() {
        with(binding) {
            etName.afterTextChanged {
                signUpViewModel.validateForm(
                    etName.text.toString(), etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
            etEmail.afterTextChanged {
                signUpViewModel.validateForm(
                    etName.text.toString(),
                    etEmail.text.toString(), etPassword.text.toString()
                )
            }
            etPassword.afterTextChanged {
                signUpViewModel.validateForm(
                    etName.text.toString(),
                    etEmail.text.toString(), etPassword.text.toString()
                )
            }
        }
    }
}