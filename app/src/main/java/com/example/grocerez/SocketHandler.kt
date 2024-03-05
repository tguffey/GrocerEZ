package com.example.grocerez

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {
    private var socket: Socket? = null

    fun setSocket() {
        try {
            // Replace "http://your_server_address:port" with your actual server URL and port
            socket = IO.socket(routeAddr)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    fun getSocket(): Socket {
        if (socket == null) {
            throw IllegalStateException("Socket not initialized. Call setSocket() first.")
        }
        return socket!!
    }

    fun establishConnection() {
        socket?.connect()
    }

    fun closeConnection() {
        socket?.disconnect()
    }
}
