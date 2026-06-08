package com.example.flashcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.databinding.FragmentDeckDetailBinding

class DeckDetailFragment : Fragment() {
    private var _binding: FragmentDeckDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DeckDetailFragmentArgs by navArgs()
    private val viewModel: DecksViewModel by viewModels({ requireActivity() })
    private lateinit var adapter: CardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeckDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CardAdapter {}
        binding.recyclerCards.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCards.adapter = adapter

        val deckId = args.deckId
        viewModel.loadCardsForDeck(deckId)
        viewModel.cards.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.fabAddCard.setOnClickListener {
            AddEditCardDialog.newInstance(deckId) { q, a -> viewModel.addCard(deckId, q, a) }
                .show(childFragmentManager, "AddCard")
        }

        binding.btnStartTraining.setOnClickListener {
            findNavController().navigate(DeckDetailFragmentDirections.actionDeckDetailFragmentToTrainingFragment(deckId))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}