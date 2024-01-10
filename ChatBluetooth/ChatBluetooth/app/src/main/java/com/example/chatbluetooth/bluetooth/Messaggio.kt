package com.example.chatbluetooth.bluetooth

// Questa classe rappresenta i messaggi scambiati tra i device connessi
// La classe MessaggioRemoto rappresenta un messaggio che arriva da un device remoto
// La classe MessaggioLocale rappresenta un messaggio che l'utente desidera inviare a un device remoto
sealed class Messaggio(val testo: String) {
    class MessaggioRemoto(testo: String) : Messaggio(testo)
    class MessaggioLocale(testo: String) : Messaggio(testo)
}