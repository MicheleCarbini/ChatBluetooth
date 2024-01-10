package com.example.chatbluetooth.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbluetooth.bluetooth.Messaggio
import com.example.chatbluetooth.R
import java.lang.IllegalArgumentException

private const val TAG = "MessageAdapter"
private const val MESSAGGIO_REMOTO = 0
private const val MESSAGGIO_LOCALE = 1

// Classe che gestisce la visualizzazione dei messaggi nella RecyclerView
class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Lista mutabile di oggetti "Messaggio" che rappresenta i messaggi da visualizzare
    private val messaggi = mutableListOf<Messaggio>()

    // Metodo chiamato quando è necessario creare una nuova istanza di ViewHolder.
    // In base al tipo di messaggio, usa il layout corretto
    // (per messaggio remoto o locale) e restituisce l'istanza appropriata del ViewHolder associato
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            MESSAGGIO_REMOTO -> {
                val view = inflater.inflate(R.layout.oggetto_messaggio_remoto, parent, false)
                MessaggioRemotoViewHolder(view)
            }
            MESSAGGIO_LOCALE -> {
                val view = inflater.inflate(R.layout.oggetto_messaggio_locale, parent, false)
                MessaggioLocaleViewHolder(view)
            }
            else -> {
                throw IllegalArgumentException("Tipo di view di MessageAdapter sconosciuta")
            }
        }
    }

    // Metodo chiamato quando è necessario associare i dati a una specifica posizione nell'elenco.
    // In base al tipo di messaggio, chiama il metodo bind del ViewHolder associato per popolare
    // i dati e aggiornare l'interfaccia utente
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, posizione: Int) {
        Log.d(TAG, "onBindViewHolder: ")
        val messaggio = messaggi[posizione]
        when(messaggio) {
            is Messaggio.MessaggioRemoto -> {
                (holder as MessaggioRemotoViewHolder).bind(messaggio)
            }
            is Messaggio.MessaggioLocale -> {
                (holder as MessaggioLocaleViewHolder).bind(messaggio)
            }
        }
    }

    // Metodo che restituisce il numero totale di elementi nella lista di messaggi
    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ")
        return messaggi.size
    }

    // Restituisce il tipo di vista per una specifica posizione nell'elenco di messaggi.
    // In base al tipo di messaggio, restituisce MESSAGGIO_REMOTO o MESSAGGIO_LOCALE
    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "getItemViewType: ")
        return when(messaggi[position]) {
            is Messaggio.MessaggioRemoto -> MESSAGGIO_REMOTO
            is Messaggio.MessaggioLocale -> MESSAGGIO_LOCALE
        }
    }

    // Aggiunge il messaggio in fondo alla lista
    fun aggiungiMessaggio(messaggio: Messaggio) {
        Log.d(TAG, "aggiungiMessaggio: ")
        messaggi.add(messaggi.lastIndex + 1, messaggio)
        notifyDataSetChanged()
    }
}