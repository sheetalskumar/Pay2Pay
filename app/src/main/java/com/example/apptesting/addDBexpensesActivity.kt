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
import kotlin.String as String

class addDBexpensesActivity : AppCompatActivity() {

    private lateinit var add_button: Button
    private lateinit var back_button: Button
    private lateinit var expensename: EditText
    private lateinit var expenseamount: EditText

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser

    private var frequencyOptions = arrayOf("Weekly", "Fortnightly", "Monthly", "6 Monthly", "Annually")
    private lateinit var expensefrequency: AutoCompleteTextView
    private lateinit var adapterItems: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addexpenses)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()


        if (user != null) {
            user.email?.let { email ->

                val expenseRef = db.collection("UserData").document(email)
                    .collection("Expenses")

                expensename = findViewById(R.id.expensename);
                expensename.setHorizontallyScrolling(true);

                expenseamount = findViewById(R.id.expenseamount);
                expenseamount.setHorizontallyScrolling(true);

                expensefrequency = findViewById(R.id.auto_complete_text)
                adapterItems = ArrayAdapter<String>(this, R.layout.frequency_list, frequencyOptions)
                expensefrequency.setAdapter(adapterItems)

                lateinit var frequency: String
                var selectedFrequencyCheck = false
                var annualFrequency = "0.0"

                expensefrequency.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
                    frequency = adapterItems.getItem(position).toString()
                    selectedFrequencyCheck = true

                    if(position == 0){
                        annualFrequency = "52.0"
                    }else if (position == 1){
                        annualFrequency = "26.0"
                    }else if (position == 2){
                        annualFrequency = "12.0"
                    }else if (position == 3){
                        annualFrequency = "2.0"
                    }else if (position == 4){
                        annualFrequency = "1.0"
                    }else {
                        annualFrequency = "0.0"
                    }
                }

                add_button = findViewById<Button>(R.id.addexpensebutton)
                add_button.setOnClickListener {
                    val name: String = expensename.text.toString();
                    val amount: String = expenseamount.text.toString();

                        val numberCheck = amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                        if (name.trim().isNotEmpty() && amount.trim().isNotEmpty()) {

                            if (numberCheck != null && numberCheck > 0.0) {

                                if (!selectedFrequencyCheck) {

                                    Toast.makeText(
                                        this, "Expense Frequency Not Selected!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else if (selectedFrequencyCheck) {

                                    addExpense(name, amount, frequency, annualFrequency, email)
                                    expenseRef.document(name).get()
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
                                }

                            } else {
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
            val intent = Intent(this, displayDBexpenseActivity::class.java)
            startActivity(intent)
        }
    }


    private fun addExpense(name: String, amount: String, selectedFrequency: String, annualFrequencyValue: String, email: String) {
        val totalAnnually : Double
        val totalAnnuallyString : String

        if(annualFrequencyValue == "52.0"){
            totalAnnually = annualFrequencyValue.toDouble() * amount.toDouble()
            totalAnnuallyString = totalAnnually.toString()
        }else if (annualFrequencyValue == "26.0"){
            totalAnnually = annualFrequencyValue.toDouble() * amount.toDouble()
            totalAnnuallyString = totalAnnually.toString()
        }else if (annualFrequencyValue == "12.0"){
            totalAnnually = annualFrequencyValue.toDouble() * amount.toDouble()
            totalAnnuallyString = totalAnnually.toString()
        }else if (annualFrequencyValue == "2.0"){
            totalAnnually = annualFrequencyValue.toDouble() * amount.toDouble()
            totalAnnuallyString = totalAnnually.toString()
        }else if (annualFrequencyValue == "1.0"){
            totalAnnually = annualFrequencyValue.toDouble() * amount.toDouble()
            totalAnnuallyString = totalAnnually.toString()
        }else {
            totalAnnually = annualFrequencyValue.toDouble() * amount.toDouble()
            totalAnnuallyString = totalAnnually.toString()
        }

        val expense = hashMapOf(
            "ExpenseName" to name,
            "ExpenseAmount" to amount,
            "ExpenseFrequency" to selectedFrequency,
            "ExpenseTotalAnnually" to totalAnnuallyString
        )

        db.collection("UserData").document(email)
            .collection("Expenses").document(name).set(expense)
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

        val intent = Intent(this, displayDBexpenseActivity::class.java)
        startActivity(intent)
    }
}

