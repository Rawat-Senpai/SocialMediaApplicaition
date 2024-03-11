package com.example.socialmediaapplicaition.models

data class Post(
    var text:String="",
    var imageUrl:String="",
    var createdBy:User=User(),
    var createdAt:Long=0L,
    val likedBy:ArrayList<String> = ArrayList(),
    val savedBy:ArrayList<String> = ArrayList()
) {
}