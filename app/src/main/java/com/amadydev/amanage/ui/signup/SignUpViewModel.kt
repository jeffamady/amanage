package com.amadydev.amanage.ui.signup

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val auth = Firebase.auth
    private val _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> = _signUpState

    fun validateForm(name: String, email: String, password: String) {
        when {
            TextUtils.isEmpty(name) -> {
                _signUpState.value = SignUpState.NameError(R.string.invalid_name)
                _signUpState.value = SignUpState.IsFormValid(false)
            }
            !isEmailValid(email) -> {
                _signUpState.value = SignUpState.EmailError(R.string.invalid_email)
                _signUpState.value = SignUpState.IsFormValid(false)
            }
            !isPasswordValid(password) -> {
                _signUpState.value = SignUpState.PasswordError(R.string.invalid_password)
                _signUpState.value = SignUpState.IsFormValid(false)
            }
            else -> {
                _signUpState.value = SignUpState.IsFormValid(true)
            }

        }

    }

    fun registerUser(name: String, email: String, password: String) {
        _signUpState.value = SignUpState.Loading(true)
        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _signUpState.value = SignUpState.Loading(false)
                when {
                    task.isSuccessful -> {
                        task.result.user?.let {
                            _signUpState.value = SignUpState.Success(R.string.success)
                        }
                        auth.signOut()
                    }
                    else -> _signUpState.value =
                        task.exception?.let {
                            it.message?.let { message -> SignUpState.NonSuccess(message) }
                        }
                }
            }
    }

    sealed class SignUpState {
        data class Success(val resourceId: Int) : SignUpState()
        data class NonSuccess(val message: String) : SignUpState()
        data class IsFormValid(val isFormValid: Boolean) : SignUpState()
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