package com.example.chatbluetooth.scan

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbluetooth.R


//  Classe utilizzata per gestire l'adattamento dei dati di dispositivi
//  Bluetooth alla visualizzazione all'interno della RecyclerView
class DeviceScanAdapter (
    // Parametro che rappresenta un gestore di eventi che verrà chiamato quando un device viene selezionato
    private val onDeviceSelected: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceScanViewHolder>() {

    // Lista mutable di oggetti BluetoothDevice, inizializzata con una lista vuota.
    // Questa lista conterrà i dispositivi Bluetooth da visualizzare nella RecyclerView
    private var items = listOf<BluetoothDevice>()

    //  Metodo chiamato quando viene creato un nuovo ViewHolder. Utilizza un layout
    //  inflater per creare una nuova view basata sul layout definito
    //  in R.layout.oggetto_device. Quindi, restituisce un nuovo oggetto DeviceScanViewHolder creato
    //  con questa vista e il gestore di eventi onDeviceSelected
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.oggetto_device, parent, false)
        return DeviceScanViewHolder(view, onDeviceSelected)
    }

    // Metodo usato per associare i dati di un elemento alla view del ViewHolder.
    // Verifica se l'elemento è valido prima di chiamare il metodo bind del ViewHolder
    // per popolare la view con i dati appropriati
    override fun onBindViewHolder(holder: DeviceScanViewHolder, position: Int) {
        items.getOrNull(position)?.let { result ->
            holder.bind(result)
        }
    }

    // Metodo che restituisce il numero totale di elementi nella lista
    override fun getItemCount(): Int {
        return items.size
    }

    //  Metodo usato per aggiornare la lista degli oggetti BluetoothDevice con una nuova lista
    //  e notifica la RecyclerView che i dati sono cambiati,
    //  in modo che la view venga aggiornata di conseguenza
    fun updateItems(results: List<BluetoothDevice>) {
        items = results
        notifyDataSetChanged()
    }
}

