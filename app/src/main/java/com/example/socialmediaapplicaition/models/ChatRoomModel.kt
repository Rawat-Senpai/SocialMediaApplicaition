package com.example.socialmediaapplicaition.models

data class ChatRoomModel (
    var chatroomId: String = "",
    var userList: ArrayList<User> = ArrayList(),
    var lastMessageTimestamp: Long? = null,
    var lastMessageSenderId: String = "",
    var lastMessage: String = ""
)