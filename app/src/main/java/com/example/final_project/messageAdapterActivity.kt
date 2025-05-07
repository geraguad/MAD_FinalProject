package com.example.final_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.Message

//gets all message objects
class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
//UI elements
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderName: TextView = itemView.findViewById(R.id.senderName)
        val messageText: TextView = itemView.findViewById(R.id.messageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_message_adapter, parent, false)
        return MessageViewHolder(view)
    }
//binds data to UI
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.senderName.text = message.senderName
        holder.messageText.text = message.messageText
    }
//how many mssages are there
    override fun getItemCount(): Int {
        return messages.size
    }
}
