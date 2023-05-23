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

class NetworthAdapter(var networthArrayList: ArrayList<Networth>) : RecyclerView.Adapter<NetworthAdapter.MyViewHolder>() {

    private lateinit var clickListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClick(listener: onItemClickListener){
        clickListener = listener
    }

    class MyAdapter(var networthArrayList: ArrayList<Networth>) {
        init {
            this.networthArrayList = networthArrayList
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.networth_item, parent, false)

        return MyViewHolder(v, clickListener)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val networth: Networth = networthArrayList[position]
        holder.networthName.text = networth.getNetworthName()

    }

    override fun getItemCount(): Int {
        return networthArrayList.size
    }

    inner class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val networthName: TextView = itemView.findViewById(R.id.networthname)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}

