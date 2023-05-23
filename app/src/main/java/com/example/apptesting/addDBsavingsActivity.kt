/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class addDBsavingsActivity : AppCompatActivity() {

    private lateinit var add_button: Button
    private lateinit var back_button: Button
    private lateinit var savingsname: EditText
    private lateinit var savingsamount: EditText
    private lateinit var savingssaved: EditText


    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addsavings)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()


        if (user != null) {
            user.email?.let { email ->

                val savingsRef = db.collection("UserData").document(email)
                    .collection("Savings")

                savingsname = findViewById(R.id.savingsname);
                savingsname.setHorizontallyScrolling(true);

                savingsamount = findViewById(R.id.savingsamount);
                savingsamount.setHorizontallyScrolling(true);

                savingssaved = findViewById(R.id.savingssaved);
                savingssaved.setHorizontallyScrolling(true);

                add_button = findViewById<Button>(R.id.addsavingsbutton)
                add_button.setOnClickListener {
                    val name: String = savingsname.text.toString();
                    val amount: String = savingsamount.text.toString();
                    val saved: String = savingssaved.text.toString();

                    val numberCheckAmount = amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number
                    val numberCheckSaved = saved?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                    if (name.trim().isNotEmpty() && amount.trim().isNotEmpty() && saved.trim().isNotEmpty()) {

                        if (numberCheckAmount != null && numberCheckAmount > 0 && numberCheckSaved != null && numberCheckSaved > 0) {

                            addSavings(name, amount, saved, email)
                            savingsRef.document(name).get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        Log.d(
                                            ContentValues.TAG,
                                            "DocumentSnapshot successfully written!"
                                        )

                                        Toast.makeText(
                                            this, "Added!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Log.d(
                                            ContentValues.TAG,
                                            "DocumentSnapshot does not exist!"
                                        )
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        ContentValues.TAG,
                                        "Error writing document",
                                        e
                                    )
                                }

                        }else {
                            Toast.makeText(
                                this, "Invalid Input!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }else {
                        Toast.makeText(
                            this, "Empty Field!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }


        back_button = findViewById(R.id.back);
        back_button.setOnClickListener {
            val intent = Intent(this, displayDBsavingsActivity::class.java)
            startActivity(intent)
        }
    }


    private fun addSavings(name: String, amount: String, saved: String,  email: String) {

        val savings = hashMapOf(
            "SavingsName" to name,
            "SavingsAmount" to amount,
            "SavingsSaved" to saved,
        )

        db.collection("UserData").document(email)
            .collection("Savings").document(name).set(savings)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e ->
                Log.w(
                    ContentValues.TAG,
                    "Error writing document",
                    e
                )
            }

        val intent = Intent(this, displayDBsavingsActivity::class.java)
        startActivity(intent)
    }
}

