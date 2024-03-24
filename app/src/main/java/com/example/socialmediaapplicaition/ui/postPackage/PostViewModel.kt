package com.example.socialmediaapplicaition.ui.postPackage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.repository.postData.PostRepository
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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

    private val _addChatRoomResultState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addChatRoomResultState :StateFlow<NetworkResult<Unit>?> = _addChatRoomResultState

    private  val _addChatMessageResultState = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addChatMessageResultState:StateFlow<NetworkResult<Unit>?> = _addChatMessageResultState

    private val _getAllChatMessages = MutableStateFlow<NetworkResult<ArrayList<ChatMessageModel>>?>(null)
    val getAllChatChatMessages :StateFlow<NetworkResult<ArrayList<ChatMessageModel>>?> = _getAllChatMessages


    // for search user in firebase data base
    private val _searchedUser = MutableStateFlow<NetworkResult<List<User>>?>(null)
    val searchedUser :StateFlow<NetworkResult<List<User>>?> = _searchedUser

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

    /*
      _allPosts.value = NetworkResult.Loading()
    val result = repository.getAllPost()
    _allPosts.value = result
     Log.d("checkingResponse",result.data?.size.toString())
     Log.d("checkingResponse",result.toString())
     */
//                try {
//                repository.getAllPost() { result ->
//                    _allPosts.value = result
//                    Log.d("checkingResponseSize5",result.toString())
//                }
//            } catch (e: Exception) {
//                    Log.d("checkingResponseSize6", e.toString())
//                _allPosts.value = NetworkResult.Error("An error occurred: ${e.message}")
//            }
//    fun getAllPost() = viewModelScope.launch{
//     _allPosts.value = NetworkResult.Loading()
//        val result = repository.getAllPost()
//        _allPosts.value=result.collect()
//    }

    fun getAllPost() = viewModelScope.launch {
        _allPosts.value = NetworkResult.Loading()
        try {
            repository.getAllPost().collect { result ->
                _allPosts.value = result


            }
        } catch (e: Exception) {
            _allPosts.value = NetworkResult.Error("An error occurred: ${e.message}")
        }

        Log.d("checkingPostResponseV",_allPosts.toString())
    }


    fun addPostToDatabase(post:Post) = viewModelScope.launch {
        _addPostResultState.value = NetworkResult.Loading()
        val result = repository.createPost(post)
        _addPostResultState.value = result
    }

    fun addChatRoomToDatabase(chatRoom: ChatRoomModel) = viewModelScope.launch {
        _addChatRoomResultState.value =  NetworkResult.Loading()
        val result = repository.createChatRoom(chatRoom)
        _addChatRoomResultState.value = result
    }

    fun addChatMessageToDatabase(chatMessage:ChatMessageModel,chatRoomId:String) = viewModelScope.launch {

        _addChatMessageResultState.value = NetworkResult.Loading()
        val result = repository.createChatMessage(chatMessage,chatRoomId)
        _addChatMessageResultState.value = result

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

    fun getAllMessages(roomId:String)=viewModelScope.launch{

      _getAllChatMessages.value = NetworkResult.Loading()
      val result = repository.getALlChats(roomId)
        _getAllChatMessages.value = result
    }


}