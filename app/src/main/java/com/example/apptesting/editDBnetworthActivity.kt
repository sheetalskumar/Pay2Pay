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

class editDBnetworthActivity : AppCompatActivity(){
    lateinit var edit_button: Button
    lateinit var delete_button: Button
    lateinit var back_button: Button
    lateinit var networthname: EditText
    lateinit var networthamount: EditText

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editnetworth)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()

        if (user != null) {
            user.email?.let { email ->

                val bundle : Bundle? = intent.extras
                val nameClicked = bundle!!.getString("name")
                val amountCLicked = bundle!!.getString("amount")

                networthname = findViewById(R.id.networthname);
                networthname.setHorizontallyScrolling(true);

                networthamount = findViewById(R.id.networthamount);
                networthamount.setHorizontallyScrolling(true);

                networthname.setText(nameClicked)
                networthamount.setText(amountCLicked)

                edit_button = findViewById<Button>(R.id.editnetworth)
                edit_button.setOnClickListener {

                    val name: String = networthname.text.toString();
                    val amount: String = networthamount.text.toString()

                    val numberCheck = amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                    if (name.trim().isNotEmpty() || amount.isNotEmpty()) {

                        if (numberCheck != null && numberCheck > 0) {

                            if (nameClicked != null) {
                                editNetworth(name, amount, email, nameClicked)
                            }

                        }

                    } else {
                        Toast.makeText(
                            this, "Empty Field!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                delete_button = findViewById<Button>(R.id.deletenetworth)
                delete_button.setOnClickListener {

                    val name: String = networthname.text.toString();

                    if (name.trim().isNotEmpty()) {

                        deleteNetworth(name, email)

                    } else {
                        Toast.makeText(
                            this, "Empty Field!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                back_button = findViewById(R.id.back);
                back_button.setOnClickListener {
                    val intent = Intent(this, displayDBnetworthActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun editNetworth(name: String, amount: String, email: String, nameClicked : String) {

        val networth = hashMapOf(
            "NetworthName" to name,
            "NetworthAmount" to amount,
        )

        if(name != nameClicked){

            db.collection("UserData").document(email)
                .collection("Networth").document(nameClicked).delete()
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            db.collection("UserData").document(email)
                .collection("Networth").document(name).set(networth)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            Toast.makeText(
                this, "Networth Updated!",
                Toast.LENGTH_SHORT
            ).show()

        }else{
            db.collection("UserData").document(email)
                .collection("Networth").document(name).set(networth)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            Toast.makeText(
                this, "Networth Updated!",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun deleteNetworth(name:String, email: String) {

        db.collection("UserData").document(email)
            .collection("Networth").document(name).delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        val intent = Intent(this, displayDBnetworthActivity::class.java)
        startActivity(intent)
    }
}