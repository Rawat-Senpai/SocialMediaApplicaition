package com.example.socialmediaapplicaition.models

data class Post(
    var id:String="",
    var text:String="",
    var imageUrl:String="",
    var createdBy:User=User(),
    var createdAt:Long=0L,
    val likedBy:ArrayList<String> = ArrayList(),
    val savedBy:ArrayList<String> = ArrayList(),
    val comments:ArrayList<PersonComments> = ArrayList()
)

data class PersonComments(
    var personName:String ="",
    var personProfile:String="",
    var comment:String="",
    var createdAt:Long=0L,
)