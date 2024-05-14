package com.example.grocerez

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    private var mSocket: Socket? = null // trying this out for singleton
//    lateinit var mSocket: Socket


    // "http://10.0.2.2:3000" for local
    // "http://localhost:3000/" will NOT work
    // Our server "http://18.222.199.102:3000"
    // If you want to use your physical phone you could use your ip address plus :3000
    // This will allow your Android Emulator and physical device at your home to connect to the server
    init {
        try {
            mSocket = IO.socket(routeAddr) // initialize socekt upon creation of socketHandler instance
            mSocket?.connect() //establish connection immediately
        }catch (e: URISyntaxException){
            // Log the exception
            e.printStackTrace()
        }
    }

//    @Synchronized
//    fun setSocket() {
//        try {
//    // "http://10.0.2.2:3000" for local
//    // "http://localhost:3000/" will NOT work
//    // Our server "http://18.222.199.102:3000"
//    // If you want to use your physical phone you could use your ip address plus :3000
//    // This will allow your Android Emulator and physical device at your home to connect to the server
//            mSocket = IO.socket(routeAddr) // routeAddr found in backendrouting.kt
//        } catch (e: URISyntaxException) {
//            // Log the exception
//            e.printStackTrace()
//        }
//    }

    @Synchronized
    fun getSocket(): Socket {
        fun getSocket(): Socket {
            if (mSocket == null) {
                // Initialize socket if not already initialized (this should ideally not happen)
                try {
                    mSocket = IO.socket(routeAddr)
                    mSocket?.connect() // Establish connection if initialized
                } catch (e: URISyntaxException) {
                    // Log the exception
                    e.printStackTrace()
                }
            }
            return mSocket!!
        }
        return mSocket!!
    }

    @Synchronized
    fun establishConnection() {
        mSocket?.connect() //
    }

    @Synchronized
    fun closeConnection() {
        mSocket?.disconnect() // Disconnect if not null
    }
}