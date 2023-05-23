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

class editDBsavingsActivity : AppCompatActivity(){
    lateinit var edit_button: Button
    lateinit var delete_button: Button
    lateinit var back_button: Button
    lateinit var savingsname: EditText
    lateinit var savingsamount: EditText
    lateinit var savingssaved: EditText
    lateinit var total: TextView
    lateinit var output: TextView

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editsavings)

        auth = FirebaseAuth.getInstance();
        Firebase.auth.currentUser
        db = FirebaseFirestore.getInstance()

        if (user != null) {
            user.email?.let { email ->

                val bundle : Bundle? = intent.extras
                val nameClicked = bundle!!.getString("name")
                val amountCLicked = bundle!!.getString("amount")
                val savedCLicked = bundle!!.getString("saved")

                savingsname = findViewById(R.id.savingsname);
                savingsname.setHorizontallyScrolling(true);

                savingsamount = findViewById(R.id.savingsamount);
                savingsamount.setHorizontallyScrolling(true);

                savingssaved = findViewById(R.id.savingssaved)
                savingssaved.setHorizontallyScrolling(true);

                savingsname.setText(nameClicked)
                savingsamount.setText(amountCLicked)
                savingssaved.setText(savedCLicked)

                total = findViewById<TextView>(R.id.total)
                output = findViewById<TextView>(R.id.output)
                if (amountCLicked != null && savedCLicked != null) {
                    var calculatedTotal = amountCLicked.toDouble() - savedCLicked.toDouble()

                    if(calculatedTotal == 0.0){
                        total.text = "Goal Achieved!"
                        output.text = "You've done great!"
                    }else if(calculatedTotal < 0.0){
                        calculatedTotal = savedCLicked.toDouble() - amountCLicked.toDouble()
                        total.text = "$${"%.2f".format(calculatedTotal)} Extra!"
                        output.text = "  Goal Achieved! \nYou've done great!"
                    }else{
                        total.text = "$${"%.2f".format(calculatedTotal)}"
                        output.text = "Remaining to Save!"
                    }
                }

                edit_button = findViewById<Button>(R.id.editsavings)
                edit_button.setOnClickListener {

                    val name: String = savingsname.text.toString();
                    val amount: String = savingsamount.text.toString()
                    val saved: String = savingssaved.text.toString()

                    val numberCheckAmount = amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number
                    val numberCheckSaved = saved?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                    if (name.trim().isNotEmpty() || amount.isNotEmpty()) {

                        if (numberCheckAmount != null && numberCheckAmount > 0 && numberCheckSaved != null && numberCheckSaved > 0) {

                            if (nameClicked != null) {
                                editSavings(name, amount, saved, email, nameClicked)
                            }

                        }

                    } else {
                        Toast.makeText(
                            this, "Empty Field!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                delete_button = findViewById<Button>(R.id.deletesavings)
                delete_button.setOnClickListener {

                    val name: String = savingsname.text.toString();

                    if (name.trim().isNotEmpty()) {

                        deleteSavings(name, email)

                    } else {
                        Toast.makeText(
                            this, "Empty Field!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                back_button = findViewById(R.id.back);
                back_button.setOnClickListener {
                    val intent = Intent(this, displayDBsavingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun editSavings(name: String, amount: String, saved: String, email: String, nameClicked : String) {

        val savings = hashMapOf(
            "SavingsName" to name,
            "SavingsAmount" to amount,
            "SavingsSaved" to saved,
        )

        if(name != nameClicked){

            db.collection("UserData").document(email)
                .collection("Savings").document(nameClicked).delete()
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            db.collection("UserData").document(email)
                .collection("Savings").document(name).set(savings)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            Toast.makeText(
                this, "Savings Updated!",
                Toast.LENGTH_SHORT
            ).show()

        }else{
            db.collection("UserData").document(email)
                .collection("Savings").document(name).set(savings)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

            Toast.makeText(
                this, "Savings Updated!",
                Toast.LENGTH_SHORT
            ).show()

            if (amount != null && saved != null) {
                var calculatedTotal = amount.toDouble() - saved.toDouble()

                if(calculatedTotal == 0.0){
                    total.text = "Goal Achieved!"
                    output.text = "You've done great!"
                }else if(calculatedTotal < 0.0){
                    calculatedTotal = saved.toDouble() - amount.toDouble()
                    total.text = "$${"%.2f".format(calculatedTotal)} Extra!"
                    output.text = "  Goal Achieved! \nYou've done great!"
                }else{
                    total.text = "$${"%.2f".format(calculatedTotal)}"
                    output.text = "Remaining to Save!"
                }
            }
        }
    }

    private fun deleteSavings(name:String, email: String) {

        db.collection("UserData").document(email)
            .collection("Savings").document(name).delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        val intent = Intent(this, displayDBsavingsActivity::class.java)
        startActivity(intent)
    }
}