package com.example.chatbluetooth.scan

import android.bluetooth.BluetoothDevice
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbluetooth.R

// Classe progettata per visualizzare e gestire la selezione di elementi della lista di dispositivi Bluetooth
class DeviceScanViewHolder(
    view: View,
    // Parametro che rappresenta il gestore di eventi che sarà chiamato quando l'elemento
    // della lista viene selezionato
    val deviceSelezionato: (BluetoothDevice) -> Unit
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    // Variabile privata nome, che rappresenta un oggetto TextView all'interno della view associata a questo ViewHolder.
    // Questa TextView sarà utilizzata per visualizzare il nome del device Bluetooth
    private val nome = itemView.findViewById<TextView>(R.id.nome_device)

    // Variabile privata indirizzo, che rappresenta un oggetto TextView all'interno della view associata a questo ViewHolder.
    // Questa TextView sarà utilizzata per visualizzare l'indirizzo del device Bluetooth
    private val indirizzo = itemView.findViewById<TextView>(R.id.indirizzo_device)

    // Variabile privata che tiene traccia dell'oggetto BluetoothDevice associato a questo ViewHolder
    private var bluetoothDevice: BluetoothDevice? = null

    // Nel blocco di inizializzazione (init), viene impostato il click listener dell'intera vista
    // associata a questo ViewHolder, che è rappresentato da itemView. Quando l'elemento della lista
    // viene cliccato, verrà chiamato il metodo onClick
    init {
        itemView.setOnClickListener(this)
    }

    // Metodo che associare i dati di BluetoothDevice alla ViewHolder
    fun bind(device: BluetoothDevice) {
        bluetoothDevice = device
        nome.text = device.name
        indirizzo.text = device.address
    }

    // Metodo chiamato quando l'elemento della lista viene cliccato. Verifica se
    // l'oggetto bluetoothDevice non è nullo e, in caso affermativo, uso una lambda expression,
    // passando l'oggetto BluetoothDevice associato a questo ViewHolder
    override fun onClick(view: View) {
        bluetoothDevice?.let { device ->
            deviceSelezionato(device)
        }
    }
}