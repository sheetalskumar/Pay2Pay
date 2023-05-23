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


class editDBexpensesActivityTrial : AppCompatActivity() {

    lateinit var edit_button: Button
    lateinit var delete_button: Button
    lateinit var back_button: Button
    lateinit var expensename: EditText
    lateinit var expenseamount: EditText
    lateinit var total: TextView
    lateinit var payperiod: TextView

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser

    private var frequencyOptions = arrayOf("Weekly", "Fortnightly", "Monthly", "6 Monthly", "Annually")
    private lateinit var expensefrequency: AutoCompleteTextView
    private lateinit var adapterItems: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editexpenses)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()

        if (user != null) {
            user.email?.let { email ->

                val bundle: Bundle? = intent.extras
                val nameClicked = bundle!!.getString("name")
                val amountCLicked = bundle!!.getString("amount")
                val frequencyCLicked = bundle!!.getString("frequency")
                val totalAnnuallyCLicked = bundle!!.getString("annual")

                expensename = findViewById(R.id.expensename);
                expensename.setHorizontallyScrolling(true);

                expenseamount = findViewById(R.id.expenseamount);
                expenseamount.setHorizontallyScrolling(true);

                expensefrequency = findViewById(R.id.auto_complete_text)

                expensename.setText(nameClicked)
                expenseamount.setText(amountCLicked)

                val name: String = expensename.text.toString();
                val amount: String = expenseamount.text.toString()

                lateinit var frequency: String
                var selectedFrequencyCheck = true
                var annualFrequency = "0.0"

                expensefrequency.setText(frequencyCLicked)
                if (frequencyCLicked != null) {
                    frequency = frequencyCLicked
                }

                adapterItems = ArrayAdapter<String>(this, R.layout.frequency_list, frequencyOptions)
                expensefrequency.setAdapter(adapterItems)

                expensefrequency.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView, view, position, id ->
                        frequency = adapterItems.getItem(position).toString()
                        selectedFrequencyCheck = true

                        if (position == 0) {
                            annualFrequency = "52.0"
                        } else if (position == 1) {
                            annualFrequency = "26.0"
                        } else if (position == 2) {
                            annualFrequency = "12.0"
                        } else if (position == 3) {
                            annualFrequency = "2.0"
                        } else if (position == 4) {
                            annualFrequency = "1.0"
                        } else {
                            if (totalAnnuallyCLicked != null) {
                                annualFrequency = ""+amount.toDouble()+ ""
                            }
                        }

                    }

                total = findViewById<TextView>(R.id.total)
                payperiod = findViewById<TextView>(R.id.pay_period)
                if (amountCLicked != null){

                    var calculatedTotal = 0.0
                    var displayTotal : Double
                    val checkPayPeriodRef = db.collection("UserData").document(email)

                    checkPayPeriodRef.get().addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.contains("PayFrequency")) {
                            // The "name" field exists in the document
                            val frequency = documentSnapshot.getString("PayFrequency").toString()
                            // do something with the name
                            payperiod.text = frequency
                        } else {
                            // The "name" field doesn't exist in the document
                            //total.text = "$${"%.2f".format(amountCLicked)}"
                            payperiod.text = "No Pay Period Saved To Account"
                        }
                    }

                    val checkAnnualTotalRef =
                        db.collection("UserData").document(email).collection("Expenses")
                            .document(nameClicked!!)
                    checkAnnualTotalRef.get().addOnSuccessListener { documentSnapshot ->

                        if (documentSnapshot.contains("ExpenseTotalAnnually")) {
                            // The "name" field exists in the document
                            val totalAnnually =
                                documentSnapshot.getString("ExpenseTotalAnnually").toString()
                            // do something with the name
                            if (totalAnnually != null) {
                                calculatedTotal = totalAnnually.toDouble()
                                displayTotal = calculatedTotal
                            }

                            checkPayPeriodRef.get().addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.contains("PayFrequencyValue")) {
                                    val payfrequencyValue = documentSnapshot.getDouble("PayFrequencyValue")
                                    if (payfrequencyValue != null) {
                                        annualFrequency = payfrequencyValue.toString()
                                        displayTotal =  calculatedTotal / annualFrequency.toDouble()
                                        total.text = "$${"%.2f".format(displayTotal)}"
                                    }

                                } else {
                                    total.text = "$${"%.2f".format(calculatedTotal)}"
                                }
                            }

                        } else {
                            // The "name" field doesn't exist in the document
                            total.text = "Pay Period Frequency Not Found"
                        }
                    }

                    edit_button = findViewById<Button>(R.id.editexpense)
                    edit_button.setOnClickListener {

                        val numberCheck =
                            amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                        if (name.trim().isNotEmpty() || amount.isNotEmpty()) {

                            if (numberCheck != null && numberCheck > 0) {

                                if (nameClicked != null) {
                                        var annualInput = calculateAnnual(annualFrequency, amount)
                                        editExpense(name, amount, frequency, annualInput, email, nameClicked)
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

                    delete_button = findViewById<Button>(R.id.deleteexpense)
                    delete_button.setOnClickListener {

                        val name: String = expensename.text.toString();

                        if (name.trim().isNotEmpty()) {
                            deleteExpense(name, email)


                        } else {
                            Toast.makeText(
                                this, "Empty Field!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    back_button = findViewById(R.id.back);
                    back_button.setOnClickListener {
                        val intent = Intent(this, displayDBexpenseActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun calculateAnnual(annualFrequencyValue : String, amount : String): String {
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

        return totalAnnuallyString
    }

    private fun editExpense(name: String, amount: String, selectedFrequency: String, annualInput: String, email: String, nameClicked: String) {

        val expense = hashMapOf(
            "ExpenseName" to name,
            "ExpenseAmount" to amount,
            "ExpenseFrequency" to selectedFrequency,
            "ExpenseTotalAnnually" to annualInput
        )

        if (name != nameClicked) {

            db.collection("UserData").document(email)
                .collection("Expenses").document(nameClicked).delete()
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully DELETED!"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w(
                        ContentValues.TAG,
                        "Error writing document",
                        e
                    )
                }

            db.collection("UserData").document(email)
                .collection("Expenses").document(name).set(expense)
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully written again!"
                    )

                    Toast.makeText(
                        this, "Expense Updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.w(
                        ContentValues.TAG,
                        "Error writing document",
                        e
                    )
                }

        } else {
            db.collection("UserData").document(email)
                .collection("Expenses").document(name).set(expense)
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully written!"
                    )

                    Toast.makeText(
                        this, "Expense Updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.w(
                        ContentValues.TAG,
                        "Error writing document",
                        e
                    )
                }

            Toast.makeText(
                this, "Expense Updated!",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun deleteExpense(name:String, email: String) {

        db.collection("UserData").document(email)
            .collection("Expenses").document(name).delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

        val intent = Intent(this, displayDBexpenseActivity::class.java)
        startActivity(intent)
    }
}
