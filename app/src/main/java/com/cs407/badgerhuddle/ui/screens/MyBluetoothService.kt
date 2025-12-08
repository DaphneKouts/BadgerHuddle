package com.cs407.badgerhuddle.ui.screens

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "MY_APP_DEBUG_TAG"

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class MyBluetoothService(
    private val handler: Handler
) {
    private var connectedThread: ConnectedThread? = null

    @Synchronized
    fun setSocket(socket: BluetoothSocket) {
        // stop old thread if any
        connectedThread?.cancel()
        connectedThread = ConnectedThread(socket)
        connectedThread?.start()
    }

    fun send(bytes: ByteArray) {
        connectedThread?.write(bytes)
    }

    fun stop() {
        connectedThread?.cancel()
        connectedThread = null
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            var numBytes: Int

            while (true) {
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                val readBytes = mmBuffer.copyOfRange(0, numBytes)

                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1, readBytes
                )
                readMsg.sendToTarget()
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            val writtenMsg = handler.obtainMessage(MESSAGE_WRITE, -1, -1, bytes)
            writtenMsg.sendToTarget()
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}