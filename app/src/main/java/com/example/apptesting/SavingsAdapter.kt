/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavingsAdapter(var savingsArrayList: ArrayList<Savings>) : RecyclerView.Adapter<SavingsAdapter.MyViewHolder>() {

    private lateinit var clickListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClick(listener: onItemClickListener){
        clickListener = listener
    }

    class MyAdapter(var savingsArrayList: ArrayList<Savings>) {
        init {
            this.savingsArrayList = savingsArrayList
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.savings_item, parent, false)

        return MyViewHolder(v, clickListener)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val savings: Savings = savingsArrayList[position]
        holder.savingsName.text = savings.getSavingsName()

    }

    override fun getItemCount(): Int {
        return savingsArrayList.size
    }

    inner class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val savingsName: TextView = itemView.findViewById(R.id.savingsname)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}

