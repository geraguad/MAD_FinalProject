package com.example.final_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class displayFlashcardsActivity : AppCompatActivity() {
    private lateinit var cardText: TextView
    private lateinit var nextButton: ImageView
    private lateinit var previousButton: ImageView

    private var flashcards: List<Pair<String, String>> = emptyList()
    private var currentIndex = 0
    private var showingQuestion = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display_flashcards)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cardText = findViewById(R.id.cardText)
        nextButton = findViewById(R.id.nextFlashcardArrow)
        previousButton = findViewById(R.id.previousFlashcardArrow)

        val fileName = intent.getStringExtra("fileName")
        val user = FirebaseAuth.getInstance().currentUser

        if (fileName == null || user == null) {
            Toast.makeText(this, "Error loading flashcards", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val file = File(File(filesDir, user.uid), fileName)
        if (!file.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        flashcards = file.readLines()
            .mapNotNull {
                val parts = it.split("|")
                if (parts.size == 2) Pair(parts[0], parts[1]) else null
            }

        if (flashcards.isEmpty()) {
            Toast.makeText(this, "No flashcards found in this set", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showCard()

        cardText.setOnClickListener {
            showingQuestion = !showingQuestion
            showCard()
        }

        nextButton.setOnClickListener {
            if (currentIndex < flashcards.size - 1) {
                currentIndex++
                showingQuestion = true
                showCard()
            }
        }

        previousButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showingQuestion = true
                showCard()
            }
        }
    }

    private fun showCard() {
        val (question, answer) = flashcards[currentIndex]
        cardText.text = if (showingQuestion) question else answer
    }
}
