package com.example.final_project

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class viewFlashcardsActivity : AppCompatActivity() {

    private lateinit var flashcardList: ListView
    private lateinit var fileNames: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_flashcards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.studyBuddy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        flashcardList = findViewById(R.id.flashcardList)

        //user authentication
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // goes to where flashcards are
        val userDir = File(filesDir, user.uid)
        if (!userDir.exists()) {
            Toast.makeText(this, "No flashcards found.", Toast.LENGTH_SHORT).show()
            return
        }

        //lists files in alphabetical order
        fileNames = userDir.listFiles()?.filter { it.extension == "csv" }
            ?.map { it.name }
            ?.sorted()
            ?: emptyList()

        if (fileNames.isEmpty()) {
            Toast.makeText(this, "No flashcard sets saved.", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
        flashcardList.adapter = adapter

        //listens for item selected and passes it to intent
        flashcardList.setOnItemClickListener { _, _, position, _ ->
            val selectedFile = fileNames[position]
            val intent = Intent(this, displayFlashcardsActivity::class.java)
            intent.putExtra("fileName", selectedFile)
            startActivity(intent)
        }
    }
}