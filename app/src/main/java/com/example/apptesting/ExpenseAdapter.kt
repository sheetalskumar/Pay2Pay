/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(var expenseArrayList: ArrayList<Expense>) : RecyclerView.Adapter<ExpenseAdapter.MyViewHolder>()  {

    private lateinit var clickListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClick(listener: onItemClickListener){
        clickListener = listener
    }

    class MyAdapter(var expenseArrayList: ArrayList<Expense>) {
        init {
            this.expenseArrayList = expenseArrayList
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)

        return MyViewHolder(v, clickListener)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val expense: Expense = expenseArrayList[position]
        holder.expenseName.text = expense.getExpenseName()

    }

    override fun getItemCount(): Int {
        return expenseArrayList.size
    }

    inner class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val expenseName: TextView = itemView.findViewById(R.id.expensename)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}

