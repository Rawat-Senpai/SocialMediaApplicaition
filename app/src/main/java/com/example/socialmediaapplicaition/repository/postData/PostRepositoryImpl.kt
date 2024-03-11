package com.example.socialmediaapplicaition.repository.postData

import android.util.Log
import com.example.socialmediaapplicaition.models.Post
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.addDataToFirestore

import com.example.socialmediaapplicaition.utils.getDataOfUserFromDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl  @Inject constructor(private val firebaseFirestore: FirebaseFirestore):PostRepository {


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
                Log.d("checkingResponseSize",userList.size.toString())
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
                val userList = ArrayList<Post>()
                for (document in snapshot.documents) {
                    val posts = document.toObject(Post::class.java)
                    posts?.let {
                        userList.add(it)
                    }
                }
                Log.d("checkingResponseSize",userList.size.toString())
                NetworkResult.Success(userList)
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
            firebaseFirestore.collection("posts")
                .document(UUID.randomUUID().toString())
                .set(post)
                .addDataToFirestore()
            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }


}