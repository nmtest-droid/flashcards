package com.example.flashcards

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class FirestoreService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun currentUserId() = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")

    fun uploadDeck(deck: Deck) {
        firestore.collection("users").document(currentUserId())
            .collection("decks").document(deck.id)
            .set(mapOf("title" to deck.title))
    }
    fun deleteDeckFromCloud(deckId: String) {
        firestore.collection("users").document(currentUserId())
            .collection("decks").document(deckId).delete()
    }
    fun downloadAllDecks(onComplete: (List<Deck>) -> Unit) {
        firestore.collection("users").document(currentUserId())
            .collection("decks").get()
            .addOnSuccessListener { snapshot ->
                val decks = snapshot.documents.map { doc ->
                    Deck(id = doc.id, title = doc.getString("title") ?: "No Title", userId = currentUserId())
                }
                onComplete(decks)
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }

    fun uploadCard(card: Card) {
        firestore.collection("users").document(currentUserId())
            .collection("decks").document(card.deckId)
            .collection("cards").document(card.id)
            .set(mapOf("question" to card.question, "answer" to card.answer))
    }
    fun deleteCardFromCloud(deckId: String, cardId: String) {
        firestore.collection("users").document(currentUserId())
            .collection("decks").document(deckId)
            .collection("cards").document(cardId).delete()
    }
    fun downloadCardsForDeck(deckId: String, onComplete: (List<Card>) -> Unit) {
        firestore.collection("users").document(currentUserId())
            .collection("decks").document(deckId)
            .collection("cards").get()
            .addOnSuccessListener { snapshot ->
                val cards = snapshot.documents.map { doc ->
                    Card(id = doc.id, deckId = deckId,
                        question = doc.getString("question") ?: "",
                        answer = doc.getString("answer") ?: "")
                }
                onComplete(cards)
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }
}