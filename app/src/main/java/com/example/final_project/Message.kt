package com.example.final_project

import com.google.firebase.Timestamp

data class Message(
    val senderId: String = "",
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: Timestamp? = null
)