package com.example.socialmediaapplicaition.repository.firebaseData

import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.PersonComments
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.Reactions
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

typealias Posts = ArrayList<Post>
typealias PostResponse = NetworkResult<Posts>

typealias Chats= ArrayList<ChatMessageModel>
typealias ChatsResponse = NetworkResult<Chats>

typealias PreviousChat = ArrayList<ChatRoomModel>
typealias PreviousChatResponse = NetworkResult<PreviousChat>

interface FirebaseRepository {
    suspend fun getAllUser():NetworkResult<ArrayList<User>>
    fun getAllPost():Flow<PostResponse>
    suspend fun createPost(post:Post):NetworkResult<Unit>
    suspend fun updateLikeStatus(post:Post, userId: String): NetworkResult<Unit>
    suspend fun savePost(post:Post, userId: String): NetworkResult<Unit>
    suspend fun createChatRoom(chat: ChatRoomModel):NetworkResult<Unit>
    suspend fun createChatMessage(chat: ChatMessageModel,chatRoomId:String):NetworkResult<Unit>
    fun getAllChats(roomId:String): Flow<ChatsResponse>
    fun getAllPreviousChat():Flow<PreviousChatResponse>
    suspend fun  addCommentToPost(post: Post, commentPerson: PersonComments): NetworkResult<Unit>
    suspend fun getUserData(uid:String):NetworkResult<User>
    suspend fun createSavePost(post: Post,userId:String,action:String):NetworkResult<Unit>
    suspend fun getUserSavedPost(userId:String):NetworkResult<ArrayList<Post>>
    suspend fun setUserStatus(userId: String,onlineString: String):NetworkResult<Unit>
    suspend fun deleteChatMessage(chat:ChatMessageModel, userId: String,chatRoomId:String): NetworkResult<Unit>
    suspend fun updateChatReaction(chat:ChatMessageModel, userId: Reactions, chatRoomId:String): NetworkResult<Unit>

}