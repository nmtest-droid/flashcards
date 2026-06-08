package com.example.flashcards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.ItemCardBinding

class CardAdapter(private val onClick: (Card) -> Unit) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var items = listOf<Card>()

    fun submitList(list: List<Card>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            binding.tvQuestion.text = card.question
            binding.tvAnswer.text = card.answer
            binding.root.setOnClickListener { onClick(card) }
        }
    }
}