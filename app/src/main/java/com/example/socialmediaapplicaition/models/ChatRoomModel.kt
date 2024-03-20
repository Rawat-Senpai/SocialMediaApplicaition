package com.example.socialmediaapplicaition.models

data class ChatRoomModel (
    var chatroomId: String = "",
    var userIds: List<User> = mutableListOf(),
    var lastMessageTimestamp: Long? = null,
    var lastMessageSenderId: String = "",
    var lastMessage: String = ""
)