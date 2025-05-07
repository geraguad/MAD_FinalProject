package com.example.final_project

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class joinGroupActivity : AppCompatActivity() {
    private lateinit var searchByNameInput: EditText
    private lateinit var searchByCodeInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsListView: ListView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var searchResults: List<String> = listOf()  // Store group descriptions to match clicks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.studyBuddy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchByNameInput = findViewById(R.id.groupNameInput2)
        searchByCodeInput = findViewById(R.id.groupCodeInput)
        searchButton = findViewById(R.id.searchGroupButton)
        resultsListView = findViewById(R.id.selectGroupList)

        searchButton.setOnClickListener {
            val nameQuery = searchByNameInput.text.toString().trim()
            val codeQuery = searchByCodeInput.text.toString().trim()

            when {
                codeQuery.isNotEmpty() -> searchByCode(codeQuery)
                nameQuery.isNotEmpty() -> searchByName(nameQuery)
                else -> Toast.makeText(this, "Enter name or code to search", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle clicking on a group to join
        resultsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedGroupInfo = searchResults[position]
            val codeLine = selectedGroupInfo.lines().find { it.startsWith("Code:") }
            val groupCode = codeLine?.substringAfter("Code:")?.trim()

            val userId = auth.currentUser?.uid

            if (groupCode != null && userId != null) {
                val memberData = hashMapOf("userId" to userId)

                db.collection("groups").document(groupCode)
                    .collection("members")
                    .document(userId)
                    .set(memberData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Joined group successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Optionally go back to previous screen
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to join group", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Invalid group or user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchByCode(code: String) {
        db.collection("groups")
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener { result ->
                val groups = result.documents.map { doc ->
                    val name = doc.getString("name") ?: "Unnamed"
                    val desc = doc.getString("description") ?: "No description"
                    "Group: $name\nDescription: $desc\nCode: $code"
                }
                displayResults(groups)
            }
    }

    private fun searchByName(name: String) {
        db.collection("groups")
            .whereGreaterThanOrEqualTo("name", name)
            .whereLessThanOrEqualTo("name", name + '\uf8ff') // partial match
            .get()
            .addOnSuccessListener { result ->
                val groups = result.documents.map { doc ->
                    val foundName = doc.getString("name") ?: "Unnamed"
                    val desc = doc.getString("description") ?: "No description"
                    val code = doc.getString("code") ?: "No code"
                    "Group: $foundName\nDescription: $desc\nCode: $code"
                }
                displayResults(groups)
            }
    }

    private fun displayResults(groups: List<String>) {
        searchResults = groups
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, groups)
        resultsListView.adapter = adapter
    }
}
