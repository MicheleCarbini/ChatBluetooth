package com.example.chatbluetooth.chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbluetooth.bluetooth.Messaggio
import com.example.chatbluetooth.R

// Classe ViewHolder progettata per gestire la visualizzazione di elementi di tipo
// Messaggio.MessaggioRemoto all'interno di una RecyclerView
class MessaggioRemotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    // Creo una proprietà privata testoMessaggio di tipo TextView e la inizializza con il
    // TextView identificato da R.id.testo_messaggio all'interno della itemView passata
    // al costruttore. Questo corrisponde a un elemento di testo all'interno
    // della riga della RecyclerView
    private val testoMessaggio = itemView.findViewById<TextView>(R.id.testo_messaggio)

    // Metodo che associa i dati di un oggetto MessaggioRemoto alla view del ViewHolder
    fun bind(messaggio: Messaggio.MessaggioRemoto) {
        testoMessaggio.text = messaggio.testo
    }
}
