package com.example.socialmediaapplicaition.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapplicaition.data.AuthRepository
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(private val repository:AuthRepository) :ViewModel() {

    private val _loginFlow = MutableStateFlow<NetworkResult<FirebaseUser>?>(null)
    val loginFlow :StateFlow<NetworkResult<FirebaseUser>?> = _loginFlow


    private val _signupFlow = MutableStateFlow<NetworkResult<FirebaseUser>?>(null)
    val signupFlow :StateFlow<NetworkResult<FirebaseUser>?> = _signupFlow

    private val _addUserResultState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addUserResultState: StateFlow<NetworkResult<Unit>?> = _addUserResultState

    val currentUser:FirebaseUser?
        get() = repository.currentUser

    init {
//        if(repository.currentUser != null){
//            _loginFlow.value= NetworkResult.Success(repository.currentUser)
//        }
    }

    fun login(email:String,password:String) = viewModelScope.launch {
        _loginFlow.value= NetworkResult.Loading()
        val result = repository.login(email,password)
        _loginFlow.value= result

        Log.d("RESPONSE",result.toString())
    }

    fun signup(name:String,email:String,password:String) = viewModelScope.launch {
        _signupFlow.value = NetworkResult.Loading()
        val result = repository.signup(name,email,password)
        _signupFlow.value = result
        Log.d("RESPONSE",result.toString())
        Log.d("RESPONSE",result.data.toString())
    }


    fun addUserToDatabase(user:User) = viewModelScope.launch {
            Log.d("checkingResultS","inside function")
            _addUserResultState.value = NetworkResult.Loading()
            val result = repository.addUserToDatabase(user)
            Log.d("checkingResultS",result.toString())
            _addUserResultState.value = result

    }


    fun logout(){
        repository.logout()
        _loginFlow.value = null
        _signupFlow.value = null
    }


}