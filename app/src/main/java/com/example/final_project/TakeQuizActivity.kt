package com.example.final_project

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileInputStream
import java.util.Scanner

data class QuizItem(val word: String, val definition: String)

class TakeQuizActivity : AppCompatActivity() {
    private val quizItems = mutableListOf<QuizItem>()
    private var currentIndex = 0
    private var score = 0

    private lateinit var wordTextView: TextView
    private lateinit var optionsListView: ListView
    private lateinit var optionsAdapter: ArrayAdapter<String>

    private val options = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_take_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        wordTextView = findViewById(R.id.questionText)
        optionsListView = findViewById(R.id.quizAnswersList)

        val quizFileName = intent.getStringExtra("quizFileName") ?: return
        loadQuiz(quizFileName)

        optionsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        optionsListView.adapter = optionsAdapter

        optionsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedDef = options[position]
            val correctDef = quizItems[currentIndex].definition

            if (selectedDef == correctDef) {
                score++
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Incorrect! Correct: $correctDef", Toast.LENGTH_SHORT).show()
            }

            currentIndex++
            if (currentIndex < quizItems.size) {
                showQuestion()
            } else {
                showFinalScore()
            }
        }

        showQuestion()
    }

    private fun loadQuiz(fileName: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val file = File(filesDir, "${user.uid}/$fileName.csv")
        if (!file.exists()) return

        val scanner = Scanner(FileInputStream(file))
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            val parts = line.split("|")
            if (parts.size == 2) {
                quizItems.add(QuizItem(parts[0], parts[1]))
            }
        }
        quizItems.shuffle()
    }

    private fun showQuestion() {
        if (currentIndex >= quizItems.size) return

        val currentItem = quizItems[currentIndex]
        wordTextView.text = currentItem.word

        // Get 3 incorrect definitions
        val incorrectDefs = quizItems
            .filter { it.definition != currentItem.definition }
            .shuffled()
            .take(3)
            .map { it.definition }

        options.clear()
        options.add(currentItem.definition)
        options.addAll(incorrectDefs)
        options.shuffle()

        optionsAdapter.notifyDataSetChanged()
    }

    private fun showFinalScore() {
        AlertDialog.Builder(this)
            .setTitle("Quiz Complete!")
            .setMessage("You scored $score out of ${quizItems.size}")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}
