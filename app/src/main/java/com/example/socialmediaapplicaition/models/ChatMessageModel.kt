package com.example.socialmediaapplicaition.models

import com.example.socialmediaapplicaition.utils.Constants

data class ChatMessageModel(
    var message:String = "",
    var removedByMe:String="",
    var removedByThem:String="",
    var emojiReacted:ArrayList<Reactions> = ArrayList<Reactions>(),
    var timeStamp:Long = 0L,
    var reply:String="",
    var senderId:String="",
    var type:String=Constants.MESSAGE_TYPE_TEXT
)
data class Reactions(
    var senderId:String="",
    var senderName:String="",
    var senderImage:String="",
)