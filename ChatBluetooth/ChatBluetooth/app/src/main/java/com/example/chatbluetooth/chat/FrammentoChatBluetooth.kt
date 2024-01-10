package com.example.chatbluetooth.chat

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbluetooth.bluetooth.Messaggio
import com.example.chatbluetooth.R
import com.example.chatbluetooth.bluetooth.ChatServer
import com.example.chatbluetooth.databinding.FrammentoChatBluetoothBinding
import com.example.chatbluetooth.gone
import com.example.chatbluetooth.visibile

private const val TAG = "FrammentoChatBluetooth"

// Classe frammento usata per gestire la chat bluetooth
class FrammentoChatBluetooth : Fragment(){

    // Variabile privata utilizzata per collegare gli elementi del layout XML
    // del frammento alle variabili nel codice
    private var _binding: FrammentoChatBluetoothBinding? = null
    // Questa proprietà è valida solo nella OnCreateView e nella OnDestroyView
    private val binding: FrammentoChatBluetoothBinding
        get() = _binding!!

    // Sono dichiarati osservatori per gli oggetti LiveData forniti da ChatServer.
    // Gli osservatori reagiscono ai cambiamenti di stato della connessione,
    // alle richieste di connessione e ai messaggi ricevuti
    private val deviceConnectionObserver = Observer<StatoConnessioneDevice> { state ->
        when(state) {
            is StatoConnessioneDevice.Connesso -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: con device $device")
                chatWith(device)
            }
            is StatoConnessioneDevice.Disconnesso -> {
                showDisconnected()
            }
        }

    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: con device $device")
        ChatServer.setConnessioneChatCorrente(device)
    }

    private val messageObserver = Observer<Messaggio> { messaggio ->
        Log.d(TAG, "Con messaggio ${messaggio.testo}")
        adapter.aggiungiMessaggio(messaggio)
    }


    // Viene creato un oggetto MessageAdapter per gestire la visualizzazione dei messaggi nella chat.
    // L'adapter viene impostato per la RecyclerView nel layout del frammento
    private val adapter = MessageAdapter()

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    //  Metodo chiamato quando il frammento crea la sua interfaccia utente. Inizializza l'adapter per
    //  la RecyclerView e imposta i listener per gli elementi UI come il pulsante "Invia Messaggio".
    //  Mostra anche lo stato iniziale della connessione come "non connesso"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrammentoChatBluetoothBinding.inflate(inflater, container, false)

        Log.d(TAG, "chatCon: imposta adapter $adapter")
        binding.messaggi.layoutManager = LinearLayoutManager(context)
        binding.messaggi.adapter = adapter

        showDisconnected()

        binding.deviceConnesso.setOnClickListener {
            findNavController().navigate(R.id.azione_trova_nuovo_device)
        }

        return binding.root
    }

    // Metodo chiamato quando il frammento diventa visibile.
    // Qui vengono registrati gli osservatori LiveData per la connessione,
    // le richieste di connessione e i messaggi
    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.titolo_chat)
        ChatServer.richiestaConnessione.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    // Metodo chiamato quando il frammento sta per terminare.
    // Qui viene impostata la variabile _binding a null per evitare il consumo di memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Metodo per aggiornare l'interfaccia utente per mostrare lo stato di connessione con un device Bluetooth.
    // Mostra il layout connesso e il nome del device con cui si sta chattando.
    // Abilita il pulsante per l'invio di messaggi
    private fun chatWith(device: BluetoothDevice) {
        binding.containerConnesso.visibile()
        binding.containerNonConnesso.gone()

        val chattingWithString = resources.getString(R.string.chattando_con_device, device.address)
        binding.nomeDeviceConnesso.text = chattingWithString
        binding.inviaMessaggio.setOnClickListener {
            val message = binding.testoMessaggio.text.toString()
            // invia messaggi solo se non sono vuoti
            if (message.isNotEmpty()) {
                ChatServer.inviaMessaggio(message)
                // cancella il messaggio precedente per inserirne uno nuovo
                binding.testoMessaggio.setText("")
            }
        }
    }

    // Metodo che mostra l'interfaccia utente quando il device è disconnesso.
    // Nasconde la tastiera virtuale e visualizza il layout "non connesso"
    private fun showDisconnected() {
        hideKeyboard()
        binding.containerNonConnesso.visibile()
        binding.containerConnesso.gone()
    }

    // Metodo per nascondere la tastiera virtuale
    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}