package com.example.chatbluetooth.chat

import android.bluetooth.BluetoothDevice

//  Sealed class in Kotlin è una classe che può avere sottoclassi, ma tutte queste sottoclassi
//  devono essere dichiarate all'interno della stessa unità di compilazione in cui è definita
//  la sealed class
sealed class StatoConnessioneDevice {

    //  Questa classe rappresenta lo stato in cui il device è connesso, contiene un
    //  parametro device di tipo BluetoothDevice che rappresenta
    //  il device Bluetooth al quale è connesso
    class Connesso(val device: BluetoothDevice) : StatoConnessioneDevice()

    // Questa classe rappresenta lo stato in cui il device è disconnesso, utilizza
    // un oggetto senza parametri
    object Disconnesso: StatoConnessioneDevice()
}