package com.example.socialmediaapplicaition.repository.postData

import android.util.Log
import com.example.socialmediaapplicaition.models.ChatMessageModel
import com.example.socialmediaapplicaition.models.ChatRoomModel
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.addDataToFirestore
import com.example.socialmediaapplicaition.utils.getDataAsFlow

import com.example.socialmediaapplicaition.utils.getDataOfUserFromDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                Log.d("checkingResponse1Size", userList.size.toString())
                NetworkResult.Success(userList)
            } else {
                NetworkResult.Error("No users found")
            }
        } catch (e: Exception) {
            NetworkResult.Error("An error occurred: ${e.message}")
        }
    }

    /*


    override fun getAllPost(): Flow<PostResponse> = callbackFlow  {

        val listenerRegistration = firebaseFirestore.collection("posts")
            .addSnapshotListener { snapshot, exception ->
                Log.d("checkingResponseSize1", snapshot.toString())
                if (exception != null) {
                    // Emit error state if there's an exception
                    //PostResponse.Error(exception.toString())
                    trySend(NetworkResult.Error(exception.toString()))
                    Log.d("checkingResponseSize2", snapshot.toString())
                    return@addSnapshotListener

                }


                if (snapshot != null && !snapshot.isEmpty) {
                val postList = ArrayList<Post>()
                for (document in snapshot.documents) {
                    val posts = document.toObject(Post::class.java)
                    posts?.let {
                        postList.add(it)
                    }
                }
                postList.sortByDescending { it.createdAt }
                    Log.d("checkingResponseSize3", postList.toString())
                Log.d("checkingResponseSize3", postList.size.toString())
                NetworkResult.Success(postList)
            } else {
                    Log.d("checkingResponseSize4", snapshot.toString())
                NetworkResult.Error("No Post found")

            }

                // Emit success state with the list of posts
//                trySend(NetworkResult.Success(posts))
            }

        awaitClose { listenerRegistration.remove() }
    }

  */
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


            firebaseFirestore.collection("chat_room")
                .document(chatRoomId)
                .collection("chats")
                .add(chat)
                .addDataToFirestore()

            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)

        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }

    override suspend fun getALlChats(roomId: String): NetworkResult<ArrayList<ChatMessageModel>> {

        return try {
            val snapshot =
                firebaseFirestore.collection("chat_room").document(roomId).collection("chats").get()
                    .getDataOfUserFromDatabase()

            if (snapshot != null && !snapshot.isEmpty) {
                val postList = ArrayList<ChatMessageModel>()
                for (document in snapshot.documents) {
                    val chat = document.toObject(ChatMessageModel::class.java)
                    chat?.let {
                        postList.add(it)
                    }
                }
                postList.sortByDescending { it.timeStamp }
                Log.d("checkingResponseSizechats", postList.size.toString())
                NetworkResult.Success(postList)
            } else {
                NetworkResult.Error("No users found")
            }

        } catch (e: Exception) {
            NetworkResult.Error(e.toString())
        }


    }


}