package com.example.flashcards

data class Deck(
    val id: String,
    val title: String,
    val userId: String
)

data class Card(
    val id: String,
    val deckId: String,
    val question: String,
    val answer: String
)

data class CardProgress(
    val cardId: String,
    val easiness: Double = 2.5,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val nextReviewDate: Long = System.currentTimeMillis()
)