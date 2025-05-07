package com.example.final_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class createGroupActivity : AppCompatActivity() {
    private lateinit var groupNameInput: EditText
    private lateinit var groupDescInput: EditText
    private lateinit var createButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun generateUniqueCode(): String {
        return (100000..999999).random().toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.studyBuddy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        groupNameInput = findViewById(R.id.groupNameInput)
        groupDescInput = findViewById(R.id.groupDescriptionInput)
        createButton = findViewById(R.id.createGroupButton2)

        createButton.setOnClickListener {
            //grabs user input
            val name = groupNameInput.text.toString().trim()
            val desc = groupDescInput.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val groupCode = generateUniqueCode()
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            //store group in firestore with information
            val groupData = hashMapOf(
                "name" to name,
                "description" to desc,
                "createdBy" to userId,
                "code" to groupCode
            )

            //creates map of group
            db.collection("groups").document(groupCode).set(groupData)
                .addOnSuccessListener {
                    //adds user to group under 'member'
                    val memberData = hashMapOf("userId" to userId)

                    db.collection("groups").document(groupCode)
                        .collection("members")
                        .document(userId)
                        .set(memberData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Group created! Code: $groupCode",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
        }
    }
}

