package com.example.flashcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import java.util.concurrent.Executors

class DecksViewModel : ViewModel() {
    private val db = FlashcardsApp.instance.databaseHelper
    private val fs = FlashcardsApp.instance.firestoreService
    private val executor = Executors.newSingleThreadExecutor()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _decks = MutableLiveData<List<Deck>>()
    val decks: LiveData<List<Deck>> = _decks

    private val _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> = _cards

    init {
        loadDecks()
        syncFromCloud()
    }

    private fun syncFromCloud() {
        fs.downloadAllDecks { decks ->
            executor.execute {
                decks.forEach { deck ->
                    db.insertDeck(deck)
                    fs.downloadCardsForDeck(deck.id) { cards ->
                        executor.execute { cards.forEach { db.insertCard(it) } }
                    }
                }
                loadDecks()
            }
        }
    }

    fun loadDecks() {
        executor.execute { _decks.postValue(db.getDecksByUser(userId)) }
    }

    fun addDeck(title: String) {
        val deck = Deck(id = UUID.randomUUID().toString(), title = title, userId = userId)
        executor.execute {
            db.insertDeck(deck)
            fs.uploadDeck(deck)
            loadDecks()
        }
    }

    fun deleteDeck(deckId: String) {
        executor.execute {
            db.deleteDeck(deckId)
            fs.deleteDeckFromCloud(deckId)
            loadDecks()
        }
    }

    fun loadCardsForDeck(deckId: String) {
        executor.execute { _cards.postValue(db.getCardsByDeck(deckId)) }
    }

    fun addCard(deckId: String, question: String, answer: String) {
        val card = Card(id = UUID.randomUUID().toString(), deckId = deckId, question = question, answer = answer)
        executor.execute {
            db.insertCard(card)
            fs.uploadCard(card)
            loadCardsForDeck(deckId)
        }
    }
}