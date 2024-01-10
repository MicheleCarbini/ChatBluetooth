package com.example.chatbluetooth.scan

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbluetooth.R
import com.example.chatbluetooth.bluetooth.ChatServer
import com.example.chatbluetooth.databinding.FrammentoScanDeviceBinding
import com.example.chatbluetooth.exhaustive
import com.example.chatbluetooth.gone
import com.example.chatbluetooth.scan.DeviceScanViewState.*
import com.example.chatbluetooth.visibile

private const val TAG = "FrammentoScanDevice"

// Classe usata per gestire la scansione dei dispositivi Bluetooth, visualizzarli in una RecyclerView,
// gestendo diversi stati della UI e permettendo la selezione di un dispositivo per la connessione al chat server
class FrammentoScanDevice : Fragment() {
    // Variabile privata utilizzata per collegare gli elementi del layout XML
    // del frammento alle variabili nel codice
    private var _binding: FrammentoScanDeviceBinding? = null

    // Questa proprietà è valida solo nella OnCreateView e nella OnDestroyView
    private val binding
        get() = _binding!!

    //  Creo un'istanza del DeviceScanViewModel utilizzando il pattern di viewModels()
    private val viewModel: DeviceScanViewModel by viewModels()

    // Creo un adapter per la visualizzazione dei dispositivi Bluetooth nella RecyclerView
    private val deviceScanAdapter by lazy {
        DeviceScanAdapter(onDeviceSelected)
    }

    // Observer usato per gestire lo stato della view
    private val viewStateObserver = Observer<DeviceScanViewState> { state ->
        when (state) {
            is ActiveScan -> mostraCaricamento()
            is risultatoScan -> mostraRisultati(state.risultatoScan)
            is Errore -> mostraErrore(state.messaggio)
            is AdvertisementNonSupportato -> mostraErroreAdvertising()
        }.exhaustive
    }

    // Lambda expression che viene passata all'adapter e viene chiamata quando un dispositivo
    // viene selezionato
    private val onDeviceSelected: (BluetoothDevice) -> Unit = { device ->
        ChatServer.setConnessioneChatCorrente(device)
        // Torna indietro al frammento chat
        findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrammentoScanDeviceBinding.inflate(inflater, container, false)
        val devAddr = getString(R.string.indirizzo_tuo_device) + ChatServer.getYourDeviceAddress()
        binding.indirizzoTuoDevice.text = devAddr
        binding.listaDevice.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceScanAdapter
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.lista_dispositivi)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.statoView.observe(viewLifecycleOwner, viewStateObserver)
    }

    private fun mostraCaricamento() {
        Log.d(TAG, "showLoading")
        binding.scanning.visibile()

        binding.listaDevice.gone()
        binding.noDevice.gone()
        binding.errore.gone()
        binding.containerConfermaChat.gone()
    }

    private fun mostraRisultati(risultatoScan: Map<String, BluetoothDevice>) {
        if (risultatoScan.isNotEmpty()) {
            binding.listaDevice.visibile()
            deviceScanAdapter.updateItems(risultatoScan.values.toList())
            binding.scanning.gone()
            binding.noDevice.gone()
            binding.errore.gone()
            binding.containerConfermaChat.gone()
        } else {
            mostraNoDevice()
        }
    }

    private fun mostraNoDevice() {
        binding.noDevice.visibile()
        binding.listaDevice.gone()
        binding.scanning.gone()
        binding.errore.gone()
        binding.containerConfermaChat.gone()
    }

    private fun mostraErrore(messaggio: String) {
        Log.d(TAG, "mostraErrore: ")
        binding.errore.visibile()
        binding.messaggioErrore.text = messaggio

        // Nascondo il bottone in caso non ci siano device
        binding.errorAction.gone()
        binding.scanning.gone()
        binding.noDevice.gone()
        binding.containerConfermaChat.gone()
    }

    private fun mostraErroreAdvertising() {
        mostraErrore("BLE advertising non è supportato su questo device")
    }

}