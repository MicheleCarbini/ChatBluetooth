package com.example.chatbluetooth.bluetooth
import java.util.*

// Costanti utilizzate nel progetto
// service UUID per BLE
val SERVICE_UUID: UUID = UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb")

// UUID per il messaggio
val MESSAGGIO_UUID: UUID = UUID.fromString("7db3e235-3608-41f3-a03c-955fcbd2ea4b")

// UUID per la conferma della connessione al device
val CONFERMA_UUID: UUID = UUID.fromString("36d4dc5c-814b-4097-a5a6-b93b39085928")

const val REQUEST_ENABLE_BT = 1
