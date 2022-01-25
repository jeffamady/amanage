package com.amadydev.amanage.ui.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.R
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val db: FirestoreDB)  : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val _signInState = MutableLiveData<SignInState>()
    val signInState: LiveData<SignInState> = _signInState

    sealed class SignInState {
        data class Success(val resourceId: Int) : SignInState()
        data class NonSuccess(val message: String) : SignInState()
        data class Loading(val isLoading: Boolean) : SignInState()
        data class IsFormValid(val isFormValid: Boolean) : SignInState()
        data class EmailError(val resourceId: Int) : SignInState()
        data class PasswordError(val resourceId: Int) : SignInState()
        object Error : SignInState()
    }

    fun validateForm(email: String, password: String) {
        when {
            !isEmailValid(email) -> {
                _signInState.value = SignInState.EmailError(R.string.invalid_email)
                _signInState.value = SignInState.IsFormValid(false)
            }
            !isPasswordValid(password) -> {
                _signInState.value = SignInState.PasswordError(R.string.invalid_password)
                _signInState.value = SignInState.IsFormValid(false)
            }
            else -> {
                _signInState.value = SignInState.IsFormValid(true)
            }

        }

    }

    fun loginUser(email: String, password: String) {
        _signInState.value = SignInState.Loading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        db.loadUserData(this)
                    }
                    else -> {
                        _signInState.value =
                            SignInState.Loading(false)

                        _signInState.value =
                            task.exception?.let {
                                it.message?.let { message -> SignInState.NonSuccess(message) }
                            }
                    }
                }

            }
    }

    // Check email
    private fun isEmailValid(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Check password
    private fun isPasswordValid(password: String) =
        Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=\\S+\$).{8,}")
            .matcher(password).matches()

    fun loginSuccess(isSuccess: Boolean) {
        _signInState.value = SignInState.Loading(false)
        when {
            isSuccess -> {
                _signInState.value =
                    SignInState.Success(R.string.success)
            }
            !isSuccess -> {
                _signInState.value = SignInState.Error
            }

        }

    }
}