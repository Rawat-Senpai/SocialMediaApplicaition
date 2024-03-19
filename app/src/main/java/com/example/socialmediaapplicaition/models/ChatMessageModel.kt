package com.example.socialmediaapplicaition.models

data class ChatMessageModel(
    var message:String = "",
    var removedByMe:String="",
    var removedByThem:String="",
    var emojiReacted:ArrayList<Reactions> = ArrayList<Reactions>(),
    var timeStamp:Long = 0L,
    var reply:String="",
    var senderId:String=""
)
data class Reactions(
    var senderId:String="",
    var senderName:String="",
    var senderImage:String="",
)