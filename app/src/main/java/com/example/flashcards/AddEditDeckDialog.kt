package com.example.flashcards

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.flashcards.databinding.DialogAddEditDeckBinding

class AddEditDeckDialog private constructor(
    private val existingDeck: Deck?,
    private val onSave: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddEditDeckBinding.inflate(layoutInflater)
        existingDeck?.let { binding.etTitle.setText(it.title) }
        return AlertDialog.Builder(requireContext())
            .setTitle(if (existingDeck == null) "Новая колода" else "Редактировать")
            .setView(binding.root)
            .setPositiveButton("Сохранить") { _, _ ->
                val title = binding.etTitle.text.toString().trim()
                if (title.isNotEmpty()) onSave(title)
            }
            .setNegativeButton("Отмена", null)
            .create()
    }

    companion object {
        fun newInstance(deck: Deck?, onSave: (String) -> Unit) = AddEditDeckDialog(deck, onSave)
    }
}