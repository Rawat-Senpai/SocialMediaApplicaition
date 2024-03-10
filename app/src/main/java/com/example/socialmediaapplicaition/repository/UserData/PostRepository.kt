package com.example.socialmediaapplicaition.repository.UserData

import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult


interface PostRepository {
    suspend fun getAllUser():NetworkResult<ArrayList<User>>


}