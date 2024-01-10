package com.example.chatbluetooth.bluetooth

import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatbluetooth.bluetooth.Messaggio.MessaggioRemoto
import com.example.chatbluetooth.chat.StatoConnessioneDevice

private const val TAG = "ChatServer"

object ChatServer {
    // Mantiene il rifermineto al contesto dell'app per avviare il chat server
    private var app: Application? = null
    private lateinit var bluetoothManager: BluetoothManager

    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // Le seguenti variabili saranno null se il bluetooth non è attivo o non è possibile l'advertisement sui device
    private var advertiser: BluetoothLeAdvertiser? = null
    private var advertiseCallback: AdvertiseCallback? = null
    private var advertiseSettings: AdvertiseSettings = buildAdvertiseSettings()
    private var advertiseData: AdvertiseData = buildAdvertiseData()

    // Live data per riportare il messaggio inviato al device
    private val _messaggio = MutableLiveData<Messaggio>()
    val messages = _messaggio as LiveData<Messaggio>

    // Live data per riportare connection request
    private val _richiestaConnessione = MutableLiveData<BluetoothDevice>()
    val richiestaConnessione = _richiestaConnessione as LiveData<BluetoothDevice>

    // Live data per riportare messaggi di richiesta inviati al device
    private val _richiestaAttivazioneBluetooth = MutableLiveData<Boolean>()
    val richiestaAttivazioneBluetooth = _richiestaAttivazioneBluetooth as LiveData<Boolean>

    private var gattServer: BluetoothGattServer? = null
    private var gattServerCallback: BluetoothGattServerCallback? = null

    private var gattClient: BluetoothGatt? = null
    private var gattClientCallback: BluetoothGattCallback? = null

    // Proprietà della chat del device attualmente connesso
    private var deviceAttuale: BluetoothDevice? = null
    private val _connessioneDevice = MutableLiveData<StatoConnessioneDevice>()
    val deviceConnection = _connessioneDevice as LiveData<StatoConnessioneDevice>
    private var gatt: BluetoothGatt? = null
    private var caratteristicheMessaggio: BluetoothGattCharacteristic? = null

    fun startServer(app: Application){
        bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if(!adapter.isEnabled) {
            // Chiedo all'utente di attivare il bluetooth
            _richiestaAttivazioneBluetooth.value = true
        } else {
            _richiestaAttivazioneBluetooth.value = false
            setupGattServer(app)
            startAdvertisement()
        }
    }

    fun stopServer() {
        stopAdvertising()
    }

    // Questo metodo per ottenere il MAC address del nostro device ritorna un valore
    // di default di solito 02:00:00:00:00:00 perchè solo app di sistema possono richiedere
    // al device il MAC address
    fun getYourDeviceAddress(): String = bluetoothManager.adapter.address

    fun setConnessioneChatCorrente(device: BluetoothDevice) {
        deviceAttuale = device
        // Imposto gatt in modo che BluetoothChatFragment può visualizzare data del device
        _connessioneDevice.value = StatoConnessioneDevice.Connesso(device)
        connettiChatDevice(device)
    }

    private fun connettiChatDevice(device: BluetoothDevice) {
        gattClientCallback = GattClientCallback()
        gattClient = device.connectGatt(app, false, gattClientCallback)
    }


    fun inviaMessaggio(messaggio: String): Boolean {
        Log.d(TAG, "Invia messaggio")
        caratteristicheMessaggio?.let { characteristic ->
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

            val byteMessaggio = messaggio.toByteArray(Charsets.UTF_8)
            characteristic.value = byteMessaggio
            gatt?.let {
                val success = it.writeCharacteristic(caratteristicheMessaggio)
                Log.d(TAG, "onServicesDiscovered: messaggio inviato: $success")
                if (success) {
                    _messaggio.value = Messaggio.MessaggioLocale(messaggio)
                }
            } ?: run {
                Log.d(TAG, "messaggioInviato: nessuna connessione gatt per inviare un messaggio")
            }
        }
        return false
    }

    // Funzione per il setup di un server GATT locale
    private fun setupGattServer(app: Application) {
        gattServerCallback = GattServerCallback()

        gattServer = bluetoothManager.openGattServer(
            app,
            gattServerCallback
        ).apply {
            addService(setupGattService())
        }
    }

    // Funzione per creare il server GATT con descrizioni e  caratteristiche richieste
    private fun setupGattService(): BluetoothGattService {

        // Setup servizio GATT
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

        // Necessario garantire che la proprietà sia scrivibile e abbia il permesso di scrittura
        val messageCharacteristic = BluetoothGattCharacteristic(
            MESSAGGIO_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(messageCharacteristic)
        val confirmCharacteristic = BluetoothGattCharacteristic(
            CONFERMA_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(confirmCharacteristic)

        return service
    }

    // Inizio advertising del device in modo che gli altri device BLE possono vederlo e connettersi
    private fun startAdvertisement() {
        advertiser = adapter.bluetoothLeAdvertiser
        Log.d(TAG, "startAsvertisement : con advertiser $advertiser")

        if(advertiseCallback == null) {
            advertiseCallback = DeviceAdvertiseCallback()
            advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
        }
    }

    // Stop advertising BLE
    private fun stopAdvertising(){
        Log.d(TAG, "Stop advertising con advertiser $advertiser")
        advertiser?.stopAdvertising(advertiseCallback)
        advertiseCallback = null
    }

    // Funzione che restituisce un oggetto AdvertiseData che include l'UUID del servizio
    // e il nome del device
    private fun buildAdvertiseData(): AdvertiseData {
        // C'è un limite di 31 byte nei pacchetti inviati con BLE advertisement
        val dataBuilder = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .setIncludeDeviceName(true)

        return dataBuilder.build()
    }

    // Funzione che restituisce un oggetto AdvertiseSettings impostato per usare poca potenza
    // (preservando durata batteria) e disabilita il timeout built-in finchè la funzione usa
    // il suo timeout
    private fun buildAdvertiseSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTimeout(0)
            .build()
    }

    // Callback custom per il GATT server implementato
    private class GattServerCallback : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            Log.d(
                TAG,
                "onConnectionStateChange: Server $device ${device.name} successo: $isSuccess connesso: $isConnected"
            )
            if (isSuccess && isConnected) {
                _richiestaConnessione.postValue(device)
            } else {
                _connessioneDevice.postValue(StatoConnessioneDevice.Disconnesso)
            }
        }
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            if (characteristic.uuid == MESSAGGIO_UUID) {
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                val message = value?.toString(Charsets.UTF_8)
                Log.d(TAG, "onCharacteristicWriteRequest: messaggio: \"$message\"")
                message?.let {
                    _messaggio.postValue(MessaggioRemoto(it))
                }
            }
        }
    }

    private class GattClientCallback : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            Log.d(TAG, "onConnectionStateChange: Client $gatt  successo: $isSuccess connesso: $isConnected")
            // try to send a message to the other device as a test
            if (isSuccess && isConnected) {
                // discover services
                gatt.discoverServices()
            }
        }

        // Device "scopre" caratteristiche dell'altro device
        override fun onServicesDiscovered(discoveredGatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(discoveredGatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServicesDiscovered: Con gatt $discoveredGatt")
                gatt = discoveredGatt
                val service = discoveredGatt.getService(SERVICE_UUID)
                caratteristicheMessaggio = service.getCharacteristic(MESSAGGIO_UUID)
            }
        }
    }

    // Callback custom per inizio Advertising (successo o fallimento). Esegue il brodcast
    // del codice di errore in un Intent in modo da essere visto da AdvertiserFragment
    // e fermare il servizio
    private class DeviceAdvertiseCallback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            // Visualizzo lo stato dell'errore
            val errorMessage = "Advertise fallito con errore: $errorCode"
            Log.d(TAG, "Advertising fallito")
            //_viewState.value = DeviceScanViewState.Error(errorMessage)
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d(TAG, "Advertising avviato con successo")
        }
    }
}
