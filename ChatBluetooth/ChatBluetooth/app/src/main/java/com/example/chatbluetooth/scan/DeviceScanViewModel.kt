package com.example.chatbluetooth.scan

import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.*
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatbluetooth.bluetooth.SERVICE_UUID
import com.example.chatbluetooth.scan.DeviceScanViewState.*

private const val TAG = "DeviceScanViewModel"
// Periodo di scan ogni 30 secondi
private const val PERIODO_SCAN = 30000L

// Classe che si occupa della scansione dei dispositivi Bluetooth Low Energy (BLE),
// gestisce lo stato della view attraverso LiveData e fornisce funzionalità per avviare
// e fermare la scansione, nonché per ottenere i risultati della scansione.
class DeviceScanViewModel(app: Application) : AndroidViewModel(app) {

    // LiveData per inviare lo stato della view a FrammentoScanDevice
    private val _statoView = MutableLiveData<DeviceScanViewState>()
    val statoView = _statoView as LiveData<DeviceScanViewState>

    // Mappa che associa l'indirizzo (String) al device trovato con lo scan
    private val risultatoScan = mutableMapOf<String, BluetoothDevice>()

    // Il BluetoothAdapter non dovrebbe mai essere nullo poiché BLE è reso obbligatorio nel file
    // AndroidManifest.xml
    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // Questa proprietà sarà null se il bluetooth non è attivo
    private var scanner: BluetoothLeScanner? = null

    private var scanCallback: DeviceScanCallback? = null
    private val scanFilters: List<ScanFilter>
    private val scanSettings: ScanSettings

    init {
        // Configuro filtri e impostazioni scan
        scanFilters = buildScanFilters()
        scanSettings = buildScanSettings()

        //Inizio lo scan per device BLE
        inizioScan()
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }

    fun inizioScan() {
        // Se advertisement non sono supportati su questo dispositivo allora gli altri dispositivi
        // non saranno in grado di trovarloo e connettersi
        if (!adapter.isMultipleAdvertisementSupported) {
            _statoView.value = AdvertisementNonSupportato
            return
        }

        if (scanCallback == null) {
            scanner = adapter.bluetoothLeScanner
            Log.d(TAG, "Inizio Scanning")
            // Aggiorno UI per indicare che lo scan sta iniziando
            _statoView.value = ActiveScan

            // Fermo scan dopo il periodo predefinito
            Handler().postDelayed({ stopScanning() }, PERIODO_SCAN)

            // Fermo l'avvio di un nuovo scan
            scanCallback = DeviceScanCallback()
            scanner?.startScan(scanFilters, scanSettings, scanCallback)
        } else {
            Log.d(TAG, "Già in scanning")
        }
    }

    private fun stopScanning() {
        Log.d(TAG, "Stop Scanning")
        scanner?.stopScan(scanCallback)
        scanCallback = null
        // Ritorno il risultato corrente
        _statoView.value = risultatoScan(risultatoScan)
    }


    // Restituisce una lista di oggetti ScanFilter per filtrare tramite UUID
    private fun buildScanFilters(): List<ScanFilter> {
        val builder = ScanFilter.Builder()

        builder.setServiceUuid(ParcelUuid(SERVICE_UUID))
        val filter = builder.build()
        return listOf(filter)
    }

    // Restituisce un oggetto ScanSetting impostato per usare poca energia (preservando batteria)
    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
    }

    // Oggetto ScanCallback custom per aggiungere alla lista i dispositivi trovati in caso di successo
    // o visualizzare un errore in caso di fallimento
    private inner class DeviceScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (item in results) {
                item.device?.let { device ->
                    risultatoScan[device.address] = device
                }
            }
            _statoView.value = risultatoScan(risultatoScan)
        }

        override fun onScanResult(
            callbackType: Int,
            result: ScanResult
        ) {
            super.onScanResult(callbackType, result)
            result.device?.let { device ->
                risultatoScan[device.address] = device
            }
            _statoView.value = risultatoScan(risultatoScan)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            // Invia lo stato di errore al frammento per mostrarlo a schermo
            val errorMessage = "Scan fallito con errore: $errorCode"
            _statoView.value = Errore(errorMessage)
        }
    }


}