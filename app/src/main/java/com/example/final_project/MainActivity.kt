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
    private lateinit var myAdapter : ArrayAdapter<String>; // connect from data to gui
    private var dataDefList = ArrayList<String>(); // data
    private var wordDefinition = mutableListOf<WordDefinition>();

    private lateinit var listView: ListView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: ArrayAdapter<String>
    private val joinedGroups = mutableListOf<String>()


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

    private fun fetchJoinedGroups() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                joinedGroups.clear()

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
                                joinedGroups.add("$groupName\n$groupDesc\nCode: $groupCode")
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.studyBuddy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.joinedGroupsList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, joinedGroups)
        listView.adapter = adapter

        fetchJoinedGroups()

        val cgbutton = findViewById<FloatingActionButton>(R.id.createGroupButton)
        cgbutton.setOnClickListener {
            createGroupActivity()
        }

        val jgbutton = findViewById<FloatingActionButton>(R.id.joinGroupButton)
        jgbutton.setOnClickListener {
            joinGroupActivity()
        }

        val cfbutton = findViewById<FloatingActionButton>(R.id.createFlashcardButton)
        cfbutton.setOnClickListener {
            createFlashcardsActivity()
        }

        val vfbutton = findViewById<FloatingActionButton>(R.id.viewFlashcardButton)
        vfbutton.setOnClickListener {
            viewFlashcardsActivity()
        }

        val cqbutton = findViewById<FloatingActionButton>(R.id.createQuizButton)
        cqbutton.setOnClickListener {
            createQuizActivity()
        }

        val vqbutton = findViewById<FloatingActionButton>(R.id.viewQuizButton)
        vqbutton.setOnClickListener {
            viewQuizActivity()
        }



    }
}