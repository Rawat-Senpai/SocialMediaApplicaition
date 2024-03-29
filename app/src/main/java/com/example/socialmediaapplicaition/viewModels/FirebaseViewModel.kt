package com.example.socialmediaapplicaition.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.PersonComments
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.repository.firebaseData.FirebaseRepository
import com.example.socialmediaapplicaition.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(private val repository:FirebaseRepository): ViewModel() {



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

    private val _getAllChatMessages = MutableStateFlow<NetworkResult<ArrayList<ChatMessageModel>>?>(NetworkResult.Loading())
    val getAllChatChatMessages :StateFlow<NetworkResult<ArrayList<ChatMessageModel>>?> = _getAllChatMessages


    private val _searchedUser = MutableStateFlow<NetworkResult<List<User>>?>(null)
    val searchedUser :StateFlow<NetworkResult<List<User>>?> = _searchedUser

    private val _searchContacts = MutableStateFlow<NetworkResult<List<ChatRoomModel>>?>(null)
    val searchContact : StateFlow<NetworkResult<List<ChatRoomModel>>?> = _searchContacts

    private val _getAllChatHistory = MutableStateFlow<NetworkResult<List<ChatRoomModel>>?>(null)
    val getAllChatHistory:StateFlow <NetworkResult<List<ChatRoomModel>>?> = _getAllChatHistory

    private  val _addCommentInPost = MutableStateFlow<NetworkResult<Unit>?>(null)
    val addCommentPost get():StateFlow<NetworkResult<Unit>?> = _addCommentInPost




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

    fun addCommentToPost(post:Post,commentPerson:PersonComments) = viewModelScope.launch {
        _addCommentInPost.value = NetworkResult.Loading()
        val result = repository.addCommentToPost(post,commentPerson)
        _addCommentInPost.value = result
    }



    fun searchUsers(keyPoint: String, yourUserId: String) {
        allUsers.value?.data?.let { users ->
            val filteredUsers = users.filter { user ->
                // Exclude the user with your ID
                user.id != yourUserId &&
                        // Filter users based on the specified key point
                        // Here you can define your condition for filtering, for example, searching by name
                        user.name.contains(keyPoint, ignoreCase = true) // Assuming you are searching by user's name
            }
            // Update the StateFlow with filtered users
            _searchedUser.value = NetworkResult.Success(filteredUsers)
        }
    }

    fun searchContacts(keyPoint: String,yourUserId: String){
//        Log.d("checkingSearchContact",getAllChatHistory.value?.data?.size.toString())
        getAllChatHistory?.value?.data.let { users->

//                for (data in users!!){
//                    Log.d("checkingSearchContact",data.userList.toString())
//                }

            val filteredUsers = users?.filter { user ->

                val otherUser = if (user.userList[0].id != yourUserId) user.userList[0] else user.userList.getOrNull(1)
                otherUser != null && otherUser.name.contains(keyPoint, ignoreCase = true)
            }
            Log.d("checkingSearchContact",filteredUsers.toString())
            _searchContacts.value=NetworkResult.Success(filteredUsers)
        }

    }





    fun getAllMessages(roomId:String)=viewModelScope.launch{

      _getAllChatMessages.value = NetworkResult.Loading()
        try {
            repository.getAllChats(roomId).collect{result->
//                _getAllChatMessages.value = it

                when(result){
                    is NetworkResult.Error ->{
                        _getAllChatMessages.value = NetworkResult.Error(result.toString())
                    }

                    is NetworkResult.Loading -> {
                        _getAllChatMessages.value = NetworkResult.Loading()
                    }

                    is NetworkResult.Success -> {
                        _getAllChatMessages.value = result
                    }
                }
            }
        }catch (e:Exception){
            _getAllChatMessages.value = NetworkResult.Error(e.toString())
        }
    }

    fun getAllPreviousChat(userId:String) = viewModelScope.launch {
        _getAllChatHistory.value = NetworkResult.Loading()
        try {
            repository.getAllPreviousChat().collect{result->

                when (result) {
                    is NetworkResult.Success -> {
                        val filteredChats = result.data?.filter { chat -> chat.chatroomId.contains(userId) }
                        Log.d("checkingChatList",filteredChats.toString())
                        if (filteredChats != null) {
                            _getAllChatHistory.value = NetworkResult.Success(filteredChats.toMutableList())
                        } else {
                            _getAllChatHistory.value = NetworkResult.Success(ArrayList()) // No matches found
                        }
                    }
                    is NetworkResult.Error -> {
                        _getAllChatHistory.value = NetworkResult.Error(result.toString()) // Pass along the error result
                    }
                    is NetworkResult.Loading -> {
                        _getAllChatHistory.value= NetworkResult.Loading()
                        // Loading state is already handled, so no need to do anything here
                    }
                }

            }
        }catch (e:Exception){
            _getAllChatHistory.value = NetworkResult.Error(e.toString())
        }
    }


}