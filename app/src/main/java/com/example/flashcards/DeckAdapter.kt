package com.example.flashcards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.ItemDeckBinding

class DeckAdapter(
    private val onItemClick: (Deck) -> Unit,
    private val onDeleteClick: (Deck) -> Unit
) : ListAdapter<Deck, DeckAdapter.VH>(DeckDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: ItemDeckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deck: Deck) {
            binding.tvDeckTitle.text = deck.title
            binding.root.setOnClickListener { onItemClick(deck) }
            binding.btnDelete.setOnClickListener { onDeleteClick(deck) }
        }
    }
}

class DeckDiffCallback : DiffUtil.ItemCallback<Deck>() {
    override fun areItemsTheSame(old: Deck, new: Deck) = old.id == new.id
    override fun areContentsTheSame(old: Deck, new: Deck) = old == new
}