/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class displayDBsavingsActivity : AppCompatActivity(){

    lateinit var recyclerView: RecyclerView
    lateinit var savingsArrayList: ArrayList<Savings>
    lateinit var savingsAdapter: SavingsAdapter
    lateinit var addSavingsButton: Button;

    private lateinit var toggle : ActionBarDrawerToggle

    var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth;
    private val user = Firebase.auth.currentUser

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
        setContentView(R.layout.displaysavings)

        auth = FirebaseAuth.getInstance();

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
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
            when(it.itemId){
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

        addSavingsButton = findViewById(R.id.addsavingsbutton);
        addSavingsButton.setOnClickListener {
            val intent = Intent(this, addDBsavingsActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        savingsArrayList = ArrayList<Savings>()
        savingsAdapter = SavingsAdapter(savingsArrayList)

        recyclerView.setAdapter(savingsAdapter)

        eventChangeListener()

    }

    private fun eventChangeListener() {

        if (user != null) {
            user.email?.let { email ->

                db.collection("UserData").document(email).collection("Savings")
                    .orderBy("SavingsName", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.d(ContentValues.TAG, "Error getting documents: ", e)
                            val intent = Intent(applicationContext, signIn_Activity::class.java)
                            val packageManager = packageManager
                            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

                            if (resolveInfo != null){


                            }else{
                                val intent = Intent(applicationContext, signIn_Activity::class.java)
                                startActivity(intent)
                            }
                        }else{

                            for (doc in snapshot!!.documentChanges) {
                                if (doc.type == DocumentChange.Type.ADDED) {
                                    savingsArrayList.add(doc.document.toObject(Savings::class.java))
                                }
                            }

                            var adapter = SavingsAdapter(savingsArrayList)
                            recyclerView.adapter = adapter

                            adapter.setOnItemClick(object : SavingsAdapter.onItemClickListener{
                                override fun onItemClick(position: Int){
                                    //Toast.makeText(this@displayDBexpenseActivity, "Clicked!", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@displayDBsavingsActivity, editDBsavingsActivity::class.java)
                                    intent.putExtra("name", savingsArrayList[position].getSavingsName())
                                    intent.putExtra("amount", savingsArrayList[position].getSavingsAmount())
                                    intent.putExtra("saved", savingsArrayList[position].getSavingsSaved())
                                    startActivity(intent)

                                }
                            })

                            savingsAdapter.notifyDataSetChanged()

                        }
                    }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}