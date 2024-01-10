package com.example.chatbluetooth.scan

import android.bluetooth.BluetoothDevice

// Sealed class che rappresenta gli stati possibili di una scansione di dispositivi bluetooth
sealed class DeviceScanViewState {
    // È un oggetto singleton che rappresenta lo stato di scansione attiva.
    // È utilizzato per indicare che la vista è attualmente impegnata in una scansione
    object ActiveScan: DeviceScanViewState()
    // Classe interna che contiene i risultati della scansione
    class risultatoScan(val risultatoScan: Map<String, BluetoothDevice>): DeviceScanViewState()
    // Classe interna che rappresenta lo stato di errore
    class Errore(val messaggio: String): DeviceScanViewState()
    //  Altro oggetto singleton che rappresenta uno stato in cui l'advertising Bluetooth non è supportato
    object AdvertisementNonSupportato: DeviceScanViewState()
}