package com.example.final_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
/*
        var hardcodeJson = "{\"Gerardo\": {\"age\": 21}}"
        var jo = JSONObject(hardcodeJson)
        var gerardo = jo.getJSONObject("Gerardo")
        if(gerardo.has("age"))
            var age = gerardo.getInt("age") ?: -1

        // building JSON
        //var jsonObect - buildJSONObject()
*/

        FirebaseApp.initializeApp(this)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()



        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.email_edit_text)
            val passwordEditText = findViewById<EditText>(R.id.password_edit_text)

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // check if email or password is empty

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("AUTH", "signInWithEmail:success")
                        Toast.makeText(
                            baseContext,
                            "Authentication Success.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        goToMainActivity()
                        finish()

                        val user = firebaseAuth.currentUser

                        //get document

                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("AUTH", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        //updateUI(null)
                    }
                }
        };
    }
}
/*
    private fun getConnections(_userID : String): Task<List<String>>
    {
        val connectionRef = database.collection("connections").document(_userID)

        return connectionRef.get().continueWith { task ->
            val document = task.result

            if (document.exists()) {
                val data = document.data
                List<String> people = data?.get("people") as List<String> ?: emptyList()
            }
        };
    }
}

 */