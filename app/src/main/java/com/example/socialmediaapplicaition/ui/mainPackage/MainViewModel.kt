package com.example.socialmediaapplicaition.ui.mainPackage

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.repository.postData.PostRepository
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

    private val _addPostResultState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addPostResultState: StateFlow<NetworkResult<Unit>?> = _addPostResultState

    private val _allPosts = MutableStateFlow<NetworkResult<ArrayList<Post>>?>(null)
    val allPosts :StateFlow<NetworkResult<ArrayList<Post>>?> = _allPosts


    init {
        getAllUser()
        getAllPost()
    }


    private fun getAllUser() = viewModelScope.launch {
        _allUsers.value = NetworkResult.Loading()
        val result = repository.getAllUser()
        _allUsers.value = result
        Log.d("checkingResponse",result.toString())
    }

    private fun getAllPost() = viewModelScope.launch {
        _allPosts.value = NetworkResult.Loading()
        val result = repository.getAllPost()
        _allPosts.value = result
        Log.d("checkingResponse",result.toString())
    }




    fun addPostToDatabase(post:Post) = viewModelScope.launch {
        _addPostResultState.value = NetworkResult.Loading()
        val result = repository.createPost(post)
        _addPostResultState.value = result
    }


}