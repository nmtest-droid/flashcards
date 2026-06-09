package com.example.flashcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.ItemTrainingCardBinding

class CardPageAdapter(private val onEvaluate: (Int, Int) -> Unit) : RecyclerView.Adapter<CardPageAdapter.VH>() {
    private val cards = mutableListOf<Card>()

    fun submitList(list: List<Card>) {
        cards.clear()
        cards.addAll(list)
        notifyDataSetChanged()
    }

    fun removeCardAt(position: Int) {
        if (position in cards.indices) {
            cards.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cards.size - position)
        }
    }

    fun getCardAt(position: Int): Card? = cards.getOrNull(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTrainingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(cards[position], position)
    override fun getItemCount() = cards.size

    inner class VH(private val binding: ItemTrainingCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card, position: Int) {
            binding.tvQuestion.text = card.question
            binding.tvAnswer.text = card.answer
            binding.groupAnswer.visibility = View.GONE
            binding.btnShowAnswer.setOnClickListener { binding.groupAnswer.visibility = View.VISIBLE }
            binding.btnForgot.setOnClickListener { onEvaluate(position, 0) }
            binding.btnRemembered.setOnClickListener { onEvaluate(position, 5) }
        }
    }
}