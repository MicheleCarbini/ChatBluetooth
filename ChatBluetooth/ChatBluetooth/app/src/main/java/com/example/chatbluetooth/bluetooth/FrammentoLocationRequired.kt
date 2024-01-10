package com.example.chatbluetooth.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatbluetooth.R
import com.example.chatbluetooth.databinding.FrammentoLocationRequiredBinding

private const val TAG = "FramLocationRequired"
private const val CODICE_LOCATION_REQUEST = 0


// Frammento che controlla se l'app ha il permesso ACCESS_FINE_LOCATION.
// Questo permesso è richiesto quando si utilizzano API BLE, quindi
// l'utente deve garantire il permesso prima di utilizzare FrammentoChatBluetooth o FrammentoListaDevice
class FrammentoLocationRequired : Fragment(){

    // Variabile privata utilizzata per collegare gli elementi del layout XML
    // del frammento alle variabili nel codice
    private var _binding: FrammentoLocationRequiredBinding? = null
    private val binding: FrammentoLocationRequiredBinding
        get() = _binding!!

    // Metodo chiamato per creare e restituire la gerarchia di view associata al frammento.
    // Qui viene modificato il layout del frammento utilizzando il binding e imposta un listener per
    // il bottone "Concedi Permesso", e nasconde messaggio di errore inizialmente
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrammentoLocationRequiredBinding.inflate(inflater, container, false)

        // Nascondo il messaggio di errore quando controllo i permessi
        binding.messaggioErroreLocation.visibility = View.GONE
        binding.bottoneConcediPermesso.visibility = View.GONE
        // Imposto un listener sul bottone per garantire permesso
        binding.bottoneConcediPermesso.setOnClickListener {
            controllaPermessiLocation()
        }

        return binding.root
    }

    // Metodo chiamato quando il frammento diventa visibile a schermo
    override fun onStart() {
        super.onStart()
        // Controllo i permessi di location quando il frammento diventa visibile a schermo
        controllaPermessiLocation()
    }

    // Metodo chiamato quando l'utente risponde alla richiesta di permessi.
    // Se l'utente concede il permesso, viene effettuata una navigazione a un altro frammento.
    // In caso contrario, viene chiamato il metodo mostraErrore per mostrare un messaggio di errore
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: ")
        when(requestCode) {
            CODICE_LOCATION_REQUEST -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Navigo al frammento della chat
                    findNavController().navigate(R.id.azione_inizio_chat)
                } else {
                    mostraErrore()
                }
            }
        }
    }
    // Metodo che mostra un messaggio di errore e il pulsante "Concedi Permesso" nel layout
    private fun mostraErrore() {
        binding.messaggioErroreLocation.visibility = View.VISIBLE
        binding.bottoneConcediPermesso.visibility = View.VISIBLE
    }

    // Metodo che verifica se l'app ha il permesso ACCESS_FINE_LOCATION. Se il permesso è già concesso,
    // viene effettuata una navigazione a un altro frammento. In caso contrario, viene richiesto il
    // permesso utilizzando requestPermissions
    private fun controllaPermessiLocation() {
        val haPermessiLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (haPermessiLocation) {
            // Navigo al frammento della chat
            findNavController().navigate(R.id.azione_inizio_chat)
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODICE_LOCATION_REQUEST
            )
        }
    }
}