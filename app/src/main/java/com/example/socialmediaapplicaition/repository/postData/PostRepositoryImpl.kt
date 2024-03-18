package com.example.socialmediaapplicaition.repository.postData

import android.util.Log
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.addDataToFirestore

import com.example.socialmediaapplicaition.utils.getDataOfUserFromDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val firebaseFirestore: FirebaseFirestore) :
    PostRepository {

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
                Log.d("checkingResponseSize", userList.size.toString())
                NetworkResult.Success(userList)
            } else {
                NetworkResult.Error("No users found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }

    override suspend fun getAllPost(): NetworkResult<ArrayList<Post>> {

        return try {
            val snapshot = firebaseFirestore.collection("posts").get().getDataOfUserFromDatabase()

            if (snapshot != null && !snapshot.isEmpty) {
                val postList = ArrayList<Post>()
                for (document in snapshot.documents) {
                    val posts = document.toObject(Post::class.java)
                    posts?.let {
                        postList.add(it)
                    }
                }
                postList.sortByDescending { it.createdAt }
                Log.d("checkingResponseSize", postList.size.toString())
                NetworkResult.Success(postList)
            } else {
                NetworkResult.Error("No users found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }

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

    override suspend fun createChatRoom(chat:ChatRoomModel): NetworkResult<Unit> {
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

    override suspend fun createChatMessage(chat: ChatMessageModel): NetworkResult<Unit> {
        return try {



            firebaseFirestore.collection("chat_room")
                .document("1_2")
                .collection("chats")
                .add(chat)
//                .addDataToFirestore()

            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }


}