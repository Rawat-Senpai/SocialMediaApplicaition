package com.example.socialmediaapplicaition.models

data  class Post(
    val text:String="",
    val imageUrl:String="",
    val createdBy:User=User(),
    var createdAt:Long=0L,
    val likedBy:ArrayList<String> = ArrayList(),
    val savedBy:ArrayList<String> = ArrayList()
) {
}