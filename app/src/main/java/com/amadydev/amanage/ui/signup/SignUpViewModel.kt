package com.amadydev.amanage.ui.signup

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> = _signUpState

    fun validateForm(name: String, email: String, password: String) {
        when {
            TextUtils.isEmpty(name) -> {
                _signUpState.value = SignUpState.NameError(R.string.invalid_name)
                _signUpState.value = SignUpState.Success(false)
            }
            !isEmailValid(email) -> {
                _signUpState.value = SignUpState.EmailError(R.string.invalid_email)
                _signUpState.value = SignUpState.Success(false)
            }
            !isPasswordValid(password) -> {
                _signUpState.value = SignUpState.PasswordError(R.string.invalid_password)
                _signUpState.value = SignUpState.Success(false)
            }
            else -> {
                _signUpState.value = SignUpState.Success(true)
            }

        }

    }

    fun registerUser(context: Context, name: String, email: String, password: String) {
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
    }

    sealed class SignUpState {
        data class Success(val isFormValid: Boolean) : SignUpState()
        data class NameError(val resourceId: Int) : SignUpState()
        data class EmailError(val resourceId: Int) : SignUpState()
        data class PasswordError(val resourceId: Int) : SignUpState()
        data class Loading(val isLoading: Boolean) : SignUpState()
        object Error : SignUpState()
    }

    // Check email
    private fun isEmailValid(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Check password
    private fun isPasswordValid(password: String) =
        Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=\\S+\$).{8,}")
            .matcher(password).matches()
}