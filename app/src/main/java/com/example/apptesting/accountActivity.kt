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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class accountActivity : AppCompatActivity() {

    private lateinit var emailtext: TextView;
    private lateinit var password: TextView;
    private lateinit var signout: Button;
    private lateinit var back_button: Button;

    private lateinit var auth: FirebaseAuth;
    var db = Firebase.firestore
    val user = Firebase.auth.currentUser

    private var frequencyOptions = arrayOf("Weekly", "Fortnightly", "Monthly", "6 Monthly", "Annually")
    private lateinit var payperiodfrequency: AutoCompleteTextView
    private lateinit var adapterItems: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)

        emailtext = findViewById(R.id.email)

        if (user != null) {
            user.email?.let { email ->

                emailtext.text = email

                val userDocRef = db.collection("UserData").document(email)

                payperiodfrequency = findViewById(R.id.auto_complete_text)

                var frequency: String? = null
                var selectedFrequencyCheck = false
                var frequencyValue = 0.0

                val checkPayPeriodRef = db.collection("UserData").document(email)
                checkPayPeriodRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.contains("PayFrequency")) {
                        // The "name" field exists in the document
                        var payperiodfrequency =
                            documentSnapshot.getString("PayFrequency").toString()
                        // do something with the name
                        selectedFrequencyCheck = true
                        frequency = payperiodfrequency

                    } else {
                        // The "name" field doesn't exist in the document
                        //total.text = "$${"%.2f".format(amountCLicked)}"
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



                back_button = findViewById(R.id.back);
                back_button.setOnClickListener {

                    if (!selectedFrequencyCheck) {

                        Toast.makeText(
                            this, "Pay Period Not Selected!",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (selectedFrequencyCheck) {

                        addpayperiod(frequency.toString(), frequencyValue, email)
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
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    ContentValues.TAG,
                                    "Error writing document",
                                    e
                                )
                            }

                    }

                    val intent = Intent(this, home_Activity::class.java)
                    startActivity(intent)
                }
            }

            password = findViewById(R.id.forgotpassword);
            password.setOnClickListener {
                val text_email: String = emailtext.text.toString();

                if (text_email.trim().isNotEmpty()) {
                    Firebase.auth.sendPasswordResetEmail(text_email);
                    Toast.makeText(
                        this, "Email Sent to Reset Password!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            signout = findViewById(R.id.signout)
            signout.setOnClickListener {

                Firebase.auth.signOut();
                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, signIn_Activity::class.java)
                startActivity(intent)
                finish()
            }

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
            }
            .addOnFailureListener { e ->
                Log.w(
                    ContentValues.TAG,
                    "Error writing document",
                    e
                )
            }

        val intent = Intent(this, calculation_Activity::class.java)
        startActivity(intent)
    }

}


