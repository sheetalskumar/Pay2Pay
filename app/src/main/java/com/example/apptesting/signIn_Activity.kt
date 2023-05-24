/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class signIn_Activity : AppCompatActivity() {

    private lateinit var createaccount_button: Button
    private lateinit var login_button: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var forgotpassword: TextView

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        auth = FirebaseAuth.getInstance();
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is already signed in, redirect to another activity
            val intent = Intent(this, home_Activity::class.java)
            startActivity(intent)
            finish() // Optional: Finish the login activity to prevent returning to it via back button
        } else {
            // User is not signed in, continue with login page
            setContentView(R.layout.signin)
            // ... Rest of your login page setup and logic

            email = findViewById(R.id.email);
            password = findViewById(R.id.password);

            email.setHorizontallyScrolling(true);
            password.setHorizontallyScrolling(true);

            createaccount_button = findViewById(R.id.createaccount)
            createaccount_button.setOnClickListener {
                openCreateAccount()
            }

            login_button = findViewById<Button>(R.id.signin)
            login_button.setOnClickListener {
                val text_email: String = email.text.toString();
                val text_password: String = password.text.toString();

                if(text_email.trim().isNotEmpty() || text_password.trim().isNotEmpty()){
                    signInUser(text_email, text_password)
                }
                else{
                    Toast.makeText(this, "Empty Field!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            forgotpassword = findViewById(R.id.forgotpassword);
            forgotpassword.setOnClickListener {
                val text_email: String = email.text.toString();

                if(text_email.trim().isNotEmpty()){
                    auth.sendPasswordResetEmail(text_email);
                    Toast.makeText(this, "Email Sent to Reset Password!",
                        Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Enter Email!",
                        Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun signInUser(email:String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->

                if(task.isSuccessful){

                    val data = hashMapOf(email to true)

                    db.collection("UserData").document(email)
                        .set(data, SetOptions.merge())

                    Toast.makeText(this, "Signed In!",
                        Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, home_Activity::class.java);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(
                        this, "ERROR: Cannot Sign In, Try Again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
    }

    private fun openCreateAccount() {
        val intent = Intent(this, createAccount_Activity::class.java)
        startActivity(intent)
        finish()
    }

}
