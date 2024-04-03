package com.example.socialmediaapplicaition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.socialmediaapplicaition.utils.TokenManager
import com.example.socialmediaapplicaition.viewModels.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager
    private val postViewModel by viewModels<FirebaseViewModel>()
    private var userId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        userId=tokenManager.getId().toString()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}