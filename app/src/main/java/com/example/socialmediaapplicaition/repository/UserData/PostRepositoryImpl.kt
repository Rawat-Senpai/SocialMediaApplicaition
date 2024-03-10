package com.example.socialmediaapplicaition.repository.UserData

import android.util.Log
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult

import com.example.socialmediaapplicaition.utils.getDataOfUserFromDatabase
import com.google.firebase.firestore.FirebaseFirestore
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


}