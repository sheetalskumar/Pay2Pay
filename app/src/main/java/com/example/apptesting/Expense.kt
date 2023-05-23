/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

open class Expense {

    private lateinit var ExpenseName : String
    private lateinit var ExpenseAmount : String
    private lateinit var ExpenseFrequency : String

    fun expense(){}

    fun expense(ExpenseName : String, ExpenseAmount : String, ExpenseFrequency : String){
        this.ExpenseName = ExpenseName
        this.ExpenseAmount = ExpenseAmount
        this.ExpenseFrequency = ExpenseFrequency
    }

    fun getExpenseName(): String {
        return ExpenseName
    }

    fun setExpenseName(){
        this.ExpenseName = ExpenseName
    }

    fun getExpenseAmount(): String {
         return ExpenseAmount
    }

    fun setExpenseAmount(){
        this.ExpenseAmount = ExpenseAmount
    }

    fun getExpenseFrequency(): String {
        return ExpenseFrequency
    }

    fun setExpenseFrequency(){
        this.ExpenseFrequency = ExpenseFrequency
    }

}