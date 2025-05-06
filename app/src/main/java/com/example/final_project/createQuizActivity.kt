package com.example.final_project

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class createQuizActivity : AppCompatActivity() {

    private lateinit var quizNameInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var answerInput: EditText
    private lateinit var addButton: Button
    private lateinit var saveButton: Button

    private val quizContent = mutableListOf<String>() // Stores "question|answer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        // Bind UI elements
        quizNameInput = findViewById(R.id.quizNameText)
        questionInput = findViewById(R.id.questionInput)
        answerInput = findViewById(R.id.AnswerInput)
        addButton = findViewById(R.id.nextQuizButton)
        saveButton = findViewById(R.id.createQuizSetButton)

        // Add word-definition pair to memory
        addButton.setOnClickListener {
            val word = questionInput.text.toString().trim()
            val definition = answerInput.text.toString().trim()

            if (word.isNotEmpty() && definition.isNotEmpty()) {
                val entry = "$word|$definition"
                quizContent.add(entry)

                // Clear input fields
                questionInput.text.clear()
                answerInput.text.clear()

                Toast.makeText(this, "Added: $word", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter both a word and definition", Toast.LENGTH_SHORT).show()
            }
        }

        // Save quiz to internal storage
        saveButton.setOnClickListener {
            val quizName = quizNameInput.text.toString().trim()
            if (quizName.isEmpty()) {
                Toast.makeText(this, "Please enter a quiz name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (quizContent.isEmpty()) {
                Toast.makeText(this, "No word-definition pairs added", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fileName = "$quizName.csv"
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
                quizContent.forEach { writer.write(it + "\n") }
            }

            Toast.makeText(this, "Quiz saved as $fileName", Toast.LENGTH_SHORT).show()
            finish() // Go back to main activity
        }
    }
}
