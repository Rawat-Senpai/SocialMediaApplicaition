package com.example.socialmediaapplicaition.repository.firebaseData

import android.util.Log
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.PersonComments
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.Reactions
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.Constants
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.addDataToFirestore

import com.example.socialmediaapplicaition.utils.getDataOfUserFromDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(private val firebaseFirestore: FirebaseFirestore) :
    FirebaseRepository {

    override suspend fun getAllUser(): NetworkResult<ArrayList<User>> {
        return try {
            val snapshot = firebaseFirestore.collection("users").get().getDataOfUserFromDatabase()

            if (snapshot != null && !snapshot.isEmpty) {
                val userList = ArrayList<User>()
                for (document in snapshot.documents) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        userList.add(it)
                    }
                }
                Log.d("checkingResponse1Size", userList.size.toString())
                NetworkResult.Success(userList)
            } else {
                NetworkResult.Error("No users found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }

    override fun getAllPost(): Flow<PostResponse> = callbackFlow {


        val listenerRegistration = firebaseFirestore.collection("posts")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    trySend(NetworkResult.Error(exception.toString()))
                    return@addSnapshotListener
                }

                val postList = ArrayList<Post>()
                if (snapshot != null && !snapshot.isEmpty) {
                    for (document in snapshot.documents) {
                        val post = document.toObject(Post::class.java)
                        post?.let {
                            postList.add(it)
                        }
                    }
                    postList.sortByDescending { it.createdAt }
                    trySend(NetworkResult.Success(postList))
                } else {
                    trySend(NetworkResult.Error("No posts found"))
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun createPost(post: Post): NetworkResult<Unit> {
        return try {
            // Add user data to Firestore
            val postId = UUID.randomUUID().toString()
            post.id = postId

            firebaseFirestore.collection("posts")
                .document(postId)
                .set(post)
                .addDataToFirestore()
            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun updateLikeStatus(post: Post, userId: String): NetworkResult<Unit> {
        return try {
            // Update like of post in  Firestore
            val isLiked = post.likedBy.contains(userId)

            if (isLiked) {
                post.likedBy.remove(userId)
            } else {
                post.likedBy.add(userId)
            }
            firebaseFirestore.collection("posts")
                .document(post.id)
                .set(post)
                .addDataToFirestore()
            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun savePost(post: Post, userId: String): NetworkResult<Unit> {
        return try {

            val isLiked = post.savedBy.contains(userId)

            if (isLiked) {
                post.savedBy.remove(userId)
            } else {
                post.savedBy.add(userId)
            }

            firebaseFirestore.collection("posts")
                .document(post.id)
                .set(post)
                .addDataToFirestore()
            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)
        }
        catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }


    override suspend fun addCommentToPost(
        post: Post,
        commentPerson: PersonComments
    ): NetworkResult<Unit> {
        return try {

            post.comments.add(commentPerson)

            firebaseFirestore.collection("posts")
                .document(post.id)
                .set(post)
                .addDataToFirestore()

            Log.d("checkingComment", "successfull")

            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            NetworkResult.Error(e.toString())
        }

    }

    override suspend fun getUserData(uid: String): NetworkResult<User> {
        return try {
            val snapshot = firebaseFirestore.collection("users").document(uid).get()
                .getDataOfUserFromDatabase()
            Log.d("checkingRes1", snapshot.toString())
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                Log.d("checkingRes2", user.toString())
                if (user != null) {
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error("Failed to parse user data")
                }
            } else {
                Log.d("checkingRes2", "user not found ")
                NetworkResult.Error("User not found")
            }
        } catch (e: Exception) {
            Log.e("checkingRes3", "Error fetching user data: ${e.message}", e)
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun createChatRoom(chat: ChatRoomModel): NetworkResult<Unit> {
        return try {

            val chatId = chat.chatroomId
            chat.chatroomId = chatId

            firebaseFirestore.collection("chat_room")
                .document(chatId)
                .set(chat)
                .addDataToFirestore()

            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun createChatMessage(
        chat: ChatMessageModel,
        chatRoomId: String
    ): NetworkResult<Unit> {
        return try {
            chat.id=  UUID.randomUUID().toString()
            firebaseFirestore.collection("chat_room")
                .document(chatRoomId)
                .collection("chats")
                .document(chat.id)
                .set(chat)
                .addDataToFirestore()

            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override fun getAllChats(roomId: String): Flow<ChatsResponse> = callbackFlow {
        val listenerRegistration =
            firebaseFirestore.collection("chat_room").document(roomId).collection("chats")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        trySend(NetworkResult.Error(exception.toString()))
                        return@addSnapshotListener
                    }

                    val chatList = ArrayList<ChatMessageModel>()
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {
                            val post = document.toObject(ChatMessageModel::class.java)
                            post?.let {
                                chatList.add(it)
                            }
                        }
                        chatList.sortBy { it.timeStamp }
                        trySend(NetworkResult.Success(chatList))
                    } else {
                        trySend(NetworkResult.Error("No posts found"))
                    }
                }
        awaitClose { listenerRegistration.remove() }
    }

    override fun getAllPreviousChat(): Flow<PreviousChatResponse> = callbackFlow {
        val listenerRegistration =
            firebaseFirestore.collection("chat_room")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        trySend(NetworkResult.Error(exception.toString()))
                        return@addSnapshotListener
                    }

                    val chatList = ArrayList<ChatRoomModel>()
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (document in snapshot.documents) {

                            Log.d("document", document.toString())

                            val post = document.toObject(ChatRoomModel::class.java)
                            post?.let {
                                chatList.add(it)
                            }
                            Log.d("document2", post.toString())
                        }
                        chatList.sortByDescending { it.lastMessageTimestamp }
                        Log.d("checkingChatList__", chatList.size.toString())
                        Log.d("checkingChatList__", chatList.toString())
                        trySend(NetworkResult.Success(chatList))
                    } else {
                        trySend(NetworkResult.Error("No posts found"))
                    }
                }
        awaitClose { listenerRegistration.remove() }

    }


    override suspend fun createSavePost(
        post: Post,
        userId: String,
        action:String
    ): NetworkResult<Unit> {
        return try {


            if(action==Constants.SAVE){
                firebaseFirestore.collection("users")
                    .document(userId)
                    .collection("savedPost")
                    .document(post.id)
                    .set(post)

            }else{
                firebaseFirestore.collection("users")
                    .document(userId)
                    .collection("savedPost")
                    .document(post.id)
                    .delete()
            }

            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun getUserSavedPost(userId: String): NetworkResult<ArrayList<Post>> {

        return try {

            //     firebaseFirestore.collection("chat_room").document(roomId).collection("chats")
            val snapshot = firebaseFirestore.collection("users").document(userId).collection("savedPost").get().getDataOfUserFromDatabase()
            Log.d("savedPost",snapshot.toString())
            if (snapshot != null && !snapshot.isEmpty) {
                val userList = ArrayList<Post>()
                for (document in snapshot.documents) {
                    val post = document.toObject(Post::class.java)
                    post?.let {
                        userList.add(it)
                    }
                }

                Log.d("savedPost",userList.toString())
                NetworkResult.Success(userList)
            } else {
                NetworkResult.Error("No post saved found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }

    override suspend fun setUserStatus(userId: String, onlineString: String): NetworkResult<Unit> {
        return try {
            // Update specific field in Firestore document
            firebaseFirestore.collection("users")
                .document(userId)
                .update("onlineStatus", onlineString)
                .addOnSuccessListener {
                    Log.d("responseData", "Field successfully updated")
                }
                .addOnFailureListener { e ->
                    Log.d("crash123", e.toString())
                }
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun deleteChatMessage(
        chat: ChatMessageModel,
        userId: String,
        chatRoomId:String
    ): NetworkResult<Unit> {
        return try {

            chat.removedBy.add(userId)

            firebaseFirestore.collection("chat_room")
                .document(chatRoomId)
                .collection("chats")
                .document(chat.id)
                .set(chat)
                .addDataToFirestore()

            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun updateChatReaction(
        chat: ChatMessageModel,
        userReaction: Reactions,
        chatRoomId: String
    ): NetworkResult<Unit> {
        return try {

            val containsId = chat.emojiReacted.any { it.senderId == userReaction.senderId }

            if (containsId) {
                chat.emojiReacted.removeIf{it.senderId == userReaction.senderId}
                chat.emojiReacted.add(userReaction)
            } else {
                chat.emojiReacted.add(userReaction)
            }
            
            firebaseFirestore.collection("chat_room")
                .document(chatRoomId)
                .collection("chats")
                .document(chat.id)
                .set(chat)
                .addDataToFirestore()

            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }


}