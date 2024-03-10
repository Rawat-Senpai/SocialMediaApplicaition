package com.example.socialmediaapplicaition.ui.auth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapplicaition.repository.AuthData.AuthRepository
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.TokenManager
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) :ViewModel() {

    @Inject
    lateinit var tokenManager: TokenManager

    private val _loginFlow = MutableStateFlow<NetworkResult<FirebaseUser>?>(null)
    val loginFlow :StateFlow<NetworkResult<FirebaseUser>?> = _loginFlow


    private val _signupFlow = MutableStateFlow<NetworkResult<FirebaseUser>?>(null)
    val signupFlow :StateFlow<NetworkResult<FirebaseUser>?> = _signupFlow

    private val _addUserResultState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addUserResultState: StateFlow<NetworkResult<Unit>?> = _addUserResultState



    private val _uploadPhotoResult = MutableStateFlow<NetworkResult<Uri>?>(null)
    val uploadPhotoResult: StateFlow<NetworkResult<Uri>?> = _uploadPhotoResult

    private val _userData = MutableStateFlow<NetworkResult<User>?>(null)
    val userData :StateFlow<NetworkResult<User>?> = _userData


    val currentUser:FirebaseUser?
        get() = repository.currentUser

    init {
        if(repository.currentUser != null){
            _loginFlow.value= NetworkResult.Success(repository.currentUser)
        }
    }

    fun login(email:String,password:String) = viewModelScope.launch {
        _loginFlow.value= NetworkResult.Loading()
        val result = repository.login(email,password)
        _loginFlow.value= result
    }

    fun signup(name:String,email:String,password:String) = viewModelScope.launch {
        _signupFlow.value = NetworkResult.Loading()
        val result = repository.signup(name,email,password)
        _signupFlow.value = result
    }


    fun addUserToDatabase(user:User) = viewModelScope.launch {
            _addUserResultState.value = NetworkResult.Loading()
            val result = repository.addUserToDatabase(user)
            _addUserResultState.value = result

    }

    fun uploadImageToFireStore(photoUri:Uri) = viewModelScope.launch {

        _uploadPhotoResult.value = NetworkResult.Loading()
        val result = repository.uploadPhotoToFireStore(photoUri)
        _uploadPhotoResult.value= result
        Log.d("checking",result.toString())




    }

    fun getUserFullDetails(userId:String) = viewModelScope.launch {

        _userData.value = NetworkResult.Loading()
        val result = repository.getUserData(userId)
        _userData.value = result

        Log.d("checkingRes",result.data?.profile.toString())
        tokenManager.saveProfile(result.data?.profile.toString())
        tokenManager.saveId(result.data?.id.toString())
        tokenManager.saveUserName(result.data?.name.toString())
    }

    fun logout(){
        repository.logout()
        _loginFlow.value = null
        _signupFlow.value = null
    }


}