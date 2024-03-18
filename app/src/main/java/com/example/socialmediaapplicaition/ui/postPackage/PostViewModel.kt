package com.example.socialmediaapplicaition.ui.postPackage

import android.util.Log
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
class PostViewModel @Inject constructor(private val repository:PostRepository): ViewModel() {

    private val _allUsers = MutableStateFlow<NetworkResult<ArrayList<User>>?>(null)
    val allUsers :StateFlow<NetworkResult<ArrayList<User>>?> = _allUsers

    private val _addPostResultState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addPostResultState: StateFlow<NetworkResult<Unit>?> = _addPostResultState

    private val _allPosts = MutableStateFlow<NetworkResult<ArrayList<Post>>?>(null)
    val allPosts :StateFlow<NetworkResult<ArrayList<Post>>?> = _allPosts

    private val _addLikeResponse = MutableStateFlow<NetworkResult<Unit>?> (null)
    val addLikeResponse:StateFlow<NetworkResult<Unit>?> = _addLikeResponse


    // for search user in firebase data base
    private val _searchedUser = MutableStateFlow<NetworkResult<List<User>>?>(null)
    val searchedUser :StateFlow<NetworkResult<List<User>>?> = _searchedUser

    init {
        getAllUser()
    }


    private fun getAllUser() = viewModelScope.launch {
        _allUsers.value = NetworkResult.Loading()
        val result = repository.getAllUser()
        _allUsers.value = result
        Log.d("checkingResponse",result.toString())
    }

     fun getAllPost() = viewModelScope.launch {
        _allPosts.value = NetworkResult.Loading()
        val result = repository.getAllPost()
        _allPosts.value = result
         Log.d("checkingResponse",result.data?.size.toString())
         Log.d("checkingResponse",result.toString())
    }


    fun addPostToDatabase(post:Post) = viewModelScope.launch {
        _addPostResultState.value = NetworkResult.Loading()
        val result = repository.createPost(post)
        _addPostResultState.value = result
    }


    fun addLikeToPost(post:Post,userId:String) = viewModelScope.launch {
        _addLikeResponse.value = NetworkResult.Loading()
        val result = repository.updateLikeStatus(post,userId)
        _addLikeResponse.value = result

    }

    // Function to search users based on a key point
    fun searchUsers(keyPoint: String) {
        allUsers.value?.data?.let { users ->
            val filteredUsers = users.filter { user ->
                // Filter users based on the specified key point
                // Here you can define your condition for filtering, for example, searching by name
                user.name.contains(keyPoint, ignoreCase = true) // Assuming you are searching by user's name
            }
            // Update the StateFlow with filtered users
            _searchedUser.value = NetworkResult.Success(filteredUsers)
        }
    }

}