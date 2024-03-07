package com.example.socialmediaapplicaition.data


import android.util.Log
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.addDataToFirestore
import com.example.socialmediaapplicaition.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuth:FirebaseAuth, private val firestore: FirebaseFirestore) :
    AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser


    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {

        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email,password).await()
            NetworkResult.Success(result.user)
        }catch (e:Exception){
            Log.d("crash",e.toString())
            NetworkResult.Error(e.toString())
        }

    }


    override suspend fun signup(
        name: String,
        email: String,
        password: String
    ): NetworkResult<FirebaseUser> {

        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email,password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            NetworkResult.Success(result.user!!)
            NetworkResult.Success(result.user)
        }catch (e:Exception){
            Log.d("crash",e.toString())
            NetworkResult.Error(e.toString())
        }
    }


    override suspend fun addUserToDatabase(user: User): NetworkResult<Unit> {
        return try {
            // Add user data to Firestore
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .addDataToFirestore()
            Log.d("responseData", "successfully")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("crash123", e.toString())
            NetworkResult.Error(e.toString())
        }
    }


    override fun logout() {
        firebaseAuth.signOut()
    }


}