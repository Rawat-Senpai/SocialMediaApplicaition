package com.example.socialmediaapplicaition.repository.postData

import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

typealias Posts = ArrayList<Post>
typealias PostResponse = NetworkResult<Posts>

interface PostRepository {
    suspend fun getAllUser():NetworkResult<ArrayList<User>>
     fun  getAllPost():Flow<PostResponse>
    suspend fun createPost(post:Post):NetworkResult<Unit>
    suspend fun updateLikeStatus(post:Post, userId: String): NetworkResult<Unit>
    suspend fun createChatRoom(chat: ChatRoomModel):NetworkResult<Unit>
    suspend fun createChatMessage(chat: ChatMessageModel,chatRoomId:String):NetworkResult<Unit>
    suspend fun getALlChats(roomId:String): NetworkResult<ArrayList<ChatMessageModel>>
}