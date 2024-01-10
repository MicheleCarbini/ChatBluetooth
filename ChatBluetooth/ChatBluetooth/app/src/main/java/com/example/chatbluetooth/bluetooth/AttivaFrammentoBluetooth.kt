package com.example.chatbluetooth.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chatbluetooth.R
import com.example.chatbluetooth.databinding.FrammentoAttivazioneBluetoothBinding


// Classe frammento usata per gestire l'attivazione del bluetooth
class AttivaFrammentoBluetooth : Fragment(){

    // Variabile privata utilizzata per collegare gli elementi del layout XML
    // del frammento alle variabili nel codice
    private var _binding: FrammentoAttivazioneBluetoothBinding? = null

    // Questa proprietà è valida solo nella OnCreateView e nella OnDestroyView
    private val binding
        get() = _binding!!

    // Oggetto di tipo Observer<Boolean>. Viene utilizzato per osservare l'oggetto chiamato
    // richiestaAttivazioneBluetooth di ChatServer. Quando il valore di shouldPrompt cambia,
    // verrà chiamato il blocco di codice all'interno della lambda
    private val attivaObserverBluetooth = Observer<Boolean> { shouldPrompt ->
        if(!shouldPrompt) {
            // Non c'è bisogno del prompt quindi navigo al RichiestaFrammentoPosizione
            findNavController().navigate(R.id.azione_controllo_location_permissions)
        }
    }

    // Metodo chiamato quando il frammento viene creato. Qui viene registrato
    // l'attivaObserverBluetooth per osservare le modifiche
    // nell'oggetto richiestaAttivazioneBluetooth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        ChatServer.richiestaAttivazioneBluetooth.observe(this, attivaObserverBluetooth)
    }

    // Metodo chiamato quando il frammento crea la sua interfaccia utente.
    // Qui viene modificato il layout del frammento utilizzando il binding e viene impostato
    // un listener per l'azione dell'utente
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrammentoAttivazioneBluetoothBinding.inflate(inflater, container, false)
        binding.errorAction.setOnClickListener {
            // Chiedo all'utente di attivare Bluetooth (continuo in onActivityResult())
            val attivaBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(attivaBtIntent, REQUEST_ENABLE_BT)
        }
        return binding.root
    }

    // Metodo chiamato quando un'attività lanciata dal frammento restituisce un risultato.
    // In questo caso, è progettato per gestire il risultato dell'attivazione del Bluetooth.
    // Se l'utente attiva il Bluetooth con successo,
    // viene avviato il server
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ){
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    ChatServer.startServer(requireActivity().application)
                }
                super.onActivityResult(requestCode, resultCode, data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}