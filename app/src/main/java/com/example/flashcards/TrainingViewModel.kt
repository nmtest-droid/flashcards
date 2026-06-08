package com.example.flashcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.Executors

class TrainingViewModel : ViewModel() {
    private val db = FlashcardsApp.instance.databaseHelper
    private val executor = Executors.newSingleThreadExecutor()
    private val _dueCards = MutableLiveData<List<Card>>()
    val dueCards: LiveData<List<Card>> = _dueCards
    private var currentDeckId: String? = null

    fun loadDueCards(deckId: String) {
        currentDeckId = deckId
        executor.execute { _dueCards.postValue(db.getDueCards(deckId, System.currentTimeMillis())) }
    }

    fun evaluate(card: Card, quality: Int) {
        executor.execute {
            val current = db.getCardProgress(card.id)?.copy(cardId = card.id)
            val newProgress = Scheduler.calculateNewProgress(quality, current)
            db.saveCardProgress(newProgress)
            currentDeckId?.let { loadDueCards(it) }
        }
    }
}