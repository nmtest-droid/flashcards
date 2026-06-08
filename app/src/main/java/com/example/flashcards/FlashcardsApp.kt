package com.example.flashcards

import android.app.Application

class FlashcardsApp : Application() {
    lateinit var databaseHelper: DatabaseHelper
    lateinit var firestoreService: FirestoreService

    override fun onCreate() {
        super.onCreate()
        instance = this
        databaseHelper = DatabaseHelper(this)
        firestoreService = FirestoreService()
    }

    companion object {
        lateinit var instance: FlashcardsApp private set
    }
}