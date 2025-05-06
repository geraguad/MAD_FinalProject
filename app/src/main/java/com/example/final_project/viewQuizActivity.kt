package com.example.final_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class viewQuizActivity : AppCompatActivity() {
    private lateinit var quizListView: ListView
    private lateinit var quizListAdapter: ArrayAdapter<String>
    private val quizNames = ArrayList<String>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.studyBuddy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        quizListView = findViewById(R.id.quizAnswersList)

        loadQuizFiles()

        quizListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, quizNames)
        quizListView.adapter = quizListAdapter

        quizListView.setOnItemClickListener { _, _, position, _ ->
            val quizFileName = quizNames[position]
            val intent = Intent(this, TakeQuizActivity::class.java)
            intent.putExtra("quizFileName", quizFileName)
            startActivity(intent)
        }
    }

    private fun loadQuizFiles() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userDir = File(filesDir, user.uid)

        Log.d("QUIZ", "Looking in directory: ${userDir.absolutePath}")
        if (!userDir.exists()) {
            userDir.mkdirs()
            Log.d("QUIZ", "User directory didn't exist. Created new one.")
        }

        val files = userDir.listFiles { file -> file.extension == "csv" }
        if (files != null) {
            for (file in files) {
                Log.d("QUIZ", "Found file: ${file.name}")
                quizNames.add(file.nameWithoutExtension)
            }
        } else {
            Log.d("QUIZ", "No CSV files found.")
            Toast.makeText(this, "No quiz files found", Toast.LENGTH_SHORT).show()
        }
    }
}
