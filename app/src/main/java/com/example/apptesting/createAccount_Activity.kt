/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class createAccount_Activity : AppCompatActivity() {

    private lateinit var signinbutton: Button
    private lateinit var createbutton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var retypepassword: EditText

    private lateinit var auth: FirebaseAuth
    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createaccount)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()
        email = findViewById(R.id.editemail);
        password = findViewById(R.id.editpassword);
        retypepassword = findViewById(R.id.retypepassword);
        createbutton = findViewById(R.id.createaccount);
        signinbutton = findViewById(R.id.signin);

        email.setHorizontallyScrolling(true);
        password.setHorizontallyScrolling(true);
        retypepassword.setHorizontallyScrolling(true);

        signinbutton.setOnClickListener{
            var intent = Intent(this, signIn_Activity::class.java);
            startActivity(intent);
        }

        createbutton.setOnClickListener {

            val text_email: String = email.text.toString();
            val text_password: String = password.text.toString();
            val text_retypepassword: String = retypepassword.text.toString();

            if(text_email.trim().isEmpty() || text_password.trim().isEmpty() || text_retypepassword.trim().isEmpty()){
                Toast.makeText(this, "Empty Field!",
                    Toast.LENGTH_SHORT).show()
            }
            else if(text_password.length < 6){
                Toast.makeText(this, "Password too short!",
                    Toast.LENGTH_SHORT).show()
            }
            else if(text_password != text_retypepassword){
                Toast.makeText(this, "Password does not match!",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                createUser(text_email, text_password)
            }
        }
    }

    private fun createUser(text_email: String, text_password: String) {

        val user = hashMapOf(
            "Email" to text_email
        )

        auth.createUserWithEmailAndPassword(text_email, text_password)
            .addOnCompleteListener(this) {task ->

                if(task.isSuccessful){
                    Toast.makeText(this, "Account Created!",
                        Toast.LENGTH_SHORT).show()

                    db.collection("UserData").document(text_email)
                        .set(user)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                    var intent = Intent(this, home_Activity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(
                        this, "ERROR: In Creating Account!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser;

        if (user != null){
            var intent = Intent(this, home_Activity::class.java);
            startActivity(intent);
        }
    }

}

