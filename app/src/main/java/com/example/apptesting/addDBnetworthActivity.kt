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

class addDBnetworthActivity : AppCompatActivity() {

    private lateinit var add_button: Button
    private lateinit var back_button: Button
    private lateinit var networthname: EditText
    private lateinit var networthamount: EditText

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnetworth)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()


        if (user != null) {
            user.email?.let { email ->

                val networthRef = db.collection("UserData").document(email)
                    .collection("Networth")

                networthname = findViewById(R.id.networthname);
                networthname.setHorizontallyScrolling(true);

                networthamount = findViewById(R.id.networthamount);
                networthamount.setHorizontallyScrolling(true);


                add_button = findViewById<Button>(R.id.addnetworthbutton)
                add_button.setOnClickListener {
                    val name: String = networthname.text.toString();
                    val amount: String = networthamount.text.toString();

                    val numberCheck = amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                    if (name.trim().isNotEmpty() && amount.trim().isNotEmpty()) {

                        if (numberCheck != null && numberCheck > 0) {

                            addNetworth(name, amount, email)
                                networthRef.document(name).get()
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
                    } else {
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
            val intent = Intent(this, displayDBnetworthActivity::class.java)
            startActivity(intent)
        }
    }


    private fun addNetworth(name: String, amount: String, email: String) {

        val networth = hashMapOf(
            "NetworthName" to name,
            "NetworthAmount" to amount,
        )

        db.collection("UserData").document(email)
            .collection("Networth").document(name).set(networth)
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

        val intent = Intent(this, displayDBnetworthActivity::class.java)
        startActivity(intent)
    }
}

