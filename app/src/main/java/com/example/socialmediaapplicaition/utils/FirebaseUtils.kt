package com.example.socialmediaapplicaition.utils

import android.util.Log
import com.example.socialmediaapplicaition.models.User
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.await(): T {

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

            Log.d("checkingShobhit","successful")
            cont.resume(result,null)
        }
            .addOnFailureListener { e ->

                Log.d("checkingShobhit","false")
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