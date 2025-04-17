package com.example.final_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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