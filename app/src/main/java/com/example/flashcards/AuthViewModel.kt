package com.example.flashcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> = _loginResult

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loginResult.postValue(
                    if (task.isSuccessful) Result.success(true)
                    else Result.failure(task.exception ?: Exception("Login failed"))
                )
            }
    }

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loginResult.postValue(
                    if (task.isSuccessful) Result.success(true)
                    else Result.failure(task.exception ?: Exception("Registration failed"))
                )
            }
    }

    fun isUserLoggedIn() = auth.currentUser != null
}