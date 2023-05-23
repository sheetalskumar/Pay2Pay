/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class home_Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;
    private lateinit var calculatefunds: Button;
    private lateinit var summary: TextView;
    private lateinit var payperiodtext: TextView;
    private lateinit var totalexpenses: TextView;
    private lateinit var totalnetworth: TextView;

    var db = Firebase.firestore
    private lateinit var toggle: ActionBarDrawerToggle

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

        }else{
            startActivity(Intent(this, signIn_Activity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        auth = FirebaseAuth.getInstance();
        val user = Firebase.auth.currentUser

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val header: View = navView.getHeaderView(0)
        var emailText: TextView = header.findViewById(R.id.email)

        if (user != null) {
            user.email?.let { email ->

                emailText.text = email

            }
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, home_Activity::class.java)
                    startActivity(intent)
                }
                R.id.nav_expense -> {
                    val intent = Intent(this, displayDBexpenseActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_savings -> {
                    val intent = Intent(this, displayDBsavingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_networth -> {
                    val intent = Intent(this, displayDBnetworthActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_account -> {
                    val intent = Intent(this, accountActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    Firebase.auth.signOut();
                    Toast.makeText(
                        this, "Signed out!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, signIn_Activity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        calculatefunds = findViewById(R.id.calculatefunds);
        summary = findViewById(R.id.summaryTitle);

        payperiodtext = findViewById(R.id.payperioddisplay)
        totalexpenses = findViewById(R.id.expensesdisplay)
        totalnetworth = findViewById(R.id.networthdisplay)

        if (user != null) {
            user.email?.let { email ->

                lateinit var payperiod : String

                //##############################################################################
                //Below this is displaying the pay period
                //##############################################################################

                val checkPayPeriodRef = db.collection("UserData").document(email)
                checkPayPeriodRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.contains("PayFrequency")) {
                        // The "name" field exists in the document
                        payperiod = documentSnapshot.getString("PayFrequency").toString()
                        // do something with the name
                        payperiodtext.setText(payperiod)

                    } else {
                        //The "name" field doesn't exist in the document
                        payperiodtext.setText("No Pay Period Saved To Account")
                    }
                }

                //##############################################################################
                //Below this is displaying total expense value
                //##############################################################################

                var overallExpensesAnnually = 0.0
                var savetotalannually: Double

                db.collection("UserData").document(email).collection("Expenses").get()
                    .addOnSuccessListener { querySnapshot ->

                        for (document in querySnapshot.documents) {
                            val totalExpenseAnnually = document.getString("ExpenseTotalAnnually")

                            // Do something with the fieldValue
                            if (totalExpenseAnnually != null) {
                                overallExpensesAnnually += totalExpenseAnnually.toDouble()
                                Log.d(ContentValues.TAG, "$overallExpensesAnnually =>" + "$totalExpenseAnnually")
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
                                            totalexpenses.setText("$${"%.2f".format(calculatedforpay)} " + payperiod)
                                        }
                                    } else {
                                        totalexpenses.text = "Pay Period Frequency Not Found"
                                    }
                                }

                        }else{
                            totalexpenses.text = "No Expenses found in Account" //No expenses found in account
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(ContentValues.TAG, "Error getting documents: ", exception)
                    }

                //##############################################################################
                //Below this is displaying Networth Value
                //##############################################################################

                db.collection("UserData").document(email)
                    .collection("Networth")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        var totalamount = 0.0

                        for (document in querySnapshot.documents) {
                            val totalNetworthAmount = document.getString("NetworthAmount")

                            // Do something with the fieldValue
                            if (totalNetworthAmount != null) {
                                totalamount = totalamount + totalNetworthAmount.toDouble()
                            }
                        }

                        if(totalamount > 0.0) {
                            totalnetworth.setText("$${"%.2f".format(totalamount)}")
                        }else{
                            totalnetworth.text = "No Assets Added To Account"
                        }
                    }

            }
        }

        calculatefunds.setOnClickListener {
            val intent = Intent(this, calculation_Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }
}


