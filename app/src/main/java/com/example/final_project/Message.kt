package com.example.final_project

import com.google.firebase.Timestamp
//converts all text into a message
data class Message(
    val senderId: String = "",
    val senderName: String = "",
    val messageText: String = "",
    val timestamp: Timestamp? = null
)