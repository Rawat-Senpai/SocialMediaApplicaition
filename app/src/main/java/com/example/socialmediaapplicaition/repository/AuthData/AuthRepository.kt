package com.example.socialmediaapplicaition.repository.AuthData

import android.net.Uri
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    val currentUser:FirebaseUser?
    suspend fun login(email:String,password:String):NetworkResult<FirebaseUser>
    suspend fun signup(name:String,email:String,password: String):NetworkResult<FirebaseUser>
    suspend fun addUserToDatabase(user:User):NetworkResult<Unit>
    suspend fun uploadPhotoToFireStore(photoUri:Uri):NetworkResult<Uri>

    suspend fun uploadVideosToFireStore(videoUri:Uri):NetworkResult<Uri>
    suspend fun getUserData(uid:String):NetworkResult<User>
    fun logout()

}