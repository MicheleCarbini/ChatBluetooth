package com.example.chatbluetooth

import android.view.View

// Definisco due estensioni per la classe view e una proprietà di estensione generica

// Usata per rendere visibile un oggetto di tipo view
fun View.visibile() {
    this.visibility = View.VISIBLE
}

// Usata per rendere invisibile un oggetto di tipo view
fun View.gone() {
    this.visibility = View.GONE
}

//  Questa proprietà è dichiarata con un parametro generico <T> e restituisce l'oggetto su cui è chiamato.
//  La sua utilità principale è nei controlli when su tipi sigillati o enumerazioni.
//  Quando si usa when per controllare tutti i possibili casi di un tipo, Kotlin richiede che il controllo sia esaustivo (copra tutti i casi).
//  L'uso di questa proprietà assicura che il compilatore Kotlin consideri il controllo when come esaustivo,
//  anche se il risultato non viene utilizzato effettivamente nel codice
val <T> T.exhaustive: T
    get() = this