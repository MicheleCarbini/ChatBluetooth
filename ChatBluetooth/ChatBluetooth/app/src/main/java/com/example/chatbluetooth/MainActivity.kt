package com.example.chatbluetooth

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.chatbluetooth.bluetooth.ChatServer

private const val TAG = "ChatBluetooth"

// Classe che inizializza l'activity principale dell'app
class MainActivity : AppCompatActivity() {

    // Richiesta di API minime, almeno livello 31
    @RequiresApi(Build.VERSION_CODES.S)
    // Setup iniziale dell'activity con richiesta dei permessi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("ChatBluetooth", "Richiesta permessi necessari")
        requestPermissions.launch(arrayOf(
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        ))
    }

    // Eseguo il chat server finchè l'app è a schermo
    override fun onStart() {
        super.onStart()
        ChatServer.startServer(application)
    }

    // Ferma il chat server quanfo l'app si chiude o va in pausa
    override fun onStop() {
        super.onStop()
        ChatServer.stopServer()
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d(TAG, "${it.key} = ${it.value}")
            }
        }
}