/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

class Savings {
    private lateinit var SavingsName : String
    private lateinit var SavingsAmount : String
    private lateinit var SavingsSaved : String

    fun savings(){}

    fun savings(SavingsName : String, SavingsAmount : String, SavingsSaved : String){
        this.SavingsName = SavingsName
        this.SavingsAmount = SavingsAmount
        this.SavingsSaved = SavingsSaved
    }

    fun getSavingsName(): String {
        return SavingsName
    }

    fun setSavingsName(){
        this.SavingsName = SavingsName
    }

    fun getSavingsAmount(): String {
        return SavingsAmount
    }

    fun setSavingsAmount(){
        this.SavingsAmount = SavingsAmount
    }

    fun getSavingsSaved(): String {
        return SavingsSaved
    }

    fun setSavingsSaved(){
        this.SavingsSaved = SavingsSaved
    }
}