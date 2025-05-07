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
import java.io.File
import java.io.FileOutputStream

class createFlashcardsActivity : AppCompatActivity() {

    private lateinit var flashNameInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var answerInput: EditText
    private lateinit var addButton: Button
    private lateinit var saveButton: Button

    private val flashContent = mutableListOf<String>() // Stores "question|answer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_flashcards)

        // Bind UI elements
        flashNameInput = findViewById(R.id.FlashcardNameText)
        questionInput = findViewById(R.id.flashcardTermInput)
        answerInput = findViewById(R.id.flashcardDefinitionInput)
        addButton = findViewById(R.id.nextFlashcardButton)
        saveButton = findViewById(R.id.createFlashcardSetButton)

        // Add word-definition pair to memory
        addButton.setOnClickListener {
            val word = questionInput.text.toString().trim()
            val definition = answerInput.text.toString().trim()

            if (word.isNotEmpty() && definition.isNotEmpty()) {
                val entry = "$word|$definition"
                flashContent.add(entry)

                // Clear input fields
                questionInput.text.clear()
                answerInput.text.clear()

                Toast.makeText(this, "Added: $word", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter both a word and definition", Toast.LENGTH_SHORT).show()
            }
        }

        // Save flashcards to internal storage
        saveButton.setOnClickListener {
            val flashcardSetName = flashNameInput.text.toString().trim()
            if (flashcardSetName.isEmpty()) {
                Toast.makeText(this, "Please enter a quiz name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (flashContent.isEmpty()) {
                Toast.makeText(this, "No word-definition pairs added", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fileName = "$flashcardSetName.csv"
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userDir = File(filesDir, user.uid)
            if (!userDir.exists()) {
                userDir.mkdirs()
            }

            val file = File(userDir, fileName)

            FileOutputStream(file).bufferedWriter().use { writer ->
                flashContent.forEach { writer.write(it + "\n") }
            }

            Toast.makeText(this, "Flashcard set saved as $fileName", Toast.LENGTH_SHORT).show()
            finish() // Go back to main activity
        }
    }
}
