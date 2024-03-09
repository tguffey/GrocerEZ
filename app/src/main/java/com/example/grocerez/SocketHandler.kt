package com.example.grocerez

import io.socket.client.IO
import io.socket.client.Socket
//import kotlinx.coroutines.Dispatchers.IO
//import java.net.Socket
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
// "http://10.0.2.2:3000" for local
// "http://localhost:3000/" will NOT work
// Our server "http://18.222.199.102:3000"
// If you want to use your physical phone you could use your ip address plus :3000
// This will allow your Android Emulator and physical device at your home to connect to the server
            mSocket = IO.socket(routeAddr) // routeAddr found in backendrouting.kt
        } catch (e: URISyntaxException) {
            // Log the exception
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}