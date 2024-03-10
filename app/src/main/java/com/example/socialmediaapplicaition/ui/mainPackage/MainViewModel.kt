package com.example.socialmediaapplicaition.ui.mainPackage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.repository.UserData.PostRepository
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository:PostRepository): ViewModel() {

    private val _allUsers = MutableStateFlow<NetworkResult<ArrayList<User>>?>(null)
    val allUsers :StateFlow<NetworkResult<ArrayList<User>>?> = _allUsers


    fun getAllUser() = viewModelScope.launch {
        _allUsers.value = NetworkResult.Loading()
        val result = repository.getAllUser()
        _allUsers.value = result

        Log.d("checkingResponse",result.toString())


    }




}