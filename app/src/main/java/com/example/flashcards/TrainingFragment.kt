package com.example.flashcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.flashcards.databinding.FragmentTrainingBinding

class TrainingFragment : Fragment() {
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!
    private val args: TrainingFragmentArgs by navArgs()
    private lateinit var viewModel: TrainingViewModel
    private lateinit var adapter: CardPageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[TrainingViewModel::class.java]

        adapter = CardPageAdapter { quality -> adapter.currentCard()?.let { viewModel.evaluate(it, quality) } }
        binding.viewPager.adapter = adapter

        viewModel.dueCards.observe(viewLifecycleOwner) { cards ->
            adapter.submitList(cards)
            binding.tvEmpty.visibility = if (cards.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loadDueCards(args.deckId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}