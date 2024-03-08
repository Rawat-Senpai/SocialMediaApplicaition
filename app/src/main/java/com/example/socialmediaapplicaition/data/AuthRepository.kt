package com.example.socialmediaapplicaition.data

import android.net.Uri
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

interface AuthRepository {
    val currentUser:FirebaseUser?

    suspend fun login(email:String,password:String):NetworkResult<FirebaseUser>
    suspend fun signup(name:String,email:String,password: String):NetworkResult<FirebaseUser>
    suspend fun addUserToDatabase(user:User):NetworkResult<Unit>

    suspend fun uploadPhotoToFireStore(photoUri:Uri):NetworkResult<Uri>
    fun logout()

}