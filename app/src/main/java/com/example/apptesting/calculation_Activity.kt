/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class calculation_Activity : AppCompatActivity() {

    private lateinit var calculate: Button
    private lateinit var back_button: Button
    private lateinit var funds: EditText
    private lateinit var total: TextView
    private lateinit var result: TextView

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser

    private var frequencyOptions = arrayOf("Weekly", "Fortnightly", "Monthly", "6 Monthly", "Annually")
    private lateinit var payperiodfrequency: AutoCompleteTextView
    private lateinit var adapterItems: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculation)

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance()


        if (user != null) {
            user.email?.let { email ->

                val userDocRef = db.collection("UserData").document(email)

                funds = findViewById(R.id.funds);
                funds.setHorizontallyScrolling(true);

                total = findViewById(R.id.totalleft);
                result = findViewById(R.id.left);

                payperiodfrequency = findViewById(R.id.auto_complete_text)

                lateinit var frequency: String
                var selectedFrequencyCheck = false
                var frequencyValue = 0.0

                val checkPayPeriodRef = db.collection("UserData").document(email)
                checkPayPeriodRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.contains("PayFrequency")) {
                        // The "name" field exists in the document
                        var payperiod =
                            documentSnapshot.getString("PayFrequency").toString()
                        // do something with the name
                        selectedFrequencyCheck = true
                        frequency = payperiod

                    } else {
                        //The "name" field doesn't exist in the document
                        frequency = "No Pay Period Saved To Account"
                    }

                    payperiodfrequency.setText(frequency)
                    if (frequency != null) {

                        if (frequency == "Weekly") {
                            frequencyValue = 52.0
                        } else if (frequency == "Fortnightly") {
                            frequencyValue = 26.0
                        } else if (frequency == "Monthly") {
                            frequencyValue = 12.0
                        } else if (frequency == "6 Monthly") {
                            frequencyValue = 2.0
                        } else if (frequency == "Annually") {
                            frequencyValue = 1.0
                        } else {
                            frequencyValue = 0.0
                        }
                    }

                    adapterItems = ArrayAdapter<String>(this, R.layout.frequency_list, frequencyOptions)
                    payperiodfrequency.setAdapter(adapterItems)
                }

                payperiodfrequency.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView, view, position, id ->
                        frequency = adapterItems.getItem(position).toString()
                        selectedFrequencyCheck = true

                        if (position == 0) {
                            frequencyValue = 52.0
                        } else if (position == 1) {
                            frequencyValue = 26.0
                        } else if (position == 2) {
                            frequencyValue = 12.0
                        } else if (position == 3) {
                            frequencyValue = 2.0
                        } else if (position == 4) {
                            frequencyValue = 1.0
                        } else {
                            frequencyValue = 0.0
                        }
                    }

                calculate = findViewById<Button>(R.id.calculate)
                calculate.setOnClickListener {
                    val amount: String = funds.text.toString()

                    val numberCheck = amount?.toDoubleOrNull() // convert amount to integer or null if not a valid number

                    if (amount.trim().isNotEmpty()) {

                        if (numberCheck != null && numberCheck > 0.0) {

                            if (!selectedFrequencyCheck) {

                                Toast.makeText(this, "Pay Period Not Selected!", Toast.LENGTH_SHORT).show()

                            } else if (selectedFrequencyCheck) {

                                //##############################################################################
                                //Below this for displaying the money left after calculation!
                                //##############################################################################

                                var overallExpensesAnnually = 0.0
                                var savetotalannually: Double
                                var displayLeftOver: Double

                                db.collection("UserData").document(email).collection("Expenses").get()
                                    .addOnSuccessListener { querySnapshot ->

                                        for (document in querySnapshot.documents) {
                                            val totalExpenseAnnually = document.getString("ExpenseTotalAnnually")

                                            // Do something with the fieldValue
                                            if (totalExpenseAnnually != null) {
                                                overallExpensesAnnually += totalExpenseAnnually.toDouble()
                                                Log.d(TAG, "$overallExpensesAnnually =>" + "$totalExpenseAnnually")
                                            }
                                        }

                                        if(overallExpensesAnnually > 0.0){
                                            //total.text = overallExpensesAnnually.toString()
                                            savetotalannually = overallExpensesAnnually //Saves total for all expenses annually

                                            checkPayPeriodRef.get()
                                                .addOnSuccessListener { documentSnapshot ->
                                                    if (documentSnapshot.contains("PayFrequencyValue")) {
                                                        val payfrequencyValue = documentSnapshot.getDouble("PayFrequencyValue")

                                                        if (payfrequencyValue != null) {
                                                            var calculatedforpay = savetotalannually / payfrequencyValue.toDouble()
                                                            displayLeftOver = (amount.toDouble()-calculatedforpay)

                                                            displayfunction(displayLeftOver)
                                                        }
                                                    } else {
                                                        total.text = "Pay Period Frequency Not Found"
                                                    }
                                                }

                                        }else{
                                            total.text = "No Expenses found in Account" //No expenses found in account
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Error getting documents: ", exception)
                                    }


                                //##############################################################################
                                    //Below this Updated the pay period on account
                                //##############################################################################

                                addpayperiod(frequency, frequencyValue, email)

                                userDocRef.get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            Log.d(
                                                ContentValues.TAG,
                                                "DocumentSnapshot successfully written!"
                                            )
                                            Toast.makeText(
                                                this, "Pay Period Updated!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Log.d(
                                                ContentValues.TAG,
                                                "DocumentSnapshot does not exist!"
                                            )
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.w(ContentValues.TAG, "Error writing document", e)
                                    }
                            }

                        }else {
                                Toast.makeText(this, "Invalid Input!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                            Toast.makeText(this, "Empty Field!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

            back_button = findViewById(R.id.back);
            back_button.setOnClickListener {
                val intent = Intent(this, home_Activity::class.java)
                startActivity(intent)
            }
        }

    private fun displayfunction(displayLeftOver: Double) {

        //total.text = "$${"%.2f".format(displayLeftOver)}"
        //result.text = "Left after all expenses"

        if (displayLeftOver >= 0.0) {
            total.text = "$${"%.2f".format(displayLeftOver)}"
            result.text = "Left after all expenses"

        }else if(displayLeftOver < 0.0){
            val positive = kotlin.math.abs(displayLeftOver)

            total.text = "-$${"%.2f".format(positive)}"
            result.text = "Insufficient funds to cover all bills"

        }
    }

    private fun addpayperiod(selectedFrequency: String, frequencyValue: Double, email: String) {

        val payperiod = hashMapOf(
            "PayFrequency" to selectedFrequency,
            "PayFrequencyValue" to frequencyValue
        )

            db.collection("UserData").document(email).set(payperiod, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully written!"
                )
            }.addOnFailureListener { e ->
                Log.w(
                    ContentValues.TAG,
                    "Error writing document",
                    e
                )
            }
    }

}

