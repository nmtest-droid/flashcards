package com.example.flashcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {
    private val db = FlashcardsApp.instance.databaseHelper
    private val fs = FlashcardsApp.instance.firestoreService
    private val executor = Executors.newSingleThreadExecutor()
    private val _syncStatus = MutableLiveData<Boolean>()
    val syncStatus: LiveData<Boolean> = _syncStatus

    fun syncData() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        fs.downloadAllDecks { decks ->
            executor.execute {
                decks.forEach { deck ->
                    db.insertDeck(deck)
                    fs.downloadCardsForDeck(deck.id) { cards ->
                        executor.execute { cards.forEach { db.insertCard(it) } }
                    }
                }
                _syncStatus.postValue(true)
            }
        }
    }

    fun logout() = FirebaseAuth.getInstance().signOut()
}