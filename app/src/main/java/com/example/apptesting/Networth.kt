/*
 * Copyright (c) 2023 Sheetal Kumar. All rights reserved.
 * This work is done by Sheetal Kumar and is owned by Sheetal Kumar.
 */

package com.example.apptesting

class Networth {
    private lateinit var NetworthName : String
    private lateinit var NetworthAmount : String

    fun networth(){}

    fun networth(NetworthName : String, NetworthAmount : String){
        this.NetworthName = NetworthName
        this.NetworthAmount = NetworthAmount
    }

    fun getNetworthName(): String {
        return NetworthName
    }

    fun setNetworthName(){
        this.NetworthName = NetworthName
    }

    fun getNetworthAmount(): String {
        return NetworthAmount
    }

    fun setNetworthAmount(){
        this.NetworthAmount = NetworthAmount
    }
}