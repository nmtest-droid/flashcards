package com.example.flashcards

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "flashcards.db"
        const val DB_VERSION = 1

        const val TABLE_DECKS = "decks"
        const val COL_DECK_ID = "id"
        const val COL_DECK_TITLE = "title"
        const val COL_DECK_USER_ID = "user_id"

        const val TABLE_CARDS = "cards"
        const val COL_CARD_ID = "id"
        const val COL_CARD_DECK_ID = "deck_id"
        const val COL_CARD_QUESTION = "question"
        const val COL_CARD_ANSWER = "answer"

        const val TABLE_CARD_PROGRESS = "card_progress"
        const val COL_PROG_CARD_ID = "card_id"
        const val COL_PROG_EASINESS = "easiness"
        const val COL_PROG_INTERVAL_DAYS = "interval_days"
        const val COL_PROG_REPETITIONS = "repetitions"
        const val COL_PROG_NEXT_REVIEW_DATE = "next_review_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_DECKS (
                $COL_DECK_ID TEXT PRIMARY KEY,
                $COL_DECK_TITLE TEXT NOT NULL,
                $COL_DECK_USER_ID TEXT NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE $TABLE_CARDS (
                $COL_CARD_ID TEXT PRIMARY KEY,
                $COL_CARD_DECK_ID TEXT NOT NULL,
                $COL_CARD_QUESTION TEXT NOT NULL,
                $COL_CARD_ANSWER TEXT NOT NULL,
                FOREIGN KEY ($COL_CARD_DECK_ID) REFERENCES $TABLE_DECKS($COL_DECK_ID)
            )
        """)
        db.execSQL("""
            CREATE TABLE $TABLE_CARD_PROGRESS (
                $COL_PROG_CARD_ID TEXT PRIMARY KEY,
                $COL_PROG_EASINESS REAL NOT NULL DEFAULT 2.5,
                $COL_PROG_INTERVAL_DAYS INTEGER NOT NULL DEFAULT 0,
                $COL_PROG_REPETITIONS INTEGER NOT NULL DEFAULT 0,
                $COL_PROG_NEXT_REVIEW_DATE INTEGER NOT NULL DEFAULT 0
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARD_PROGRESS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DECKS")
        onCreate(db)
    }

    // Деки
    fun insertDeck(deck: Deck) = writableDatabase.insert(TABLE_DECKS, null, deck.toCV())
    fun updateDeck(deck: Deck) = writableDatabase.update(TABLE_DECKS, deck.toCV(), "$COL_DECK_ID = ?", arrayOf(deck.id))
    fun deleteDeck(deckId: String) {
        writableDatabase.delete(TABLE_CARDS, "$COL_CARD_DECK_ID = ?", arrayOf(deckId))
        writableDatabase.delete(TABLE_CARD_PROGRESS, "$COL_PROG_CARD_ID IN (SELECT $COL_CARD_ID FROM $TABLE_CARDS WHERE $COL_CARD_DECK_ID = ?)", arrayOf(deckId))
        writableDatabase.delete(TABLE_DECKS, "$COL_DECK_ID = ?", arrayOf(deckId))
    }
    fun getDecksByUser(userId: String): List<Deck> {
        val cursor = readableDatabase.query(TABLE_DECKS, null, "$COL_DECK_USER_ID = ?", arrayOf(userId), null, null, null)
        return cursor.use {
            val list = mutableListOf<Deck>()
            while (it.moveToNext()) {
                list.add(Deck(
                    id = it.getString(it.getColumnIndexOrThrow(COL_DECK_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_DECK_TITLE)),
                    userId = it.getString(it.getColumnIndexOrThrow(COL_DECK_USER_ID))
                ))
            }
            list
        }
    }
    fun getDeckById(deckId: String): Deck? {
        val cursor = readableDatabase.query(TABLE_DECKS, null, "$COL_DECK_ID = ?", arrayOf(deckId), null, null, null)
        return cursor.use {
            if (it.moveToFirst()) Deck(
                id = it.getString(it.getColumnIndexOrThrow(COL_DECK_ID)),
                title = it.getString(it.getColumnIndexOrThrow(COL_DECK_TITLE)),
                userId = it.getString(it.getColumnIndexOrThrow(COL_DECK_USER_ID))
            ) else null
        }
    }

    // Карточки
    fun insertCard(card: Card) = writableDatabase.insert(TABLE_CARDS, null, card.toCV())
    fun updateCard(card: Card) = writableDatabase.update(TABLE_CARDS, card.toCV(), "$COL_CARD_ID = ?", arrayOf(card.id))
    fun deleteCard(cardId: String) {
        writableDatabase.delete(TABLE_CARD_PROGRESS, "$COL_PROG_CARD_ID = ?", arrayOf(cardId))
        writableDatabase.delete(TABLE_CARDS, "$COL_CARD_ID = ?", arrayOf(cardId))
    }
    fun getCardsByDeck(deckId: String): List<Card> {
        val cursor = readableDatabase.query(TABLE_CARDS, null, "$COL_CARD_DECK_ID = ?", arrayOf(deckId), null, null, null)
        return cursor.use {
            val list = mutableListOf<Card>()
            while (it.moveToNext()) {
                list.add(Card(
                    id = it.getString(it.getColumnIndexOrThrow(COL_CARD_ID)),
                    deckId = it.getString(it.getColumnIndexOrThrow(COL_CARD_DECK_ID)),
                    question = it.getString(it.getColumnIndexOrThrow(COL_CARD_QUESTION)),
                    answer = it.getString(it.getColumnIndexOrThrow(COL_CARD_ANSWER))
                ))
            }
            list
        }
    }

    // Прогресс
    fun saveCardProgress(progress: CardProgress) {
        writableDatabase.insertWithOnConflict(TABLE_CARD_PROGRESS, null, progress.toCV(), SQLiteDatabase.CONFLICT_REPLACE)
    }
    fun getCardProgress(cardId: String): CardProgress? {
        val cursor = readableDatabase.query(TABLE_CARD_PROGRESS, null, "$COL_PROG_CARD_ID = ?", arrayOf(cardId), null, null, null)
        return cursor.use {
            if (it.moveToFirst()) CardProgress(
                cardId = it.getString(it.getColumnIndexOrThrow(COL_PROG_CARD_ID)),
                easiness = it.getDouble(it.getColumnIndexOrThrow(COL_PROG_EASINESS)),
                intervalDays = it.getInt(it.getColumnIndexOrThrow(COL_PROG_INTERVAL_DAYS)),
                repetitions = it.getInt(it.getColumnIndexOrThrow(COL_PROG_REPETITIONS)),
                nextReviewDate = it.getLong(it.getColumnIndexOrThrow(COL_PROG_NEXT_REVIEW_DATE))
            ) else null
        }
    }
    fun getDueCards(deckId: String, currentTimeMillis: Long): List<Card> {
        val cursor = readableDatabase.rawQuery("""
            SELECT c.* FROM $TABLE_CARDS c
            LEFT JOIN $TABLE_CARD_PROGRESS p ON c.$COL_CARD_ID = p.$COL_PROG_CARD_ID
            WHERE c.$COL_CARD_DECK_ID = ? AND (p.$COL_PROG_NEXT_REVIEW_DATE IS NULL OR p.$COL_PROG_NEXT_REVIEW_DATE <= ?)
        """, arrayOf(deckId, currentTimeMillis.toString()))
        return cursor.use {
            val list = mutableListOf<Card>()
            while (it.moveToNext()) {
                list.add(Card(
                    id = it.getString(it.getColumnIndexOrThrow(COL_CARD_ID)),
                    deckId = it.getString(it.getColumnIndexOrThrow(COL_CARD_DECK_ID)),
                    question = it.getString(it.getColumnIndexOrThrow(COL_CARD_QUESTION)),
                    answer = it.getString(it.getColumnIndexOrThrow(COL_CARD_ANSWER))
                ))
            }
            list
        }
    }

    private fun Deck.toCV() = ContentValues().apply {
        put(COL_DECK_ID, id)
        put(COL_DECK_TITLE, title)
        put(COL_DECK_USER_ID, userId)
    }
    private fun Card.toCV() = ContentValues().apply {
        put(COL_CARD_ID, id)
        put(COL_CARD_DECK_ID, deckId)
        put(COL_CARD_QUESTION, question)
        put(COL_CARD_ANSWER, answer)
    }
    private fun CardProgress.toCV() = ContentValues().apply {
        put(COL_PROG_CARD_ID, cardId)
        put(COL_PROG_EASINESS, easiness)
        put(COL_PROG_INTERVAL_DAYS, intervalDays)
        put(COL_PROG_REPETITIONS, repetitions)
        put(COL_PROG_NEXT_REVIEW_DATE, nextReviewDate)
    }
}