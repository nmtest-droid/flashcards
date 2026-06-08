package com.example.flashcards

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.flashcards.databinding.DialogAddEditCardBinding

class AddEditCardDialog private constructor(
    private val deckId: String,
    private val onSave: (String, String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddEditCardBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setTitle("Новая карточка")
            .setView(binding.root)
            .setPositiveButton("Сохранить") { _, _ ->
                val question = binding.etQuestion.text.toString().trim()
                val answer = binding.etAnswer.text.toString().trim()
                if (question.isNotEmpty() && answer.isNotEmpty()) {
                    onSave(question, answer)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
    }

    companion object {
        fun newInstance(deckId: String, onSave: (String, String) -> Unit) =
            AddEditCardDialog(deckId, onSave)
    }
}