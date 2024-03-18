package com.example.socialmediaapplicaition.repository.postData

import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult


interface PostRepository {
    suspend fun getAllUser():NetworkResult<ArrayList<User>>
    suspend fun getAllPost():NetworkResult<ArrayList<Post>>
    suspend fun createPost(post:Post):NetworkResult<Unit>
    suspend fun updateLikeStatus(post:Post, userId: String): NetworkResult<Unit>
    suspend fun createChatRoom(chat: ChatRoomModel):NetworkResult<Unit>

    suspend fun createChatMessage(chat: ChatMessageModel):NetworkResult<Unit>
}