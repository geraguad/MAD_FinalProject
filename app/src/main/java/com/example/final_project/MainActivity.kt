package com.example.final_project

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileInputStream
import java.util.Scanner

data class WordDefinition(val word: String, val definition: String);


class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val joinedGroups = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.joinedGroupsList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, joinedGroups)
        listView.adapter = adapter

        // Fetch the list of joined groups from Firestore
        fetchJoinedGroups()

        // Set onClickListener for ListView to navigate to group chat
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedGroup = joinedGroups[position]
            val groupId = selectedGroup.split("\n")[2].removePrefix("Code: ").trim() // Extract group code (ID)
            navigateToGroupChat(groupId)
        }

        // FloatingActionButton actions (existing code)
        findViewById<FloatingActionButton>(R.id.createGroupButton).setOnClickListener {
            createGroupActivity()
        }
        findViewById<FloatingActionButton>(R.id.joinGroupButton).setOnClickListener {
            joinGroupActivity()
        }
        findViewById<FloatingActionButton>(R.id.createFlashcardButton).setOnClickListener {
            createFlashcardsActivity()
        }
        findViewById<FloatingActionButton>(R.id.viewFlashcardButton).setOnClickListener {
            viewFlashcardsActivity()
        }
        findViewById<FloatingActionButton>(R.id.createQuizButton).setOnClickListener {
            createQuizActivity()
        }
        findViewById<FloatingActionButton>(R.id.viewQuizButton).setOnClickListener {
            viewQuizActivity()
        }
    }

    // Method to fetch the joined groups from Firestore
    private fun fetchJoinedGroups() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                val tempGroups = mutableListOf<String>()
                var remaining = result.size()

                if (remaining == 0) {
                    joinedGroups.clear()
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                for (doc in result) {
                    val groupCode = doc.id
                    val groupName = doc.getString("name") ?: "Unnamed Group"
                    val groupDesc = doc.getString("description") ?: ""

                    db.collection("groups").document(groupCode)
                        .collection("members")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { memberDoc ->
                            if (memberDoc.exists()) {
                                tempGroups.add("$groupName\n$groupDesc\nCode: $groupCode")
                            }
                        }
                        .addOnCompleteListener {
                            remaining--
                            if (remaining == 0) {
                                joinedGroups.clear()
                                joinedGroups.addAll(tempGroups)
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
    }

    // Method to navigate to group chat
    private fun navigateToGroupChat(groupId: String) {
        val intent = Intent(this, groupChatActivity::class.java)
        intent.putExtra("groupId", groupId)  // Pass the group ID to the group chat activity
        startActivity(intent)
    }

    // Methods to navigate to other activities (create/join flashcards, quizzes, etc.)
    private fun createGroupActivity() {
        val intent = Intent(this, createGroupActivity::class.java)
        startActivity(intent)
    }

    private fun joinGroupActivity() {
        val intent = Intent(this, joinGroupActivity::class.java)
        startActivity(intent)
    }

    private fun createFlashcardsActivity() {
        val intent = Intent(this, createFlashcardsActivity::class.java)
        startActivity(intent)
    }

    private fun viewFlashcardsActivity() {
        val intent = Intent(this, viewFlashcardsActivity::class.java)
        startActivity(intent)
    }

    private fun createQuizActivity() {
        val intent = Intent(this, createQuizActivity::class.java)
        startActivity(intent)
    }

    private fun viewQuizActivity() {
        val intent = Intent(this, viewQuizActivity::class.java)
        startActivity(intent)
    }
}
