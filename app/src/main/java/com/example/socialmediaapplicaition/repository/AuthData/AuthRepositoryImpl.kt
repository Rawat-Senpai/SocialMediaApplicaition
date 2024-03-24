package com.example.socialmediaapplicaition.repository.AuthData


import android.net.Uri
import android.util.Log
import com.example.socialmediaapplicaition.models.User
import com.example.socialmediaapplicaition.utils.NetworkResult
import com.example.socialmediaapplicaition.utils.addDataToFirestore
import com.example.socialmediaapplicaition.utils.awaitFunction
import com.example.socialmediaapplicaition.utils.getDataOfUserFromDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuth:FirebaseAuth, private val firestore: FirebaseFirestore,private val storage:FirebaseStorage) :
    AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email,password).awaitFunction()
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
            val result = firebaseAuth.createUserWithEmailAndPassword(email,password).awaitFunction()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.awaitFunction()
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

    override suspend fun getUserData(uid: String): NetworkResult<User> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().getDataOfUserFromDatabase()
            Log.d("checkingRes1",snapshot.toString())
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                Log.d("checkingRes2",user.toString())
                if (user != null) {
                    NetworkResult.Success(user)
                } else {
                    NetworkResult.Error("Failed to parse user data")
                }
            } else {
                Log.d("checkingRes2","user not found ")
                NetworkResult.Error("User not found")
            }
        } catch (e: Exception) {
            Log.e("checkingRes3", "Error fetching user data: ${e.message}", e)
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun uploadPhotoToFireStore(photoUri: Uri): NetworkResult<Uri> {

        Log.d("CheckingResponse","here")

        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(photoUri)
            uploadTask.awaitFunction()

            // Get the download URL of the uploaded photo
            val downloadUrl = imageRef.downloadUrl.awaitFunction()
            Log.d("ShobhitResponse",downloadUrl.toString())
            Log.d("CheckingResponse","success")
            NetworkResult.Success(downloadUrl)

        }catch (e:Exception){
            Log.d("CheckingResponse",e.toString())
            NetworkResult.Error(e.message ?: "Failed to upload photo")
        }
    }




    override fun logout() {
        firebaseAuth.signOut()
    }


}