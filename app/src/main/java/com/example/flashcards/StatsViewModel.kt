package com.example.flashcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.Executors

class StatsViewModel : ViewModel() {
    private val db = FlashcardsApp.instance.databaseHelper
    private val executor = Executors.newSingleThreadExecutor()
    private val _statsText = MutableLiveData<String>()
    val statsText: LiveData<String> = _statsText

    fun loadStats() {
        executor.execute {
            val total = db.readableDatabase.rawQuery("SELECT COUNT(*) FROM cards", null).use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
            val due = db.readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM cards c LEFT JOIN card_progress p ON c.id = p.card_id WHERE p.next_review_date <= ? OR p.next_review_date IS NULL",
                arrayOf(System.currentTimeMillis().toString())
            ).use { if (it.moveToFirst()) it.getInt(0) else 0 }
            _statsText.postValue("Всего карточек: $total\nОжидают повторения сегодня: $due")
        }
    }
}