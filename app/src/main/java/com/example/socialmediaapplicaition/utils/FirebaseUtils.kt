package com.example.socialmediaapplicaition.utils

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
suspend fun <T> Task<T>.awaitFunction(): T {

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            Log.d("functionAwait", cont.toString())
            if (it.exception != null) {
                cont.resumeWithException(it.exception!!)
            } else {
                cont.resume(it.result, null)
            }
        }
    }

}

suspend fun <T> Task<T>.addDataToFirestore(): T {
    return suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            Log.d("checkingShobhit",result.toString())
            cont.resume(result,null)
        }
            .addOnFailureListener { e ->

                Log.d("checkingShobhit",e.toString())
                cont.resumeWithException(e)
            }
    }
}

suspend fun <T> Task<T>.getDataOfUserFromDatabase(): T {
    return suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->

            Log.d("checkingShobhit","successful")

            Log.d("checkingShobhit",result.toString())

            cont.resume(result,null)
        }
            .addOnFailureListener { e ->

                Log.d("checkingShobhit","false")
                cont.resumeWithException(e)
            }
    }
}



 fun DocumentReference.getDataAsFlow(): Flow<DocumentSnapshot?> = callbackFlow {
    val listenerRegistration = addSnapshotListener { snapshot, exception ->
        if (exception != null) {
            close(exception)
            return@addSnapshotListener
        }
        trySend(snapshot)
    }

    awaitClose {
        listenerRegistration.remove()
    }
}