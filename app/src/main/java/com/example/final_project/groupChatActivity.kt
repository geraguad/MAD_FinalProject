package com.example.final_project

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.final_project.Message

class groupChatActivity : AppCompatActivity() {
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_group_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        groupId = intent.getStringExtra("groupId")
        if (groupId != null) {
            Log.d("groupChatActivity", "Group ID: $groupId")
            // Proceed with loading messages for this group
        } else {
            Log.e("groupChatActivity", "No groupId received!")
            finish() // Close activity if groupId is missing
        }

        // Initialize views
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        recyclerView = findViewById(R.id.displaymessages)

        // Setup RecyclerView
        adapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            sendMessage()
        }

        listenForMessages()
    }

    private fun sendMessage() {
        val text = messageInput.text.toString().trim()
        val currentUser = auth.currentUser

        if (text.isNotEmpty() && currentUser != null && groupId != null) {
            val message = hashMapOf(
                "senderId" to currentUser.uid,
                "senderName" to (currentUser.displayName ?: "Anonymous"),
                "messageText" to text,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("groups")
                .document(groupId!!)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    messageInput.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.e("groupChatActivity", "Error sending message", e)
                }
        }
    }

    private fun listenForMessages() {
        db.collection("groups")
            .document(groupId!!)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e("groupChatActivity", "Error loading messages", error)
                    return@addSnapshotListener
                }

                messages.clear()
                messages.addAll(snapshot.toObjects(Message::class.java))
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
            }
    }
}
